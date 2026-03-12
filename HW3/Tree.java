package Basic;

public class Tree<T extends Comparable<T>> {
    private TreeNode<T> root;
    private int size;

    //todo: реализовать в домашке
    // private int height;

    //todo: чтобы быстро создать конструктор или геттеры\сеттеры
    // можно нажать комбинацию клавиш alt + insert (на Mac -- cmd + n)

    public Tree() {
       this.height = 0;
    }

    public Tree(TreeNode<T> root){
        this.root = root;
        this.height = 1;
    }

    public void add(T value) {
        TreeNode<T> node = new TreeNode<>(value);

        if (this.root == null) {
            this.root = node;
//            this.height = 1;
        } else {
            add(this.root, value);
        }
        size++;
    }

    private void add(TreeNode<T> current, T value) {
        if (current.getValue().compareTo(value) > 0) {
            if (current.getLeft() == null) {
                current.setLeft(new TreeNode<>(value));
            } else {
                add(current.getLeft(), value);
            }
        } else if (current.getValue().compareTo(value) <= 0) {
            if (current.getRight() == null) {
                current.setRight(new TreeNode<>(value));
            } else {
                add(current.getRight(), value);
            }
        }
    }


    public void preOrder() {
        preOrder(this.root);
    }
    private void preOrder(TreeNode<T> current) {
        if (current == null) {
            return;
        }
        System.out.println(current.getValue());
        preOrder(current.getLeft());
        preOrder(current.getRight());
    }

    public void inOrder() {
        preOrder(this.root);
    }
    private void inOrder(TreeNode<T> current) {
        if (current == null) {
            return;
        }
        preOrder(current.getLeft());
        System.out.println(current.getValue());
        preOrder(current.getRight());
    }

    public void postOrder() {
        postOrder(this.root);
    }
    private void postOrder(TreeNode<T> current) {
        if (current == null) return;
        postOrder(current.getLeft());
        postOrder(current.getRight());
        System.out.println(current.getValue());
    }

    private TreeNode<T> leftAngle(){
        TreeNode<T> current = this.root;
        while(current.getLeft()!= null){
            current = current.getLeft();
        }
        return current;
    }

    public TreeNode<T> get(T data){
        TreeNode<T> current = this.root;
        while(current != null){
            if(current.getValue().compareTo(data) == 0){
                return current;
            }else if(current.getValue().compareTo(data) < 0){
                current = current.getRight();
            }else{
                current = current.getLeft();
            }
        }
        return null;
    }

    public int height() {
        return height(this.root);
    }
    private int height(TreeNode<T> node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(height(node.getLeft()), height(node.getRight()));
    }
