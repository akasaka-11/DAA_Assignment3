package daa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import daa.algorithms.Kahn;
import daa.graph.Graph;
import daa.graph.GraphIO;
import daa.metrics.Metrics;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import org.junit.jupiter.api.Test;

class TopologicalOrderTest {
    @Test
    void validOrderOnRandomDags() {
        Random rnd = new Random(21);
        for (int t = 0; t < 100; t++) {
            int n = 1 + rnd.nextInt(150);
            Graph g = TestUtil.randomDag(n, rnd.nextInt(4 * n + 1), rnd, 1, 20);
            Kahn.Result r = Kahn.run(g, new Metrics());
            assertFalse(r.cyclic);
            assertEquals(n, r.count);
            assertTrue(Kahn.isValidOrder(g, r.order));
        }
    }

    @Test
    void dagFile() throws IOException {
        Graph g = GraphIO.read(Path.of("data/dag.txt"));
        Kahn.Result r = Kahn.run(g, new Metrics());
        assertFalse(r.cyclic);
        assertTrue(Kahn.isValidOrder(g, r.order));
    }

    @Test
    void cycleIsDetected() throws IOException {
        Graph g = GraphIO.read(Path.of("data/cyclic.txt"));
        Kahn.Result r = Kahn.run(g, new Metrics());
        assertTrue(r.cyclic);
        assertTrue(r.count < g.n());
        assertFalse(Kahn.isValidOrder(g, r.order));
    }

    @Test
    void selfLoopIsACycle() {
        Graph g = new Graph(2, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 1, 1);
        assertTrue(Kahn.run(g, new Metrics()).cyclic);
    }

    @Test
    void scansEveryEdgeOnce() {
        Random rnd = new Random(2);
        Graph g = TestUtil.randomDag(300, 1500, rnd, 1, 9);
        Metrics mt = new Metrics();
        Kahn.run(g, mt);
        assertEquals(g.m(), mt.edgeScans);
    }

    @Test
    void validatorRejectsBadOrders() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        assertTrue(Kahn.isValidOrder(g, new int[] {0, 1, 2}));
        assertFalse(Kahn.isValidOrder(g, new int[] {1, 0, 2}));
        assertFalse(Kahn.isValidOrder(g, new int[] {0, 1}));
        assertFalse(Kahn.isValidOrder(g, new int[] {0, 0, 2}));
    }
}
