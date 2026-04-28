package Semestrovka2;

public class Floyd_Uorshill {
    static final int INF = 1_000_000_000;

    public static void floyd(int[][] dist) {
        int n = dist.length;
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] != INF && dist[k][j] != INF) {
                        if (dist[i][k] + dist[k][j] < dist[i][j]) {
                            dist[i][j] = dist[i][k] + dist[k][j];
                        }
                    }
                }
            }
        }
    }
    public static void printMatrix(int[][] dist) {
        System.out.println("Матрица кратчайших расстояний:");
        System.out.println();

        System.out.print("      ");

        for (int i = 0; i < dist.length; i++) {
            System.out.printf("%6d", i + 1);
        }

        System.out.println();

        System.out.print("      ");

        for (int i = 0; i < dist.length; i++) {
            System.out.print("------");
        }

        System.out.println();

        for (int i = 0; i < dist.length; i++) {
            System.out.printf("%3d | ", i + 1);

            for (int j = 0; j < dist.length; j++) {
                if (dist[i][j] == INF) {
                    System.out.printf("%6s", "∞");
                } else {
                    System.out.printf("%6d", dist[i][j]);
                }
            }

            System.out.println();
        }
    }

}
