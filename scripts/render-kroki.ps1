#Requires -Version 5.1
<#
.SYNOPSIS
    Renders kroki-* fenced code blocks in notes files to SVG images.

.DESCRIPTION
    Scans *_notes.md files under notes/topics/ for fenced blocks tagged
    kroki-<type> (e.g. kroki-plantuml), renders each via the Kroki REST API,
    saves the SVG to images/rendered/<hash>.svg beside the notes file, and
    inserts a rendered marker + image link immediately after the fenced block.

    Idempotent: blocks whose source has not changed (same SHA-256) are skipped.
    When the source changes the SVG is re-rendered and the marker is updated.

    Alt text is resolved in order:
      1. A PlantUML line comment:  ' alt: My description
      2. A PlantUML title directive: title My description
      3. Fallback: "Diagram N" (1-based index within the file)
    If a marker already exists the stored alt text is preserved across re-renders.

.PARAMETER All
    Process every *_notes.md file found under notes/topics/.

.PARAMETER File
    Process a single notes file (absolute or repo-relative path).

.EXAMPLE
    .\scripts\render-kroki.ps1 -All
    .\scripts\render-kroki.ps1 -File notes\topics\t02_recursion\t02_recursion_notes.md

.NOTES
    Requires internet access to reach https://kroki.io.
    SVG files are committed alongside the notes so GitHub/GitLab always show images.
#>
param(
    [switch]$All,
    [string]$File
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot  = Split-Path $PSScriptRoot -Parent
$topicsDir = Join-Path $repoRoot "notes\topics"
$krokiBase = "https://kroki.io"

# ── helpers ────────────────────────────────────────────────────────────────────

function Get-HashShort([string]$text) {
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($text)
    $sha   = [System.Security.Cryptography.SHA256]::Create()
    return [BitConverter]::ToString($sha.ComputeHash($bytes)).Replace("-", "").Substring(0, 8).ToLower()
}

function Invoke-Kroki([string]$source, [string]$type) {
    $wc = New-Object System.Net.WebClient
    $wc.Headers.Add("Content-Type", "text/plain; charset=utf-8")
    $bodyBytes = [System.Text.Encoding]::UTF8.GetBytes($source)
    $svgBytes  = $wc.UploadData("$krokiBase/$type/svg", "POST", $bodyBytes)
    return [System.Text.Encoding]::UTF8.GetString($svgBytes)
}

function Get-AltFromSource([string]$source, [string]$type, [int]$index) {
    if ($type -eq "plantuml") {
        # ' alt: My description
        $m = [regex]::Match($source, "(?m)^'\s*alt:\s*(.+)$")
        if ($m.Success) { return $m.Groups[1].Value.Trim() }
        # title My description
        $m = [regex]::Match($source, "(?m)^title\s+(.+)$")
        if ($m.Success) { return $m.Groups[1].Value.Trim() }
    }
    return "Diagram $index"
}

# Strip lines used only for metadata so they don't affect rendering or the hash
function Get-CleanSource([string]$source, [string]$type) {
    if ($type -eq "plantuml") {
        return ($source -split "`n" | Where-Object { $_ -notmatch "^'\s*alt:\s*" }) -join "`n"
    }
    return $source
}

# ── per-file processing ─────────────────────────────────────────────────────────

function Invoke-ProcessFile([string]$filePath) {
    $raw = [System.IO.File]::ReadAllText($filePath, [System.Text.Encoding]::UTF8)
    if ($raw -notmatch '```kroki-') { return }

    $fileName = Split-Path $filePath -Leaf
    Write-Host "`nFile: $fileName"

    # Matches a complete kroki fenced block.
    # Group 1 = diagram type, Group 2 = raw source (including alt comment lines)
    $blockRx = [regex]'(?m)^```kroki-([a-z]+)\r?\n([\s\S]*?)^```[ \t]*(\r?\n|$)'

    # Matches the rendered marker that may follow a block.
    # Group 1 = rel path, Group 2 = hash, Group 3 = alt text
    $markerRx = [regex]'^<!-- kroki:rendered ([^\s]+) sha256:([a-f0-9]{8}) alt="([^"]*)" -->\r?\n!\[[^\]]*\]\([^\)]*\)\r?\n?'

    $allBlocks = $blockRx.Matches($raw)
    if ($allBlocks.Count -eq 0) { return }

    $newRaw  = $raw
    $changed = $false

    # Process in reverse so earlier block positions stay valid after insertions
    for ($i = $allBlocks.Count - 1; $i -ge 0; $i--) {
        $m        = $allBlocks[$i]
        $diagIdx  = $i + 1
        $type     = $m.Groups[1].Value
        $rawSrc   = $m.Groups[2].Value.Trim() -replace "`r`n", "`n" -replace "`r", "`n"
        $cleanSrc = Get-CleanSource $rawSrc $type   # source sent to Kroki (no alt comment)
        $hash     = Get-HashShort $cleanSrc

        # insertAt: position in $newRaw immediately after the closing fence + newline
        $insertAt = $m.Index + $m.Length

        # Check for an existing marker at this position
        $afterBlock  = $newRaw.Substring($insertAt)
        $existMarker = $markerRx.Match($afterBlock)

        if ($existMarker.Success -and $existMarker.Index -eq 0) {
            if ($existMarker.Groups[2].Value -eq $hash) {
                Write-Host ("  [{0}] {1} — up-to-date ({2})" -f $diagIdx, $type, $hash)
                continue
            }
            # Source changed — preserve the stored alt text
            $altText = $existMarker.Groups[3].Value
            Write-Host ("  [{0}] {1} — source changed, re-rendering" -f $diagIdx, $type) -ForegroundColor Yellow
        } else {
            $altText = Get-AltFromSource $rawSrc $type $diagIdx
            Write-Host ("  [{0}] {1} — new block, rendering" -f $diagIdx, $type)
        }

        # Render via Kroki API
        try {
            $svg = Invoke-Kroki $cleanSrc $type
        } catch {
            Write-Host ("    ERROR rendering block {0}: {1}" -f $diagIdx, $_.Exception.Message) -ForegroundColor Red
            continue
        }

        # Save SVG next to the notes file
        $topicDir   = Split-Path $filePath -Parent
        $imgDir     = Join-Path $topicDir "images\rendered"
        $svgAbsPath = Join-Path $imgDir "$hash.svg"
        $svgRelPath = "images/rendered/$hash.svg"

        if (-not (Test-Path $imgDir)) {
            New-Item -ItemType Directory -Force -Path $imgDir | Out-Null
        }
        [System.IO.File]::WriteAllText($svgAbsPath, $svg, [System.Text.Encoding]::UTF8)
        Write-Host ("    Saved {0}" -f $svgRelPath) -ForegroundColor Green

        # Build new marker and image link
        $newMarker = "<!-- kroki:rendered $svgRelPath sha256:$hash alt=`"$altText`" -->`n![$altText]($svgRelPath)`n"

        # Insert or replace in $newRaw
        if ($existMarker.Success -and $existMarker.Index -eq 0) {
            $newRaw = $newRaw.Remove($insertAt, $existMarker.Length).Insert($insertAt, $newMarker)
        } else {
            $newRaw = $newRaw.Insert($insertAt, $newMarker)
        }
        $changed = $true
    }

    if ($changed) {
        [System.IO.File]::WriteAllText($filePath, $newRaw, [System.Text.Encoding]::UTF8)
        Write-Host ("  Written: {0}" -f $fileName) -ForegroundColor Cyan
    }
}

# ── entry point ────────────────────────────────────────────────────────────────

if ($All) {
    $files = Get-ChildItem $topicsDir -Recurse -Filter "*_notes.md"
    Write-Host "Scanning $($files.Count) notes files under notes/topics/"
    foreach ($f in $files) {
        Invoke-ProcessFile $f.FullName
    }
} elseif ($File) {
    $absPath = if ([System.IO.Path]::IsPathRooted($File)) {
        $File
    } else {
        Join-Path $repoRoot $File
    }
    Invoke-ProcessFile $absPath
} else {
    Write-Host "Usage:"
    Write-Host "  .\scripts\render-kroki.ps1 -All"
    Write-Host "  .\scripts\render-kroki.ps1 -File <path>"
    exit 1
}

Write-Host "`nDone."
