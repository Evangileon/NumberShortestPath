
import java.util.LinkedList;

public class Vertex implements Comparable<Vertex> {
    LinkedList<Integer> adj = new LinkedList<Integer>();
    LinkedList<Integer> adjWeight = new LinkedList<Integer>();

    int index = 0;
    boolean visited = false;
    long dis = Integer.MAX_VALUE;
    int pred = 0;

    long numPath = 0;

    boolean inQueue = false;
    int count = 0;

    public Vertex(int index) {
        this.index = index;
    }

    public void addAdj(int adjIndex, int weight) {
        adj.add(adjIndex);
        adjWeight.add(weight);
    }

    @Override
    public int compareTo(Vertex o) {
        long diff = this.dis - o.dis;
        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
