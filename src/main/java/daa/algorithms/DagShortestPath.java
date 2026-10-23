package daa.algorithms;

import daa.graph.Edge;
import daa.graph.Graph;
import daa.metrics.Metrics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class DagShortestPath {
    public static final long INF = Long.MAX_VALUE;

    private DagShortestPath() {
    }

    public static final class Result {
        public final long[] dist;
        public final int[] parent;

        Result(long[] dist, int[] parent) {
            this.dist = dist;
            this.parent = parent;
        }

        public long sum() {
            long s = 0;
            for (long d : dist) {
                s += d;
            }
            return s;
        }

        public List<Integer> path(int v) {
            List<Integer> p = new ArrayList<>();
            for (int x = v; x != -1; x = parent[x]) {
                p.add(x);
            }
            Collections.reverse(p);
            return p;
        }
    }

    public static Result run(Graph g, int[] order, Metrics mt) {
        g.requireDirected();
        int n = g.n();
        if (order.length != n) {
            throw new IllegalArgumentException("order must contain every vertex (graph has a cycle?)");
        }
        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);
        boolean[] hasIn = new boolean[n];
        for (Edge e : g.edges()) {
            hasIn[e.v] = true;
        }
        for (int v = 0; v < n; v++) {
            if (!hasIn[v]) {
                dist[v] = 0;
            }
        }
        for (int u : order) {
            if (dist[u] == INF) {
                continue;
            }
            for (Edge e : g.adj(u)) {
                mt.edgeScans++;
                mt.comparisons++;
                long nd = dist[u] + e.w;
                if (nd < dist[e.v]) {
                    dist[e.v] = nd;
                    parent[e.v] = u;
                }
            }
        }
        return new Result(dist, parent);
    }
}
