package daa.graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GraphIO {
    private GraphIO() {
    }

    public static Graph read(Path path) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(path)) {
            return read(br);
        }
    }

    public static Graph parse(String text) throws IOException {
        try (BufferedReader br = new BufferedReader(new StringReader(text))) {
            return read(br);
        }
    }

    private static Graph read(BufferedReader br) throws IOException {
        String line = nextLine(br);
        if (line == null) {
            throw new IOException("empty input");
        }
        String[] head = line.split("\\s+");
        if (head.length != 3) {
            throw new IOException("header must be: n m directed|undirected");
        }
        int n = Integer.parseInt(head[0]);
        int m = Integer.parseInt(head[1]);
        boolean directed;
        if (head[2].equals("directed")) {
            directed = true;
        } else if (head[2].equals("undirected")) {
            directed = false;
        } else {
            throw new IOException("unknown graph type: " + head[2]);
        }
        Graph g = new Graph(n, directed);
        for (int i = 0; i < m; i++) {
            line = nextLine(br);
            if (line == null) {
                throw new IOException("expected " + m + " edges, found " + i);
            }
            String[] p = line.split("\\s+");
            if (p.length != 3) {
                throw new IOException("bad edge line: " + line);
            }
            g.addEdge(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
        }
        return g;
    }

    private static String nextLine(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) {
                return line;
            }
        }
        return null;
    }
}
