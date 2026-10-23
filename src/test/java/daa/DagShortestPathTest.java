package daa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import daa.algorithms.DagShortestPath;
import daa.algorithms.Kahn;
import daa.graph.Edge;
import daa.graph.Graph;
import daa.graph.GraphIO;
import daa.metrics.Metrics;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class DagShortestPathTest {
    private static long[] bellmanFord(Graph g) {
        int n = g.n();
        long inf = Long.MAX_VALUE;
        long[] d = new long[n];
        Arrays.fill(d, inf);
        boolean[] hasIn = new boolean[n];
        for (Edge e : g.edges()) {
            hasIn[e.v] = true;
        }
        for (int v = 0; v < n; v++) {
            if (!hasIn[v]) {
                d[v] = 0;
            }
        }
        for (int round = 0; round < n; round++) {
            boolean changed = false;
            for (Edge e : g.edges()) {
                if (d[e.u] != inf && d[e.u] + e.w < d[e.v]) {
                    d[e.v] = d[e.u] + e.w;
                    changed = true;
                }
            }
            if (!changed) {
                break;
            }
        }
        return d;
    }

    @Test
    void matchesBellmanFordWithNegativeWeights() {
        Random rnd = new Random(31);
        for (int t = 0; t < 100; t++) {
            int n = 1 + rnd.nextInt(60);
            Graph g = TestUtil.randomDag(n, rnd.nextInt(4 * n + 1), rnd, -20, 30);
            Kahn.Result topo = Kahn.run(g, new Metrics());
            DagShortestPath.Result r = DagShortestPath.run(g, topo.order, new Metrics());
            assertEquals(Arrays.toString(bellmanFord(g)), Arrays.toString(r.dist));
        }
    }

    @Test
    void fileExampleAndPathRestoring() throws IOException {
        Graph g = GraphIO.read(Path.of("data/dag.txt"));
        Kahn.Result topo = Kahn.run(g, new Metrics());
        DagShortestPath.Result r = DagShortestPath.run(g, topo.order, new Metrics());
        assertEquals(0, r.dist[0]);
        assertEquals(5, r.dist[1]);
        assertEquals(3, r.dist[2]);
        assertEquals(3, r.dist[3]);
        assertEquals(2, r.dist[4]);
        assertEquals(4, r.dist[5]);
        assertEquals(List.of(0, 2, 4, 5), r.path(5));
    }

    @Test
    void negativeWeightsAndPath() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 5);
        g.addEdge(0, 2, 2);
        g.addEdge(2, 1, -4);
        g.addEdge(1, 3, 1);
        Kahn.Result topo = Kahn.run(g, new Metrics());
        DagShortestPath.Result r = DagShortestPath.run(g, topo.order, new Metrics());
        assertEquals(-2, r.dist[1]);
        assertEquals(-1, r.dist[3]);
        assertEquals(List.of(0, 2, 1, 3), r.path(3));
    }

    @Test
    void multipleSourcesStartAtZero() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 2, 7);
        g.addEdge(1, 2, 3);
        g.addEdge(2, 3, 1);
        Kahn.Result topo = Kahn.run(g, new Metrics());
        DagShortestPath.Result r = DagShortestPath.run(g, topo.order, new Metrics());
        assertEquals(0, r.dist[0]);
        assertEquals(0, r.dist[1]);
        assertEquals(3, r.dist[2]);
        assertEquals(4, r.dist[3]);
        assertEquals(List.of(1, 2, 3), r.path(3));
    }

    @Test
    void isolatedVerticesAndScanCount() {
        Graph g = new Graph(6, true);
        g.addEdge(0, 1, 2);
        g.addEdge(0, 1, 1);
        Kahn.Result topo = Kahn.run(g, new Metrics());
        Metrics mt = new Metrics();
        DagShortestPath.Result r = DagShortestPath.run(g, topo.order, mt);
        assertEquals(1, r.dist[1]);
        assertEquals(0, r.dist[5]);
        assertEquals(g.m(), mt.edgeScans);
        assertEquals(1, r.sum());
    }

    @Test
    void cyclicGraphIsRejected() throws IOException {
        Graph g = GraphIO.read(Path.of("data/cyclic.txt"));
        Kahn.Result topo = Kahn.run(g, new Metrics());
        assertThrows(IllegalArgumentException.class,
            () -> DagShortestPath.run(g, topo.order, new Metrics()));
    }
}
