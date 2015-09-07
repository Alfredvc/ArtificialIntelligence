package dfs;

import search_algorithm.SearchAlgorithm;
import search_algorithm.State;

/**
 * Created by Alfredvc on 8/29/2015.
 */
public class DFS extends SearchAlgorithm {

    public DFS(State state, int maxNodes) {
        super(new DFSAgenda(state), maxNodes);
    }
}
