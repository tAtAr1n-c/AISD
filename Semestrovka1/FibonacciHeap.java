package Semestrovka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FibonacciHeap {

    private static final class Node {
        private int key;
        private int degree;
        private boolean mark;
        private Node parent;
        private Node child;
        private Node left;
        private Node right;

        private Node(int key) {
            this.key = key;
            this.left = this;
            this.right = this;
        }
    }

    private Node min;
    private int size;

    public void add(int key) {
        Node node = new Node(key);
        addToRootList(node);
        size++;
    }

    public boolean isEmpty() {
        return min == null;
    }

    public int size() {
        return size;
    }

    public Integer extractMin() {
        Node node = extractMinInternal();
        return node == null ? null : node.key;
    }

    public boolean search(int key) {
        return searchInList(min, key) != null;
    }

    public boolean delete(int key) {
        Node node = searchInList(min, key);
        if (node == null) {
            return false;
        }
        decreaseKey(node, Integer.MIN_VALUE);
        extractMinInternal();
        return true;
    }

    private void addToRootList(Node node) {
        if (min == null) {
            node.left = node;
            node.right = node;
            min = node;
            return;
        }

        node.left = min;
        node.right = min.right;
        min.right.left = node;
        min.right = node;

        if (node.key < min.key) {
            min = node;
        }
    }

    private Node searchInList(Node start, int key) {
        if (start == null) {
            return null;
        }

        Node current = start;
        do {
            if (current.key == key) {
                return current;
            }
            Node fromChild = searchInList(current.child, key);
            if (fromChild != null) {
                return fromChild;
            }
            current = current.right;
        } while (current != start);

        return null;
    }

    private void decreaseKey(Node node, int newKey) {
        if (newKey > node.key) {
            throw new IllegalArgumentException("New key is greater than current key");
        }

        node.key = newKey;
        Node parent = node.parent;

        if (parent != null && node.key < parent.key) {
            cut(node, parent);
            cascadingCut(parent);
        }

        if (min == null || node.key < min.key) {
            min = node;
        }
    }

    private void cut(Node node, Node parent) {
        if (parent.child == node) {
            parent.child = (node.right == node) ? null : node.right;
        }

        removeFromList(node);
        parent.degree--;

        node.parent = null;
        node.mark = false;
        node.left = node;
        node.right = node;
        addToRootList(node);
    }

    private void cascadingCut(Node node) {
        Node parent = node.parent;
        if (parent == null) {
            return;
        }

        if (!node.mark) {
            node.mark = true;
        } else {
            cut(node, parent);
            cascadingCut(parent);
        }
    }

    private Node extractMinInternal() {
        Node z = min;
        if (z == null) {
            return null;
        }

        if (z.child != null) {
            List<Node> children = collectSiblings(z.child);
            for (Node child : children) {
                child.parent = null;
                child.left = child;
                child.right = child;
                addToRootList(child);
            }
            z.child = null;
            z.degree = 0;
        }

        if (z == z.right) {
            min = null;
        } else {
            Node next = z.right;
            removeFromList(z);
            min = next;
            consolidate();
        }

        size--;
        z.left = z;
        z.right = z;
        z.parent = null;
        z.mark = false;
        return z;
    }

    private void consolidate() {
        Map<Integer, Node> degreeTable = new HashMap<>();
        List<Node> roots = collectSiblings(min);

        for (Node root : roots) {
            Node x = root;
            x.left = x;
            x.right = x;
            int degree = x.degree;

            while (degreeTable.containsKey(degree)) {
                Node y = degreeTable.remove(degree);
                if (x.key > y.key) {
                    Node tmp = x;
                    x = y;
                    y = tmp;
                }
                link(y, x);
                degree = x.degree;
            }
            degreeTable.put(degree, x);
        }

        min = null;
        for (Node node : degreeTable.values()) {
            node.left = node;
            node.right = node;
            if (min == null) {
                min = node;
            } else {
                addToRootList(node);
            }
        }
    }

    private void link(Node childNode, Node parentNode) {
        removeFromList(childNode);
        childNode.parent = parentNode;
        childNode.mark = false;

        if (parentNode.child == null) {
            parentNode.child = childNode;
            childNode.left = childNode;
            childNode.right = childNode;
        } else {
            Node child = parentNode.child;
            childNode.left = child;
            childNode.right = child.right;
            child.right.left = childNode;
            child.right = childNode;
        }

        parentNode.degree++;
    }

    private void removeFromList(Node node) {
        node.left.right = node.right;
        node.right.left = node.left;
    }

    private List<Node> collectSiblings(Node start) {
        List<Node> nodes = new ArrayList<>();
        if (start == null) {
            return nodes;
        }

        Node current = start;
        do {
            nodes.add(current);
            current = current.right;
        } while (current != start);

        return nodes;
    }
}

