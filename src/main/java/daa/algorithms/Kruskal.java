package daa.algorithms;

import daa.graph.Edge;
import daa.graph.Graph;
import daa.metrics.Metrics;
import java.util.ArrayList;
import java.util.List;

public final class Kruskal {
    private Kruskal() {
    }

    public static final class Result {
        public final long weight;
        public final List<Edge> edges;
        public final int components;

        Result(long weight, List<Edge> edges, int components) {
            this.weight = weight;
            this.edges = edges;
            this.components = components;
        }
    }

    public static Result run(Graph g, Metrics mt) {
        g.requireUndirected();
        int n = g.n();
        List<Edge> sorted = new ArrayList<>(g.edges());
        sorted.sort((a, b) -> {
            mt.comparisons++;
            return Integer.compare(a.w, b.w);
        });
        Dsu dsu = new Dsu(n, mt);
        List<Edge> tree = new ArrayList<>();
        long total = 0;
        for (Edge e : sorted) {
            if (tree.size() == n - 1) {
                break;
            }
            mt.edgeScans++;
            if (dsu.find(e.u) != dsu.find(e.v)) {
                dsu.union(e.u, e.v);
                tree.add(e);
                total += e.w;
            }
        }
        return new Result(total, tree, dsu.components());
    }
}
