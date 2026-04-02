public class Main{
  public static void main(String[] args) {
        int[] arr = new int[]{1, 2, 2, 3, 4, 4, 5};
        int right = arr.length - 1;
        int target = 7;
        int left = 0;
        while(arr[left] + arr[right] != target) {
            if (arr[left] + arr[right] > target) {
                right--;
            } else {
                left++;
            }
        }
        System.out.println(arr[right]);
        System.out.println(arr[left]);
    }
}
