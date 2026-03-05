public class Postfix{
    public static void main(String[] args){
        System.out.println(eval("3 4 - 7 +"));
    }
    public static double eval(String input){
        String[] chisla = splitPoPass(input);
        NewStack stack = new NewStack(chisla.length);
        for(int i = 0; i < chisla.length; i++){
            String k = chisla[i];
            if(k.length() == 1 && isOp(k.charAt(0))){
                double b = stack.remove();
                double a = stack.remove();
                stack.add(operation(a, b, k.charAt(0)));
            }else{
                stack.add(Double.valueOf(k));
            }
        }
        return stack.remove();
    }


    public static String[] splitPoPass(String str){
        int count = 0;
        for(int i = 0; i < str.length(); i++){
            if(str.charAt(i) == ' ') count++;
        }
        int ukaz = 0;
        String[] res = new String[count + 1];
        for(int i = 0; i < str.length(); i++){
            if(str.charAt(i) != ' '){
                res[ukaz] = str.charAt(i) + "";
                ukaz++;
            }
        }
        return res;
    }

    public static boolean isOp(char c){
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    static double operation(double a, double b, char op){
        if(op == '+') return a+b;
        if(op == '-') return a-b;
        if(op == '*') return a*b;
        if(op == '/') return  a/b;
        throw new IllegalArgumentException();
    }
}
