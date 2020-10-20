import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Architecture {
    private ArrayList<Node> nodes = new ArrayList<>();
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
    }

    public void addLink(Link l) {
        links.add(l);
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
}
