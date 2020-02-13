package main.java;

import java.util.List;
import java.util.Map;

public class DirectedGraph extends Graph {

    @Override
    public void addEdge(String label1, String label2) throws Exception {
        try {
            Vertex v1 = new Vertex(label1);
            Vertex v2 = new Vertex(label2);
            verticesArray.get(v1).add(v2);
        } catch (Exception e) {
            throw new Exception("Label " + label1 + " or " + label2 + " not found");
        }
    }

    @Override
    public void removeEdge(String label1, String label2) throws Exception  {
        try {
            Vertex v1 = new Vertex(label1);
            Vertex v2 = new Vertex(label2);
            List<Vertex> eV1 = verticesArray.get(v1);
            if (eV1 != null)
                eV1.remove(v2);
        } catch (Exception e) {
            throw new Exception("Label " + label1 + " or " + label2 + " not found");
        }
    }
}
