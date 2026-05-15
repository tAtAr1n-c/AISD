public class Task2 {
    static void main(String[] args) {
        int first = 10;
        int second = 101;
        System.out.println(first * first == second);
        System.out.println(first == multiplyByShifts(second , second));
        //тк умножение в джава уже реализовано, через сдвиги
    }
    public static long multiplyByShifts(long a, long b) {
        long result = 0;
        long x = Math.abs(a);
        long y = Math.abs(b);

        while (x != 0) {
            if ((x & 1) == 1) {
                result += y;
            }
            x >>= 1;
            y <<= 1;
        }
        if ((a < 0) ^ (b < 0)) {
            result = -result;
        }
        return result;
    }
}
