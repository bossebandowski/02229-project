package router;

import java.util.ArrayList;

public class Node {
    private static int count = 0;

    private String name;
    private int id;
    private boolean type; // type == true -> end system :: else -> switch
    private ArrayList<Node> children = new ArrayList<>();

    private int weight;

    public Node(String name, boolean type) {
        this.name = name;
        this.type = type;
        this.id = count;
        count++;
    }

    public static void resetCount() {
        count = 0;
    }

    public void addChild(Node child) {
        this.children.add(child);
    }
    public ArrayList<Node> getChildren() {
        ArrayList<Node> out = new ArrayList<>();
        for (Node child : this.children) out.add(child);
        return out;
    }

    public static int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public boolean isType() {
        return type;
    }
}
