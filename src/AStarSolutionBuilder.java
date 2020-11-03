import java.util.*;
import java.util.concurrent.TimeUnit;


public class AStarSolutionBuilder extends SolutionBuilder{

    private Architecture a;
    public AStarSolutionBuilder(Architecture a) { this.a = a; }
    public ArrayList<Integer> builtSingleSolution(Stream stream) {
        Node src = stream.getSource();
        Node dest = stream.getDestination();
        int numNodes = a.getGraph().length;

        ArrayList<Integer> route = new ArrayList<Integer>(numNodes);
        route.add(src.getId());

        Node currentNode = src;
        Link currentBest_link = new Link(src,dest,-99999);
        while (!currentNode.getName().equals(dest.getName())) {
            System.out.println(currentNode.getName());
            System.out.println(dest.getName());
            for (Node child : currentNode.getChildren()) {
                // for each path to child check if it has higher leftover bandwidth
                int link_id = this.a.getGraph()[currentNode.getId()][child.getId()];
                Link link = this.a.getLinks().get(link_id);
                System.out.println("Checking bandwidths: \n" +
                        "   Current_Best: " + currentBest_link.getLeftoverbandwidth() +"\n" +
                        "   Checked Link: " + link.getLeftoverbandwidth());
                // first check if we got the destination
                if (link.getEnd().getName().equals(dest.getName())) {
                    currentBest_link = link;
                } else
                if (currentBest_link.getLeftoverbandwidth() >= link.getLeftoverbandwidth()) {
                } else {
                    currentBest_link = link;
                }

            }
            System.out.println("Added route from "+currentNode.getName() + " to " + currentBest_link.getEnd().getName() );
            currentNode = currentBest_link.getEnd();
            System.out.println("Setting current node = " +currentNode.getName());

            currentBest_link.addStream(stream);
            route.add(currentNode.getId());
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
