package suffix;

public class Kasai {
    public int[] lcp;
    public long charComparisons;

    public static Kasai run(String s, int[] sa) {
        Kasai r = new Kasai();
        int n = s.length();
        if (sa.length != n) {
            throw new IllegalArgumentException("suffix array size does not match string length");
        }
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }
        int[] lcp = new int[n];
        int k = 0;
        for (int i = 0; i < n; i++) {
            if (rank[i] == 0) {
                k = 0;
                continue;
            }
            int j = sa[rank[i] - 1];
            while (i + k < n && j + k < n) {
                r.charComparisons++;
                if (s.charAt(i + k) != s.charAt(j + k)) {
                    break;
                }
                k++;
            }
            lcp[rank[i]] = k;
            if (k > 0) {
                k--;
            }
        }
        r.lcp = lcp;
        return r;
    }

    public static int[] buildLCP(String s, int[] sa) {
        return run(s, sa).lcp;
    }
}
