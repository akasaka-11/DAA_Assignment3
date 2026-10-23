package daa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import daa.algorithms.Kruskal;
import daa.algorithms.Prim;
import daa.graph.Graph;
import daa.graph.GraphIO;
import daa.metrics.Metrics;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import org.junit.jupiter.api.Test;

class MstTest {
    @Test
    void kruskalAndPrimAgreeOnRandomConnectedGraphs() {
        Random rnd = new Random(11);
        for (int t = 0; t < 100; t++) {
            int n = 2 + rnd.nextInt(60);
            int m = n - 1 + rnd.nextInt(4 * n);
            Graph g = TestUtil.randomUndirected(n, m, rnd, true);
            Kruskal.Result k = Kruskal.run(g, new Metrics());
            Prim.Result p = Prim.run(g, new Metrics());
            assertEquals(k.weight, p.weight);
            assertEquals(n - 1, k.edges.size());
            assertEquals(n - 1, p.treeEdges);
            assertEquals(1, k.components);
            assertEquals(1, p.components);
        }
    }

    @Test
    void forestOnDisconnectedFile() throws IOException {
        Graph g = GraphIO.read(Path.of("data/disconnected.txt"));
        Kruskal.Result k = Kruskal.run(g, new Metrics());
        Prim.Result p = Prim.run(g, new Metrics());
        assertEquals(10, k.weight);
        assertEquals(k.weight, p.weight);
        assertEquals(4, k.components);
        assertEquals(4, p.components);
        assertEquals(4, k.edges.size());
        assertEquals(4, p.treeEdges);
    }

    @Test
    void forestWeightsAgreeOnRandomDisconnectedGraphs() {
        Random rnd = new Random(5);
        for (int t = 0; t < 100; t++) {
            int n = 1 + rnd.nextInt(40);
            Graph g = TestUtil.randomUndirected(n, rnd.nextInt(2 * n), rnd, false);
            Kruskal.Result k = Kruskal.run(g, new Metrics());
            Prim.Result p = Prim.run(g, new Metrics());
            assertEquals(k.weight, p.weight);
            assertEquals(k.components, p.components);
            assertEquals(n - k.components, k.edges.size());
        }
    }

    @Test
    void parallelEdgesAndSelfLoops() throws IOException {
        Graph g = GraphIO.read(Path.of("data/edge_cases.txt"));
        Kruskal.Result k = Kruskal.run(g, new Metrics());
        Prim.Result p = Prim.run(g, new Metrics());
        assertEquals(k.weight, p.weight);
        assertEquals(1 + 2 + 6, k.weight);
        assertEquals(2, k.components);
    }

    @Test
    void emptyAndSingleVertex() {
        Graph empty = new Graph(0, false);
        assertEquals(0, Kruskal.run(empty, new Metrics()).weight);
        assertEquals(0, Prim.run(empty, new Metrics()).weight);
        Graph one = new Graph(1, false);
        assertEquals(0, Kruskal.run(one, new Metrics()).weight);
        assertEquals(1, Prim.run(one, new Metrics()).components);
    }

    @Test
    void rejectsDirectedGraph() {
        Graph g = new Graph(2, true);
        assertThrows(IllegalArgumentException.class, () -> Kruskal.run(g, new Metrics()));
        assertThrows(IllegalArgumentException.class, () -> Prim.run(g, new Metrics()));
    }
}
