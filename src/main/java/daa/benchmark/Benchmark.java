package daa.benchmark;

import daa.algorithms.Condensation;
import daa.algorithms.DagShortestPath;
import daa.algorithms.Kahn;
import daa.algorithms.Kosaraju;
import daa.algorithms.Kruskal;
import daa.algorithms.Prim;
import daa.algorithms.Tarjan;
import daa.graph.Graph;
import daa.metrics.Metrics;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public final class Benchmark {
    static final int[] SIZES = {1000, 3162, 10000, 31623, 100000};
    static final int[] DENSITIES = {4, 20};
    static final String[] DENSITY_NAMES = {"sparse", "dense"};
    static final int RUNS = 5;

    static final long[] REF_MST = {147970L, 30363L, 468911L, 97589L, 1486139L, 309550L,
        4715515L, 968517L, 15012094L, 3056681L};
    static final long[] REF_SCC = {27L, 1L, 137L, 1L, 373L, 1L, 1188L, 1L, 3923L, 1L};
    static final long[] REF_DAG = {396274L, 252411L, 1271911L, 723157L, 3941828L, 2381342L,
        12551891L, 7376612L, 40096539L, 23512087L};

    static final String HEADER =
        "algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result";

    private static final List<String> failures = new ArrayList<>();

    private static final class Row {
        final String algorithm;
        final String graph;
        final int n;
        final int m;
        final double timeMs;
        final Metrics metrics;
        final long result;

        Row(String algorithm, String graph, int n, int m, double timeMs, Metrics metrics, long result) {
            this.algorithm = algorithm;
            this.graph = graph;
            this.n = n;
            this.m = m;
            this.timeMs = timeMs;
            this.metrics = metrics;
            this.result = result;
        }

        String csv() {
            return String.format(java.util.Locale.ROOT, "%s,%s,%d,%d,%.3f,%d,%d,%d,%d,%d",
                algorithm, graph, n, m, timeMs, metrics.dsuOps, metrics.edgeScans,
                metrics.comparisons, metrics.maxDepth, result);
        }
    }

    private static Row measure(String algorithm, String graph, int n, int m,
                               Function<Metrics, Long> body) {
        double[] times = new double[RUNS];
        Metrics last = null;
        long result = 0;
        for (int r = 0; r < RUNS; r++) {
            Metrics mt = new Metrics();
            long t0 = System.nanoTime();
            result = body.apply(mt);
            times[r] = (System.nanoTime() - t0) / 1e6;
            last = mt;
        }
        Arrays.sort(times);
        return new Row(algorithm, graph, n, m, times[RUNS / 2], last, result);
    }

    private static void check(boolean ok, String message) {
        if (!ok) {
            failures.add(message);
            System.err.println("CHECK FAILED: " + message);
        }
    }

    public static void main(String[] args) throws IOException {
        Path outDir = Path.of(args.length > 0 ? args[0] : "results");
        Files.createDirectories(outDir);
        List<Row> rows = new ArrayList<>();
        List<Row> bonus = new ArrayList<>();
        List<String> condensation = new ArrayList<>();
        condensation.add("graph,n,m,scc_count,condensed_edges,longest_path");

        for (int si = 0; si < SIZES.length; si++) {
            for (int di = 0; di < DENSITIES.length; di++) {
                int n = SIZES[si];
                int m = n * DENSITIES[di];
                int ref = si * 2 + di;
                String label = "n" + n + "_" + DENSITY_NAMES[di];
                System.out.println("== " + label + " (n=" + n + ", m=" + m + ")");

                final Graph ug = GraphGenerator.undirected(n, m);
                check(ug.m() == m, label + ": undirected edge count " + ug.m());
                Row kr = measure("Kruskal", label, n, m, mt -> Kruskal.run(ug, mt).weight);
                Row pr = measure("Prim", label, n, m, mt -> Prim.run(ug, mt).weight);
                check(kr.result == pr.result, label + ": Kruskal " + kr.result + " != Prim " + pr.result);
                check(kr.result == REF_MST[ref], label + ": MST " + kr.result + " != reference " + REF_MST[ref]);
                check(Kruskal.run(ug, new Metrics()).edges.size() == n - 1, label + ": Kruskal tree size");
                check(Prim.run(ug, new Metrics()).treeEdges == n - 1, label + ": Prim tree size");
                rows.add(kr);
                rows.add(pr);

                final Graph dg = GraphGenerator.directed(n, m);
                final Graph dgf = dg;
                Row ko = measure("Kosaraju", label, n, m, mt -> (long) Kosaraju.run(dgf, mt).count);
                check(ko.result == REF_SCC[ref], label + ": SCC " + ko.result + " != reference " + REF_SCC[ref]);
                check(ko.metrics.edgeScans == 2L * m, label + ": Kosaraju scans " + ko.metrics.edgeScans);
                rows.add(ko);
                Row ta = measure("Tarjan", label, n, m, mt -> (long) Tarjan.run(dgf, mt).count);
                Kosaraju.Result kres = Kosaraju.run(dg, new Metrics());
                Kosaraju.Result tres = Tarjan.run(dg, new Metrics());
                check(samePartition(kres.component, tres.component), label + ": Tarjan differs from Kosaraju");
                check(ta.metrics.edgeScans == m, label + ": Tarjan scans " + ta.metrics.edgeScans);
                bonus.add(ta);
                Condensation.Result cr = Condensation.criticalPath(dg, kres.component, kres.count);
                condensation.add(label + "," + n + "," + m + "," + kres.count + ","
                    + cr.dag.m() + "," + cr.longestPath);

                Graph ag = GraphGenerator.dag(n, m);
                final Graph agf = ag;
                Row ka = measure("Kahn", label, n, m, mt -> {
                    Kahn.Result r = Kahn.run(agf, mt);
                    return (long) r.count;
                });
                Kahn.Result topo = Kahn.run(ag, new Metrics());
                check(!topo.cyclic, label + ": generated DAG reported cyclic");
                check(Kahn.isValidOrder(ag, topo.order), label + ": invalid topological order");
                check(ka.result == n, label + ": Kahn order size " + ka.result);
                check(ka.metrics.edgeScans == m, label + ": Kahn scans " + ka.metrics.edgeScans);
                rows.add(ka);
                Row ds = measure("DagShortestPath", label, n, m,
                    mt -> DagShortestPath.run(agf, topo.order, mt).sum());
                check(ds.result == REF_DAG[ref], label + ": DAG sum " + ds.result + " != reference " + REF_DAG[ref]);
                check(ds.metrics.edgeScans == m, label + ": DAG scans " + ds.metrics.edgeScans);
                rows.add(ds);

                for (Row r : new Row[] {kr, pr, ko, ta, ka, ds}) {
                    System.out.println("  " + r.csv());
                }
            }
        }

        if (!failures.isEmpty()) {
            System.err.println(failures.size() + " check(s) failed, nothing written");
            System.exit(1);
        }
        write(outDir.resolve("results.csv"), HEADER, rows);
        write(outDir.resolve("results_bonus.csv"), HEADER, bonus);
        Files.write(outDir.resolve("condensation.csv"), condensation);
        System.out.println("rows written: " + rows.size() + ", all reference values reproduced");
    }

    private static void write(Path path, String header, List<Row> rows) throws IOException {
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(path))) {
            w.println(header);
            for (Row r : rows) {
                w.println(r.csv());
            }
        }
    }

    private static boolean samePartition(int[] a, int[] b) {
        if (a.length != b.length) {
            return false;
        }
        int[] ab = new int[a.length];
        int[] ba = new int[a.length];
        Arrays.fill(ab, -1);
        Arrays.fill(ba, -1);
        for (int i = 0; i < a.length; i++) {
            if (ab[a[i]] == -1 && ba[b[i]] == -1) {
                ab[a[i]] = b[i];
                ba[b[i]] = a[i];
            } else if (ab[a[i]] != b[i] || ba[b[i]] != a[i]) {
                return false;
            }
        }
        return true;
    }
}
