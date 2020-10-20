public class Node {
    private static int count = 0;

    private String name;
    private int id;
    private boolean type; // type == true -> end system :: else -> switch

    public Node(String name, boolean type) {
        this.name = name;
        this.type = type;
        this.id = count;
        count++;
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
