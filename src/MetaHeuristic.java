import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import java.util.*;

public abstract class MetaHeuristic {

    protected Architecture a;
    protected List<List<Integer>> bestSolution;

    public MetaHeuristic(Architecture a) {
        this.a = a;
    }

    public abstract void run(int runtimeSeconds);

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

    public List<List<Integer>> generateNeighborhood(List<List<Integer>> solution,Integer numPaths ){
        //Generates a neighborhood for a random
        int[][] overlapGraph = initializeOverlapGraph( solution);

        //Picks random route in solution list to work on
        int ranSolutionIndex = new Random().nextInt(solution.size());
        List<Integer> shortestPath = solution.get(ranSolutionIndex);
        // System.out.println(ranSolutionIndex);

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
        List<Integer> newRoute = shortestPaths.get(ranIndex);


        //Replace the random route with the new solution
        solution.set(ranSolutionIndex,newRoute);

        return solution;
    }

    public float calculateCostFunction(List<List<Integer>> solution) {
        float result;

        final float bandwidthCoeff = 1f;
        final float overlapCoeff = 1f;
        final float routeLengthCoeff = 1f;
        final float delayCoeff = 1f;

        float bandwidthCost = calculateBandwidthCost(solution);
        float overlapCost = calculateOverlapCost(solution);
        float routeLengthCost = calculateRouteLengthCost(solution);
        float delayCost = calculateDelayCost(solution);

        result = delayCoeff * delayCost + overlapCoeff * overlapCost + routeLengthCoeff * routeLengthCost + bandwidthCoeff * bandwidthCost;
        return result;
    }

    public boolean isViable(List<List<Integer>> solution) {
        return this.deadlinesOk(solution) && this.bandwidthOk(solution);
    }

    // auxiliary functions
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

    private ArrayList<Float> calculateUsedBandwidth(List<List<Integer>> solution) {
        ArrayList<Float> usedBandwidth = new ArrayList<>();

        for (Link l : this.a.getLinks()) {
            usedBandwidth.add(0f);
        }

        int idx = 0;
        // iterate over all streams
        for (Stream s : this.a.getStreams()) {
            // get stream period
            float period = s.getPeriod();
            float size = s.getSize();
            float bwReq = size/period;

            // iterate over all routes associated with the stream
            for (int rep = 0; rep < s.getRl(); rep++) {
                List<Integer> route = solution.get(idx);
                int parent = route.get(0);
                int child;
                // get all links. add bw required to all links along the route
                for (int rId = 1; rId < route.size(); rId++) {
                    child = route.get(rId);
                    Link l = this.a.getLinks().get(this.a.getGraph()[parent][child]);
                    usedBandwidth.set(l.getId(), usedBandwidth.get(l.getId()) + bwReq);
                    parent = child;
                }
                // check next route
                idx++;
            }
        }
        return usedBandwidth;
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

    public List<List<Integer>> createSolutionCopy(List<List<Integer>> solution) {
        List<List<Integer>> out = new ArrayList<>();

        for (List<Integer> route : solution) {
            List<Integer> routeCopy = new ArrayList<Integer>(route);
            out.add(routeCopy);
        }

        return out;
    }

    public List<List<Integer>> getSolution() {
        return this.bestSolution;
    }

    // partial viability evaluations
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

    private boolean bandwidthOk(List<List<Integer>> solution) {
        ArrayList<Float> usedBandwidth = calculateUsedBandwidth(solution);

        for (Link l : this.a.getLinks()) {
            if (l.getSpeed() < usedBandwidth.get(l.getId())) return false;
        }

        return true;
    }

    // partial cost functions
    private float calculateBandwidthCost(List<List<Integer>> solution) {
        ArrayList<Float> usedBandwidth = calculateUsedBandwidth(solution);
        float cost = 0f;

        for (Link l : this.a.getLinks()) {
            cost += 1 - (usedBandwidth.get(l.getId())/l.getSpeed());
        }

        return cost;
    }

    private float calculateOverlapCost(List<List<Integer>> solution) {
        float cost = 0;
        int idx = 0;

        // go stream by stream
        for (Stream s : this.a.getStreams()) {
            // instantiate aux vars
            int overlaps = 0;
            int shortestRouteLength = Integer.MAX_VALUE;
            ArrayList<Integer> linksVisited = new ArrayList<>();

            // for every stream, check all routes according to redundancy level
            for (int rep = 0; rep < s.getRl(); rep++) {
                List<Integer> route = solution.get(idx);

                // preserve shortest route length
                if (route.size() < shortestRouteLength) shortestRouteLength = route.size();

                // calculate number of shared links
                // iterate over links in route
                int parent = route.get(0);
                int child;

                for (int rId = 1; rId < route.size(); rId++) {
                    child = route.get(rId);
                    Link l = this.a.getLinks().get(this.a.getGraph()[parent][child]);

                    // if the link has already been visited, increment overlap count.
                    // if not, mark as visited

                    if (linksVisited.contains(l.getId())) {
                        overlaps++;
                    } else {
                        linksVisited.add(l.getId());
                    }

                    parent = child;
                }

                // check next route
                idx++;
            }

            // divide number of shared links by shortest route length of stream
            cost += (float) overlaps / shortestRouteLength;

        }

        return cost;
    }

    private float calculateRouteLengthCost(List<List<Integer>> solution) {
        int totalLength = 0;

        // Calculate total length of all routes in solution
        for (List<Integer> route : solution) {
            totalLength += route.size();
        }

        return totalLength;
    }

    private float calculateDelayCost(List<List<Integer>> solution) {
        this.calculateMaxLinkDelays(solution);
        float cost = 0f;

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

                // add to cost
                cost += t/deadline;

                // check next route
                idx++;
            }
        }

        return cost;

    }
}
