package graph;

public class WeightedEdge {
    private Vertex vertex;
    private double weight;

    WeightedEdge(Vertex vertex) {
        this.vertex = vertex;
        this.weight = 1;
    }

    WeightedEdge(Vertex vertex, double  weight) {
        this.vertex = vertex;
        this.weight = weight;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public double getWeight() {
        return weight;
    }

}
