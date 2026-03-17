public class Main{
    public static void main(String[] args){
        System.out.println(findDeletedNumber(new int[]{1, 2, 3, 4, 5, 6,7, 8, 9, 10, 11, 13}));
    }

    public static int findDeletedNumber(int[] arr){
        int res = 0;
        for(int i = 0; i <= arr[arr.length - 1]; i++){
            res += i;
        }
        for(int i = 0; i < arr.length; i++){
            res -= arr[i];
        }
        return res;
    }
}
