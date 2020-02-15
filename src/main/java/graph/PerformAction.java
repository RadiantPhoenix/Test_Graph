package graph;

@FunctionalInterface
public interface PerformAction<V extends Vertex> {
    public String doSimpleAction(V v);
}