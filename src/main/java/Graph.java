package main.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class Graph {
    public Map<Vertex, List<WeightedEdge>> verticesArray;

    public Graph() {
        this.verticesArray = new HashMap<Vertex, List<WeightedEdge>>();
    }

    public void addVertex(String label) {
        verticesArray.putIfAbsent(new Vertex(label), new ArrayList<>());
    }

    public void removeVertex(String label) {
        Vertex v = new Vertex(label);
        verticesArray.values().stream().forEach(e -> {
            for (WeightedEdge w : e) {
                if (v.equals(w.getVertex())) {
                    e.remove(w);
                }
            }
        });
        verticesArray.remove(new Vertex(label));
    }

    public void addEdge(String label1, String label2) throws Exception {
        try {
            Vertex v1 = new Vertex(label1);
            Vertex v2 = new Vertex(label2);
            WeightedEdge w1 = new WeightedEdge(v1);
            WeightedEdge w2 = new WeightedEdge(v2);
            verticesArray.get(v1).add(w2);
            verticesArray.get(v2).add(w1);
        } catch (Exception e) {
            throw new Exception("Label " + label1 + " or " + label2 + " not found");
        }
    }

    public void addEdge(String label1, String label2, int weight) throws Exception {
        try {
            Vertex v1 = new Vertex(label1);
            Vertex v2 = new Vertex(label2);
            WeightedEdge w1 = new WeightedEdge(v1, weight);
            WeightedEdge w2 = new WeightedEdge(v2, weight);
            verticesArray.get(v1).add(w2);
            verticesArray.get(v2).add(w1);
        } catch (Exception e) {
            throw new Exception("Label " + label1 + " or " + label2 + " not found");
        }
    }


    public void removeEdge(String label1, String label2) throws Exception  {
        try {
            Vertex v1 = new Vertex(label1);
            Vertex v2 = new Vertex(label2);
            List<WeightedEdge> eV1 = verticesArray.get(v1);
            List<WeightedEdge> eV2 = verticesArray.get(v2);
            for (WeightedEdge w : eV1) {
                if (v2.equals(w.getVertex())) {
                    eV1.remove(w);
                }
            }

            for (WeightedEdge w : eV2) {
                if (v1.equals(w.getVertex())) {
                    eV2.remove(w);
                }
            }

        } catch (Exception e) {
            throw new Exception("Label " + label1 + " or " + label2 + " not found");
        }
    }

    public List<WeightedEdge> getLinkedVertices(Vertex currVertex) {
        return verticesArray.get(currVertex);
    }


    public void depthFirstTraversal(String root, PerformAction<Vertex> action) throws Exception {
        try {
            Set<Vertex> visited = new LinkedHashSet<Vertex>();
            Stack<Vertex> stack = new Stack<Vertex>();
            stack.push(new Vertex(root));
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
        }
    }

    public Set<Vertex> getRoute(String startNode, String endNode) throws Exception {
        try {
            Set<Vertex> visited = new LinkedHashSet<Vertex>();
            Set<Vertex> route = new LinkedHashSet<Vertex>();
            boolean res = getRouteStep(new Vertex(startNode), new Vertex(endNode), visited, route);

            if (res) {
                return route;
            } else {
                return null;
            }


         } catch (Exception e) {
            throw new Exception("Label " + startNode + " or " + endNode + " not found");
        }
    }

    private boolean getRouteStep(Vertex currNode, Vertex endNode, Set<Vertex> visited, Set<Vertex> route){
        if (!visited.contains(currNode)) {
            visited.add(currNode);
            route.add(currNode);

            if (!endNode.equals(currNode)) {

                for (WeightedEdge w : getLinkedVertices(currNode)) {
                    if (!visited.contains(w.getVertex())) {
                        boolean res = getRouteStep(w.getVertex(), endNode, visited, route);
                        if (res) {
                            return true;
                        }
                    }
                }

                route.remove(currNode);

            } else {
                return true;
            }


        }

        return false;

    }
}
