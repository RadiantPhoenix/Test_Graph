package graph;

import javafx.util.Pair;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class ImplementationTest {

    public static void fillGraph(Graph graph) {
        graph.addVertex("Moscow");
        graph.addVertex("St Petersburg");
        graph.addVertex("Novosibirsk");
        graph.addVertex("Nizhny Novgorod");
        graph.addVertex("Samara");
        graph.addVertex("Magadan");
        graph.removeVertex("Magadan");
        try {
            graph.addEdge("Moscow", "St Petersburg");
            graph.addEdge("Moscow", "Novosibirsk");
            graph.removeEdge("Moscow", "Novosibirsk");
            graph.addEdge("Moscow", "Novosibirsk", 5);
            graph.addEdge("Novosibirsk", "St Petersburg");
            graph.addEdge("St Petersburg", "Nizhny Novgorod");
            graph.addEdge("Nizhny Novgorod", "Samara");
            graph.addEdge("Samara", "Novosibirsk");
        } catch (WrongLabelException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void checkGettingRouteForNonDirectedGraph() {
        Graph graph = new Graph();
        fillGraph(graph);
        String startNode = "Moscow";
        String endNode = "Novosibirsk";

        String resultString = graph.getRoute(startNode, endNode);

        assertEquals("Moscow St Petersburg Novosibirsk ", resultString);
    }

    @Test
    public void checkGettingRouteForDirectedGraph() {
        Graph directedGraph = new DirectedGraph();
        fillGraph(directedGraph);
        String startNode = "Moscow";
        String endNode = "Novosibirsk";
        String resultString = directedGraph.getRoute(startNode, endNode);

        assertEquals("Moscow St Petersburg Nizhny Novgorod Samara Novosibirsk ", resultString);
    }

    @Test
    public void checkGettingAllRoutesForNonDirectedGraph() {
        Graph graph = new Graph();
        fillGraph(graph);
        String startNode = "Moscow";
        String endNode = "Novosibirsk";
        List<String> resultString = graph.getRoutesSortedByCost(startNode, endNode);

        assertEquals("The route between Moscow and Novosibirsk is: Moscow St Petersburg Novosibirsk  Its cost is: 2.0",
                resultString.get(0));
        assertEquals("The route between Moscow and Novosibirsk is: Moscow St Petersburg Nizhny Novgorod Samara Novosibirsk  Its cost is: 4.0", resultString.get(1));
        assertEquals("The route between Moscow and Novosibirsk is: Moscow Novosibirsk  Its cost is: 5.0", resultString.get(2));
    }

    @Test
    public void checkGettingAllRoutesForDirectedGraph() {
        Graph directedGraph = new DirectedGraph();
        fillGraph(directedGraph);
        String startNode = "Moscow";
        String endNode = "Novosibirsk";
        List<String> resultString = directedGraph.getRoutesSortedByCost(startNode, endNode);

        assertEquals("The route between Moscow and Novosibirsk is: Moscow St Petersburg Nizhny Novgorod Samara Novosibirsk  Its cost is: 4.0",
                resultString.get(0));
        assertEquals("The route between Moscow and Novosibirsk is: Moscow Novosibirsk  Its cost is: 5.0", resultString.get(1));
    }

    @Test
    public void checkGettingShortestRouteForNonDirectedGraph() {
        Graph graph = new Graph();
        fillGraph(graph);
        String startNode = "Moscow";
        String endNode = "Novosibirsk";
        String resultString = graph.getShortestRoute(startNode, endNode);

        assertEquals("The shortest route between Moscow and Novosibirsk is: Moscow St Petersburg Novosibirsk  Its cost is: 2.0", resultString);
    }

    @Test
    public void checkGettingShortestRouteForDirectedGraph() {
        Graph directedGraph = new DirectedGraph();
        fillGraph(directedGraph);
        String startNode = "Moscow";
        String endNode = "Novosibirsk";
        String resultString = directedGraph.getShortestRoute(startNode, endNode);

        assertEquals("The shortest route between Moscow and Novosibirsk is: Moscow St Petersburg Nizhny Novgorod Samara Novosibirsk  Its cost is: 4.0", resultString);
    }


    @Test
    public void applyFunctionToAllVerticesInGraph() {
        Graph graph = new Graph();
        fillGraph(graph);

        String result = graph.depthFirstTraversalWithAction("Moscow", graph::getNumberOfEdges);

        assertEquals("Vertex Moscow has number of edges=2 Vertex Novosibirsk has number of edges=3 Vertex Samara has number of edges=2 Vertex Nizhny Novgorod has number of edges=2 Vertex St Petersburg has number of edges=3 ", result);

        StringBuilder resultString = new StringBuilder();

        for(Map.Entry<Vertex, List<WeightedEdge>> entry : graph.getVerticesArray().entrySet()) {
            resultString.append(entry.getKey().getLabel()).append(" ");
        }

        assertEquals("St Petersburg Novosibirsk Nizhny Novgorod Samara Moscow ", resultString.toString());

    }

    private static class TestThread implements Runnable {
        protected String res;

        public String getRes() {
            return res;
        }

        @Override
        public void run() {
        }
    }

    public static String countRoute(Graph graph) {
        String startNode = "Moscow";
        String endNode = "Novosibirsk";

        return graph.getShortestRoute(startNode, endNode);
    }

    @Test
    public void checkThreadNotSafeBehavior() {
        Graph graph = new Graph();
        fillGraph(graph);

        TestThread testThread = new TestThread(){
            @Override
            public void run() {
                res = countRoute(graph);
            }
        };

        String res1 = countRoute(graph);

        Thread t = new Thread(testThread);
        t.start();

        try {
            TimeUnit.MILLISECONDS.sleep(20);
            graph.removeEdge("St Petersburg","Novosibirsk");
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException ignored) {
        }

        String res2 = testThread.getRes();
        assertNotEquals(res1, res2);
    }

    @Test
    public void checkThreadSafeBehavior() {
        Graph directedGraph = new DirectedGraph();
        fillGraph(directedGraph);

        TestThread testThread = new TestThread(){
            @Override
            public void run() {
                res = countRoute(directedGraph);
            }
        };

        String res1 = countRoute(directedGraph);

        Thread t = new Thread(testThread);

        t.start();
        try {
            TimeUnit.MILLISECONDS.sleep(20);
            directedGraph.removeEdge("Samara","Novosibirsk");
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException ignored) {
        }
        String res2 = testThread.getRes();
        assertEquals(res1, res2);

    }
}
