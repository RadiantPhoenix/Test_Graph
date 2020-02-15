package graph;

import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Graph {
    private Map<Vertex, List<WeightedEdge>> verticesArray;
    public ReadWriteLock lock = new ReentrantReadWriteLock();

    public Graph() {
        this.verticesArray = new HashMap<>();
    }

    protected Map<Vertex, List<WeightedEdge>> getVerticesArray() {
        return verticesArray;
    }

    public void addVertex(String label) {
            lock.writeLock().lock();
            verticesArray.putIfAbsent(new Vertex(label), new ArrayList<>());
            lock.writeLock().unlock();
    }

    public void removeVertex(String label)  {
        Vertex v = new Vertex(label);
        lock.writeLock().lock();
        verticesArray.values().forEach(e -> e.removeIf(w  -> v.equals(w.getVertex())));
        verticesArray.remove(new Vertex(label));

        lock.writeLock().unlock();
    }

    public void addEdge(String label1, String label2) throws WrongLabelException  {
        try {
            Vertex v1 = new Vertex(label1);
            Vertex v2 = new Vertex(label2);
            WeightedEdge w1 = new WeightedEdge(v1);
            WeightedEdge w2 = new WeightedEdge(v2);

            lock.writeLock().lock();
            verticesArray.get(v1).add(w2);
            verticesArray.get(v2).add(w1);
        } catch (NullPointerException e) {
            throw new WrongLabelException("Label " + label1 + " or " + label2 + " not found");
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addEdge(String label1, String label2, double weight) throws WrongLabelException {
        try {
            Vertex v1 = new Vertex(label1);
            Vertex v2 = new Vertex(label2);
            WeightedEdge w1 = new WeightedEdge(v1, weight);
            WeightedEdge w2 = new WeightedEdge(v2, weight);

            lock.writeLock().lock();
            verticesArray.get(v1).add(w2);
            verticesArray.get(v2).add(w1);
        } catch (NullPointerException e) {
            throw new WrongLabelException("Label " + label1 + " or " + label2 + " not found");
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Thread not safe method
    public void removeEdge(String label1, String label2) throws WrongLabelException  {
        try {
            Vertex v1 = new Vertex(label1);
            Vertex v2 = new Vertex(label2);
            List<WeightedEdge> eV1 = verticesArray.get(v1);
            List<WeightedEdge> eV2 = verticesArray.get(v2);

   //         lock.writeLock().lock();
            eV1.removeIf(w -> v2.equals(w.getVertex()));
            eV2.removeIf(w -> v1.equals(w.getVertex()));

        } catch (NullPointerException e) {
            throw new WrongLabelException("Label " + label1 + " or " + label2 + " not found");
        } finally {
   //         lock.writeLock().unlock();
        }
    }

    private List<WeightedEdge> getLinkedVertices(Vertex currVertex) throws WrongLabelException {
        if (verticesArray.get(currVertex) != null) {
            return verticesArray.get(currVertex);
        } else {
            throw new WrongLabelException("Label " + currVertex.getLabel() + " not found");
        }
    }

    public String getNumberOfEdges(Vertex currVertex) throws WrongLabelException {
        if (verticesArray.get(currVertex) != null) {
            return "number of edges=" + verticesArray.get(currVertex).size();
        } else {
            throw new WrongLabelException("Label " + currVertex.getLabel() + " not found");
        }
    }


    public String depthFirstTraversalWithAction(String root, PerformAction<Vertex> action) {
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

            StringBuilder funcResult = new StringBuilder();
            for (Vertex vertex : visited) {
                funcResult.append("Vertex ").append(vertex.getLabel()).append(" has ").append(action.doSimpleAction(vertex)).append(" ");
            }

            return funcResult.toString();
        } catch (WrongLabelException e) {
            return e.getMessage();
        } catch (NullPointerException e) {
            return "Label " + root + " not found";
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getRoute(String startNode, String endNode) {
        StringBuilder resultString = new StringBuilder();
        List<Vertex> visited = new ArrayList<>();
        List<Vertex> route = new ArrayList<Vertex>();

        try {
            lock.readLock().lock();
            boolean res = getRouteStep(new Vertex(startNode), new Vertex(endNode), visited, route);

            if (res) {
                route.forEach(v -> resultString.append(v.getLabel()).append(" "));
                return resultString.toString();
            } else {
                return null;
            }
        } catch (WrongLabelException e) {
            return e.getMessage();
        } catch (NullPointerException e) {
            return "Label " + startNode + " or " + endNode + " not found";
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean getRouteStep(Vertex currNode,
                                 Vertex endNode,
                                 List<Vertex> visited,
                                 List<Vertex> route) throws WrongLabelException {
        try {
            visited.add(currNode);
            route.add(currNode);
            if (!endNode.equals(currNode)) {
                for (WeightedEdge w : getLinkedVertices(currNode)) {
                    if (!visited.contains(w.getVertex())) {
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
        } catch (NullPointerException e) {
            throw new WrongLabelException("Label " + currNode.getLabel() + " has a bad link to non-existing node");
        }
    }

    public List<String> getRoutesSortedByCost(String startNode, String endNode) {
        List<String> results = new ArrayList<>();
        Set<Vertex> visited = new LinkedHashSet<Vertex>();
        ArrayList<Set<WeightedEdge>> allRoutes = new ArrayList<>();
        Set<WeightedEdge> route = new LinkedHashSet<>();
        Map<Set<WeightedEdge>, Double> routesWithCost = new HashMap<>();

        try {
            lock.readLock().lock();
            getAllRouteStep(new Vertex(startNode), new Vertex(endNode), visited, route, allRoutes);

            allRoutes.forEach(oneRoute -> {
                    double routeCost = 0;
                    for(WeightedEdge w:oneRoute) {
                        routeCost += w.getWeight();
                    }
                    routesWithCost.put(oneRoute, routeCost);
                }
            );

            List<Map.Entry<Set<WeightedEdge>, Double>> sortList = new ArrayList<>(routesWithCost.entrySet());
            sortList.sort(Map.Entry.comparingByValue());

            sortList.forEach((Map.Entry<Set<WeightedEdge>, Double> oneRoute) -> {
                StringBuilder resultString = new StringBuilder();
                resultString.append("The route between ").append(startNode).append(" and ").append(endNode).append(" is: ").append(startNode).append(" ");
                oneRoute.getKey().forEach(w -> resultString.append(w.getVertex().getLabel()).append(" "));
                resultString.append(" Its cost is: ").append(oneRoute.getValue());
                results.add(resultString.toString());
            });

            return results;
        } catch (WrongLabelException e) {
            return Collections.singletonList(e.getMessage());
        } catch (NullPointerException e) {
            return Collections.singletonList("Label " + startNode + " or " + endNode + " not found");
        } finally {
            lock.readLock().unlock();
        }
    }

    private void getAllRouteStep(Vertex currNode,
                                 Vertex endNode,
                                 Set<Vertex> visited,
                                 Set<WeightedEdge> route,
                                 ArrayList<Set<WeightedEdge>> allRoutes) throws WrongLabelException {
        try {
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
        } catch (NullPointerException e) {
            throw new WrongLabelException("Label " + currNode.getLabel() + " has a bad link to non-existing node");
        }
    }

    // Dijkstra’s algorithm
    public String getShortestRoute(String startNode, String endNode) {
        StringBuilder resultString = new StringBuilder();
        Set<Vertex> visited = new LinkedHashSet<Vertex>();
        Map<Vertex, Double> vertexCost = new HashMap<>();
        Map<Vertex, Vertex> vertexVector = new HashMap<>();

        try {
            lock.readLock().lock();
            verticesArray.forEach((k, v)->{
                    vertexCost.put(k, Double.MAX_VALUE);
                    vertexVector.put(k, new Vertex(startNode));
                }
            );
            vertexCost.put(new Vertex(startNode), (double) 0);
            getShortestRouteStep(new Vertex(startNode), visited, vertexCost, vertexVector);

            if (vertexCost.get(new Vertex(endNode)) != Double.MAX_VALUE) {
                List<Vertex> route = new ArrayList<>();
                Vertex currVertex = new Vertex(endNode);
                Vertex startVertex = new Vertex(startNode);
                while (!currVertex.equals(startVertex)) {
                    route.add(currVertex);
                    currVertex = vertexVector.get(currVertex);
                }
                route.add(startVertex);

                resultString.append("The shortest route between ").append(startNode).append(" and ").append(endNode).append(" is: ");
                for (int i = route.size() - 1; i >= 0; i--) {
                    resultString.append(route.get(i).getLabel()).append(" ");
                }
                resultString.append(" Its cost is: ").append(vertexCost.get(new Vertex(endNode)));

            } else {
                resultString.append("There is no route between ").append(startNode).append(" and ").append(endNode);
            }

            return resultString.toString();

        } catch (WrongLabelException e) {
            return e.getMessage();
        } catch (NullPointerException e) {
            return "Label " + startNode + " or " + endNode + " not found";
        } finally {
            lock.readLock().unlock();
        }
    }

    private void getShortestRouteStep(Vertex currNode,
                                      Set<Vertex> visited,
                                      Map<Vertex, Double> vertexCost,
                                      Map<Vertex, Vertex> vertexVector) throws WrongLabelException{
        visited.add(currNode);
        try {
            TimeUnit.MILLISECONDS.sleep(10);

            for (WeightedEdge w : getLinkedVertices(currNode)) {
                if (!visited.contains(w.getVertex())) {
                    if (vertexCost.get(w.getVertex()) > vertexCost.get(currNode) + w.getWeight()) {
                        vertexCost.put(w.getVertex(), vertexCost.get(currNode) + w.getWeight());
                        vertexVector.put(w.getVertex(), currNode);
                    }
                    getShortestRouteStep(w.getVertex(), visited, vertexCost, vertexVector);
                }
            }


        } catch (NullPointerException e) {
            throw new WrongLabelException("Label " + currNode.getLabel() + " has a bad link to non-existing node");
        } catch (InterruptedException ignored) {
        }

    }

}
