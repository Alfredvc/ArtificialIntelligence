package a_star;

import search_algorithm.SearchAlgorithm;
import search_algorithm.State;

/**
 * Created by Alfredvc on 8/27/2015.
 */
public class AStar extends SearchAlgorithm {

    public AStar(State state, int maxNodes) {
        super(new AStarAgenda(state), maxNodes);
    }
}
