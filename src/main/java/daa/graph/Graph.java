package daa.graph;

import java.util.ArrayList;
import java.util.List;

public final class Graph {
    private final int n;
    private final boolean directed;
    private final List<Edge>[] adj;
    private final List<Edge> edges = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public Graph(int n, boolean directed) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative");
        }
        this.n = n;
        this.directed = directed;
        this.adj = new List[n];
        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>();
        }
    }

    public Edge addEdge(int u, int v, int w) {
        if (u < 0 || u >= n || v < 0 || v >= n) {
            throw new IllegalArgumentException("vertex out of range: " + u + " " + v);
        }
        Edge e = new Edge(u, v, w);
        edges.add(e);
        adj[u].add(e);
        if (!directed && u != v) {
            adj[v].add(e);
        }
        return e;
    }

    public int n() {
        return n;
    }

    public int m() {
        return edges.size();
    }

    public boolean isDirected() {
        return directed;
    }

    public List<Edge> adj(int v) {
        return adj[v];
    }

    public List<Edge> edges() {
        return edges;
    }

    public Graph reversed() {
        Graph r = new Graph(n, directed);
        for (Edge e : edges) {
            r.addEdge(e.v, e.u, e.w);
        }
        return r;
    }

    public void requireDirected() {
        if (!directed) {
            throw new IllegalArgumentException("directed graph required");
        }
    }

    public void requireUndirected() {
        if (directed) {
            throw new IllegalArgumentException("undirected graph required");
        }
    }
}
