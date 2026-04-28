package Semestrovka2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class BenchmarkRunner {
    private static final int INF = 1_000_000_000;
    private static final int WARMUP_ROUNDS = 8;
    private static final int MEASURE_ROUNDS = 12;

    private static final Path TESTS_DIR = Path.of("/Users/peace/IdeaProjects/Spiski/src/Semestrovka2/Tests");
    private static final Path BENCHMARKS_DIR = Path.of("/Users/peace/IdeaProjects/Spiski/src/Semestrovka2/Becnhmarks");

    public static void main(String[] args) throws IOException {
        Files.createDirectories(BENCHMARKS_DIR);

        List<Path> testFiles = Files.list(TESTS_DIR)
                .filter(path -> path.getFileName().toString().startsWith("test_"))
                .filter(path -> path.getFileName().toString().endsWith(".txt"))
                .sorted(Comparator.comparing(Path::toString))
                .toList();

        if (testFiles.isEmpty()) {
            System.out.println("Тестовые файлы не найдены: " + TESTS_DIR);
            return;
        }

        List<TestCase> testCases = new ArrayList<>();
        for (Path testFile : testFiles) {
            testCases.add(readTestCase(testFile));
        }

        System.out.println("Прогрев JVM: " + WARMUP_ROUNDS + " кругов по " + testCases.size() + " тестов");
        warmUp(testCases);

        Path csvPath = BENCHMARKS_DIR.resolve("floyd_warshall_benchmark_results.csv");
        Path markdownPath = BENCHMARKS_DIR.resolve("floyd_warshall_benchmark_report.md");

        List<Result> results = new ArrayList<>();

        for (TestCase testCase : testCases) {
            long[] times = new long[MEASURE_ROUNDS];
            long iterations = 0;

            for (int round = 0; round < MEASURE_ROUNDS; round++) {
                int[][] matrixCopy = copyMatrix(testCase.matrix);

                long startTime = System.nanoTime();
                iterations = floydWithIterations(matrixCopy);
                long stopTime = System.nanoTime();

                times[round] = stopTime - startTime;
            }

            Arrays.sort(times);

            long minNs = times[0];
            long medianNs = times[times.length / 2];
            long avgNs = average(times);

            Result result = new Result(
                    testCase.name,
                    testCase.size,
                    testCase.size * testCase.size,
                    iterations,
                    minNs,
                    medianNs,
                    avgNs
            );

            results.add(result);

            System.out.printf(
                    Locale.US,
                    "%s | n=%d | elements=%d | iterations=%d | avg=%.3f ms%n",
                    result.fileName,
                    result.size,
                    result.elements,
                    result.iterations,
                    result.avgNs / 1_000_000.0
            );
        }

        writeCsv(csvPath, results);
        writeMarkdown(markdownPath, results);

        System.out.println("CSV: " + csvPath);
        System.out.println("Report: " + markdownPath);
    }

    private static void warmUp(List<TestCase> testCases) {
        for (int round = 0; round < WARMUP_ROUNDS; round++) {
            for (TestCase testCase : testCases) {
                floydWithIterations(copyMatrix(testCase.matrix));
            }
        }
    }

    private static long floydWithIterations(int[][] dist) {
        int n = dist.length;
        long iterations = 0;

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    iterations++;

                    if (dist[i][k] != INF && dist[k][j] != INF) {
                        int newDistance = dist[i][k] + dist[k][j];

                        if (newDistance < dist[i][j]) {
                            dist[i][j] = newDistance;
                        }
                    }
                }
            }
        }

        return iterations;
    }

    private static TestCase readTestCase(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        int n = Integer.parseInt(lines.get(0).trim());
        int[][] matrix = new int[n][n];

        for (int i = 0; i < n; i++) {
            String[] values = lines.get(i + 1).trim().split("\\s+");

            for (int j = 0; j < n; j++) {
                matrix[i][j] = Integer.parseInt(values[j]);
            }
        }

        return new TestCase(path.getFileName().toString(), n, matrix);
    }

    private static int[][] copyMatrix(int[][] source) {
        int[][] copy = new int[source.length][source.length];

        for (int i = 0; i < source.length; i++) {
            System.arraycopy(source[i], 0, copy[i], 0, source[i].length);
        }

        return copy;
    }

    private static long average(long[] values) {
        long sum = 0;

        for (long value : values) {
            sum += value;
        }

        return sum / values.length;
    }

    private static void writeCsv(Path path, List<Result> results) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("file,n,elements,iterations,min_ns,median_ns,avg_ns,min_ms,median_ms,avg_ms");
            writer.newLine();

            for (Result result : results) {
                writer.write(String.format(
                        Locale.US,
                        "%s,%d,%d,%d,%d,%d,%d,%.6f,%.6f,%.6f",
                        result.fileName,
                        result.size,
                        result.elements,
                        result.iterations,
                        result.minNs,
                        result.medianNs,
                        result.avgNs,
                        result.minNs / 1_000_000.0,
                        result.medianNs / 1_000_000.0,
                        result.avgNs / 1_000_000.0
                ));
                writer.newLine();
            }
        }
    }

    private static void writeMarkdown(Path path, List<Result> results) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("# Бенчмарк алгоритма Флойда-Уоршелла");
            writer.newLine();
            writer.newLine();
            writer.write("- Прогрев JVM: " + WARMUP_ROUNDS + " кругов");
            writer.newLine();
            writer.write("- Измерения: " + MEASURE_ROUNDS + " запусков на каждый набор");
            writer.newLine();
            writer.write("- Время измеряется через `System.nanoTime()`");
            writer.newLine();
            writer.write("- Время генерации и чтения данных не учитывается");
            writer.newLine();
            writer.write("- Итерации считаются в самом вложенном цикле `j`");
            writer.newLine();
            writer.newLine();
            writer.write("| Файл | n | Элементов | Итераций | Min ms | Median ms | Avg ms |");
            writer.newLine();
            writer.write("|---|---:|---:|---:|---:|---:|---:|");
            writer.newLine();

            for (Result result : results) {
                writer.write(String.format(
                        Locale.US,
                        "| %s | %d | %d | %d | %.6f | %.6f | %.6f |",
                        result.fileName,
                        result.size,
                        result.elements,
                        result.iterations,
                        result.minNs / 1_000_000.0,
                        result.medianNs / 1_000_000.0,
                        result.avgNs / 1_000_000.0
                ));
                writer.newLine();
            }
        }
    }

    private record TestCase(String name, int size, int[][] matrix) {
    }

    private record Result(
            String fileName,
            int size,
            int elements,
            long iterations,
            long minNs,
            long medianNs,
            long avgNs
    ) {
    }
}
