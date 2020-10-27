import java.util.*;

public class BfsSolutionBuilder extends SolutionBuilder {
    private Architecture a;

    public BfsSolutionBuilder(Architecture a) {
        this.a = a;
    }

    public ArrayList<Integer> builtSingleRoute(Stream s) {
        Node src = s.getSource();
        Node dest = s.getDestination();
        int numNodes = a.getGraph().length;
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
                Link l = this.a.getLinks().get(this.a.getGraph()[n.getId()][child.getId()]);
                if(shortest_path_time[n.getId()] + 1/l.getSpeed() < shortest_path_time[child.getId()]) {
                    queue.add(child);
                    shortest_path_time[child.getId()] = shortest_path_time[n.getId()] + 1/l.getSpeed();
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

    public List<List<Integer>> builtSolution() {

        // init
        int count_routes = 0;
        for (Stream s: this.a.getStreams()) {
            count_routes += s.getRl();
        }

        System.out.println(count_routes);
        List<List<Integer>> initSolution = new ArrayList<>(count_routes);

        for (Stream s: this.a.getStreams()) {
            for (int rep = 0; rep < s.getRl(); rep++) {
                initSolution.add(this.builtSingleRoute(s));
            }
        }

        System.out.println(initSolution.size());

        int count = 0;
        for (List<Integer> route : initSolution) {
            System.out.println("Route " + count);
            for (Integer i : route) {
                System.out.print(a.getNodes().get(i).getName() + " -> ");
            }
            System.out.println("|");
            count++;
        }

        return null;
    }
}
