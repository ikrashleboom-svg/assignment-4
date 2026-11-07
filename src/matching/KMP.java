package matching;

public class KMP {
    public static int[] computeLPS(String p) {
        return computeLPS(p, null);
    }

    static int[] computeLPS(String p, MatchResult r) {
        int[] lps = new int[p.length()];
        int len = 0;
        for (int i = 1; i < p.length(); ) {
            if (r != null) r.preprocessingOps++;
            if (p.charAt(i) == p.charAt(len)) lps[i++] = ++len;
            else if (len > 0) len = lps[len - 1];
            else lps[i++] = 0;
        }
        return lps;
    }

    public static MatchResult kmpSearch(String t, String p) {
        MatchResult r = new MatchResult("KMP");
        int n = t.length(), m = p.length();
        if (m == 0 || m > n) return r;
        int[] lps = computeLPS(p, r);
        int j = 0;
        for (int i = 0; i < n; ) {
            r.charComparisons++;
            if (t.charAt(i) == p.charAt(j)) { i++; j++; }
            if (j == m) {
                r.positions.add(i - j);
                j = lps[j - 1];
            } else if (i < n) {
                r.charComparisons++;
                if (t.charAt(i) != p.charAt(j)) {
                    if (j > 0) j = lps[j - 1]; else i++;
                }
            }
        }
        return r;
    }
}
