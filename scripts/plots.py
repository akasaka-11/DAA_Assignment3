import csv
import math
import os
import sys

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt

path = sys.argv[1] if len(sys.argv) > 1 else "results/results.csv"
outdir = sys.argv[2] if len(sys.argv) > 2 else "results/plots"
os.makedirs(outdir, exist_ok=True)

rows = []
with open(path) as f:
    for r in csv.DictReader(f):
        for k in ("n", "m", "dsu_ops", "edge_scans", "comparisons", "max_depth", "result"):
            r[k] = int(r[k])
        r["density"] = r["graph"].split("_")[1]
        rows.append(r)

algos = ["Kruskal", "Prim", "Kosaraju", "Kahn", "DagShortestPath"]
colors = {"Kruskal": "tab:blue", "Prim": "tab:orange", "Kosaraju": "tab:green",
          "Kahn": "tab:red", "DagShortestPath": "tab:purple"}
styles = {"sparse": "-", "dense": "--"}


def series(algo, density):
    rs = sorted((r for r in rows if r["algorithm"] == algo and r["density"] == density), key=lambda r: r["n"])
    return rs


fig, ax = plt.subplots(figsize=(8, 5.5))
for algo in algos:
    for density in ("sparse", "dense"):
        rs = series(algo, density)
        hollow = algo == "Kosaraju"
        ax.plot([r["n"] for r in rs], [r["edge_scans"] + r["dsu_ops"] + r["comparisons"] for r in rs],
                styles[density], marker="s" if hollow else "o", markersize=11 if hollow else 6,
                markerfacecolor="none" if hollow else colors[algo], color=colors[algo],
                label=algo + " " + density)
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xlabel("n (vertices)")
ax.set_ylabel("counted operations (edge scans + DSU ops + comparisons)")
ax.set_title("Operations versus n")
ax.grid(True, which="both", alpha=0.3)
ax.legend(fontsize=8, ncol=2)
fig.tight_layout()
fig.savefig(os.path.join(outdir, "operations_vs_n.png"), dpi=150)
plt.close(fig)

fig, ax = plt.subplots(figsize=(8, 5.5))
for algo in algos:
    for density in ("sparse", "dense"):
        rs = series(algo, density)
        ax.plot([r["n"] for r in rs], [r["edge_scans"] / r["m"] for r in rs],
                styles[density], marker="o", color=colors[algo], label=algo + " " + density)
ax.set_xscale("log")
ax.set_xlabel("n (vertices)")
ax.set_ylabel("edge scans per edge (edge_scans / m)")
ax.set_title("Scans per edge versus n")
ax.set_ylim(0, 2.4)
ax.grid(True, which="both", alpha=0.3)
ax.legend(fontsize=8, ncol=2)
fig.tight_layout()
fig.savefig(os.path.join(outdir, "scans_per_edge.png"), dpi=150)
plt.close(fig)

fig, ax = plt.subplots(figsize=(8, 5.5))
for density, c in (("sparse", "tab:blue"), ("dense", "tab:orange")):
    rs = series("Kruskal", density)
    ax.plot([r["n"] for r in rs], [r["edge_scans"] / r["m"] for r in rs], marker="o", color=c,
            label="Kruskal " + density + " (m = %dn)" % (4 if density == "sparse" else 20))
ax.set_xscale("log")
ax.set_xlabel("n (vertices)")
ax.set_ylabel("share of edges examined (edge_scans / m)")
ax.set_title("Share of edges Kruskal examines before the tree is complete")
ax.set_ylim(0, 1.05)
ax.grid(True, which="both", alpha=0.3)
ax.legend()
fig.tight_layout()
fig.savefig(os.path.join(outdir, "kruskal_share.png"), dpi=150)
plt.close(fig)
print("plots written to", outdir)
