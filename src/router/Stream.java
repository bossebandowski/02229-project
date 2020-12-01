package router;

public class Stream {
    private static int count = 0;

    private final String name;
    private final int id;
    private final Node source;
    private final Node destination;
    private final int size;
    private final int period;
    private final int deadline;
    private final int rl;

    public Stream(String name, Node source, Node destination, int size, int period, int deadline, int rl) {
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.size = size;
        this.period = period;
        this.deadline = deadline;
        this.rl = rl;
        this.id = count;
        count++;
    }

    public static void resetCount() {
        count = 0;
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

    public Node getSource() {
        return source;
    }

    public Node getDestination() {
        return destination;
    }

    public int getSize() {
        return size;
    }

    public int getPeriod() {
        return period;
    }

    public int getDeadline() {
        return deadline;
    }

    public int getRl() {
        return rl;
    }

    public float getBandwith() {
        return (float) this.size/this.period;
    }
}
