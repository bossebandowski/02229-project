package router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Architecture {

    private ArrayList<Node> nodes = new ArrayList<>();
    private HashMap<String, Node> nodeMap = new HashMap<>();
    private ArrayList<Link> links = new ArrayList<>();
    private ArrayList<Stream> streams = new ArrayList<>();
    private int[][] graph;
    private int numNodes;

    private static boolean singleton = true;

    public Architecture() {
        if (singleton) {
            singleton = false;
        } else {
            System.err.println("WARN: YOU HAVE CREATED MULTIPLE ARCHITECTURES. CAN LEAD TO ERRORS");
        }
    }

    public void addNode(Node n) {
        nodes.add(n);
        nodeMap.put(n.getName(), n);
    }

    public void addLink(Link l) {
        links.add(l);
        l.getStart().addChild(l.getEnd());
    }

    public void addStream(Stream s) {
        streams.add(s);
    }

    public void buildGraph() {
        // create NxN matrix
        this.numNodes = Node.getCount();
        this.graph = new int[numNodes][numNodes];
        // initialise with -1
        for (int row = 0; row < numNodes; row++) {
            for (int col = 0; col < numNodes; col++) {
                this.graph[row][col] = -1;
            }
        }

        // add link ids. row indices are source node, column indices are destination node
        int startID;
        int destID;

        for (Link link : this.links) {
            startID = link.getStart().getId();
            destID = link.getEnd().getId();

            this.graph[startID][destID] = link.getId();
        }
    }

    public int[][] getGraph() {
        return this.graph;
    }

    public Node getNodeByName(String name) {
        return this.nodeMap.get(name);
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public ArrayList<Link> getLinks() {
        return links;
    }

    public ArrayList<Stream> getStreams() {
        return streams;
    }

    public Stream getStreambyID(int streamID)
    {
        for (Stream stream : this.streams) {
            if(streamID == stream.getId())
            {
                return stream;
            }
        }
        System.err.println("The requested Stream does not found!");
        return null;
    }

    public Stream getStream(int source, int destination)
    {
        for (Stream stream : this.streams) {
            if(stream.getSource().getId() == source && stream.getDestination().getId() == destination)
            {
                return stream;
            }
        }
        System.err.println("The requested Stream does not found!");
        return null;
    }

    public Link getLink(int startID, int endID)
    {
        for (Link link : this.links) {
            if (link.getStart().getId() == startID && link.getEnd().getId() == endID)
            {
                return link;
            }
        }
        System.err.println("There is no link in the architecture, which meets your requirement!");
        return null;
    }

    public Link getLinkByID(int linkID)
    {
        for(Link currentLink:links)
        {
            if(currentLink.getId() == linkID)
            {
                return currentLink;
            }
        }
        return null;
    }

    public Node getNodeByID(int nodeID)
    {
        for(Node currentNode:nodes)
        {
            if(currentNode.getId() == nodeID)
            {
                return currentNode;
            }
        }
        return null;
    }

    public ArrayList<Link> getConnectingLinks(Node node)
    {
        ArrayList<Link> result = new ArrayList<>();
        for(Link currentLink:links)
        {
            if(currentLink.getStart() == node || currentLink.getEnd() == node)
            {
                result.add(currentLink);
            }
        }
        return result;
    }
}
