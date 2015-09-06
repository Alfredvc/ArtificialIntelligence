package search_algorithm;

import java.util.List;

/**
 * Created by erpa_ on 8/27/2015.
 */
public abstract class State {
    public abstract int getH();
    public abstract int getG();
    public abstract boolean isASolution();
    public abstract List<State> generateSuccessors();
    public abstract int getArcCost();
    public abstract int getCostFrom(State state);

    /**
     * Must be unique for every unique state.
     * @return
     */
    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract String toString();
}
