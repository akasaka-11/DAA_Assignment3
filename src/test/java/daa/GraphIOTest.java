package daa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import daa.graph.Graph;
import daa.graph.GraphIO;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class GraphIOTest {
    @Test
    void readsFilesFromDataDirectory() throws IOException {
        Graph d = GraphIO.read(Path.of("data/disconnected.txt"));
        assertEquals(8, d.n());
        assertEquals(6, d.m());
        assertFalse(d.isDirected());
        Graph c = GraphIO.read(Path.of("data/cyclic.txt"));
        assertTrue(c.isDirected());
        assertEquals(9, c.m());
    }

    @Test
    void undirectedEdgeAppearsInBothLists() throws IOException {
        Graph g = GraphIO.parse("3 1 undirected\n0 2 5\n");
        assertEquals(1, g.adj(0).size());
        assertEquals(1, g.adj(2).size());
        assertEquals(0, g.adj(1).size());
    }

    @Test
    void rejectsBadInput() {
        assertThrows(IOException.class, () -> GraphIO.parse(""));
        assertThrows(IOException.class, () -> GraphIO.parse("2 1 sideways\n0 1 1\n"));
        assertThrows(IOException.class, () -> GraphIO.parse("2 2 directed\n0 1 1\n"));
        assertThrows(IllegalArgumentException.class, () -> GraphIO.parse("2 1 directed\n0 5 1\n"));
    }
}
