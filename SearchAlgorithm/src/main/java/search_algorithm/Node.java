package search_algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Used in SearchAlgorithm, used only to keep ancestor-descendant relationships. Delegates
 * all operations to the containing State.
 * @param <T> the type of State that it contains.
 */
public final class Node<T extends State> {
    private final T state;
    private int g;
    private int f;
    private boolean isOpen;
    private Node<T> parent;
    private List<Node<T>> children;

    private List<FChangeListener> fChangeListeners;

    public Node(T state, int g) {
        this.children = new ArrayList<>();
        this.state = state;
        setG(g);
    }

    public T getState() {
        return state;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
        calculateF();
    }

    private void calculateF() {
        int previousF = this.f;
        this.f = getG() + getH();
        if (previousF != this.f) {
            fireFChanged();
        }
    }

    public int getH() {
        return state.getH();
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Node<T> getParent() {
        return parent;
    }

    public void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public List<Node<T>> getChildren() {
        return children;
    }

    public void addChild(Node<T> child) {
        this.children.add(child);
    }

    /**
     * New F value is calculated whenever called
     * @return calculated value for f
     */
    public int getF() {
        calculateF();
        return f;
    }

    public boolean isASolution() {
        return this.state.isASolution();
    }

    public List<T> generateSuccessors() {
        return this.state.generateSuccessors();
    }

    public int getArcCost() {
        return this.state.getArcCost();
    }

    @Override
    public int hashCode() {
        return this.state.hashCode();
    }

    /**
     * Nodes can be equal to other equivalent nodes, and also be equal to States equivalent to its
     * containing state
     * @param obj the object to be compared
     * @return whether or not the objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Node) {
            return state.equals(((Node) obj).getState());
        }
        if (obj instanceof State) {
            return state.equals(obj);
        }
        return false;
    }

    /**
     * Adds a listener that is notified whenever the value of F changes
     * @param listener
     */
    public void addFChangedListener(FChangeListener listener) {
        if (fChangeListeners == null) {
            fChangeListeners = new ArrayList<>();
        }
        fChangeListeners.add(listener);
    }

    public void removeFChangedListener(FChangeListener listener) {
        fChangeListeners.remove(listener);
    }

    public void fireFChanged() {
        if (fChangeListeners == null) return;
        for (FChangeListener listener : fChangeListeners) {
            listener.fChanged(this);
        }
    }

    public int getCostFrom(Node<T> node) {
        return this.state.getCostFrom(node.getState());
    }

    @Override
    public String toString() {
        return state.toString();
    }

    /**
     * Interface to be implemented by classes that must be notified that the F value of the node
     * has changed
     */
    public interface FChangeListener {
        void fChanged(Node node);
    }
}
