package dp;

public class EditDistance {
    public int[][] table;
    public long cellsFilled;
    public long comparisons;

    public static EditDistance run(String a, String b) {
        EditDistance r = new EditDistance();
        int n = a.length();
        int m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                r.comparisons++;
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                int del = dp[i - 1][j] + 1;
                int ins = dp[i][j - 1] + 1;
                int sub = dp[i - 1][j - 1] + cost;
                dp[i][j] = Math.min(sub, Math.min(del, ins));
                r.cellsFilled++;
            }
        }
        r.table = dp;
        return r;
    }

    public int distance() {
        return table[table.length - 1][table[0].length - 1];
    }

    public static int distance(String a, String b) {
        return run(a, b).distance();
    }
}
