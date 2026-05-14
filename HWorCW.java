import java.util.Arrays;

public class HWorCW {
    public static void main(String[] args){

    }

    static class Task1{
        public static boolean isSquare(long x){
            long root = (long)Math.sqrt(x);
            return root * root == x || (root + 1)* (root + 1) == x;
        }

        public static boolean isFibonachi(long x){
            return isSquare(5 * x * x + 4) || isSquare(5 * x * x - 4);
        }
    }

    static class Task2{
        public static int lastDigit(long n){
            n %= 60;
            int a = 0;
            int b = 1;
            if(n == 0 || n == 1) return (int) n;
            for(int i = 2; i <= n; i++){
                int c = (a + b) % 10;
                a = b;
                b = c;
            }
            return b;
        }
    }

    static class Task3{

        static void printFibonacciChars(String s) {
            int n = s.length();
            int a = 1;
            int b = 2;
            if (n >= 1) System.out.print(s.charAt(0));
            while (b <= n) {
                System.out.print(s.charAt(b - 1));
                int c = a + b;
                a = b;
                b = c;
            }
        }



    }

    static class Task4{
        public static int turtle(int[][] a){
            int n = a.length;
            int m =  a[0].length;
            int[][] dp = new int[n][m];
            dp[0][0] = a[0][0];
            for (int j = 1; j < m; j++) {
                dp[0][j] = dp[0][j - 1] + a[0][j];
            }

            for (int i = 1; i < n; i++) {
                dp[i][0] = dp[i - 1][0] + a[i][0];
            }
            for (int i = 1; i < n; i++) {
                for (int j = 1; j < m; j++) {
                    dp[i][j] = a[i][j] + Helpers.min(dp[i - 1][j], dp[i][j - 1]);
                }
            }
            return dp[n - 1][m - 1];
        }
    }

    static class Task5{
        public static int nail(int[] a){
            int n = a.length;
            Arrays.sort(a);
            int[] dp =  new int[n];
            dp[0] = 1000000000;
            dp[1] = a[1] - a[0];

            for (int i = 2; i < n; i++) {
                int length = a[i] - a[i - 1];
                dp[i] = Helpers.min(dp[i - 1], dp[i - 2]) + length;
            }

            return dp[n - 1];
        }
    }

    static class Helpers{
        public static int min(int a, int b){
            return a < b ? a : b;
        }

        //ну sqrt бессмысленно писать так как ну я не могу написать алгоритм быстрее чем в джаве


    }
}
