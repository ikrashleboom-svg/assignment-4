package suffix;

import java.util.Arrays;

public class SuffixArray {
    public static int[] build(String s) {
        int n = s.length();
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) {
            idx[i] = i;
        }
        Arrays.sort(idx, (a, b) -> compare(s, a, b));
        int[] sa = new int[n];
        for (int i = 0; i < n; i++) {
            sa[i] = idx[i];
        }
        return sa;
    }

    private static int compare(String s, int a, int b) {
        int n = s.length();
        while (a < n && b < n) {
            char x = s.charAt(a);
            char y = s.charAt(b);
            if (x != y) {
                return x - y;
            }
            a++;
            b++;
        }
        return (n - a) - (n - b);
    }
}
