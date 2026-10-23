package daa.algorithms;

import daa.graph.Edge;
import daa.graph.Graph;
import daa.metrics.Metrics;
import java.util.PriorityQueue;

public final class Prim {
    private Prim() {
    }

    public static final class Result {
        public final long weight;
        public final int treeEdges;
        public final int components;

        Result(long weight, int treeEdges, int components) {
            this.weight = weight;
            this.treeEdges = treeEdges;
            this.components = components;
        }
    }

    public static Result run(Graph g, Metrics mt) {
        g.requireUndirected();
        int n = g.n();
        boolean[] inTree = new boolean[n];
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> {
            mt.comparisons++;
            return Integer.compare(a[0], b[0]);
        });
        long total = 0;
        int treeEdges = 0;
        int components = 0;
        for (int start = 0; start < n; start++) {
            if (inTree[start]) {
                continue;
            }
            components++;
            pq.add(new int[] {0, start});
            while (!pq.isEmpty()) {
                int[] top = pq.poll();
                int v = top[1];
                if (inTree[v]) {
                    continue;
                }
                inTree[v] = true;
                total += top[0];
                if (v != start) {
                    treeEdges++;
                }
                for (Edge e : g.adj(v)) {
                    mt.edgeScans++;
                    int x = e.other(v);
                    if (!inTree[x]) {
                        pq.add(new int[] {e.w, x});
                    }
                }
            }
        }
        return new Result(total, treeEdges, components);
    }
}
