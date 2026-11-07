package app;

import dp.EditDistance;
import dp.LCS;
import matching.KMP;
import matching.NaiveMatcher;
import matching.RabinKarp;
import suffix.Kasai;
import suffix.SuffixArray;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("bench")) {
            Benchmark.main(new String[0]);
            return;
        }
        if (args.length > 0 && args[0].equals("test")) {
            Tests.main(new String[0]);
            return;
        }
        demoDp();
        demoMatching();
        demoKasai();
    }

    static void demoDp() {
        System.out.println("=== Part A: dynamic programming ===");
        String a = "ABCBDAB";
        String b = "BDCABA";
        LCS l = LCS.run(a, b);
        System.out.println("LCS(" + a + ", " + b + ") = " + l.length() + "  one subsequence: " + l.sequence(a, b));

        String x = "kitten";
        String y = "sitting";
        System.out.println("EditDistance(" + x + ", " + y + ") = " + EditDistance.distance(x, y));
        System.out.println("EditDistance(sunday, saturday) = " + EditDistance.distance("sunday", "saturday"));
        System.out.println();
    }

    static void demoMatching() {
        System.out.println("=== Part B: string matching ===");
        String text = "ababdabacdababcabab";
        String pattern = "ababcabab";
        System.out.println("text = " + text + ", pattern = " + pattern);
        System.out.println(NaiveMatcher.search(text, pattern));
        System.out.println(KMP.kmpSearch(text, pattern));
        System.out.println(RabinKarp.search(text, pattern));
        System.out.println("LPS of AAACAAAA = " + Arrays.toString(KMP.computeLPS("AAACAAAA")));

        String t2 = "abababab";
        String p2 = "abab";
        System.out.println("text = " + t2 + ", pattern = " + p2 + " -> " + KMP.kmpSearch(t2, p2).positions);
        System.out.println();
    }

    static void demoKasai() {
        System.out.println("=== Part C: Kasai LCP ===");
        String s = "banana";
        int[] sa = SuffixArray.build(s);
        int[] lcp = Kasai.buildLCP(s, sa);
        System.out.println("string = " + s);
        System.out.println("SA  = " + Arrays.toString(sa));
        System.out.println("LCP = " + Arrays.toString(lcp));
        for (int i = 0; i < sa.length; i++) {
            System.out.println("   " + i + "  sa=" + sa[i] + "  lcp=" + lcp[i] + "  " + s.substring(sa[i]));
        }
    }
}
