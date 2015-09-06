package bfs;

import search_algorithm.Agenda;
import search_algorithm.Node;
import search_algorithm.State;

import java.util.LinkedList;

/**
 * Created by erpa_ on 8/29/2015.
 */
public class BFSAgenda extends Agenda {

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
