package search_algorithm;

import java.util.List;

/**
 * Abstract class that must be implemented for all states to be used in the SearchAlgorithm.
 * It is an abstract class and not an Interface in order to force subclasses to implement
 * hashCode() and equals() which are vital for SearchAlgorithm.
 * @param <T> subclasses should supply themselves as this parameter.
 */
public abstract class State<T extends State> {
    public abstract int getH();

    public abstract boolean isASolution();

    public abstract List<T> generateSuccessors();

    public abstract int getArcCost();

    public abstract int getCostFrom(T state);

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);
}
