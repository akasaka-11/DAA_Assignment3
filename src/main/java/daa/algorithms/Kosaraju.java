package daa.algorithms;

import daa.graph.Edge;
import daa.graph.Graph;
import daa.metrics.Metrics;
import java.util.List;

public final class Kosaraju {
    private Kosaraju() {
    }

    public static final class Result {
        public final int[] component;
        public final int count;

        Result(int[] component, int count) {
            this.component = component;
            this.count = count;
        }
    }

    public static Result run(Graph g, Metrics mt) {
        g.requireDirected();
        int n = g.n();
        int[] finish = new int[n];
        boolean[] seen = new boolean[n];
        int[] ptr = new int[n];
        int[] stack = new int[n];
        int fc = 0;
        for (int s = 0; s < n; s++) {
            if (!seen[s]) {
                fc = dfs(g, s, seen, ptr, stack, finish, fc, mt);
            }
        }
        Graph rev = g.reversed();
        boolean[] seen2 = new boolean[n];
        int[] ptr2 = new int[n];
        int[] out = new int[n];
        int[] comp = new int[n];
        int oc = 0;
        int count = 0;
        for (int i = n - 1; i >= 0; i--) {
            int s = finish[i];
            if (seen2[s]) {
                continue;
            }
            int before = oc;
            oc = dfs(rev, s, seen2, ptr2, stack, out, oc, mt);
            for (int j = before; j < oc; j++) {
                comp[out[j]] = count;
            }
            count++;
        }
        return new Result(comp, count);
    }

    private static int dfs(Graph h, int s, boolean[] seen, int[] ptr, int[] stack,
                           int[] out, int oc, Metrics mt) {
        int sp = 0;
        stack[sp++] = s;
        seen[s] = true;
        if (sp > mt.maxDepth) {
            mt.maxDepth = sp;
        }
        while (sp > 0) {
            int v = stack[sp - 1];
            List<Edge> a = h.adj(v);
            if (ptr[v] < a.size()) {
                Edge e = a.get(ptr[v]++);
                mt.edgeScans++;
                int x = e.v;
                if (!seen[x]) {
                    seen[x] = true;
                    stack[sp++] = x;
                    if (sp > mt.maxDepth) {
                        mt.maxDepth = sp;
                    }
                }
            } else {
                out[oc++] = v;
                sp--;
            }
        }
        return oc;
    }
}
