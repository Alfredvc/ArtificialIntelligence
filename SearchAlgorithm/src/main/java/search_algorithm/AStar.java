package search_algorithm;

/**
 * Solves search problems with a best first search algorithm.
 */
public class AStar extends SearchAlgorithm {

    public AStar(State state, int maxNodes) {
        super(new AStarAgenda(state), maxNodes);
    }
}
