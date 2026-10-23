package daa.graph;

public final class Edge {
    public final int u;
    public final int v;
    public final int w;

    public Edge(int u, int v, int w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    public int other(int x) {
        return x == u ? v : u;
    }
}
