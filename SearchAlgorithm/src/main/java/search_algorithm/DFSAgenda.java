package search_algorithm;

import java.util.LinkedList;

import search_algorithm.Agenda;
import search_algorithm.Node;
import search_algorithm.State;

/**
 * Agenda for a DFS search algorithm, is backed by a LIFO queue.
 */
class DFSAgenda extends Agenda {

    private LinkedList<Node> nodes;

    public DFSAgenda(State initialState) {
        this.nodes = new LinkedList<>();
        this.add(new Node(initialState, 0));
    }

    @Override
    protected void internalAdd(Node node) {
        nodes.offerFirst(node);
    }

    @Override
    protected Node internalGet() {
        return nodes.pollFirst();
    }

    @Override
    protected boolean isEmpty() {
        return nodes.isEmpty();
    }
}
