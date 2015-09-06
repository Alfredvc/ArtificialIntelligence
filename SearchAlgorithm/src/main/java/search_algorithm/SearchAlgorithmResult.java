package search_algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by erpa_ on 8/29/2015.
 */
public class SearchAlgorithmResult {
    private final Node finalNode;
    private final int generatedNodes;
    private final int nodeCount;
    private final Status status;
    private final List<Node> solution;

    public static SearchAlgorithmResult failed(){
        return new SearchAlgorithmResult(null, -1, Status.FAILED);
    }

    public static SearchAlgorithmResult succeeded(Node finalNode, int generatedNodes) {
        return new SearchAlgorithmResult(finalNode, generatedNodes, Status.SUCCEEDED);
    }

    private SearchAlgorithmResult(Node finalNode, int generatedNodes, Status status) {
        this.finalNode = finalNode;
        this.generatedNodes = generatedNodes;
        int nodeCount = 0;
        if (finalNode != null){
            Node currentNode = finalNode;
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

    public Status getStatus() {
        return status;
    }

    public int getGeneratedNodes() {
        return generatedNodes;
    }

    public Node getFinalNode() {
        return finalNode;
    }

    public int getSolutionLength(){
        return nodeCount;
    }

    public List<Node> getSolution() {
        return solution;
    }

    public enum Status{
        FAILED,
        SUCCEEDED
    }

    public static List<Node> generateSolution(Node finalNode) {
        if (finalNode == null) return Collections.emptyList();
        List<Node> solution = new ArrayList<>();
        Node currentNode = finalNode;
        while (currentNode.getParent() != null) {
            solution.add(currentNode);
            currentNode = currentNode.getParent();
        }
        solution.add(currentNode);
        Collections.reverse(solution);
        return solution;
    }
}
