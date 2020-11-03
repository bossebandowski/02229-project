import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MetaHeuristic {


    public int[][] initializeOverlapGraph(List<List<Integer>> solutions, Architecture architecture) {
        //Select route in current solution

        int[][] graph = new int[architecture.getGraph().length][architecture.getGraph()[0].length];

        //generate graph with overlap counter

        for (int row = 0; row < architecture.getGraph().length; row++){
            for (int column = 0; column < architecture.getGraph()[row].length; column++){
                if (architecture.getGraph()[row][column] != -1){
                    graph[row][column]=0;
                }else{ graph[row][column]=-1;}
            }
        }

        for (List<Integer> route : solutions){
            for (int node = 0; node < route.size()-1;node++){
                graph[route.get(node)][route.get(node+1)] +=1;
            }
        }

        return graph;
    }

    public List<List<Integer>> generateNeighborhood(List<List<Integer>> solutions, Architecture architecture, Integer numPaths){
        int[][] overlapGraph = initializeOverlapGraph(solutions,architecture);

        //Generate neighborhood to route at index 0
        List<Integer> shortestPath = solutions.get(0);


        Node src = architecture.getNodes().get(shortestPath.get(0));
        Node dest = architecture.getNodes().get(shortestPath.get(shortestPath.size()-1));

        List<List<Integer>> shortestPaths  = new ArrayList<>(numPaths);
        for (int i = 0; i < numPaths; i++){

            List<Integer> shortPath = BFS(src, dest, overlapGraph);

            for (int node = 0; node < shortPath.size()-1; node++){
                overlapGraph[shortPath.get(node)][shortPath.get(node+1)] += 1;
            }
            shortestPaths.add(shortPath);
        }

        return shortestPaths;
    }

    public ArrayList<Integer> BFS(Node src, Node dest, int[][] graph) {

        int numNodes = graph.length;
        ArrayList<Integer> route = new ArrayList<>();
        route = new ArrayList<>();
        // keep track of shortest path between src node and other nodes
        float[] shortest_path_time = new float[numNodes];
        Arrays.fill(shortest_path_time, Float.MAX_VALUE);
        shortest_path_time[src.getId()] = 0f;
        // keep track of node parents in shortest path
        int[] parent_ids = new int[numNodes];
        Arrays.fill(parent_ids, -1);
        LinkedList<Node> queue = new LinkedList<>();
        queue.add(src);

        // find shortest path
        while (!queue.isEmpty()) {
            Node n = queue.pop();

            for (Node child : n.getChildren()) {

                if(shortest_path_time[n.getId()] + graph[n.getId()][child.getId()] < shortest_path_time[child.getId()]) {
                    queue.add(child);
                    shortest_path_time[child.getId()] = shortest_path_time[n.getId()] + graph[n.getId()][child.getId()];
                    parent_ids[child.getId()] = n.getId();


                }
            }
        }

        // reverse order
        route.add(0, dest.getId());
        int parent_id = parent_ids[dest.getId()];
        while (!(parent_id == src.getId())) {
            route.add(0, parent_id);
            parent_id = parent_ids[parent_id];
        }
        route.add(0, src.getId());

        return route;
    }

    public void printGraph( int[][] graph){
        System.out.println("NEW GRAPH");
        for (int row = 0; row < graph.length; row++){

            for (int column = 0; column < graph[row].length; column++){
                System.out.print(graph[row][column] + " ");
            }
            System.out.println(" ---------- " + row  );


        }
    }

}
