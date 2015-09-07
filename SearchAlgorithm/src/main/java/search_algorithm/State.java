package search_algorithm;

import java.util.List;

/**
 * Created by erpa_ on 8/27/2015.
 */
public abstract class State<T extends State> {
    public abstract int getH();

    public abstract boolean isASolution();

    public abstract List<T> generateSuccessors();

    public abstract int getArcCost();

    public abstract int getCostFrom(T state);

    /**
     * Must be unique for every unique state.
     */
    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);
}
