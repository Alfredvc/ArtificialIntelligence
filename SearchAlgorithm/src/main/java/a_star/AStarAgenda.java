package a_star;

import search_algorithm.Agenda;
import search_algorithm.Node;
import search_algorithm.State;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by erpa_ on 8/27/2015.
 */
public class AStarAgenda extends Agenda implements Node.FChangeListener {

    private PriorityQueue<Node> nodes;

    public AStarAgenda(State initialState) {
        this. nodes = new PriorityQueue<>(new NodeComparator());
        this.add(new Node(initialState, 0));
    }

    @Override
    public void fChanged(Node node) {
        nodes.remove(node);
        nodes.add(node);
    }

    @Override
    protected void internalAdd(Node node) {
        nodes.add(node);
        node.addFChangedListener(this);
    }

    @Override
    protected Node internalGet() {
        return nodes.poll();
    }

    @Override
    protected boolean isEmpty() {
        return nodes.isEmpty();
    }

    private static class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            return o1.getF() - o2.getF();
        }

        @Override
        public boolean equals(Object obj) {
            throw new UnsupportedOperationException();
        }
    }
}
