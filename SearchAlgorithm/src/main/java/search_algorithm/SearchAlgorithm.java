package search_algorithm;
import java.util.*;

/**
 * Created by erpa_ on 8/27/2015.
 */

/**
 * Abtract class representing a search algorithm, in the general form of A*
 */
public abstract class SearchAlgorithm {
    private Map<Integer, Node> closedNodes;
    private Agenda agenda;
    private Map<Integer, Node> generatedStates;
    private final int maxNodes;
    private int generatedNodes;
    private List<NodeEvaluateListener> nodeEvaluateListeners;

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
        this.nodeEvaluateListeners = new ArrayList<>();
    }

    public SearchAlgorithmResult search(){
        Node currentParent = null;
        while (generatedNodes < maxNodes) {
            if (agenda == null || agenda.isEmpty()) {
                return SearchAlgorithmResult.failed();
            }
            currentParent = agenda.pop();
            fireNodeEvaluated(currentParent);
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

    private boolean openOrClosed(Node node){
        boolean inClosedNodes = closedNodes.containsKey(node.hashCode());
        if (inClosedNodes) {
            if (!node.equals(closedNodes.get(node.hashCode()))) {
                throw new IllegalStateException("Different nodes have same hash key");
            }
        }
        return (agenda.contains(node) || closedNodes.containsKey(node.hashCode()));
    }

    private void attachAndEval(Node child, Node parent){
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

    private void fireNodeEvaluated(Node node) {
        for (NodeEvaluateListener listener : nodeEvaluateListeners) {
            listener.onNodeEvaluated(node);
        }
    }

    public void addNodeEvaluateListener(NodeEvaluateListener listener) {
        this.nodeEvaluateListeners.add(listener);
    }

    public void removeNodeEvaluateListener(NodeEvaluateListener listener) {
        this.nodeEvaluateListeners.remove(nodeEvaluateListeners);
    }

    public interface NodeEvaluateListener{
        void onNodeEvaluated(Node node);
    }
}
