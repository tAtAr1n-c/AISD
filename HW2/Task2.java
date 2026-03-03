public static int[] sortedMix(int[] arr1, int[] arr2){
        int[] res = new int[arr1.length + arr2.length];
        int obj = 0;
        int curs1 = 0;
        int curs2 = 0;
        while(curs1 != arr1.length && curs2 != arr2.length){
            if(arr1[curs1] > arr2[curs2]){
                res[obj] = arr2[curs2];
                curs2++;
            }else{
                res[obj] = arr1[curs1];
                curs1++;
            }
            obj++;
        }
        while (curs1 < arr1.length) {
            res[obj] = arr1[curs1];
            curs1++;
            obj++;
        }

        while (curs2 < arr2.length) {
            res[obj] = arr2[curs2];
            curs2++;
            obj++;
        }
        return res;
    }
