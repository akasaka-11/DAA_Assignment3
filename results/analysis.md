**Counter divided by formula, sparse graphs (m = 4n)**

| algorithm | ratio | n=1000 | n=3162 | n=10000 | n=31623 | n=100000 |
|---|---|---|---|---|---|---|
| Kruskal | dsu_ops / E | 1.791 | 2.019 | 2.182 | 2.232 | 2.248 |
| Kruskal | comparisons / (E log2 E) | 0.891 | 0.907 | 0.869 | 0.809 | 0.745 |
| Prim | edge_scans / (2E) | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 |
| Prim | comparisons / (E log2 V) | 2.196 | 2.186 | 2.180 | 2.178 | 2.167 |
| Kosaraju | edge_scans / (2E) | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 |
| Kosaraju | edge_scans / (V + E) | 1.600 | 1.600 | 1.600 | 1.600 | 1.600 |
| Kahn | edge_scans / E | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 |
| Kahn | edge_scans / (V + E) | 0.800 | 0.800 | 0.800 | 0.800 | 0.800 |
| DagShortestPath | edge_scans / E | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 |
| DagShortestPath | edge_scans / (V + E) | 0.800 | 0.800 | 0.800 | 0.800 | 0.800 |

**Counter divided by formula, dense graphs (m = 20n)**

| algorithm | ratio | n=1000 | n=3162 | n=10000 | n=31623 | n=100000 |
|---|---|---|---|---|---|---|
| Kruskal | dsu_ops / E | 0.480 | 0.506 | 0.465 | 0.654 | 0.794 |
| Kruskal | comparisons / (E log2 E) | 0.903 | 0.846 | 0.784 | 0.720 | 0.666 |
| Prim | edge_scans / (2E) | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 |
| Prim | comparisons / (E log2 V) | 2.616 | 2.533 | 2.473 | 2.421 | 2.381 |
| Kosaraju | edge_scans / (2E) | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 |
| Kosaraju | edge_scans / (V + E) | 1.905 | 1.905 | 1.905 | 1.905 | 1.905 |
| Kahn | edge_scans / E | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 |
| Kahn | edge_scans / (V + E) | 0.952 | 0.952 | 0.952 | 0.952 | 0.952 |
| DagShortestPath | edge_scans / E | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 |
| DagShortestPath | edge_scans / (V + E) | 0.952 | 0.952 | 0.952 | 0.952 | 0.952 |

**Log-log slope of edge_scans versus n**

| algorithm | sparse | dense |
|---|---|---|
| Kruskal | 1.055 | 1.119 |
| Prim | 1.000 | 1.000 |
| Kosaraju | 1.000 | 1.000 |
| Kahn | 1.000 | 1.000 |
| DagShortestPath | 1.000 | 1.000 |

**Further slopes**

| counter | density | slope |
|---|---|---|
| Kruskal comparisons | sparse | 1.055 |
| Kruskal edge_scans + dsu_ops + comparisons | sparse | 1.054 |
| Prim comparisons | sparse | 1.108 |
| Kruskal comparisons | dense | 1.016 |
| Kruskal edge_scans + dsu_ops + comparisons | dense | 1.022 |
| Prim comparisons | dense | 1.090 |

**Share of edges Kruskal examines (edge_scans / m)**

| graph | n=1000 | n=3162 | n=10000 | n=31623 | n=100000 |
|---|---|---|---|---|---|
| Kruskal sparse | 0.770 | 0.885 | 0.966 | 0.991 | 0.999 |
| Kruskal dense | 0.215 | 0.228 | 0.207 | 0.302 | 0.372 |

**max_depth (explicit DFS stack for Kosaraju, peak queue size for Kahn)**

| algorithm | n=1000 | n=3162 | n=10000 | n=31623 | n=100000 |
|---|---|---|---|---|---|
| Kosaraju sparse | 626 | 1871 | 6002 | 18822 | 59668 |
| Kosaraju dense | 925 | 2937 | 9217 | 29073 | 91920 |
| Kahn sparse | 126 | 392 | 1241 | 3969 | 12484 |
| Kahn dense | 27 | 78 | 246 | 792 | 2488 |

**Median time in ms over 5 runs**

| algorithm | n=1000 | n=3162 | n=10000 | n=31623 | n=100000 |
|---|---|---|---|---|---|
| Kruskal sparse | 5.7 | 7.9 | 17.4 | 41.7 | 80.1 |
| Kruskal dense | 22.0 | 45.8 | 45.3 | 138.6 | 446.4 |
| Prim sparse | 7.7 | 7.1 | 11.5 | 51.4 | 264.4 |
| Prim dense | 17.2 | 25.0 | 79.8 | 456.4 | 1936.4 |
| Kosaraju sparse | 5.5 | 5.4 | 13.1 | 26.1 | 185.6 |
| Kosaraju dense | 5.6 | 20.7 | 23.3 | 133.9 | 577.5 |
| Kahn sparse | 0.6 | 0.3 | 1.2 | 8.4 | 22.4 |
| Kahn dense | 4.7 | 0.7 | 7.4 | 19.1 | 70.3 |
| DagShortestPath sparse | 0.7 | 1.4 | 3.6 | 9.3 | 23.9 |
| DagShortestPath dense | 0.7 | 6.2 | 9.9 | 21.2 | 93.8 |
