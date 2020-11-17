import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSolutionBuilder extends SolutionBuilder {
    private final Random rand = new Random();

    public RandomSolutionBuilder(Architecture a) {
        this.a = a;
    }

    public List<Integer> generateRandomRoute(Stream stream) {
        List<Integer> route = new ArrayList<>();
        route.add(stream.getSource().getId());
        Node currentNode = stream.getSource();
        boolean invalid = false;
        while (currentNode != stream.getDestination()) {
            if (invalid) {
                invalid = false;
                currentNode = stream.getSource();
                route.clear();
                route.add(currentNode.getId());
            }
            Link randomLink = getRandomLink(currentNode, route);
            if (randomLink == null) {
                invalid = true;
                continue;
            }
            currentNode = randomLink.getOtherEnd(currentNode);
            route.add(currentNode.getId());
        }
        return route;
    }

    public Link getRandomLink(Node node, List<Integer> route) {
        ArrayList<Link> connectingLinks = this.a.getConnectingLinks(node);
        int maxTry = connectingLinks.size();
        int probes = 0;
        int randomLinkID;
        while (probes <= maxTry) {
            randomLinkID = rand.nextInt(connectingLinks.size());
            if (!contains(route, connectingLinks.get(randomLinkID).getOtherEnd(node))) {
                return connectingLinks.get(randomLinkID);
            }
            probes++;
        }
        for (Link currentLink : connectingLinks) {
            if (!contains(route, currentLink.getOtherEnd(node))) {
                return currentLink;
            }
        }
        return null;
    }

    public boolean contains(List<Integer> route, Node node)
    {
        for(Integer currentInteger:route)
        {
            if(node.getId() == currentInteger)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<List<Integer>> builtSolution() {
        List<List<Integer>> initSolution = new ArrayList<>();

        for (Stream s : this.a.getStreams()) {
            for (int rep = 0; rep < s.getRl(); rep++) {
                initSolution.add(generateRandomRoute(s));
            }
        }

        return initSolution;
    }
}
