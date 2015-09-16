package search_algorithm;

/**
 * Solves search problems with a depth first search algorithm.
 */
public class DFS extends SearchAlgorithm {

    public DFS(State state, int maxNodes) {
        super(new DFSAgenda(state), maxNodes);
    }
}
