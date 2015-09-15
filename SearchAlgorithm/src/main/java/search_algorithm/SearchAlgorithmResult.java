package search_algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the result of an executed SearchAlgorithm instance
 * @param <T> the type of State used
 */
public class SearchAlgorithmResult<T extends State> {
    private final Node<T> finalNode;
    private final int generatedNodes;
    private final int solutionLength;
    private final int poppedNodes;
    private final Status status;
    private final List<Node<T>> solution;

    private SearchAlgorithmResult(Node<T> finalNode, int generatedNodes, Status status, int poppedNodes) {
        this.finalNode = finalNode;
        this.generatedNodes = generatedNodes;
        int nodeCount = 0;
        if (finalNode != null) {
            Node<T> currentNode = finalNode;
            while (currentNode.getParent() != null) {
                nodeCount++;
                currentNode = currentNode.getParent();
            }
            this.solutionLength = nodeCount;
        } else {
            this.solutionLength = -1;
        }
        this.status = status;
        this.solution = SearchAlgorithmResult.generateSolution(finalNode);
        this.poppedNodes = poppedNodes;
    }

    public static <E extends State>SearchAlgorithmResult failed(Node<E> finalNode, int generatedNodes, int poppedNodes) {
        return new SearchAlgorithmResult(finalNode, generatedNodes, Status.FAILED, poppedNodes);
    }

    public static <F extends State> SearchAlgorithmResult succeeded(Node<F> finalNode, int generatedNodes, int poppedNodes) {
        return new SearchAlgorithmResult(finalNode, generatedNodes, Status.SUCCEEDED, poppedNodes);
    }

    private static <F extends State> List<Node<F>> generateSolution(Node<F> finalNode) {
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
        return solutionLength;
    }

    public List<Node<T>> getSolution() {
        return solution;
    }

    public int getPoppedNodes() {
        return poppedNodes;
    }

    public enum Status {
        FAILED,
        SUCCEEDED
    }
}
