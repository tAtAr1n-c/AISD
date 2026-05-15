public class Tree<T extends Comparable<T>> {
    private TreeNode<T> root;
    private int height;

    static class TreeNode<T> {
        private T data;
        private TreeNode<T> left;
        private TreeNode<T> right;
        private TreeNode<T> parent;

        public TreeNode(T data) {
            this.data = data;
        }

        public TreeNode(T data, TreeNode<T> parent) {
            this.data = data;
            this.parent = parent;
        }

        public TreeNode(T data, TreeNode<T> left, TreeNode<T> right, TreeNode<T> parent) {
            this.data = data;
            this.left = left;
            this.right = right;
            this.parent = parent;
        }

        public TreeNode<T> getParent() {
            return parent;
        }
        public void setParent(TreeNode<T> parent) {
            this.parent = parent;
        }
        public T getData() {
            return data;
        }

        public TreeNode<T> getLeft() {
            return left;
        }

        public TreeNode<T> getRight() {
            return right;
        }

        public void setData(T data) {
            this.data = data;
        }

        public void setLeft(TreeNode<T> left) {
            this.left = left;
        }

        public void setRight(TreeNode<T> right) {
            this.right = right;
        }
        public boolean isRoot(){
            return this.parent == null;
        }
        public boolean isLeaf(){
            return this.left == null && this.right == null;
        }
    }

    public Tree(TreeNode<T> root) {
        this.root = root;
        this.height = 1;
    }

    public Tree() {
        this.root = null;
        this.height = 0;
    }
    private int computeHeight(TreeNode<T> node) {
        if (node == null) return 0;
        return 1 + Math.max(computeHeight(node.getLeft()), computeHeight(node.getRight()));
    }
    public int getHeight() {
        return computeHeight(root);
    }
    public TreeNode<T> getRoot(){
        return root;
    }
    public void add(T value){
        TreeNode<T> current = root;
        if(this.root == null){
            this.root = new TreeNode<>(value);
        }
        else {
            add(current , value);
        }
        height = getHeight();
    }
    private void add(TreeNode<T> current , T value){
        if(current.getData().compareTo(value) > 0){
            if(current.getLeft() == null){
                current.setLeft(new TreeNode<T>(value));
            }
            else{
                add(current.getLeft() , value);
            }
        }else if(current.getData().compareTo(value) <= 0){
            if(current.getRight() == null){
                current.setRight(new TreeNode<T>(value));
            }
            else{
                add(current.getRight() , value);
            }
        }
    }
    //Basa
    public TreeNode<T> get(T data){
        TreeNode<T> current = root;
        while(current != null){
            if(data.compareTo(current.getData()) == 0){
                return current;
            }
            else if(current.getData().compareTo(data) < 0){
                current = current.getRight();
            }
            else{
                current = current.getLeft();
            }
        }
        return null;
    }
    //Раз уж тема recursion
    //Не смог сделать без изменения сигнатурки чуточку
    private TreeNode<T> getRecursion(TreeNode<T> current ,T data){
        if(current == null){
            return null;
        }
        else if(current.getData() == data){
            return current;
        }
        else if(current.getData().compareTo(data) < 0){
            return getRecursion(current.getLeft() , data);
        }
        else{
            return getRecursion(current.getRight() , data);
        }
    }
    public TreeNode<T> getRecursionForUser(T data){
        return getRecursion(root , data);
    }
    public TreeNode<T> remove(T data) {
        TreeNode<T> current = root;
        while (current != null && current.getData().compareTo(data) != 0) {
            if (current.getData().compareTo(data) < 0) {
                current = current.getRight();
            } else {
                current = current.getLeft();
            }
        }
        if (current == null) {
            height = getHeight();
            return null;
        }
        TreeNode<T> parent = current.getParent();
        //нет детей
        if (current.getLeft() == null && current.getRight() == null) {
            if (parent == null) {
                root = null;
            } else if (parent.getLeft() == current) {
                parent.setLeft(null);
            } else {
                parent.setRight(null);
            }
        }
        //один ребёнок
        else if (current.getLeft() == null) {
            TreeNode<T> child = current.getRight();
            if (parent == null) {
                root = child;
            } else if (parent.getLeft() == current) {
                parent.setLeft(child);
            } else {
                parent.setRight(child);
            }
            child.setParent(parent);
        }
        else if (current.getRight() == null) {
            TreeNode<T> child = current.getLeft();
            if (parent == null) {
                root = child;
            } else if (parent.getLeft() == current) {
                parent.setLeft(child);
            } else {
                parent.setRight(child);
            }
            child.setParent(parent);
        }
        //два ребёнка
        else {
            TreeNode<T> successor = getMin(current.getRight());
            current.setData(successor.getData());
            removeNode(successor);
        }
        height = getHeight();
        return current;
    }
    private TreeNode<T> getMin(TreeNode<T> node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }
    private void removeNode(TreeNode<T> node) {
        TreeNode<T> parent = node.getParent();
        TreeNode<T> child = null;
        if (node.getLeft() != null) {
            child = node.getLeft();
        } else if (node.getRight() != null) {
            child = node.getRight();
        }

        if (parent == null) {
            root = child;
        } else if (parent.getLeft() == node) {
            parent.setLeft(child);
        } else {
            parent.setRight(child);
        }
        if (child != null) {
            child.setParent(parent);
        }
    }
    public void directTreeTraversal(TreeNode<T> node) {
        if (node == null) return;
        System.out.println(node.getData());
        directTreeTraversal(node.getLeft());
        directTreeTraversal(node.getRight());
    }
    public void symmetricTreeTraversal(TreeNode<T> node) {
        if (node == null) return;
        symmetricTreeTraversal(node.getLeft());
        System.out.println(node.getData());
        symmetricTreeTraversal(node.getRight());
    }
    public void reverseTreeTraversal(TreeNode<T> node) {
        if (node == null) return;
        reverseTreeTraversal(node.getLeft());
        reverseTreeTraversal(node.getRight());
        System.out.println(node.getData());
    }
    public void printSumOfChildren(TreeNode<Integer> node) {
        if (node == null) {
            return;
        }
        int sum = 0;
        if (node.getLeft() != null) {
            sum += node.getLeft().getData();
        }
        if (node.getRight() != null) {
            sum += node.getRight().getData();
        }
        System.out.println("Узел: " + node.getData() + ", сумма детей: " + sum);
        printSumOfChildren(node.getLeft());
        printSumOfChildren(node.getRight());
    }
}
