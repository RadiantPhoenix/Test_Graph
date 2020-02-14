package graph;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        Graph graph = new Graph();
        graph = fillGraph(graph);

        try {
            graph.depthFirstTraversalWithAction("Moscow", (Vertex v)->{ System.out.println("Connected city="+v.getLabel());} );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Graph directedGraph = new DirectedGraph();
        directedGraph = fillGraph(directedGraph);

        try {
            directedGraph.depthFirstTraversalWithAction("Moscow", (Vertex v)->{ System.out.println("Linked city="+v.getLabel());} );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String startNode = "Moscow";
        String endNode = "Novosibirsk";

        try {
       //     graph.removeEdge(startNode,"St Petersburg");
      //      graph.removeVertex("Samara");
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
            List<Map.Entry<Set<WeightedEdge>, Double>> routesList = graph.getRoutesSortedByCost(startNode, endNode);
            if (routesList != null) {
                routesList.forEach((Map.Entry<Set<WeightedEdge>, Double> route)->{
                        System.out.print("The route between " + startNode + " and " + endNode + " is: " + startNode + " ");
                        route.getKey().forEach((WeightedEdge w)->{System.out.print(w.getVertex().getLabel()+" ");});
                        System.out.print(" Its cost is: " + route.getValue());
                        System.out.println();
                    }
                );
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
                System.out.println();
            } else {
                System.out.println("There is no route between " + startNode + " and " + endNode);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            directedGraph.removeEdge(startNode,"St Petersburg");
            Set<Vertex> route = directedGraph.getRoute(startNode, endNode);
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
    }

    public static Graph fillGraph(Graph graph) {
        try {
            graph.addVertex("Moscow");
            graph.addVertex("St Petersburg");
            graph.addVertex("Novosibirsk");
            graph.addVertex("Nizhny Novgorod");
            graph.addVertex("Samara");
            graph.addVertex("Magadan");

            graph.addEdge("Moscow", "St Petersburg");
            graph.addEdge("Moscow", "Novosibirsk", 5);
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
