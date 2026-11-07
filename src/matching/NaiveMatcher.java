package matching;

public class NaiveMatcher {
    public static MatchResult search(String text, String pattern) {
        MatchResult r = new MatchResult("Naive");
        int n = text.length();
        int m = pattern.length();
        if (m == 0 || m > n) {
            return r;
        }
        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m) {
                r.charComparisons++;
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    break;
                }
                j++;
            }
            if (j == m) {
                r.positions.add(i);
            }
        }
        return r;
    }
}
