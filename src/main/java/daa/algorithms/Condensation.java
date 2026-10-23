package daa.algorithms;

import daa.graph.Edge;
import daa.graph.Graph;
import daa.metrics.Metrics;
import java.util.List;

public final class Condensation {
    private Condensation() {
    }

    public static final class Result {
        public final Graph dag;
        public final long longestPath;
        public final List<Integer> path;

        Result(Graph dag, long longestPath, List<Integer> path) {
            this.dag = dag;
            this.longestPath = longestPath;
            this.path = path;
        }
    }

    public static Graph build(Graph g, int[] comp, int count) {
        Graph dag = new Graph(count, true);
        for (Edge e : g.edges()) {
            if (comp[e.u] != comp[e.v]) {
                dag.addEdge(comp[e.u], comp[e.v], e.w);
            }
        }
        return dag;
    }

    public static Result criticalPath(Graph g, int[] comp, int count) {
        Graph dag = build(g, comp, count);
        Graph negated = new Graph(count, true);
        for (Edge e : dag.edges()) {
            negated.addEdge(e.u, e.v, -e.w);
        }
        Metrics mt = new Metrics();
        Kahn.Result topo = Kahn.run(negated, mt);
        DagShortestPath.Result sp = DagShortestPath.run(negated, topo.order, mt);
        int best = 0;
        for (int v = 1; v < count; v++) {
            if (sp.dist[v] < sp.dist[best]) {
                best = v;
            }
        }
        if (count == 0) {
            return new Result(dag, 0, java.util.Collections.emptyList());
        }
        return new Result(dag, -sp.dist[best], sp.path(best));
    }
}
