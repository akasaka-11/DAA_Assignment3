package daa.algorithms;

import daa.graph.Edge;
import daa.graph.Graph;
import daa.metrics.Metrics;

public final class Kahn {
    private Kahn() {
    }

    public static final class Result {
        public final int[] order;
        public final int count;
        public final boolean cyclic;

        Result(int[] order, int count, boolean cyclic) {
            this.order = order;
            this.count = count;
            this.cyclic = cyclic;
        }
    }

    public static Result run(Graph g, Metrics mt) {
        g.requireDirected();
        int n = g.n();
        int[] indeg = new int[n];
        for (Edge e : g.edges()) {
            indeg[e.v]++;
        }
        int[] queue = new int[n];
        int head = 0;
        int tail = 0;
        for (int v = 0; v < n; v++) {
            if (indeg[v] == 0) {
                queue[tail++] = v;
            }
        }
        if (tail - head > mt.maxDepth) {
            mt.maxDepth = tail - head;
        }
        while (head < tail) {
            int u = queue[head++];
            for (Edge e : g.adj(u)) {
                mt.edgeScans++;
                if (--indeg[e.v] == 0) {
                    queue[tail++] = e.v;
                }
            }
            if (tail - head > mt.maxDepth) {
                mt.maxDepth = tail - head;
            }
        }
        boolean cyclic = tail < n;
        int[] order = new int[tail];
        System.arraycopy(queue, 0, order, 0, tail);
        return new Result(order, tail, cyclic);
    }

    public static boolean isValidOrder(Graph g, int[] order) {
        int n = g.n();
        if (order.length != n) {
            return false;
        }
        int[] pos = new int[n];
        java.util.Arrays.fill(pos, -1);
        for (int i = 0; i < n; i++) {
            int v = order[i];
            if (v < 0 || v >= n || pos[v] != -1) {
                return false;
            }
            pos[v] = i;
        }
        for (Edge e : g.edges()) {
            if (pos[e.u] >= pos[e.v]) {
                return false;
            }
        }
        return true;
    }
}
