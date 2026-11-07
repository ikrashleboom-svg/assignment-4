package util;

import java.util.Random;

public class StringGenerator {
    public static final String ALPHABET = "abcd";

    private final Random rnd;

    public StringGenerator(long seed) {
        rnd = new Random(seed);
    }

    public String random(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(rnd.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public String absentPattern(String text, int length) {
        while (true) {
            String p = random(length);
            if (!contains(text, p)) {
                return p;
            }
        }
    }

    private static boolean contains(String text, String p) {
        int n = text.length();
        int m = p.length();
        for (int i = 0; i + m <= n; i++) {
            int j = 0;
            while (j < m && text.charAt(i + j) == p.charAt(j)) {
                j++;
            }
            if (j == m) {
                return true;
            }
        }
        return false;
    }
}
