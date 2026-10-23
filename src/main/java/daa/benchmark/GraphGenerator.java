package daa.benchmark;

import daa.graph.Graph;
import java.util.Random;

public final class GraphGenerator {
    public static final long SEED = 42;

    private GraphGenerator() {
    }

    private static int[] shuffled(int n, Random rnd) {
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) {
            perm[i] = i;
        }
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int t = perm[i];
            perm[i] = perm[j];
            perm[j] = t;
        }
        return perm;
    }

    public static Graph undirected(int n, int m) {
        Random rnd = new Random(SEED);
        int[] perm = shuffled(n, rnd);
        Graph g = new Graph(n, false);
        for (int i = 1; i < n; i++) {
            int a = perm[rnd.nextInt(i)];
            int b = perm[i];
            g.addEdge(a, b, 1 + rnd.nextInt(1000));
        }
        for (int k = n - 1; k < m; k++) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            while (b == a) {
                b = rnd.nextInt(n);
            }
            g.addEdge(a, b, 1 + rnd.nextInt(1000));
        }
        return g;
    }

    public static Graph directed(int n, int m) {
        Random rnd = new Random(SEED);
        Graph g = new Graph(n, true);
        for (int k = 0; k < m; k++) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            while (b == a) {
                b = rnd.nextInt(n);
            }
            g.addEdge(a, b, 1 + rnd.nextInt(1000));
        }
        return g;
    }

    public static Graph dag(int n, int m) {
        Random rnd = new Random(SEED);
        int[] rank = shuffled(n, rnd);
        Graph g = new Graph(n, true);
        for (int k = 0; k < m; k++) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            while (b == a) {
                b = rnd.nextInt(n);
            }
            if (rank[a] > rank[b]) {
                int t = a;
                a = b;
                b = t;
            }
            g.addEdge(a, b, 1 + rnd.nextInt(1000));
        }
        return g;
    }
}
