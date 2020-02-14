package graph;

import java.util.List;
import java.util.Map;

public class DirectedGraph extends Graph {

    @Override
    public void addEdge(String label1, String label2) throws Exception {
        try {
            Vertex v1 = new Vertex(label1);
            Vertex v2 = new Vertex(label2);
            WeightedEdge w = new WeightedEdge(v2);

            lock.writeLock().lock();
            super.getVerticesArray().get(v1).add(w);
        } catch (Exception e) {
            throw new Exception("Label " + label1 + " or " + label2 + " not found");
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void addEdge(String label1, String label2, int weight) throws Exception {
        try {

            Vertex v1 = new Vertex(label1);
            Vertex v2 = new Vertex(label2);
            WeightedEdge w = new WeightedEdge(v2, weight);
            lock.writeLock().lock();
            super.getVerticesArray().get(v1).add(w);
        } catch (Exception e) {
            throw new Exception("Label " + label1 + " or " + label2 + " not found");
        } finally {
            lock.writeLock().unlock();
        }
    }


    @Override
    public void removeEdge(String label1, String label2) throws Exception  {
        try {
            Vertex v1 = new Vertex(label1);
            Vertex v2 = new Vertex(label2);

            lock.writeLock().lock();
            List<WeightedEdge> eV1 = super.getVerticesArray().get(v1);
            eV1.removeIf((WeightedEdge w)-> v2.equals(w.getVertex()));

        } catch (Exception e) {
            throw new Exception("Label " + label1 + " or " + label2 + " not found");
        } finally {
            lock.writeLock().unlock();
        }
    }

}
