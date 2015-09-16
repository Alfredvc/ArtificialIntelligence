package search_algorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * Common class for all agendas. Subclasses must implement internalAdd and internalGet.
 */
abstract class Agenda {

    private Map<Node, Node> currentNodes;

    protected Agenda() {
        this.currentNodes = new HashMap<>();
    }

    public boolean contains(Node node) {
        return currentNodes.containsKey(node);
    }

    public Node pop() {
        Node toReturn = internalGet();
        currentNodes.remove(toReturn);
        return toReturn;
    }

    public void add(Node node) {
        currentNodes.put(node, node);
        internalAdd(node);
    }

    public int size() {
        return currentNodes.size();
    }

    protected abstract void internalAdd(Node node);

    protected abstract Node internalGet();

    protected abstract boolean isEmpty();

}
