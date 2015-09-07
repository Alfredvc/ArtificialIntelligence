package search_algorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alfredvc on 8/27/2015.
 */
public abstract class Agenda {

    private Map<Integer, Node> currentNodes;

    protected Agenda() {
        this.currentNodes = new HashMap<>();
    }

    public boolean contains(Node node) {
        boolean containsKey = currentNodes.containsKey(node.hashCode());
        if (containsKey) {
            if (!node.equals(currentNodes.get(node.hashCode()))) {
                throw new IllegalStateException("Different nodes have same hash code.");
            }
        }
        return containsKey;
    }

    public Node pop() {
        Node toReturn = internalGet();
        currentNodes.remove(toReturn.hashCode());
        return toReturn;
    }

    public void add(Node node) {
        currentNodes.put(node.hashCode(), node);
        internalAdd(node);
    }

    protected abstract void internalAdd(Node node);

    protected abstract Node internalGet();

    protected abstract boolean isEmpty();

}
