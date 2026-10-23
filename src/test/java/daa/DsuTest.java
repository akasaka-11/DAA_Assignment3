package daa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import daa.algorithms.Dsu;
import java.util.Random;
import org.junit.jupiter.api.Test;

class DsuTest {
    private static int refFind(int[] label, int x) {
        return label[x];
    }

    private static void refUnion(int[] label, int x, int y) {
        int a = label[x];
        int b = label[y];
        if (a == b) {
            return;
        }
        for (int i = 0; i < label.length; i++) {
            if (label[i] == b) {
                label[i] = a;
            }
        }
    }

    private static int countSets(int[] label) {
        return (int) java.util.Arrays.stream(label).distinct().count();
    }

    @Test
    void agreesWithReferenceAfterRandomUnions() {
        Random rnd = new Random(7);
        for (int trial = 0; trial < 50; trial++) {
            int n = 1 + rnd.nextInt(200);
            Dsu dsu = new Dsu(n);
            int[] label = new int[n];
            for (int i = 0; i < n; i++) {
                label[i] = i;
            }
            int ops = rnd.nextInt(3 * n);
            for (int k = 0; k < ops; k++) {
                int x = rnd.nextInt(n);
                int y = rnd.nextInt(n);
                boolean merged = dsu.union(x, y);
                assertEquals(refFind(label, x) != refFind(label, y), merged);
                refUnion(label, x, y);
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j += 7) {
                    assertEquals(refFind(label, i) == refFind(label, j), dsu.find(i) == dsu.find(j));
                }
            }
            assertEquals(countSets(label), dsu.components());
        }
    }

    @Test
    void countersTrackFindsAndSuccessfulUnions() {
        Dsu dsu = new Dsu(5);
        dsu.union(0, 1);
        dsu.union(0, 1);
        dsu.union(2, 3);
        dsu.find(0);
        dsu.find(4);
        assertEquals(2, dsu.unionCalls());
        assertEquals(2, dsu.findCalls());
        assertEquals(3, dsu.components());
    }

    @Test
    void longChainStaysFast() {
        int n = 200000;
        Dsu dsu = new Dsu(n);
        for (int i = 1; i < n; i++) {
            dsu.union(i - 1, i);
        }
        assertEquals(1, dsu.components());
        int r = dsu.find(0);
        for (int i = 0; i < n; i += 997) {
            assertTrue(dsu.find(i) == r);
        }
    }

    @Test
    void makeSetResets() {
        Dsu dsu = new Dsu(3);
        dsu.union(0, 1);
        dsu.makeSet(10);
        assertEquals(10, dsu.components());
        assertEquals(0, dsu.unionCalls());
    }
}
