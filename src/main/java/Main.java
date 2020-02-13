package main.java;

import java.util.Iterator;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        Graph graph = new Graph();
        graph = fillGraph(graph);

        try {
            graph.depthFirstTraversal("Moscow", (Vertex v)->{ System.out.println("Connected city="+v.getLabel());} );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Graph directedGraph = new DirectedGraph();
        directedGraph = fillGraph(directedGraph);

        try {
            directedGraph.depthFirstTraversal("Moscow", (Vertex v)->{ System.out.println("Linked city="+v.getLabel());} );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String startNode = "Moscow";
        String endNode = "Novosibirsk";

        try {
            Set<Vertex> route = graph.getRoute(startNode, endNode);
            if (route != null) {
                System.out.print("The route between " + startNode + " and " + endNode + " is: ");
                route.forEach((Vertex v)->{System.out.print(v.getLabel()+" ");});
                System.out.println();
            } else {
                System.out.println("There is no route between " + startNode + " and " + endNode);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            Set<Vertex> route = directedGraph.getRoute(startNode, endNode);
            if (route != null) {
                System.out.print("The route between " + startNode + " and " + endNode + " is: ");
                route.forEach((Vertex v)->{System.out.print(v.getLabel()+" ");});
            } else {
                System.out.println("There is no route between " + startNode + " and " + endNode);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static Graph fillGraph(Graph graph) {

        graph.addVertex("Moscow");
        graph.addVertex("St Petersburg");
        graph.addVertex("Novosibirsk");
        graph.addVertex("Nizhny Novgorod");
        graph.addVertex("Samara");
        graph.addVertex("Magadan");
        try {
            graph.addEdge("Moscow", "St Petersburg");
      //      graph.addEdge("Moscow", "Novosibirsk");
            graph.addEdge("Novosibirsk", "St Petersburg");
            graph.addEdge("St Petersburg", "Nizhny Novgorod");
            graph.addEdge("Nizhny Novgorod", "Samara");
            graph.addEdge("Samara", "Novosibirsk");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return graph;
    }
}
