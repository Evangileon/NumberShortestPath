import java.util.ArrayList;

public class VertexMinHeap {
    // vertices reference, 0 index is unused, so real size is size() - 1
    private ArrayList<Vertex> vertices;
    // priority queue using integer index
    private ArrayList<Integer> Q;
    // cross reference
    private ArrayList<Integer> X;
    // heap size
    private int heapSize;

    public VertexMinHeap(ArrayList<Vertex> vertices) {
        this.vertices = vertices;
        this.heapSize = vertices.size() - 1;
        Q = new ArrayList<Integer>(this.heapSize + 1);
        Q.add(0); // 0 unused
        X = new ArrayList<Integer>(this.heapSize);
        X.add(0); // 0 unused
    }

    /**
     * Must run before any operation exception construction
     */
    public void buildHeap() {
        int size = heapSize;

        for (int i = 1; i <= size; i++) {
            Q.add(i);
            X.add(i);
        }

        for (int i = heapSize / 2; i > 0; i--) {
            percolateDown(i);
        }
    }

    /**
     * Percolate down
     * @param hole the element to be processed
     */
    private void percolateDown(int hole) {
        int child;

        for (; hole * 2 <= heapSize; hole = child) {
            child = hole * 2;
            if (child != heapSize
                    && vertices.get(Q.get(child + 1)).compareTo(vertices.get(Q.get(child))) < 0)
                child++;
            if (vertices.get(Q.get(child)).compareTo(vertices.get(Q.get(hole))) < 0)
                exchange(hole, child);
            else
                break;
        }
    }

    /**
     * Percolate up
     * @param hole the element to be processed
     */
    private int percolateUp(int hole) {
        for (Q.set(0, Q.get(hole)); vertices.get(Q.get(hole)).compareTo(vertices.get(Q.get(hole / 2))) < 0; hole /= 2) {
            exchange(hole, hole / 2);
        }
        return hole;
    }

    /**
     * Exchange to element in queue
     * @param i first position
     * @param j second position
     */
    private void exchange(int i, int j) {
        int tmp = Q.get(i);
        Q.set(i, Q.get(j));
        Q.set(j, tmp);
        X.set(Q.get(i), i);
        X.set(Q.get(j), j);
    }

    public boolean isEmpty() {
        return (heapSize == 0);
    }

    /**
     * Decrease key of element, also adjust heap to ensure heap property
     * @param index in vertices reference
     * @param newDis new distance
     */
    public void decreaseKey(int index, int newDis) {
        int a = X.get(index);

        if (vertices.get(index).dis < newDis)
            return;

        vertices.get(index).dis = newDis;

        percolateUp(a);
    }

    /**
     *
     * @return index of minimum element
     */
    public int deleteMin() {
        int minItem = Q.get(1);
        Q.set(1, Q.get(heapSize--));
        percolateDown(1);

        return minItem;
    }
}
