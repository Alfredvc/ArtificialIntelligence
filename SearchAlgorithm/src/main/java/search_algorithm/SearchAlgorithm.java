package search_algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alfredvc on 8/27/2015.
 */

/**
 * Abstract class representing a search algorithm, in the general form of A*
 */
public abstract class SearchAlgorithm<T extends State> {
    private final int maxNodes;
    private Map<Node<T>, Node<T>> closedNodes;
    private Agenda agenda;
    private Map<Node<T>, Node<T>> generatedStates;
    private int generatedNodes;
    private int repeatedNodes;
    private List<NodePopListener<T>> nodePopListeners;
    private List<NodePrePushListener<T>> nodePrePushListeners;

    protected SearchAlgorithm(Agenda agenda, int maxNodes) {
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
        this.repeatedNodes = 0;
        this.nodePopListeners = new ArrayList<>();
        this.nodePrePushListeners = new ArrayList<>();
    }

    public SearchAlgorithmResult search() {
        Node<T> currentParent = null;
        while (generatedNodes < maxNodes) {
            if (agenda == null || agenda.isEmpty()) {
                return SearchAlgorithmResult.failed(currentParent, generatedNodes);
            }
            currentParent = agenda.pop();
            fireNodePopped(currentParent);
            closeNode(currentParent);
            if (currentParent.isASolution()) {
                return SearchAlgorithmResult.succeeded(currentParent, generatedNodes);
            }
            List<T> successorStates = currentParent.generateSuccessors();
            for (T state : successorStates) {
                Node<T> currentSuccessor;
                if (generatedStates.containsKey(state)) {
                    currentSuccessor = generatedStates.get(state);
                } else {
                    currentSuccessor = new Node<>(state, currentParent.getG() + state.getArcCost());
                    generatedNodes++;
                }
                currentParent.addChild(currentSuccessor);

                if (!openOrClosed(currentSuccessor)) {
                    attachAndEval(currentSuccessor, currentParent);
                    fireNodePrePushed(currentSuccessor);
                    agenda.add(currentSuccessor);
                } else if (currentParent.getG() + currentSuccessor.getArcCost() < currentSuccessor.getG()) {
                    System.out.println("a");
                    attachAndEval(currentSuccessor, currentParent);
                    if (closedNodes.containsKey(currentSuccessor)) {
                        propagatePathImprovements(currentSuccessor);
                    }
                }

            }
        }
        return SearchAlgorithmResult.succeeded(currentParent, generatedNodes);
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
            l.onNodeEvaluated(node);
        }
    }

    public void addNodePopListener(NodePopListener<T> listener) {
        this.nodePopListeners.add(listener);
    }

    public void removeNodePopListener(NodePopListener<T> listener) {
        this.nodePopListeners.remove(nodePopListeners);
    }

    public interface NodePopListener<V extends State> {
        void onNodeEvaluated(Node<V> node);
    }

    private void fireNodePrePushed(Node<T> node) {
        for (NodePrePushListener<T> l : nodePrePushListeners) {
            l.onNodePrePush(node);
        }
    }

    public void addNodePrePushListener(NodePrePushListener<T> listener) {
        this.nodePrePushListeners.add(listener);
    }

    public void removeNodePrePushListener(NodePrePushListener<T> listener) {
        this.nodePrePushListeners.remove(listener);
    }

    public interface NodePrePushListener<F extends State> {
        void onNodePrePush(Node<F> node);
    }


}
