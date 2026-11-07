
### LCS and Edit Distance

| n = m | LCS avg time (µs) | LCS length | Edit avg time (µs) | Edit distance | cells filled | char comparisons |
|---|---|---|---|---|---|---|
| 100 | 95.46 | 61 | 149.70 | 56 | 10000 | 10000 |
| 500 | 2740.78 | 320 | 2610.32 | 262 | 250000 | 250000 |
| 1000 | 11834.98 | 647 | 11292.50 | 524 | 1000000 | 1000000 |

### String matching, pattern present (m = 12)

| n | Naive (µs) | KMP (µs) | Rabin-Karp (µs) | Naive char cmp | KMP char cmp | RK hash cmp | RK char cmp | RK spurious | matches |
|---|---|---|---|---|---|---|---|---|---|
| 10000 | 87.78 | 118.50 | 364.28 | 13437 | 20012 | 9989 | 12 | 0 | 1 |
| 50000 | 561.12 | 1321.60 | 1810.34 | 66621 | 99998 | 49989 | 12 | 0 | 1 |
| 100000 | 743.92 | 2637.30 | 4973.86 | 133409 | 210177 | 99989 | 12 | 0 | 1 |

### String matching, pattern absent (m = 12)

| n | Naive (µs) | KMP (µs) | Rabin-Karp (µs) | Naive char cmp | KMP char cmp | RK hash cmp | RK char cmp | RK spurious | matches |
|---|---|---|---|---|---|---|---|---|---|
| 10000 | 166.18 | 199.16 | 621.20 | 13387 | 19999 | 9989 | 0 | 0 | 0 |
| 50000 | 1695.52 | 1274.16 | 3575.94 | 66637 | 100038 | 49989 | 0 | 0 | 0 |
| 100000 | 1837.96 | 2224.42 | 6144.88 | 132845 | 200076 | 99989 | 0 | 0 | 0 |

### Worst case for Naive: text = "aaa...a", pattern = "aa...ab" (m = 100, absent)

| n | Naive (µs) | KMP (µs) | Rabin-Karp (µs) | Naive char cmp | KMP char cmp | RK char cmp |
|---|---|---|---|---|---|---|
| 10000 | 3113.00 | 95.84 | 715.82 | 990100 | 19999 | 0 |
| 50000 | 27195.24 | 427.98 | 3159.94 | 4990100 | 99999 | 0 |
| 100000 | 32113.08 | 1445.20 | 3648.98 | 9990100 | 199999 | 0 |

### Kasai LCP (suffix array built before timing)

| n | Kasai avg time (µs) | char comparisons | comparisons / n | max LCP |
|---|---|---|---|---|
| 1000 | 19.56 | 1990 | 1.99 | 9 |
| 5000 | 122.24 | 9989 | 2.00 | 11 |
| 10000 | 281.64 | 19987 | 2.00 | 14 |

