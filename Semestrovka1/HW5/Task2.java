public class Main{
        public static void main(String[] args) {

          int[] nums = new int[]{3, 30, 34, 5, 9};

            String[] arr = new String[nums.length];
            for(int i = 0; i < nums.length; i++){
                arr[i] = String.valueOf(nums[i]);
            }

            Arrays.sort(arr, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return lexCompare(s1, s2);
                }
            });

            System.out.println(Arrays.toString(arr));
        }

        static int lexCompare(String a, String b) {
            int n = Math.min(a.length(), b.length());
            for (int i = 0; i < n; i++) {
                char c1 = a.charAt(i);
                char c2 = b.charAt(i);
                if (c1 != c2) {
                    return c2 - c1;
                }
            }
            return b.length() - a.length();
        }
}
