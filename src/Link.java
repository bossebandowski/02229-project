public class Link {
    private static int count = 0;
    private final Node start;
    private final Node end;
    private final float speed;
    private final int id;

    public Link(Node start, Node end, float speed) {
        this.start = start;
        this.end = end;
        this.speed = speed;
        this.id = count;
        count++;
    }

    public Node getStart() {
        return start;
    }

    public Node getEnd() {
        return end;
    }

    public float getSpeed() {
        return speed;
    }

    public int getId() {
        return id;
    }
}
