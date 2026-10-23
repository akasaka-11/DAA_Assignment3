# DAA Assignment 3: Graphs (DSU, MST, SCC, Shortest Paths in DAGs)

City Network & Job Scheduler. A single `Graph` class (adjacency list, directed or undirected, `int` weights) runs
DSU, Kruskal, Prim, Kosaraju, Kahn and the DAG shortest path. Bonus: iterative Tarjan and condensation with a critical path.

## Requirements

- JDK 17 or newer
- Maven 3.8 or newer
- Python 3 with matplotlib (only for the plots)

## Build and test

```
mvn clean test
```

## Run the benchmark

```
MAVEN_OPTS="-Xmx3g" mvn -q compile exec:java
```

or without Maven's exec plugin:

```
mvn -q compile
java -Xmx3g -cp target/classes daa.benchmark.Benchmark results
```

The run generates all ten graphs with `new Random(42)`, checks the Kruskal/Prim agreement, validates every topological
order, checks the scan counters (Kosaraju 2m, Kahn m, DAG m) and compares every result with the reference table of the
assignment. If anything differs the program prints `CHECK FAILED` and exits with code 1 without writing the CSV.

Output files:

- `results/results.csv` - 50 rows, the graded evidence
- `results/results_bonus.csv` - Tarjan rows
- `results/condensation.csv` - condensation size and critical path length

## Plots and analysis tables

```
python3 scripts/plots.py
python3 scripts/analyze.py
```

Plots are written to `results/plots/`, tables to `results/analysis.md`.

## Layout

```
src/main/java/daa/graph        Graph, Edge, GraphIO
src/main/java/daa/algorithms   Dsu, Kruskal, Prim, Kosaraju, Tarjan, Kahn, DagShortestPath, Condensation
src/main/java/daa/metrics      Metrics (dsuOps, edgeScans, comparisons, maxDepth)
src/main/java/daa/benchmark    GraphGenerator, Benchmark
src/test/java/daa              JUnit 5 tests
data/                          input graphs (disconnected, cyclic, dag, edge_cases)
results/                       results.csv, plots/
REPORT.md                      report
```

## Input format

```
n m directed|undirected
u v w
...
```

## Git workflow

Branches: `main`, `feature/dsu`, `feature/mst`, `feature/scc`, `feature/paths`, `feature/metrics`.
Release: tag `v1.0` on `main`.

## AI-use acknowledgement

This project was produced with the help of Claude (Anthropic). Claude generated the code, tests, scripts and the first
draft of the report. The benchmark was run and checked against the reference table of the assignment, and the tests were
run before submission. I reviewed the code and am responsible for being able to explain and defend it.
