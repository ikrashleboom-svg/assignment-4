package dp;

public class LCS {
    public int[][] table;
    public long cellsFilled;
    public long comparisons;

    public static LCS run(String a, String b) {
        LCS r = new LCS();
        int n = a.length();
        int m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                r.comparisons++;
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
                r.cellsFilled++;
            }
        }
        r.table = dp;
        return r;
    }

    public int length() {
        return table[table.length - 1][table[0].length - 1];
    }

    public static int length(String a, String b) {
        return run(a, b).length();
    }

    public String sequence(String a, String b) {
        StringBuilder sb = new StringBuilder();
        int i = a.length();
        int j = b.length();
        while (i > 0 && j > 0) {
            if (a.charAt(i - 1) == b.charAt(j - 1)) {
                sb.append(a.charAt(i - 1));
                i--;
                j--;
            } else if (table[i - 1][j] >= table[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        return sb.reverse().toString();
    }
}
