public class Task1 {
    static void main(String[] args) {
        //Отсортированный массив
        int[] numb = {1 , 3 , 5 , 6 , 7};
        int target = 6;
        int x = -1;
        int y = -1;
        for(int i = 0 ; i < numb.length ; i++){
            if(numb[i] <= target && i != numb.length - 1 && numb[i + 1] >= target ){
                x = i;
                y = i + 1;
            }
        }
        if(x != - 1 && y != -1){
            System.out.println(Math.min(x , y));
        }
        else if(target > numb[numb.length -1]){
            System.out.println(numb.length -1);
        }
        else{
            System.out.println(0);
        }
    }
}
