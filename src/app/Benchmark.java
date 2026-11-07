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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Benchmark {
    static final long SEED = 42;
    static final int RUNS = 5;
    static final int WARMUP = 30;
    static final long WARMUP_NANOS = 300_000_000L;
    static final int[] DP_SIZES = {100, 500, 1000};
    static final int[] TEXT_SIZES = {10_000, 50_000, 100_000};
    static final int[] KASAI_SIZES = {1000, 5000, 10_000};
    static final int PATTERN_LENGTH = 12;
    static final String OUT = "results/tables";

    static List<String> csv = new ArrayList<>();
    static StringBuilder md = new StringBuilder();
    static long sink;

    static double measure(Runnable r) {
        long start = System.nanoTime();
        int done = 0;
        while (done < WARMUP || System.nanoTime() - start < WARMUP_NANOS) {
            r.run();
            done++;
        }
        long total = 0;
        for (int i = 0; i < RUNS; i++) {
            long t1 = System.nanoTime();
            r.run();
            long t2 = System.nanoTime();
            total += t2 - t1;
        }
        return total / (double) RUNS / 1000.0;
    }

    static String f(double x) {
        return String.format(Locale.US, "%.2f", x);
    }

    public static void main(String[] args) {
        dpExperiment();
        matchingExperiment();
        worstCaseExperiment();
        kasaiExperiment();
        save();
        System.out.println(md);
    }

    static void dpExperiment() {
        md.append("\n### LCS and Edit Distance\n\n");
        md.append("| n = m | LCS avg time (µs) | LCS length | Edit avg time (µs) | Edit distance | cells filled | char comparisons |\n");
        md.append("|---|---|---|---|---|---|---|\n");
        for (int n : DP_SIZES) {
            StringGenerator gen = new StringGenerator(SEED + n);
            String a = gen.random(n);
            String b = gen.random(n);
            for (int i = 0; i < 20; i++) {
                LCS.run(a, b);
                EditDistance.run(a, b);
            }
            double tl = measure(() -> sink += LCS.length(a, b));
            double te = measure(() -> sink += EditDistance.distance(a, b));
            LCS l = LCS.run(a, b);
            EditDistance e = EditDistance.run(a, b);
            md.append("| ").append(n).append(" | ").append(f(tl)).append(" | ").append(l.length())
              .append(" | ").append(f(te)).append(" | ").append(e.distance())
              .append(" | ").append(l.cellsFilled).append(" | ").append(l.comparisons).append(" |\n");
            csv.add("LCS," + n + "," + f(tl) + "," + l.comparisons);
            csv.add("EditDistance," + n + "," + f(te) + "," + e.comparisons);
        }
    }

    static void matchingExperiment() {
        StringGenerator gen = new StringGenerator(SEED);
        String[] texts = new String[TEXT_SIZES.length];
        String[] present = new String[TEXT_SIZES.length];
        String[] absent = new String[TEXT_SIZES.length];
        for (int k = 0; k < TEXT_SIZES.length; k++) {
            int n = TEXT_SIZES[k];
            texts[k] = gen.random(n);
            present[k] = texts[k].substring(n / 2, n / 2 + PATTERN_LENGTH);
            absent[k] = gen.absentPattern(texts[k], PATTERN_LENGTH);
        }
        for (int i = 0; i < 50; i++) {
            for (int k = 0; k < TEXT_SIZES.length; k++) {
                NaiveMatcher.search(texts[k], present[k]);
                KMP.kmpSearch(texts[k], present[k]);
                RabinKarp.search(texts[k], present[k]);
            }
        }
        matchingTable("present", texts, present);
        matchingTable("absent", texts, absent);
    }

    static void matchingTable(String kind, String[] texts, String[] patterns) {
        md.append("\n### String matching, pattern ").append(kind).append(" (m = ").append(PATTERN_LENGTH).append(")\n\n");
        md.append("| n | Naive (µs) | KMP (µs) | Rabin-Karp (µs) | Naive char cmp | KMP char cmp | RK hash cmp | RK char cmp | RK spurious | matches |\n");
        md.append("|---|---|---|---|---|---|---|---|---|---|\n");
        for (int k = 0; k < texts.length; k++) {
            String t = texts[k];
            String p = patterns[k];
            double tn = measure(() -> sink += NaiveMatcher.search(t, p).positions.size());
            double tk = measure(() -> sink += KMP.kmpSearch(t, p).positions.size());
            double tr = measure(() -> sink += RabinKarp.search(t, p).positions.size());
            MatchResult a = NaiveMatcher.search(t, p);
            MatchResult b = KMP.kmpSearch(t, p);
            MatchResult c = RabinKarp.search(t, p);
            if (!a.positions.equals(b.positions) || !a.positions.equals(c.positions)) {
                throw new IllegalStateException("algorithms disagree");
            }
            int n = t.length();
            md.append("| ").append(n).append(" | ").append(f(tn)).append(" | ").append(f(tk)).append(" | ").append(f(tr))
              .append(" | ").append(a.charComparisons).append(" | ").append(b.charComparisons)
              .append(" | ").append(c.hashComparisons).append(" | ").append(c.charComparisons)
              .append(" | ").append(c.spuriousHits).append(" | ").append(a.positions.size()).append(" |\n");
            csv.add("Naive-" + kind + "," + n + "," + f(tn) + "," + a.charComparisons);
            csv.add("KMP-" + kind + "," + n + "," + f(tk) + "," + b.charComparisons);
            csv.add("RabinKarp-" + kind + "," + n + "," + f(tr) + "," + c.charComparisons);
        }
    }

    static void worstCaseExperiment() {
        md.append("\n### Worst case for Naive: text = \"aaa...a\", pattern = \"aa...ab\" (m = 100, absent)\n\n");
        md.append("| n | Naive (µs) | KMP (µs) | Rabin-Karp (µs) | Naive char cmp | KMP char cmp | RK char cmp |\n");
        md.append("|---|---|---|---|---|---|---|\n");
        String p = "a".repeat(99) + "b";
        for (int n : TEXT_SIZES) {
            String t = "a".repeat(n);
            double tn = measure(() -> sink += NaiveMatcher.search(t, p).positions.size());
            double tk = measure(() -> sink += KMP.kmpSearch(t, p).positions.size());
            double tr = measure(() -> sink += RabinKarp.search(t, p).positions.size());
            md.append("| ").append(n).append(" | ").append(f(tn)).append(" | ").append(f(tk)).append(" | ").append(f(tr))
              .append(" | ").append(NaiveMatcher.search(t, p).charComparisons)
              .append(" | ").append(KMP.kmpSearch(t, p).charComparisons)
              .append(" | ").append(RabinKarp.search(t, p).charComparisons).append(" |\n");
            csv.add("Naive-worst," + n + "," + f(tn) + "," + NaiveMatcher.search(t, p).charComparisons);
            csv.add("KMP-worst," + n + "," + f(tk) + "," + KMP.kmpSearch(t, p).charComparisons);
            csv.add("RabinKarp-worst," + n + "," + f(tr) + "," + RabinKarp.search(t, p).charComparisons);
        }
    }

    static void kasaiExperiment() {
        md.append("\n### Kasai LCP (suffix array built before timing)\n\n");
        md.append("| n | Kasai avg time (µs) | char comparisons | comparisons / n | max LCP |\n");
        md.append("|---|---|---|---|---|\n");
        for (int n : KASAI_SIZES) {
            String s = new StringGenerator(SEED + 100 + n).random(n);
            int[] sa = SuffixArray.build(s);
            for (int i = 0; i < 50; i++) {
                Kasai.run(s, sa);
            }
            double t = measure(() -> sink += Kasai.run(s, sa).lcp.length);
            Kasai k = Kasai.run(s, sa);
            int max = 0;
            for (int x : k.lcp) {
                max = Math.max(max, x);
            }
            md.append("| ").append(n).append(" | ").append(f(t)).append(" | ").append(k.charComparisons)
              .append(" | ").append(f(k.charComparisons / (double) n)).append(" | ").append(max).append(" |\n");
            csv.add("Kasai," + n + "," + f(t) + "," + k.charComparisons);
        }
    }

    static void save() {
        new File(OUT).mkdirs();
        try (PrintWriter w = new PrintWriter(new FileWriter(OUT + "/results.csv"))) {
            w.println("algorithm,n,avg_time_us,operations");
            for (String s : csv) {
                w.println(s);
            }
        } catch (IOException e) {
            System.out.println("could not write csv: " + e.getMessage());
        }
        try (PrintWriter w = new PrintWriter(new FileWriter(OUT + "/results.md"))) {
            w.print(md);
        } catch (IOException e) {
            System.out.println("could not write md: " + e.getMessage());
        }
    }
}
