package daa;

import daa.graph.Edge;
import daa.graph.Graph;
import java.util.ArrayDeque;
import java.util.Random;

final class TestUtil {
    private TestUtil() {
    }

    static Graph randomUndirected(int n, int m, Random rnd, boolean connected) {
        Graph g = new Graph(n, false);
        if (connected) {
            for (int i = 1; i < n; i++) {
                g.addEdge(rnd.nextInt(i), i, 1 + rnd.nextInt(50));
            }
        }
        int extra = connected ? Math.max(0, m - (n - 1)) : m;
        for (int k = 0; k < extra; k++) {
            g.addEdge(rnd.nextInt(n), rnd.nextInt(n), 1 + rnd.nextInt(50));
        }
        return g;
    }

    static Graph randomDirected(int n, int m, Random rnd) {
        Graph g = new Graph(n, true);
        for (int k = 0; k < m; k++) {
            g.addEdge(rnd.nextInt(n), rnd.nextInt(n), 1 + rnd.nextInt(20));
        }
        return g;
    }

    static Graph randomDag(int n, int m, Random rnd, int minW, int maxW) {
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[i] = i;
        }
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int t = rank[i];
            rank[i] = rank[j];
            rank[j] = t;
        }
        Graph g = new Graph(n, true);
        for (int k = 0; k < m; k++) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            if (a == b) {
                continue;
            }
            if (rank[a] > rank[b]) {
                int t = a;
                a = b;
                b = t;
            }
            g.addEdge(a, b, minW + rnd.nextInt(maxW - minW + 1));
        }
        return g;
    }

    static boolean[][] reachability(Graph g) {
        int n = g.n();
        boolean[][] reach = new boolean[n][n];
        for (int s = 0; s < n; s++) {
            ArrayDeque<Integer> q = new ArrayDeque<>();
            q.add(s);
            reach[s][s] = true;
            while (!q.isEmpty()) {
                int u = q.poll();
                for (Edge e : g.adj(u)) {
                    if (!reach[s][e.v]) {
                        reach[s][e.v] = true;
                        q.add(e.v);
                    }
                }
            }
        }
        return reach;
    }
}
