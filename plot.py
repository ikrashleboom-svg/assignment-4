import csv
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

rows = list(csv.DictReader(open("results/tables/results.csv")))
data = {}
for r in rows:
    data.setdefault(r["algorithm"], []).append((int(r["n"]), float(r["avg_time_us"]), int(r["operations"])))


def line(ax, name, label, marker):
    pts = sorted(data[name])
    ax.plot([p[0] for p in pts], [p[1] for p in pts], marker=marker, label=label)


fig, ax = plt.subplots(figsize=(7, 5))
line(ax, "LCS", "LCS", "o")
line(ax, "EditDistance", "Edit Distance", "s")
ax.set_xlabel("string length n (both strings)")
ax.set_ylabel("average time (microseconds)")
ax.set_title("LCS and Edit Distance: time vs input size")
ax.grid(True, alpha=0.3)
ax.legend()
fig.tight_layout()
fig.savefig("results/plots/dp_time.png", dpi=120)
plt.close(fig)

fig, axes = plt.subplots(1, 2, figsize=(12, 5), sharey=True)
for ax, kind in zip(axes, ["present", "absent"]):
    line(ax, "Naive-" + kind, "Naive", "o")
    line(ax, "KMP-" + kind, "KMP", "s")
    line(ax, "RabinKarp-" + kind, "Rabin-Karp", "^")
    ax.set_xlabel("text length n")
    ax.set_title("pattern " + kind + " (m = 12)")
    ax.grid(True, alpha=0.3)
    ax.legend()
axes[0].set_ylabel("average time (microseconds)")
fig.suptitle("String matching: time vs text length (random text)")
fig.tight_layout()
fig.savefig("results/plots/matching_time.png", dpi=120)
plt.close(fig)

fig, ax = plt.subplots(figsize=(7, 5))
line(ax, "Naive-worst", "Naive", "o")
line(ax, "KMP-worst", "KMP", "s")
line(ax, "RabinKarp-worst", "Rabin-Karp", "^")
ax.set_yscale("log")
ax.set_xlabel("text length n")
ax.set_ylabel("average time (microseconds, log)")
ax.set_title("Worst case: text = aaa...a, pattern = a...ab (m = 100)")
ax.grid(True, which="both", alpha=0.3)
ax.legend()
fig.tight_layout()
fig.savefig("results/plots/matching_worst_case.png", dpi=120)
plt.close(fig)

fig, ax = plt.subplots(figsize=(7, 5))
line(ax, "Kasai", "Kasai", "o")
ax.set_xlabel("string length n")
ax.set_ylabel("average time (microseconds)")
ax.set_title("Kasai LCP: time vs string length")
ax.grid(True, alpha=0.3)
fig.tight_layout()
fig.savefig("results/plots/kasai_time.png", dpi=120)
plt.close(fig)

print("plots saved to results/plots")
