package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Graph {
    private Map<Vertex, List<WeightedEdge>> verticesArray;
    public ReadWriteLock lock = new ReentrantReadWriteLock();

    public Graph() {
        this.verticesArray = new HashMap<Vertex, List<WeightedEdge>>();
    }

    public Map<Vertex, List<WeightedEdge>> getVerticesArray() {
        return verticesArray;
    }

    public void setVerticesArray(Map<Vertex, List<WeightedEdge>> verticesArray) {
        this.verticesArray = verticesArray;
    }

    public void addVertex(String label) throws Exception {
        try {
            lock.writeLock().lock();
            verticesArray.putIfAbsent(new Vertex(label), new ArrayList<>());
        }  catch (Exception e) {
            throw new Exception("Label " + label + " not found");
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeVertex(String label) throws Exception {
        try {
            Vertex v = new Vertex(label);
            lock.writeLock().lock();
            verticesArray.values().stream().forEach(e -> {
                e.removeIf((WeightedEdge w) -> v.equals(w.getVertex()));
            });
            verticesArray.remove(new Vertex(label));
        } catch (Exception e) {
            throw new Exception("Label " + label + " not found");
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addEdge(String label1, String label2) throws Exception {
        try {
            Vertex v1 = new Vertex(label1);
            Vertex v2 = new Vertex(label2);
            WeightedEdge w1 = new WeightedEdge(v1);
            WeightedEdge w2 = new WeightedEdge(v2);

            lock.writeLock().lock();
            verticesArray.get(v1).add(w2);
            verticesArray.get(v2).add(w1);
        } catch (Exception e) {
            throw new Exception("Label " + label1 + " or " + label2 + " not found");
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addEdge(String label1, String label2, int weight) throws Exception {
        try {
            Vertex v1 = new Vertex(label1);
            Vertex v2 = new Vertex(label2);
            WeightedEdge w1 = new WeightedEdge(v1, weight);
            WeightedEdge w2 = new WeightedEdge(v2, weight);

            lock.writeLock().lock();
            verticesArray.get(v1).add(w2);
            verticesArray.get(v2).add(w1);
        } catch (Exception e) {
            throw new Exception("Label " + label1 + " or " + label2 + " not found");
        } finally {
            lock.writeLock().unlock();
        }
    }


    public void removeEdge(String label1, String label2) throws Exception  {
        try {
            Vertex v1 = new Vertex(label1);
            Vertex v2 = new Vertex(label2);
            List<WeightedEdge> eV1 = verticesArray.get(v1);
            List<WeightedEdge> eV2 = verticesArray.get(v2);

            lock.writeLock().lock();
            eV1.removeIf((WeightedEdge w)-> v2.equals(w.getVertex()));
            eV2.removeIf((WeightedEdge w)-> v1.equals(w.getVertex()));

        } catch (Exception e) {
            throw new Exception("Label " + label1 + " or " + label2 + " not found");
        } finally {
            lock.writeLock().unlock();
        }
    }

    private List<WeightedEdge> getLinkedVertices(Vertex currVertex) {
        return verticesArray.get(currVertex);
    }


    public void depthFirstTraversalWithAction(String root, PerformAction<Vertex> action) throws Exception {
        try {
            Set<Vertex> visited = new LinkedHashSet<Vertex>();
            Stack<Vertex> stack = new Stack<Vertex>();
            stack.push(new Vertex(root));
            lock.readLock().lock();
            while (!stack.isEmpty()) {
                Vertex vertex = stack.pop();
                if (!visited.contains(vertex)) {
                    visited.add(vertex);
                    for (WeightedEdge w : getLinkedVertices(vertex)) {
                        stack.push(w.getVertex());
                    }
                }
            }

            Iterator<Vertex> it = visited.iterator();
            while (it.hasNext()) {
                action.doSimpleAction(it.next());
            }

        } catch (Exception e) {
            throw new Exception("Label " + root + " not found");
        } finally {
            lock.readLock().unlock();
        }
    }

    public Set<Vertex> getRoute(String startNode, String endNode) throws Exception {
        try {
            Set<Vertex> visited = new LinkedHashSet<Vertex>();
            Set<Vertex> route = new LinkedHashSet<Vertex>();
            lock.readLock().lock();
            boolean res = getRouteStep(new Vertex(startNode), new Vertex(endNode), visited, route);

            if (res) {
                return route;
            } else {
                return null;
            }


        } catch (Exception e) {
            throw new Exception("Label " + startNode + " or " + endNode + " not found");
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean getRouteStep(Vertex currNode, Vertex endNode, Set<Vertex> visited, Set<Vertex> route){
        visited.add(currNode);

        if (!endNode.equals(currNode)) {
            for (WeightedEdge w : getLinkedVertices(currNode)) {
                if (!visited.contains(w.getVertex())) {
                    route.add(currNode);
                    boolean res = getRouteStep(w.getVertex(), endNode, visited, route);
                    if (res) {
                        return true;
                    }
                    route.remove(currNode);
                }
            }
        } else {
            return true;
        }
        return false;
    }

    public List<Map.Entry<Set<WeightedEdge>, Double>> getRoutesSortedByCost(String startNode, String endNode) throws Exception {
        try {
            Set<Vertex> visited = new LinkedHashSet<Vertex>();
            ArrayList<Set<WeightedEdge>> allRoutes = new ArrayList<>();
            Set<WeightedEdge> route = new LinkedHashSet<>();

            lock.readLock().lock();
            getAllRouteStep(new Vertex(startNode), new Vertex(endNode), visited, route, allRoutes);

            Map<Set<WeightedEdge>, Double> routesWithCost = new HashMap<>();

            allRoutes.forEach((Set<WeightedEdge> oneRoute)->{
                    Double routeCost = new Double(0);
                    for(WeightedEdge w:oneRoute) {
                        routeCost += w.getWeight();
                    }
                    routesWithCost.put(oneRoute, routeCost);
                }
            );

            List<Map.Entry<Set<WeightedEdge>, Double>> sortList = new ArrayList<>(routesWithCost.entrySet());
            sortList.sort(Map.Entry.comparingByValue());
            return sortList;
        } catch (Exception e) {
            throw new Exception("Label " + startNode + " or " + endNode + " not found");
        } finally {
            lock.readLock().unlock();
        }
    }

    private void getAllRouteStep(Vertex currNode, Vertex endNode, Set<Vertex> visited, Set<WeightedEdge> route, ArrayList<Set<WeightedEdge>> allRoutes){
        visited.add(currNode);

        if (!endNode.equals(currNode)) {
            for (WeightedEdge w : getLinkedVertices(currNode)) {
                if (!visited.contains(w.getVertex())) {
                    route.add(w);
                    getAllRouteStep(w.getVertex(), endNode, visited, route, allRoutes);
                    route.remove(w);
                }
            }
        } else {
            Set<WeightedEdge> savedRoute = new LinkedHashSet<WeightedEdge>(route);
            allRoutes.add(savedRoute);
            visited.remove(currNode);
        }

        visited.remove(currNode);
    }
}
