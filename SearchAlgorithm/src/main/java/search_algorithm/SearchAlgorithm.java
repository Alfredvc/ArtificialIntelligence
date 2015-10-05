package search_algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class representing a search algorithm.
 */
public abstract class SearchAlgorithm<T extends State> {
    private final int maxNodes;
    private Map<Node<T>, Node<T>> closedNodes;
    private Agenda agenda;
    private Map<State<T>, Node<T>> generatedStates;
    private int generatedNodes;
    private int poppedNodes;
    private List<NodePopListener<T>> nodePopListeners;

    SearchAlgorithm(Agenda agenda, int maxNodes) {
        if (agenda == null) {
            throw new NullPointerException("Agenda cannot be null");
        }
        this.agenda = agenda;
        if (maxNodes < 0) {
            throw new IllegalArgumentException("maxNodes must be a positive integer");
        }
        this.maxNodes = maxNodes;
        this.closedNodes = new HashMap<>();
        this.generatedStates = new HashMap<>();
        this.generatedNodes = 0;
        this.poppedNodes = 0;
        this.nodePopListeners = new ArrayList<>();
    }

    public SearchAlgorithmResult search() {
        Node<T> currentParent = null;
        while (generatedNodes < maxNodes) {
            if (agenda == null || agenda.isEmpty()) {
                return SearchAlgorithmResult.failed(currentParent, generatedNodes, poppedNodes);
            }
            currentParent = agenda.pop();
            poppedNodes++;
            fireNodePopped(currentParent);
            closeNode(currentParent);
            if (currentParent.isASolution()) {
                return SearchAlgorithmResult.succeeded(currentParent, generatedNodes, poppedNodes);
            }
            List<T> successorStates = currentParent.generateSuccessors();
            for (T state : successorStates) {
                Node<T> currentSuccessor;
                if (generatedStates.containsKey(state)) {
                    currentSuccessor = generatedStates.get(state);
                } else {
                    currentSuccessor = new Node<>(state, currentParent.getG() + state.getArcCost());
                    generatedStates.put(state, currentSuccessor);
                    generatedNodes++;
                }
                currentParent.addChild(currentSuccessor);

                if (!agenda.contains(currentSuccessor) && !closedNodes.containsKey(currentSuccessor)) {
                    attachAndEval(currentSuccessor, currentParent);
                    agenda.add(currentSuccessor);
                }
                else if (currentParent.getG() + currentSuccessor.getArcCost() < currentSuccessor.getG()) {
                    attachAndEval(currentSuccessor, currentParent);
                    if (closedNodes.containsKey(currentSuccessor)) {
                        propagatePathImprovements(currentSuccessor);
                    }
                }

            }
        }
        return SearchAlgorithmResult.succeeded(currentParent, generatedNodes, poppedNodes);
    }

    private boolean openOrClosed(Node<T> node) {
        return (agenda.contains(node) || closedNodes.containsKey(node));
    }

    private void attachAndEval(Node<T> child, Node<T> parent) {
        child.setParent(parent);
        child.setG(parent.getG() + child.getCostFrom(parent));
    }

    private void closeNode(Node<T> node) {
        node.setOpen(false);
        closedNodes.put(node, node);
    }

    private void propagatePathImprovements(Node<T> parent) {
        System.out.println("Happened");
        for (Node<T> child : parent.getChildren()) {
            int costToChild = child.getCostFrom(parent);
            if (parent.getG() + costToChild < child.getG()) {
                child.setParent(parent);
                child.setG(parent.getG() + costToChild);
                propagatePathImprovements(child);
            }
        }
    }

    private void fireNodePopped(Node<T> node) {
        for (NodePopListener<T> l : nodePopListeners) {
            l.onNodePopped(node);
        }
    }

    /**
     * Adds a listener to be notified whenever a Node is popped from the Agenda
     * @param listener the listener to be added
     */
    public void addNodePopListener(NodePopListener<T> listener) {
        this.nodePopListeners.add(listener);
    }

    public void removeNodePopListener(NodePopListener<T> listener) {
        this.nodePopListeners.remove(listener);
    }

    public interface NodePopListener<V extends State> {
        void onNodePopped(Node<V> node);
    }


}
