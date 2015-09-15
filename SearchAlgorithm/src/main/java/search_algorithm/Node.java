package search_algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alfredvc on 8/27/2015.
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

    public interface FChangeListener {
        void fChanged(Node node);
    }
}
