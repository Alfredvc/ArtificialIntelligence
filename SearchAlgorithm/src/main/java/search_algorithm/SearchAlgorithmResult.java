package search_algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Alfredvc on 8/29/2015.
 */
public class SearchAlgorithmResult<T extends State> {
    private final Node<T> finalNode;
    private final int generatedNodes;
    private final int nodeCount;
    private final Status status;
    private final List<Node<T>> solution;

    private SearchAlgorithmResult(Node<T> finalNode, int generatedNodes, Status status) {
        this.finalNode = finalNode;
        this.generatedNodes = generatedNodes;
        int nodeCount = 0;
        if (finalNode != null) {
            Node<T> currentNode = finalNode;
            while (currentNode.getParent() != null) {
                nodeCount++;
                currentNode = currentNode.getParent();
            }
            this.nodeCount = nodeCount;
        } else {
            this.nodeCount = -1;
        }
        this.status = status;
        this.solution = SearchAlgorithmResult.generateSolution(finalNode);
    }

    public static SearchAlgorithmResult failed() {
        return new SearchAlgorithmResult(null, -1, Status.FAILED);
    }

    public static <F extends State> SearchAlgorithmResult succeeded(Node<F> finalNode, int generatedNodes) {
        return new SearchAlgorithmResult(finalNode, generatedNodes, Status.SUCCEEDED);
    }

    public static <F extends State> List<Node<F>> generateSolution(Node<F> finalNode) {
        if (finalNode == null) return Collections.emptyList();
        List<Node<F>> solution = new ArrayList<>();
        Node<F> currentNode = finalNode;
        while (currentNode.getParent() != null) {
            solution.add(currentNode);
            currentNode = currentNode.getParent();
        }
        solution.add(currentNode);
        Collections.reverse(solution);
        return solution;
    }

    public Status getStatus() {
        return status;
    }

    public int getGeneratedNodes() {
        return generatedNodes;
    }

    public Node<T> getFinalNode() {
        return finalNode;
    }

    public int getSolutionLength() {
        return nodeCount;
    }

    public List<Node<T>> getSolution() {
        return solution;
    }

    public enum Status {
        FAILED,
        SUCCEEDED
    }
}
