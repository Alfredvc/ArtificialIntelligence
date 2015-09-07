package search_algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alfredvc on 8/27/2015.
 */
public final class Node {
    private final State state;
    private final int h;
    private int g;
    private int f;
    private boolean isOpen;
    private Node parent;
    private List<Node> children;

    private List<FChangeListener> fChangeListeners;

    public Node(State state, int g) {
        this.children = new ArrayList<>();
        this.state = state;
        this.h = state.getH();
        setG(g);
    }

    public State getState() {
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
        this.f = this.g + this.h;
        if (previousF == this.f) {
            fireFChanged();
        }
    }

    public int getH() {
        return h;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public int getF() {
        return g + h;
    }

    public boolean isASolution() {
        return this.state.isASolution();
    }

    public List<State> generateSuccessors() {
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
        if (!(obj instanceof Node)) {
            return false;
        }
        return state.equals(((Node) obj).getState());
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

    public int getCostFrom(Node node) {
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
