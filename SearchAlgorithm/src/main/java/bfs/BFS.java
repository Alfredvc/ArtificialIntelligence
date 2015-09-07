package bfs;

import search_algorithm.SearchAlgorithm;
import search_algorithm.State;

/**
 * Created by Alfredvc on 8/29/2015.
 */
public class BFS extends SearchAlgorithm {

    public BFS(State state, int maxNodes) {
        super(new BFSAgenda(state), maxNodes);
    }
}
