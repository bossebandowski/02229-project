import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import com.sun.org.apache.xerces.internal.impl.xpath.XPath;

import java.util.*;

import java.util.Arrays;
import java.util.List;

public abstract class MetaHeuristic {

    private Architecture a;

    public MetaHeuristic(Architecture a) {
        this.a = a;
    }

    public int[][] initializeOverlapGraph(List<List<Integer>> solutions) {
        //Select route in current solution

        int[][] graph = new int[a.getGraph().length][a.getGraph()[0].length];

        //generate graph with overlap counter

        for (int row = 0; row < a.getGraph().length; row++){
            for (int column = 0; column < a.getGraph()[row].length; column++){
                if (a.getGraph()[row][column] != -1){
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

    public List<List<Integer>> generateNeighborhood(List<List<Integer>> solutions, Integer numPaths){
        //Generates a neighborhood for a random
        int[][] overlapGraph = initializeOverlapGraph(solutions);

        //Picks random route in solution list to work on
        int ranSolutionIndex = new Random().nextInt(solutions.size());
        List<Integer> shortestPath = solutions.get(ranSolutionIndex);


        Node src = a.getNodes().get(shortestPath.get(0));
        Node dest = a.getNodes().get(shortestPath.get(shortestPath.size()-1));

        List<List<Integer>> shortestPaths  = new ArrayList<>(numPaths);
        for (int i = 0; i < numPaths; i++){

            List<Integer> shortPath = BFS(src, dest, overlapGraph);

            for (int node = 0; node < shortPath.size()-1; node++){
                overlapGraph[shortPath.get(node)][shortPath.get(node+1)] += 1;
            }
            shortestPaths.add(shortPath);
        }

        //Choose a random path in the generated shortestPaths
        int ranIndex = new Random().nextInt(numPaths);
        List<Integer> newSolution = shortestPaths.get(ranIndex);

        //Replace the random route with the new solution
        solutions.set(ranSolutionIndex,newSolution);

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



    float calculateCostFunction(List<List<Integer>> solution) {
        float result = 0.0f;

        final float bandwidthCoeff = 0.0f;
        final float rlCoeff = 0.0f;
        final float totalLengthCoeff = 0.0f;
        final float deadlineCoeff = 0.0f;
        final float overlappingCoeff = 0.0f;

        // Calculate free bandwidth of each link
        ArrayList<Link> linkBuffer = this.a.getLinks();
        HashMap<Link,Float> linkCapacity = new HashMap<Link,Float>();
        for(Link link:linkBuffer)
        {
            linkCapacity.put(link, link.getSpeed());
        }
        for (Integer streamID = 0; streamID < solution.size(); streamID++)
        {
            for(Integer nodeID = 0; nodeID < solution.get(streamID).size(); nodeID++)
            {
                if(nodeID > 0)
                {
                    Link currentLink = this.a.getLink(solution.get(streamID).get(nodeID - 1),solution.get(streamID).get(nodeID));
                    linkCapacity.put(currentLink, linkCapacity.get(currentLink) - (this.a.getStreambyID(streamID).getSize()
                            / this.a.getStreambyID(streamID).getPeriod()));
                }
            }
        }
        for(Link link:linkCapacity.keySet())
        {
            result += linkCapacity.get(link) * bandwidthCoeff;
        }



        // Calculate overlapping (number of common links of Streams with common start and end Node)
        HashMap<Integer,Integer> streamOverlaps = new HashMap<Integer,Integer>();
        for(Stream currentStream:this.a.getStreams())
        {
            streamOverlaps.put(currentStream.getId(),0);
        }
        ArrayList<Stream> streams = this.a.getStreams();
        int k = 0;
        for(int i = 0; i < streams.size(); i++)
        {
            Stream currentStream = streams.get(i);
            ArrayList<Integer> usedLinks = new ArrayList<Integer>();
            int currentRl = currentStream.getRl();
            int currentStreamStart = k;
            for(; k < currentStreamStart + currentRl; k++)
            {
                for(int j = 0; j < solution.get(k).size(); j++)
                {
                    if(!usedLinks.contains(solution.get(k).get(j)))
                    {
                        usedLinks.add(solution.get(k).get(j));
                    }
                    else
                    {
                        streamOverlaps.put(i,streamOverlaps.get(i) + 1);
                    }
                }
            }
            result -= overlappingCoeff * streamOverlaps.get(i);
        }

        // Calculate total length of routes
        ArrayList<Integer> lengths = new ArrayList<Integer>();
        for (Integer streamID = 0; streamID < solution.size(); streamID++)
        {
            lengths.add(solution.get(streamID).size());
            // Todo: How is the length affects the cost function
        }




        return  result;
    }

    public boolean isViable(List<List<Integer>> solution) {
        return this.deadlinesOk(solution);
    }

    private void calculateMaxLinkDelays(List<List<Integer>> solution) {
        float[] dataTransferred = new float[this.a.getLinks().size()];
        Arrays.fill(dataTransferred, 0f);
        int idx = 0;

        for (Stream s : this.a.getStreams()) {
            int size = s.getSize();
            for (int rep = 0; rep < s.getRl(); rep++) {
                List<Integer> route = solution.get(idx);

                int parent = route.get(0);
                int child;
                // get all links. calculate time it takes to pass the link and add to t
                for (int rId = 1; rId < route.size(); rId++) {
                    child = route.get(rId);
                    int linkId = this.a.getGraph()[parent][child];
                    dataTransferred[linkId] += size;
                    parent = child;
                }
                idx++;
            }
        }

        for (int i = 0; i < dataTransferred.length; i++) {
            Link l = this.a.getLinks().get(i);
            l.setC(dataTransferred[i]/l.getSpeed());
        }
    }

    private boolean deadlinesOk(List<List<Integer>> solution) {
        this.calculateMaxLinkDelays(solution);

        int idx = 0;
        // iterate over all streams
        for (Stream s : this.a.getStreams()) {
            // get stream deadline
            float deadline = (float) s.getDeadline();
            // iterate over all routes associated with the stream
            for (int rep = 0; rep < s.getRl(); rep++) {
                float t = 0f;
                List<Integer> route = solution.get(idx);
                int parent = route.get(0);
                int child;
                // get all links. calculate time it takes to pass the link and add to t
                for (int rId = 1; rId < route.size(); rId++) {
                    child = route.get(rId);
                    Link l = this.a.getLinks().get(this.a.getGraph()[parent][child]);
                    t += l.getC();
                    parent = child;
                }

                // if t is greater than the stream's deadline, then the check has been failed
                if (t > deadline) return false;
                // check next route
                idx++;
            }
        }
        return true;
    }
}
