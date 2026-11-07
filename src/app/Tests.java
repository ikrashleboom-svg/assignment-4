package app;

import dp.EditDistance;
import dp.LCS;
import matching.KMP;
import matching.MatchResult;
import matching.NaiveMatcher;
import matching.RabinKarp;
import suffix.Kasai;
import suffix.SuffixArray;
import util.StringGenerator;

import java.util.Arrays;
import java.util.List;

public class Tests {
    static int passed = 0;
    static int failed = 0;

    static void check(boolean ok, String name) {
        if (ok) {
            passed++;
            System.out.println("[OK]   " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }

    public static void main(String[] args) {
        testLcs();
        testEditDistance();
        testMatching();
        testRabinKarpCollisions();
        testRandomMatching();
        testKasai();
        testRandomKasai();

        System.out.println();
        System.out.println("passed: " + passed + ", failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    static void testLcs() {
        check(LCS.length("abcdef", "abcdef") == 6, "LCS identical strings = length of string");
        check(LCS.length("abc", "xyz") == 0, "LCS completely different strings = 0");
        check(LCS.length("", "abc") == 0, "LCS with empty first string = 0");
        check(LCS.length("abc", "") == 0, "LCS with empty second string = 0");
        check(LCS.length("", "") == 0, "LCS of two empty strings = 0");
        check(LCS.length("ABCBDAB", "BDCABA") == 4, "LCS(ABCBDAB, BDCABA) = 4");
        check(LCS.length("AGGTAB", "GXTXAYB") == 4, "LCS(AGGTAB, GXTXAYB) = 4");
        LCS r = LCS.run("AGGTAB", "GXTXAYB");
        check(r.sequence("AGGTAB", "GXTXAYB").equals("GTAB"), "LCS reconstruction gives GTAB");
    }

    static void testEditDistance() {
        check(EditDistance.distance("hello", "hello") == 0, "Edit distance identical strings = 0");
        check(EditDistance.distance("", "abc") == 3, "Edit distance from empty string = 3 insertions");
        check(EditDistance.distance("abc", "") == 3, "Edit distance to empty string = 3 deletions");
        check(EditDistance.distance("cat", "cut") == 1, "substitution only: cat -> cut = 1");
        check(EditDistance.distance("cat", "cart") == 1, "insertion only: cat -> cart = 1");
        check(EditDistance.distance("cart", "cat") == 1, "deletion only: cart -> cat = 1");
        check(EditDistance.distance("kitten", "sitting") == 3, "kitten -> sitting = 3 (2 subs + 1 insert)");
        check(EditDistance.distance("sunday", "saturday") == 3, "sunday -> saturday = 3");
        check(EditDistance.distance("intention", "execution") == 5, "intention -> execution = 5");
    }

    static boolean sameAll(String text, String pattern, List<Integer> expected) {
        List<Integer> a = NaiveMatcher.search(text, pattern).positions;
        List<Integer> b = KMP.kmpSearch(text, pattern).positions;
        List<Integer> c = RabinKarp.search(text, pattern).positions;
        return a.equals(expected) && b.equals(expected) && c.equals(expected);
    }

    static void testMatching() {
        check(sameAll("abcdefgh", "abc", List.of(0)), "pattern at the beginning");
        check(sameAll("abcdefgh", "def", List.of(3)), "pattern in the middle");
        check(sameAll("abcdefgh", "fgh", List.of(5)), "pattern at the end");
        check(sameAll("abcabcabc", "abc", List.of(0, 3, 6)), "multiple occurrences");
        check(sameAll("aaaaa", "aa", List.of(0, 1, 2, 3)), "overlapping occurrences");
        check(sameAll("abcdefgh", "xyz", List.of()), "pattern absent");
        check(sameAll("abc", "abcdef", List.of()), "pattern longer than text");
        check(sameAll("abcdef", "", List.of()), "empty pattern returns empty list");
        check(sameAll("", "a", List.of()), "empty text");
        check(sameAll("abcdef", "abcdef", List.of(0)), "pattern equal to text");
        check(sameAll("ababdabacdababcabab", "ababcabab", List.of(10)), "lecture example: match at 10");
        check(Arrays.equals(KMP.computeLPS("AAACAAAA"), new int[]{0, 1, 2, 0, 1, 2, 3, 3}), "LPS of AAACAAAA from lecture");
        check(Arrays.equals(KMP.computeLPS("ABABCABAB"), new int[]{0, 0, 1, 2, 0, 1, 2, 3, 4}), "LPS of ABABCABAB");
    }

    static void testRabinKarpCollisions() {
        StringGenerator gen = new StringGenerator(5);
        String text = gen.random(5000);
        String pattern = text.substring(1000, 1006);
        MatchResult small = RabinKarp.search(text, pattern, 7);
        MatchResult naive = NaiveMatcher.search(text, pattern);
        check(small.spuriousHits > 0, "tiny modulus 7 creates hash collisions (" + small.spuriousHits + " spurious hits)");
        check(small.positions.equals(naive.positions), "with collisions Rabin-Karp still returns correct positions");
        boolean thrown = false;
        try {
            RabinKarp.search("ABC", "B");
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        check(thrown, "Rabin-Karp rejects letters outside a-z");
    }

    static void testRandomMatching() {
        boolean ok = true;
        for (int seed = 1; seed <= 50; seed++) {
            StringGenerator gen = new StringGenerator(seed);
            String text = gen.random(2000);
            String pattern = gen.random(1 + seed % 5);
            List<Integer> a = NaiveMatcher.search(text, pattern).positions;
            if (!a.equals(KMP.kmpSearch(text, pattern).positions) || !a.equals(RabinKarp.search(text, pattern).positions)) {
                ok = false;
            }
        }
        check(ok, "50 random texts: Naive, KMP and Rabin-Karp return the same lists");
    }

    static void testKasai() {
        String s = "banana";
        int[] sa = SuffixArray.build(s);
        check(Arrays.equals(sa, new int[]{5, 3, 1, 0, 4, 2}), "suffix array of banana = [5,3,1,0,4,2]");
        check(Arrays.equals(Kasai.buildLCP(s, new int[]{5, 3, 1, 0, 4, 2}), new int[]{0, 1, 3, 0, 0, 2}), "LCP of banana = [0,1,3,0,0,2]");

        String s2 = "banana$";
        int[] sa2 = {6, 5, 3, 1, 0, 4, 2};
        check(Arrays.equals(SuffixArray.build(s2), sa2), "suffix array of banana$ matches lecture");
        check(Arrays.equals(Kasai.buildLCP(s2, sa2), new int[]{0, 0, 1, 3, 0, 0, 2}), "LCP of banana$ matches lecture [0,0,1,3,0,0,2]");

        String s3 = "aaaa";
        check(Arrays.equals(Kasai.buildLCP(s3, SuffixArray.build(s3)), new int[]{0, 1, 2, 3}), "LCP of aaaa = [0,1,2,3]");

        check(Kasai.buildLCP("a", new int[]{0}).length == 1, "single character string");
        check(Kasai.buildLCP("", new int[0]).length == 0, "empty string gives empty LCP");
    }

    static int bruteLcp(String s, int a, int b) {
        int k = 0;
        while (a + k < s.length() && b + k < s.length() && s.charAt(a + k) == s.charAt(b + k)) {
            k++;
        }
        return k;
    }

    static void testRandomKasai() {
        boolean ok = true;
        boolean linear = true;
        for (int seed = 1; seed <= 20; seed++) {
            String s = new StringGenerator(seed).random(500);
            int[] sa = SuffixArray.build(s);
            Kasai k = Kasai.run(s, sa);
            for (int i = 1; i < s.length(); i++) {
                if (k.lcp[i] != bruteLcp(s, sa[i], sa[i - 1])) {
                    ok = false;
                }
            }
            if (k.charComparisons > 3L * s.length()) {
                linear = false;
            }
        }
        check(ok, "20 random strings: Kasai LCP equals brute force LCP");
        check(linear, "Kasai makes at most 3n character comparisons (2n matches + n mismatches)");
    }
}
