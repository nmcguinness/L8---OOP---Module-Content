package t01_arrays.exercises.ex04;

public class Exercise {

    /**
     * Builds an 11-bin histogram for scores in [0,100].
     * Bins: 0–9, 10–19, ..., 90–99, 100.
     */
    static int[] histogram(int[] scores) {
        int[] bins = new int[11];
        if (scores == null) return bins;
        for (int s : scores) {
            if (s < 0 || s > 100) continue;  // ignore out-of-range safely
            int bin = (s == 100) ? 10 : (s / 10);
            bins[bin]++;
        }
        return bins;
    }

    static void printHistogram(int[] bins) {
        for (int i = 0; i < bins.length; i++) {
            String label = (i == 10) ? "100  " : String.format("%02d–%02d", i * 10, i * 10 + 9);
            System.out.print(label + ": ");
            for (int k = 0; k < bins[i]; k++) System.out.print("*");
            System.out.println();
        }
    }

    public static void run() {
        int[] scores = {0, 7, 12, 15, 19, 33, 55, 67, 88, 99, 100, 100, -5, 123};
        int[] bins = histogram(scores);
        printHistogram(bins);
    }
}
