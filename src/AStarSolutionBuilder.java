import java.util.*;


public class AStarSolutionBuilder extends SolutionBuilder{

    private Architecture a;
    int[][] h;
    /**
     * A star sb. heuristic minimizes link used bandwidth + route length
     */
    public AStarSolutionBuilder(Architecture a) {
        this.a = a;
        h = new int[this.a.getNodes().size()][this.a.getNodes().size()];
    }



    public void preprocess(Stream s) {
        Node src = s.getSource();
        Node dests = s.getDestination();
        // calculate distance from each node as heuristics
        Stack<Node> frontier = new Stack<>();
        int[] pathLenghts = new int[this.a.getNodes().size()];
        Arrays.fill(pathLenghts, Integer.MAX_VALUE);
        frontier.push(src);
        pathLenghts[src.getId()] = 99;
        Boolean[] discovered  = new Boolean[this.a.getLinks().size()];
        Arrays.fill(discovered, false);
        ArrayList<Node> localPath = new ArrayList<Node>();

        calc_path_scores(src, dests, discovered, localPath);
        System.out.println("done");
        // dfs for h
        // for unexplored, want to see which end systems they connect to.
    }

    private void calc_path_scores(Node src, Node dests, Boolean[] discovered, ArrayList<Node> localPath) {
        if (src.equals(dests)){
            int path_length = localPath.size();
            int[][] h = new int[this.a.getNodes().size()][this.a.getNodes().size()];

            System.out.println("Got path of size: " + path_length);
            for (Node pathNod : localPath) {
                path_length -= 1;
                System.out.println(pathNod.getName() + " d: " + path_length);
                this.h[pathNod.getId()][dests.getId()] = path_length;
            }
        }
        discovered[src.getId()] = true;

        for (Node child : src.getChildren()){
            if (!discovered[child.getId()]){
                localPath.add(child);
                calc_path_scores(child, dests, discovered, localPath);

                localPath.remove(child);
            }
        }
        discovered[src.getId()] = false;
    }

    public ArrayList<Integer> builtSingleSolution(Stream stream) {
        Node src = stream.getSource();
        Node dest = stream.getDestination();
        int numNodes = a.getGraph().length;
        this.preprocess(stream);
        ArrayList<Integer> route = new ArrayList<Integer>(numNodes);
        route.add(src.getId());

        Node currentNode = src;
        Link currentBest_link = null;
        while (!currentNode.getName().equals(dest.getName())) {
            System.out.println(currentNode.getName());
            float best_link_score = 9999999;
            for (Node child : currentNode.getChildren()) {
                int link_id = this.a.getGraph()[currentNode.getId()][child.getId()];
                Link link = this.a.getLinks().get(link_id);

                System.out.println("    child: " + child.getName());
                if (child.equals(dest)) {
                    currentBest_link = link;
                    break;
                }

                if (this.h[child.getId()][dest.getId()] == 0) {
                    System.out.println("Bad");
                    continue;
                }

                // seek the link with smallest score(pnalty) = used bandiwdth + distance to dest
                float link_score = link.getUsedbandwidth() * 10 + this.h[child.getId()][dest.getId()];

                System.out.println("Score: " + link_score);

                if (link_score < best_link_score || link.getEnd().equals(dest)) {
                    best_link_score = link_score;
                    System.out.println("New link is better. " + currentNode.getName() + "->" + child.getName());
                    currentBest_link = link;
                } else {
                    continue;
                }
            }
            assert currentBest_link != null;
            currentBest_link.addStream(stream);
            route.add(currentBest_link.getEnd().getId());
            currentNode = currentBest_link.getEnd();
        }
        System.out.println("------------Got destination------------");
        return route;
    }

    @Override
    public List<List<Integer>> builtSolution() {
        // init
        int count_routes = 0;
        for (Stream s: this.a.getStreams()) {
            count_routes += s.getRl();
        }

        List<List<Integer>> initSolution = new ArrayList<>(count_routes);
        for (Stream stream : a.getStreams()) {
            for (int level = 0; level < stream.getRl(); level++){
                ArrayList<Integer> route = builtSingleSolution(stream);
                initSolution.add(route);
            }
        }
        int count = 0;
        for (List<Integer> route : initSolution) {
            System.out.println("Route " + count);
            for (Integer i : route) {
                System.out.print(a.getNodes().get(i).getName() + " -> ");
            }
            System.out.println("|");
            count++;
        }
        return initSolution;
    }
}
