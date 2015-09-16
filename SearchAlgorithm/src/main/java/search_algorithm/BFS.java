package search_algorithm;

/**
 * Solves search problems with a breadth first search algorithm.
 */
public class BFS extends SearchAlgorithm {

    public BFS(State state, int maxNodes) {
        super(new BFSAgenda(state), maxNodes);
    }
}
