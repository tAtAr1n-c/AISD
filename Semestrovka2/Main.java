package Semestrovka2;

import static Semestrovka2.Floyd_Uorshill.*;

public class Main {
    public static void main(String[] args) {
        int[][] dist = {
                {0,   5,   INF, 10},
                {2, 0,   3,   INF},
                {2, INF, 0,   1},
                {INF, 5, INF, 0}
        };

        floyd(dist);
        printMatrix(dist);
    }
}
