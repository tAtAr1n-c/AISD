import java.util.HashSet;

public class Task6 {
    static void main(String[] args) {
        int[] first = {1 , 2 ,3 , 4 ,5 , 6};
        int[] second = {1 , 3 , 5};
        int[] third = {1 , 5 , 6 , 7,  8, 10 , 9};
        HashSet<Integer> firstAsHash = new HashSet<>();
        HashSet<Integer> containNandM = new HashSet<>();
        HashSet<Integer> result = new HashSet<>();
        for (int j : first) {
            firstAsHash.add(j);
        }
        for (int i : second) {
            if (firstAsHash.contains(i)) {
                containNandM.add(i);
            }
        }
        for (int j : third) {
            if (containNandM.contains(j)) {
                result.add(j);
            }
        }
        System.out.println(result.toString());
    }
}
