package daa.algorithms;

import daa.metrics.Metrics;

public final class Dsu {
    private int[] parent;
    private int[] rank;
    private int components;
    private final Metrics metrics;
    private long findCalls;
    private long unionCalls;

    public Dsu(int n, Metrics metrics) {
        this.metrics = metrics;
        makeSet(n);
    }

    public Dsu(int n) {
        this(n, new Metrics());
    }

    public void makeSet(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
        components = n;
        findCalls = 0;
        unionCalls = 0;
    }

    public int find(int x) {
        findCalls++;
        metrics.dsuOps++;
        return root(x);
    }

    public boolean union(int x, int y) {
        int rx = root(x);
        int ry = root(y);
        if (rx == ry) {
            return false;
        }
        if (rank[rx] < rank[ry]) {
            parent[rx] = ry;
        } else if (rank[rx] > rank[ry]) {
            parent[ry] = rx;
        } else {
            parent[ry] = rx;
            rank[rx]++;
        }
        components--;
        unionCalls++;
        metrics.dsuOps++;
        return true;
    }

    public int components() {
        return components;
    }

    public long findCalls() {
        return findCalls;
    }

    public long unionCalls() {
        return unionCalls;
    }

    private int root(int x) {
        int r = x;
        while (parent[r] != r) {
            r = parent[r];
        }
        while (parent[x] != r) {
            int next = parent[x];
            parent[x] = r;
            x = next;
        }
        return r;
    }
}
