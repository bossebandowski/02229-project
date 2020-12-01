package router;

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
        while (currentNode != stream.getDestination())
        {
            if (invalid)
            {
                invalid = false;
                currentNode = stream.getSource();
                route.clear();
                route.add(currentNode.getId());
            }
            int randomLinkID = getRandomLink(currentNode, route);
            if(randomLinkID == -1)
            {
                invalid = true;
                continue;
            }
            Link randomLink = a.getLinkByID(randomLinkID);
            if (randomLink == null) {
                invalid = true;
                continue;
            }
            currentNode = randomLink.getOtherEnd(currentNode);
            route.add(currentNode.getId());
        }
        return route;
    }

    public int getRandomLink(Node node, List<Integer> route)
    {
        List<Integer> startingLinkIDs = new ArrayList<>();
        for(int i = 0; i < a.getGraph()[0].length;i++)
        {
            if(a.getGraph()[node.getId()][i] != -1)
            {
                startingLinkIDs.add(a.getGraph()[node.getId()][i]);
            }
        }
        if(startingLinkIDs.size() == 0)
        {
            return -1;
        }
        int maxTry = startingLinkIDs.size();
        int probes = 0;
        int randomLinkID;
        while(probes <= maxTry)
        {
            randomLinkID = rand.nextInt(startingLinkIDs.size());
            if (!contains(route, a.getLinkByID(startingLinkIDs.get(randomLinkID)).getOtherEnd(node)))
            {
                return startingLinkIDs.get(randomLinkID);
            }
            probes++;
        }
        return -1;
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
