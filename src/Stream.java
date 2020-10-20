public class Stream {
    private static int count = 0;

    private String name;
    private int id;
    private Node source;
    private Node destination;
    private int size;
    private int period;
    private int deadline;

    public Stream(String name, Node source, Node destination, int size, int period, int deadline) {
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.size = size;
        this.period = period;
        this.deadline = deadline;
        this.id = count;
        count++;
    }
}
