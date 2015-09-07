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
public abstract class SearchAlgorithm {
    private final int maxNodes;
    private Map<Integer, Node> closedNodes;
    private Agenda agenda;
    private Map<Integer, Node> generatedStates;
    private int generatedNodes;
    private List<NodePopListener> nodePopListeners;
    private List<NodePrePushListener> nodePrePushListeners;

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
        this.nodePopListeners = new ArrayList<>();
        this.nodePrePushListeners = new ArrayList<>();
    }

    public SearchAlgorithmResult search() {
        Node currentParent = null;
        while (generatedNodes < maxNodes) {
            if (agenda == null || agenda.isEmpty()) {
                return SearchAlgorithmResult.failed();
            }
            currentParent = agenda.pop();
            fireNodePopped(currentParent);
            closeNode(currentParent);
            if (currentParent.isASolution()) {
                return SearchAlgorithmResult.succeeded(currentParent, generatedNodes);
            }
            List<State> successorStates = currentParent.generateSuccessors();
            for (State state : successorStates) {
                Node currentSuccessor;
                if (generatedStates.containsKey(state.hashCode())) {
                    currentSuccessor = generatedStates.get(state.hashCode());
                } else {
                    currentSuccessor = new Node(state, currentParent.getG() + state.getArcCost());
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
                    if (closedNodes.containsKey(currentSuccessor.hashCode())) {
                        propagatePathImprovements(currentSuccessor);
                    }
                }

            }
        }
        return SearchAlgorithmResult.succeeded(currentParent, generatedNodes);
    }

    private boolean openOrClosed(Node node) {
        boolean inClosedNodes = closedNodes.containsKey(node.hashCode());
        if (inClosedNodes) {
            if (!node.equals(closedNodes.get(node.hashCode()))) {
                throw new IllegalStateException("Different nodes have same hash key");
            }
        }
        return (agenda.contains(node) || closedNodes.containsKey(node.hashCode()));
    }

    private void attachAndEval(Node child, Node parent) {
        child.setParent(parent);
        child.setG(parent.getG() + child.getCostFrom(parent));
    }

    private void closeNode(Node node) {
        node.setOpen(false);
        closedNodes.put(node.hashCode(), node);
    }

    private void propagatePathImprovements(Node parent) {
        for (Node child : parent.getChildren()) {
            int costToChild = child.getCostFrom(parent);
            if (parent.getG() + costToChild < child.getG()) {
                child.setParent(parent);
                child.setG(parent.getG() + costToChild);
                propagatePathImprovements(child);
            }
        }
    }

    private void fireNodePopped(Node node) {
        nodePopListeners.stream().peek(l -> l.onNodeEvaluated(node));
    }

    public void addNodePopListener(NodePopListener listener) {
        this.nodePopListeners.add(listener);
    }

    public void removeNodePopListener(NodePopListener listener) {
        this.nodePopListeners.remove(nodePopListeners);
    }

    public interface NodePopListener {
        void onNodeEvaluated(Node node);
    }

    private void fireNodePrePushed(Node node) {
        nodePrePushListeners.stream().peek(l -> l.onNodePrePush(node));
    }

    public void addNodePrePushListener(NodePrePushListener listener) {
        this.nodePrePushListeners.add(listener);
    }

    public void removeNodePrePushListener(NodePrePushListener listener) {
        this.nodePrePushListeners.remove(listener);
    }

    public interface NodePrePushListener {
        void onNodePrePush(Node node);
    }


}
