import java.util.ArrayList;

public class Link {
    private static int count = 0;
    private final Node start;
    private final Node end;
    private final float speed;
    private final int id;
    private float c = 0f;

    private float leftoverbandwidth;
    private float usedbandwidth;

    public Link(Node start, Node end, float speed) {
        this.start = start;
        this.end = end;
        this.speed = speed;
        this.id = count;
        count++;
        this.leftoverbandwidth = speed;
        this.usedbandwidth = 0;
    }

    public Node getStart() {
        return start;
    }

    public Node getEnd() {
        return end;
    }

    public Node getOtherEnd(Node node)
    {
        if(start == node)
        {
            return  end;
        }
        else if(end == node)
        {
            return start;
        }
        System.out.println("The node is not connected to the Link");
        return null;
    }

    public float getSpeed() {
        return speed;
    }

    public int getId() {
        return id;
    }

    public float getLeftoverbandwidth(){ return leftoverbandwidth;}

    public float getUsedbandwidth(){ return usedbandwidth;}

    public void addStream(Stream stream) {
        this.usedbandwidth += stream.getBandwith();
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
