package Semestrovka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FibonacciHeap<T> {
    private static final class Node<T> {
        private int priority;
        private final T value;
        private int degree;
        private boolean mark;
        private Node<T> parent;
        private Node<T> child;
        private Node<T> left;
        private Node<T> right;

        private Node(int priority, T value) {
            this.priority = priority;
            this.value = value;
            this.left = this;
            this.right = this;
        }
    }

    private Node<T> min;
    private int size;
    // 1) Добавление
    public void add(int priority, T value) {
        Node<T> node = new Node<>(priority, value);
        addToRootList(node);
        size++;
    }

    public boolean isEmpty() {
        return min == null;
    }

    public T extractMinValue() {
        Node<T> node = extractMinInternal();
        return node == null ? null : node.value;
    }


    private void addToRootList(Node<T> node) {
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

        if (node.priority < min.priority) {
            min = node;
        }
    }


    // 2) Поиск
    public boolean search(T value) {
        return searchInList(min, value) != null;
    }

    private Node<T> searchInList(Node<T> start, T value) {
        if (start == null) {
            return null;
        }

        Node<T> current = start;
        do {
            if (Objects.equals(current.value, value)) {
                return current;
            }
            Node<T> fromChild = searchInList(current.child, value);
            if (fromChild != null) {
                return fromChild;
            }
            current = current.right;
        } while (current != start);

        return null;
    }



    // 3) Удаление
    public boolean delete(T value) {
        Node<T> node = searchInList(min, value);
        if (node == null) {
            return false;
        }
        decreasePriority(node, Integer.MIN_VALUE);
        extractMinInternal();
        return true;
    }
    private void decreasePriority(Node<T> node, int newPriority) {
        if (newPriority > node.priority) {
            throw new IllegalArgumentException("New priority is greater than current priority");
        }

        node.priority = newPriority;
        Node<T> parent = node.parent;

        if (parent != null && node.priority < parent.priority) {
            cut(node, parent);
            cascadingCut(parent);
        }

        if (min == null || node.priority < min.priority) {
            min = node;
        }
    }

    private void cut(Node<T> node, Node<T> parent) {
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
    private void removeFromList(Node<T> node) {
        node.left.right = node.right;
        node.right.left = node.left;
    }
    private void cascadingCut(Node<T> node) {
        Node<T> parent = node.parent;
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

    private Node<T> extractMinInternal() {
        Node<T> z = min;
        if (z == null) {
            return null;
        }

        if (z.child != null) {
            List<Node<T>> children = collectSiblings(z.child);
            for (Node<T> child : children) {
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
            Node<T> next = z.right;
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
        Map<Integer, Node<T>> degreeTable = new HashMap<>();
        List<Node<T>> roots = collectSiblings(min);

        for (Node<T> root : roots) {
            Node<T> x = root;
            x.left = x;
            x.right = x;
            int degree = x.degree;

            while (degreeTable.containsKey(degree)) {
                Node<T> y = degreeTable.remove(degree);
                if (x.priority > y.priority) {
                    Node<T> tmp = x;
                    x = y;
                    y = tmp;
                }
                link(y, x);
                degree = x.degree;
            }
            degreeTable.put(degree, x);
        }

        min = null;
        for (Node<T> node : degreeTable.values()) {
            node.left = node;
            node.right = node;
            if (min == null) {
                min = node;
            } else {
                addToRootList(node);
            }
        }
    }

    private void link(Node<T> childNode, Node<T> parentNode) {
        removeFromList(childNode);
        childNode.parent = parentNode;
        childNode.mark = false;

        if (parentNode.child == null) {
            parentNode.child = childNode;
            childNode.left = childNode;
            childNode.right = childNode;
        } else {
            Node<T> child = parentNode.child;
            childNode.left = child;
            childNode.right = child.right;
            child.right.left = childNode;
            child.right = childNode;
        }

        parentNode.degree++;
    }

    private List<Node<T>> collectSiblings(Node<T> start) {
        List<Node<T>> nodes = new ArrayList<>();
        if (start == null) {
            return nodes;
        }

        Node<T> current = start;
        do {
            nodes.add(current);
            current = current.right;
        } while (current != start);

        return nodes;
    }
}
