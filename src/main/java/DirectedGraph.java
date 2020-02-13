package main.java;

import java.util.List;
import java.util.Map;

public class DirectedGraph extends Graph {

    @Override
    public void addEdge(String label1, String label2) throws Exception {
        try {
            Vertex v1 = new Vertex(label1);
            Vertex v2 = new Vertex(label2);
            WeightedEdge w = new WeightedEdge(v2);
            verticesArray.get(v1).add(w);
        } catch (Exception e) {
            throw new Exception("Label " + label1 + " or " + label2 + " not found");
        }
    }

    @Override
    public void addEdge(String label1, String label2, int weight) throws Exception {
        try {
            Vertex v1 = new Vertex(label1);
            Vertex v2 = new Vertex(label2);
            WeightedEdge w = new WeightedEdge(v2, weight);
            verticesArray.get(v1).add(w);
        } catch (Exception e) {
            throw new Exception("Label " + label1 + " or " + label2 + " not found");
        }
    }


    @Override
    public void removeEdge(String label1, String label2) throws Exception  {
        try {
            Vertex v1 = new Vertex(label1);
            Vertex v2 = new Vertex(label2);
            List<WeightedEdge> eV1 = verticesArray.get(v1);

            for (WeightedEdge w : eV1) {
                if (v2.equals(w.getVertex())) {
                    eV1.remove(w);
                }
            }

        } catch (Exception e) {
            throw new Exception("Label " + label1 + " or " + label2 + " not found");
        }
    }

}
