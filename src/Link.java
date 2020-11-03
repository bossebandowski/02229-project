public class Link {
    private static int count = 0;
    private final Node start;
    private final Node end;
    private final float speed;
    private final int id;
    private float c = 0f;

    private float leftoverbandwidth;

    public Link(Node start, Node end, float speed) {
        this.start = start;
        this.end = end;
        this.speed = speed;
        this.id = count;
        count++;
        this.leftoverbandwidth = speed;
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

    public float getLeftoverbandwidth(){ return leftoverbandwidth;}

    public void addStream(Stream stream) {
        this.leftoverbandwidth -= stream.getBandwith();
//        System.out.println(this.id + " has " + leftoverbandwidth + "byte/micro sec");
    }
    public float getC() {
        return c;
    }

    public void setC(float c) {
        this.c = c;
    }
}
