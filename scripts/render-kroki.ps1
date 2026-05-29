#Requires -Version 5.1
<#
.SYNOPSIS
    Renders kroki-* fenced code blocks in notes files to SVG images.

.DESCRIPTION
    Scans *_notes.md files under notes/topics/ for fenced blocks tagged
    kroki-<type> (e.g. kroki-plantuml), renders each via the Kroki REST API,
    and writes a canonical unit into the file in this form:

        <details>
        <summary>Diagram markdown</summary>

        ```kroki-plantuml
        ...source...
        ```

        </details>

        <!-- kroki:rendered images/rendered/<hash>.svg sha256:<hash> alt="..." -->
        ![...](images/rendered/<hash>.svg)

    The blank line after </details> is required so that GitHub's CommonMark
    renderer ends the HTML block before the image link, allowing the image to
    be parsed as Markdown rather than as raw HTML text.

    Idempotent: a block whose source hash matches the stored marker and whose
    format is already canonical is skipped. When source changes the SVG is
    re-rendered; when only the format is stale the SVG is reused without a
    second API call.

    Alt text is resolved in order:
      1. PlantUML comment:    ' alt: My description
      2. PlantUML title line: title My description
      3. Fallback:            "Diagram N" (1-based within the file)
    Stored alt text is preserved across re-renders.

.PARAMETER All
    Process every *_notes.md file found under notes/topics/.

.PARAMETER File
    Process a single notes file (absolute or repo-relative path).

.EXAMPLE
    .\scripts\render-kroki.ps1 -All
    .\scripts\render-kroki.ps1 -File notes\topics\t02_recursion\t02_recursion_notes.md

.NOTES
    Requires internet access to reach https://kroki.io.
    Commit the generated SVG files alongside the notes.
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

# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

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
        $m = [regex]::Match($source, "(?m)^'\s*alt:\s*(.+)$")
        if ($m.Success) { return $m.Groups[1].Value.Trim() }
        $m = [regex]::Match($source, "(?m)^title\s+(.+)$")
        if ($m.Success) { return $m.Groups[1].Value.Trim() }
    }
    return "Diagram $index"
}

# Strip ' alt: lines before hashing/rendering (metadata only)
function Get-CleanSource([string]$source, [string]$type) {
    if ($type -eq "plantuml") {
        return ($source -split "`n" | Where-Object { $_ -notmatch "^'\s*alt:\s*" }) -join "`n"
    }
    return $source
}

# ---------------------------------------------------------------------------
# Per-file processing
# ---------------------------------------------------------------------------

function Invoke-ProcessFile([string]$filePath) {
    $raw = [System.IO.File]::ReadAllText($filePath, [System.Text.Encoding]::UTF8)
    if ($raw -notmatch '```kroki-') { return }

    $fileName = Split-Path $filePath -Leaf
    Write-Host "`nFile: $fileName"

    # Matches a complete kroki fenced block.
    # Group 1 = type, Group 2 = raw source (including any ' alt: line)
    $blockRx = [regex]'(?m)^```kroki-([a-z]+)\r?\n([\s\S]*?)^```[ \t]*(\r?\n|$)'

    # Marker written after the blank line that follows </details>.
    # Group 1 = rel SVG path, Group 2 = hash, Group 3 = alt text
    $markerRx = [regex]'^<!-- kroki:rendered ([^\s]+) sha256:([a-f0-9]{8}) alt="([^"]*)" -->\r?\n!\[[^\]]*\]\([^\)]*\)\r?\n?'

    # Stale duplicate: old-format footer+marker (consumed silently for cleanup)
    $dupeRx = [regex]'^(\r?\n)?</details>\r?\n<!-- kroki:rendered [^\s]+ sha256:[a-f0-9]{8} alt="[^"]*" -->\r?\n!\[[^\]]*\]\([^\)]*\)\r?\n?'

    # <details> header (what must immediately precede the opening fence)
    $hdrLF   = "<details>`n<summary>Diagram markdown</summary>`n`n"
    $hdrCRLF = "<details>`r`n<summary>Diagram markdown</summary>`r`n`r`n"

    # Canonical footer: \n</details>\n\n  (blank line AFTER </details>)
    # The block regex consumes the first \n after the closing fence, so
    # $afterFence starts with \n</details>\n\n in the canonical file.
    $canonicalFtr = "`n</details>`n`n"

    # Footer detection regex: backward-compatible, handles any number of trailing \n
    # Matches: (optional \n) </details> (one or more \n)
    $footerRx = [regex]'^(\r?\n)?</details>(\r?\n)+'

    $fence = '```'

    $allBlocks = $blockRx.Matches($raw)
    if ($allBlocks.Count -eq 0) { return }

    $newRaw  = $raw
    $changed = $false

    # Process in reverse so earlier block positions remain valid after replacements
    for ($i = $allBlocks.Count - 1; $i -ge 0; $i--) {
        $m        = $allBlocks[$i]
        $diagIdx  = $i + 1
        $type     = $m.Groups[1].Value
        $rawSrc   = $m.Groups[2].Value.Trim() -replace "`r`n", "`n" -replace "`r", "`n"
        $cleanSrc = Get-CleanSource $rawSrc $type
        $hash     = Get-HashShort $cleanSrc

        # Positions 0..$m.Index unchanged (reverse-processing guarantee)
        $beforeFence = $newRaw.Substring(0, $m.Index)
        $afterFence  = $newRaw.Substring($m.Index + $m.Length)

        # Is the fence preceded by a <details> header?
        $isWrapped = $false
        $hdrLen    = 0
        if ($beforeFence.EndsWith($hdrLF))       { $isWrapped = $true; $hdrLen = $hdrLF.Length }
        elseif ($beforeFence.EndsWith($hdrCRLF)) { $isWrapped = $true; $hdrLen = $hdrCRLF.Length }
        $unitStart = $m.Index - $hdrLen

        # Is the fence followed by a </details> footer? (any variant)
        $footerMatch = $footerRx.Match($afterFence)
        $ftrLen      = if ($footerMatch.Success) { $footerMatch.Length } else { 0 }
        $unitEnd     = $m.Index + $m.Length + $ftrLen

        # Is the canonical footer format present? (determines whether a reformat is needed)
        $isCanonicalFmt = $afterFence.StartsWith($canonicalFtr)

        # Is there a rendered marker immediately after the unit?
        $afterUnit   = $newRaw.Substring($unitEnd)
        $existMarker = $markerRx.Match($afterUnit)
        $markerEnd   = if ($existMarker.Success -and $existMarker.Index -eq 0) {
                           $unitEnd + $existMarker.Length
                       } else { $unitEnd }

        # Consume stale duplicate footer+marker (cleanup for old buggy canonical)
        while ($true) {
            $dupeMatch = $dupeRx.Match($newRaw.Substring($markerEnd))
            if ($dupeMatch.Success -and $dupeMatch.Index -eq 0) {
                $markerEnd += $dupeMatch.Length
                Write-Host ("    [cleanup] stale duplicate removed for block {0}" -f $diagIdx) -ForegroundColor DarkYellow
            } else { break }
        }

        $hasDupe = ($markerEnd -gt ($unitEnd + $(if ($existMarker.Success -and $existMarker.Index -eq 0) { $existMarker.Length } else { 0 })))

        # --------------- decide action ---------------
        if ($isWrapped -and $existMarker.Success -and $existMarker.Index -eq 0) {
            $storedHash = $existMarker.Groups[2].Value
            $altText    = $existMarker.Groups[3].Value   # preserve stored alt text

            if ($storedHash -eq $hash -and -not $hasDupe -and $isCanonicalFmt) {
                Write-Host ("  [{0}] {1} up-to-date ({2})" -f $diagIdx, $type, $hash)
                continue
            }

            if ($storedHash -ne $hash) {
                Write-Host ("  [{0}] {1} source changed, re-rendering" -f $diagIdx, $type) -ForegroundColor Yellow
            } else {
                Write-Host ("  [{0}] {1} reformatting (blank line fix)" -f $diagIdx, $type) -ForegroundColor Cyan
            }
        } else {
            $altText = Get-AltFromSource $rawSrc $type $diagIdx
            Write-Host ("  [{0}] {1} rendering" -f $diagIdx, $type)
        }

        # --------------- render / cache SVG ---------------
        $topicDir   = Split-Path $filePath -Parent
        $imgDir     = Join-Path $topicDir "images\rendered"
        $svgAbsPath = Join-Path $imgDir "$hash.svg"
        $svgRelPath = "images/rendered/$hash.svg"

        if (Test-Path $svgAbsPath) {
            Write-Host ("    SVG cached ({0})" -f $hash) -ForegroundColor DarkGray
        } else {
            try { $svg = Invoke-Kroki $cleanSrc $type }
            catch {
                Write-Host ("    ERROR: {0}" -f $_.Exception.Message) -ForegroundColor Red
                continue
            }
            if (-not (Test-Path $imgDir)) { New-Item -ItemType Directory -Force -Path $imgDir | Out-Null }
            [System.IO.File]::WriteAllText($svgAbsPath, $svg, [System.Text.Encoding]::UTF8)
            Write-Host ("    Saved {0}" -f $svgRelPath) -ForegroundColor Green
        }

        # --------------- build canonical unit ---------------
        # $fence + "\n" + $canonicalFtr = ```\n\n</details>\n\n
        # Block regex consumes first \n; $afterFence then starts with \n</details>\n\n
        # which matches $canonicalFtr on the next run -> idempotent.
        # The blank line after </details> ends the HTML block so GitHub parses
        # the following image link as Markdown rather than raw HTML.
        $canonical = $hdrLF +
                     ($fence + "kroki-$type`n") +
                     ($rawSrc + "`n") +
                     ($fence + "`n" + $canonicalFtr) +
                     "<!-- kroki:rendered $svgRelPath sha256:$hash alt=`"$altText`" -->`n" +
                     "![$altText]($svgRelPath)`n"

        # Replace full unit (including any stale duplicates) in $newRaw
        $fullLen = $markerEnd - $unitStart
        $newRaw  = $newRaw.Remove($unitStart, $fullLen).Insert($unitStart, $canonical)
        $changed = $true
    }

    if ($changed) {
        [System.IO.File]::WriteAllText($filePath, $newRaw, [System.Text.Encoding]::UTF8)
        Write-Host ("  Written: {0}" -f $fileName) -ForegroundColor Cyan
    }
}

# ---------------------------------------------------------------------------
# Entry point
# ---------------------------------------------------------------------------

if ($All) {
    $files = Get-ChildItem $topicsDir -Recurse -Filter "*_notes.md"
    Write-Host "Scanning $($files.Count) notes files under notes/topics/"
    foreach ($f in $files) { Invoke-ProcessFile $f.FullName }
} elseif ($File) {
    $absPath = if ([System.IO.Path]::IsPathRooted($File)) { $File } else { Join-Path $repoRoot $File }
    Invoke-ProcessFile $absPath
} else {
    Write-Host "Usage:"
    Write-Host "  .\scripts\render-kroki.ps1 -All"
    Write-Host "  .\scripts\render-kroki.ps1 -File <path>"
    exit 1
}

Write-Host "`nDone."
