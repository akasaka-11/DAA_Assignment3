package daa.algorithms;

import daa.graph.Edge;
import daa.graph.Graph;
import daa.metrics.Metrics;
import java.util.Arrays;
import java.util.List;

public final class Tarjan {
    private Tarjan() {
    }

    public static Kosaraju.Result run(Graph g, Metrics mt) {
        g.requireDirected();
        int n = g.n();
        int[] index = new int[n];
        int[] low = new int[n];
        int[] comp = new int[n];
        int[] ptr = new int[n];
        boolean[] onStack = new boolean[n];
        int[] active = new int[n];
        int[] call = new int[n];
        Arrays.fill(index, -1);
        int idx = 0;
        int ap = 0;
        int count = 0;
        for (int s = 0; s < n; s++) {
            if (index[s] != -1) {
                continue;
            }
            int cp = 0;
            index[s] = idx;
            low[s] = idx;
            idx++;
            active[ap++] = s;
            onStack[s] = true;
            call[cp++] = s;
            if (cp > mt.maxDepth) {
                mt.maxDepth = cp;
            }
            while (cp > 0) {
                int v = call[cp - 1];
                List<Edge> a = g.adj(v);
                if (ptr[v] < a.size()) {
                    Edge e = a.get(ptr[v]++);
                    mt.edgeScans++;
                    int x = e.v;
                    if (index[x] == -1) {
                        index[x] = idx;
                        low[x] = idx;
                        idx++;
                        active[ap++] = x;
                        onStack[x] = true;
                        call[cp++] = x;
                        if (cp > mt.maxDepth) {
                            mt.maxDepth = cp;
                        }
                    } else if (onStack[x]) {
                        mt.comparisons++;
                        if (index[x] < low[v]) {
                            low[v] = index[x];
                        }
                    }
                } else {
                    cp--;
                    if (low[v] == index[v]) {
                        int x;
                        do {
                            x = active[--ap];
                            onStack[x] = false;
                            comp[x] = count;
                        } while (x != v);
                        count++;
                    }
                    if (cp > 0) {
                        int p = call[cp - 1];
                        mt.comparisons++;
                        if (low[v] < low[p]) {
                            low[p] = low[v];
                        }
                    }
                }
            }
        }
        return new Kosaraju.Result(comp, count);
    }
}
