package Basic;

public class Tree<T extends Comparable<T>> {
    private TreeNode<T> root;
    //private int height;


    public Tree(){
        //this.height = 0;
    }
    public Tree(TreeNode<T> root) {
        this.root = root;
       // height = 1;
    }
//    public void add(T value){
//        this.root = new TreeNode<T>(value);
//        if(root == null){
//
//        }else{
//            TreeNode<T> current = root;
//
//        }
//    }

    
    private void add(TreeNode<T> current, T value){
        if(current.getData().compareTo(value) > 0){
            if(current.getLeft() == null){
                current.setLeft(new TreeNode<>(value));
            }else{
                add(current.getLeft(), value);
            }
        }else if(current.getData().compareTo(value) <= 0){
            if(current.getRight() == null){
                current.setRight(new TreeNode<>(value));
            }else{
                add(current.getRight(), value);
            }
        }
    }

}
