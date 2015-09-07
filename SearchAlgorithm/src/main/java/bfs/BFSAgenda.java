package bfs;

import java.util.LinkedList;

import search_algorithm.Agenda;
import search_algorithm.Node;
import search_algorithm.State;

/**
 * Created by Alfredvc on 8/29/2015.
 */
class BFSAgenda extends Agenda {

    private LinkedList<Node> nodes;

    public BFSAgenda(State initialState) {
        this.nodes = new LinkedList<>();
        this.add(new Node(initialState, 0));
    }

    @Override
    protected void internalAdd(Node node) {
        nodes.offerLast(node);
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
