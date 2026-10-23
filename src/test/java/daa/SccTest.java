package daa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import daa.algorithms.Condensation;
import daa.algorithms.Kosaraju;
import daa.algorithms.Tarjan;
import daa.graph.Graph;
import daa.graph.GraphIO;
import daa.metrics.Metrics;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import org.junit.jupiter.api.Test;

class SccTest {
    private static void assertMatchesBruteForce(Graph g, int[] comp, int count) {
        int n = g.n();
        boolean[][] reach = TestUtil.reachability(g);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                boolean mutual = reach[i][j] && reach[j][i];
                assertEquals(mutual, comp[i] == comp[j]);
            }
        }
        assertEquals(java.util.Arrays.stream(comp).distinct().count(), count);
    }

    @Test
    void kosarajuMatchesBruteForce() {
        Random rnd = new Random(3);
        for (int t = 0; t < 60; t++) {
            int n = 1 + rnd.nextInt(200);
            Graph g = TestUtil.randomDirected(n, rnd.nextInt(3 * n + 1), rnd);
            Kosaraju.Result r = Kosaraju.run(g, new Metrics());
            assertMatchesBruteForce(g, r.component, r.count);
        }
    }

    @Test
    void tarjanMatchesBruteForceAndKosaraju() {
        Random rnd = new Random(4);
        for (int t = 0; t < 60; t++) {
            int n = 1 + rnd.nextInt(200);
            Graph g = TestUtil.randomDirected(n, rnd.nextInt(3 * n + 1), rnd);
            Kosaraju.Result k = Kosaraju.run(g, new Metrics());
            Kosaraju.Result a = Tarjan.run(g, new Metrics());
            assertMatchesBruteForce(g, a.component, a.count);
            assertEquals(k.count, a.count);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    assertEquals(k.component[i] == k.component[j], a.component[i] == a.component[j]);
                }
            }
        }
    }

    @Test
    void cyclicFile() throws IOException {
        Graph g = GraphIO.read(Path.of("data/cyclic.txt"));
        Kosaraju.Result r = Kosaraju.run(g, new Metrics());
        assertEquals(3, r.count);
        assertEquals(r.component[0], r.component[1]);
        assertEquals(r.component[1], r.component[2]);
        assertEquals(r.component[3], r.component[4]);
        assertEquals(r.component[4], r.component[5]);
    }

    @Test
    void scanCounts() {
        Random rnd = new Random(9);
        Graph g = TestUtil.randomDirected(500, 2000, rnd);
        Metrics k = new Metrics();
        Metrics a = new Metrics();
        Kosaraju.run(g, k);
        Tarjan.run(g, a);
        assertEquals(2L * g.m(), k.edgeScans);
        assertEquals(g.m(), a.edgeScans);
    }

    @Test
    void stackSafeOnPathOf100000() {
        int n = 100000;
        Graph g = new Graph(n, true);
        for (int i = 0; i + 1 < n; i++) {
            g.addEdge(i, i + 1, 1);
        }
        Metrics mt = new Metrics();
        Kosaraju.Result r = Kosaraju.run(g, mt);
        assertEquals(n, r.count);
        assertTrue(mt.maxDepth >= n);
        Metrics mt2 = new Metrics();
        assertEquals(n, Tarjan.run(g, mt2).count);
    }

    @Test
    void stackSafeOnBigCycle() {
        int n = 100000;
        Graph g = new Graph(n, true);
        for (int i = 0; i < n; i++) {
            g.addEdge(i, (i + 1) % n, 1);
        }
        assertEquals(1, Kosaraju.run(g, new Metrics()).count);
        assertEquals(1, Tarjan.run(g, new Metrics()).count);
    }

    @Test
    void criticalPathOnCondensation() throws IOException {
        Graph g = GraphIO.read(Path.of("data/cyclic.txt"));
        Kosaraju.Result r = Kosaraju.run(g, new Metrics());
        Condensation.Result c = Condensation.criticalPath(g, r.component, r.count);
        assertEquals(2 + 4, c.longestPath);
    }
}
