package matching;

public class RabinKarp {
    public static final long P = 31, MOD = 1_000_000_009L;

    public static MatchResult search(String text, String pattern) {
        return search(text, pattern, MOD);
    }

    public static MatchResult search(String text, String pattern, long mod) {
        MatchResult r = new MatchResult("Rabin-Karp");
        int n = text.length(), m = pattern.length();
        if (m == 0 || m > n) return r;
        checkLetters(text);
        checkLetters(pattern);

        long[] pPow = new long[Math.max(n, m) + 1];
        pPow[0] = 1;
        for (int i = 1; i < pPow.length; i++) pPow[i] = pPow[i - 1] * P % mod;

        long[] h = new long[n + 1];
        for (int i = 0; i < n; i++)
            h[i + 1] = (h[i] + (text.charAt(i) - 'a' + 1) * pPow[i]) % mod;

        long patternHash = 0;
        for (int i = 0; i < m; i++)
            patternHash = (patternHash + (pattern.charAt(i) - 'a' + 1) * pPow[i]) % mod;
        r.preprocessingOps = n + m;

        for (int i = 0; i + m <= n; i++) {
            long cur = (h[i + m] - h[i] + mod) % mod;
            r.hashComparisons++;
            if (cur == patternHash * pPow[i] % mod) {
                if (sameLetters(text, pattern, i, r)) r.positions.add(i);
                else r.spuriousHits++;
            }
        }
        return r;
    }

    private static boolean sameLetters(String text, String pattern, int start, MatchResult r) {
        for (int j = 0; j < pattern.length(); j++) {
            r.charComparisons++;
            if (text.charAt(start + j) != pattern.charAt(j)) return false;
        }
        return true;
    }

    private static void checkLetters(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < 'a' || c > 'z') {
                throw new IllegalArgumentException("only lowercase a-z is supported, got '" + c + "'");
            }
        }
    }
}
