import csv
import math
import sys

path = sys.argv[1] if len(sys.argv) > 1 else "results/results.csv"
out = sys.argv[2] if len(sys.argv) > 2 else "results/analysis.md"

rows = []
with open(path) as f:
    for r in csv.DictReader(f):
        for k in ("n", "m", "dsu_ops", "edge_scans", "comparisons", "max_depth", "result"):
            r[k] = int(r[k])
        r["time_ms"] = float(r["time_ms"])
        r["density"] = r["graph"].split("_")[1]
        rows.append(r)


def pick(algo, density):
    return sorted((r for r in rows if r["algorithm"] == algo and r["density"] == density), key=lambda r: r["n"])


def slope(xs, ys):
    lx = [math.log(x) for x in xs]
    ly = [math.log(y) for y in ys]
    mx = sum(lx) / len(lx)
    my = sum(ly) / len(ly)
    num = sum((a - mx) * (b - my) for a, b in zip(lx, ly))
    den = sum((a - mx) ** 2 for a in lx)
    return num / den


lines = []


def table(title, header, body):
    lines.append("**" + title + "**")
    lines.append("")
    lines.append("| " + " | ".join(header) + " |")
    lines.append("|" + "|".join(["---"] * len(header)) + "|")
    for b in body:
        lines.append("| " + " | ".join(b) + " |")
    lines.append("")


formulas = [
    ("Kruskal", "dsu_ops / E", lambda r: r["dsu_ops"] / r["m"]),
    ("Kruskal", "comparisons / (E log2 E)", lambda r: r["comparisons"] / (r["m"] * math.log2(r["m"]))),
    ("Prim", "edge_scans / (2E)", lambda r: r["edge_scans"] / (2 * r["m"])),
    ("Prim", "comparisons / (E log2 V)", lambda r: r["comparisons"] / (r["m"] * math.log2(r["n"]))),
    ("Kosaraju", "edge_scans / (2E)", lambda r: r["edge_scans"] / (2 * r["m"])),
    ("Kosaraju", "edge_scans / (V + E)", lambda r: r["edge_scans"] / (r["n"] + r["m"])),
    ("Kahn", "edge_scans / E", lambda r: r["edge_scans"] / r["m"]),
    ("Kahn", "edge_scans / (V + E)", lambda r: r["edge_scans"] / (r["n"] + r["m"])),
    ("DagShortestPath", "edge_scans / E", lambda r: r["edge_scans"] / r["m"]),
    ("DagShortestPath", "edge_scans / (V + E)", lambda r: r["edge_scans"] / (r["n"] + r["m"])),
]

for density in ("sparse", "dense"):
    sizes = [r["n"] for r in pick("Kruskal", density)]
    body = []
    for algo, name, fn in formulas:
        vals = [fn(r) for r in pick(algo, density)]
        body.append([algo, name] + ["%.3f" % v for v in vals])
    table("Counter divided by formula, %s graphs (m = %dn)" % (density, 4 if density == "sparse" else 20),
          ["algorithm", "ratio"] + ["n=%d" % s for s in sizes], body)

slope_rows = []
for algo in ("Kruskal", "Prim", "Kosaraju", "Kahn", "DagShortestPath"):
    line = [algo]
    for density in ("sparse", "dense"):
        rs = pick(algo, density)
        line.append("%.3f" % slope([r["n"] for r in rs], [r["edge_scans"] for r in rs]))
    slope_rows.append(line)
table("Log-log slope of edge_scans versus n", ["algorithm", "sparse", "dense"], slope_rows)

extra = []
for density in ("sparse", "dense"):
    rs = pick("Kruskal", density)
    ns = [r["n"] for r in rs]
    total = [r["edge_scans"] + r["dsu_ops"] + r["comparisons"] for r in rs]
    extra.append(["Kruskal comparisons", density, "%.3f" % slope(ns, [r["comparisons"] for r in rs])])
    extra.append(["Kruskal edge_scans + dsu_ops + comparisons", density, "%.3f" % slope(ns, total)])
    ps = pick("Prim", density)
    extra.append(["Prim comparisons", density, "%.3f" % slope(ns, [r["comparisons"] for r in ps])])
table("Further slopes", ["counter", "density", "slope"], extra)

share = []
for density in ("sparse", "dense"):
    rs = pick("Kruskal", density)
    share.append(["Kruskal " + density] + ["%.3f" % (r["edge_scans"] / r["m"]) for r in rs])
sizes = [r["n"] for r in pick("Kruskal", "sparse")]
table("Share of edges Kruskal examines (edge_scans / m)", ["graph"] + ["n=%d" % s for s in sizes], share)

depth = []
for algo in ("Kosaraju", "Kahn"):
    for density in ("sparse", "dense"):
        depth.append([algo + " " + density] + [str(r["max_depth"]) for r in pick(algo, density)])
table("max_depth (explicit DFS stack for Kosaraju, peak queue size for Kahn)", ["algorithm"] + ["n=%d" % s for s in sizes], depth)

times = []
for algo in ("Kruskal", "Prim", "Kosaraju", "Kahn", "DagShortestPath"):
    for density in ("sparse", "dense"):
        times.append([algo + " " + density] + ["%.1f" % r["time_ms"] for r in pick(algo, density)])
table("Median time in ms over 5 runs", ["algorithm"] + ["n=%d" % s for s in sizes], times)

with open(out, "w") as f:
    f.write("\n".join(lines))
print("\n".join(lines))
