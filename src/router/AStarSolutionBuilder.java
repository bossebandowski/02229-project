package router;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class AStarSolutionBuilder extends SolutionBuilder{

    private Architecture a;
    public AStarSolutionBuilder(Architecture a) { this.a = a; }
    public ArrayList<Integer> builtSingleSolution(Stream stream) {
        Node src = stream.getSource();
        Node dest = stream.getDestination();
        int numNodes = a.getGraph().length;
        Node badNode = null;

        ArrayList<Integer> route = new ArrayList<Integer>(numNodes);
        route.add(src.getId());

        Node currentNode = src;
        Link currentBest_link = new Link(src,dest,-99999);
        while (!currentNode.getName().equals(dest.getName())) {
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            };

            System.out.println(currentNode.getName());
            System.out.println(dest.getName());

            if (currentNode.getChildren().size() == 0 ) {
                // remove wrong end system from route
                route.remove(route.size() -1);
                System.out.println("At wrong endsystem");
                System.out.println("Previous node was");
                int previous = route.get(route.size()-1);
                System.out.println(a.getNodes().get(previous).getName());

                ArrayList<Node> prevchildren = a.getNodes().get(previous).getChildren();
                // remove wrong end system
                // check if other children
                Node previousNode = a.getNodes().get(previous);
                if (prevchildren.size() > 1) {
                    for (Node child: prevchildren) {
                        if (child.isType()) {
                            continue;
                        }
                        else {
                            int link_id = this.a.getGraph()[previousNode.getId()][child.getId()];
                            Link link = this.a.getLinks().get(link_id);
                            currentBest_link = link;
                        }
                    }

                }

                else {
                    // no children other than false end system
                    // dont visit this node on the route again
                    // remove this node from current route
                    badNode = a.getNodes().get(route.remove(route.size()-1));
                    System.out.println("Bad node  " + badNode.getName() );
                    currentNode = a.getNodes().get(route.get(route.size()-1));
                    System.out.println(" Set current node = " + currentNode.getName());
                }


            }
            for (Node child : currentNode.getChildren()) {
                if (child.equals(badNode)){
                    continue;
                };
                // make sure not to set a route to an endsystem that isn't ourss

                // for each path to child check if it has higher leftover bandwidth
                int link_id = this.a.getGraph()[currentNode.getId()][child.getId()];
                Link link = this.a.getLinks().get(link_id);
                System.out.println("Checking bandwidths: \n" +
                        "   Current_Best: " +  + currentBest_link.getLeftoverbandwidth() +"\n" +
                        "   Checked Link: " + link.getLeftoverbandwidth());
                // first check if we got the destination
                if (child.isType()){
                    if (link.getEnd().getName().equals(dest.getName())) {
                        System.out.println("Equals");
                        currentBest_link = link;
                        break;
                    }
                    // dont chose this because its an endsystem
                    // set a false arbitary link that's super slow so it won't be chosen
                    else {
                        link = new Link(currentNode, child, -99);
                    }

                }
                if (currentBest_link.getLeftoverbandwidth() >= link.getLeftoverbandwidth()) { }
                else {
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
