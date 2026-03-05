public class NewStack {
    private double[] data;
    private int next;

    public NewStack(int size){
        if(size < 0) throw new IllegalArgumentException();
        data = new double[size];
        next = 0;
    }
    public void add(double value){
        if(next == data.length) throw new IllegalArgumentException();
        data[next] = value;
        next++;
    }
    public double remove(){
        if(next == 0) throw new IllegalArgumentException();
        double value = data[next-1];
        data[next-1] = 0;
        next--;
        return value;
    }
    public int size(){
        return next;
    }
}
