package Semestrovka.Tests;

import Semestrovka.FibonacciHeap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RemoveOnlyBenchmark {

    private static final int DEFAULT_WARMUP = 10;
    private static final int DEFAULT_REPEATS = 40;
    private static final int DEFAULT_BATCH = 20;

    private static final class Stat {
        private final String dataset;
        private final int size;
        private final double medianMs;
        private final double meanMs;
        private final double minMs;
        private final double maxMs;

        private Stat(String dataset, int size, double medianMs, double meanMs, double minMs, double maxMs) {
            this.dataset = dataset;
            this.size = size;
            this.medianMs = medianMs;
            this.meanMs = meanMs;
            this.minMs = minMs;
            this.maxMs = maxMs;
        }
    }

    public static void main(String[] args) throws Exception {
        Path datasetDir = args.length > 0 ? Paths.get(args[0]) : Paths.get("src/Semestrovka/input_sets");
        Path outputTxt = args.length > 1 ? Paths.get(args[1]) : datasetDir.resolve("remove_benchmark_results.txt");
        int warmupRounds = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_WARMUP;
        int repeats = args.length > 3 ? Integer.parseInt(args[3]) : DEFAULT_REPEATS;
        int batch = args.length > 4 ? Integer.parseInt(args[4]) : DEFAULT_BATCH;

        List<Path> datasets = readDatasetList(datasetDir);
        if (datasets.isEmpty()) {
            System.err.println("No dataset files found in " + datasetDir.toAbsolutePath());
            return;
        }

        List<Stat> stats = new ArrayList<>();
        for (Path dataset : datasets) {
            List<Integer> data = readIntegers(dataset);
            Stat stat = benchmarkOne(dataset.getFileName().toString(), data, warmupRounds, repeats, batch);
            stats.add(stat);
            System.out.printf("Done %-26s n=%5d median=%.4f ms%n", stat.dataset, stat.size, stat.medianMs);
        }

        writeResults(outputTxt, stats, warmupRounds, repeats, batch);
        System.out.println("Saved: " + outputTxt.toAbsolutePath());
    }

    private static Stat benchmarkOne(String datasetName, List<Integer> data, int warmupRounds, int repeats, int batch) {
        for (int i = 0; i < warmupRounds; i++) {
            measureRemoveNs(data, batch);
        }

        List<Long> samplesNs = new ArrayList<>(repeats);
        long sum = 0L;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        for (int i = 0; i < repeats; i++) {
            long ns = measureRemoveNs(data, batch);
            samplesNs.add(ns);
            sum += ns;
            if (ns < min) {
                min = ns;
            }
            if (ns > max) {
                max = ns;
            }
        }

        samplesNs.sort(Long::compare);
        long medianNs;
        int mid = repeats / 2;
        if ((repeats & 1) == 0) {
            medianNs = (samplesNs.get(mid - 1) + samplesNs.get(mid)) / 2;
        } else {
            medianNs = samplesNs.get(mid);
        }

        double meanMs = sum / (double) repeats / 1_000_000.0;
        double medianMs = medianNs / 1_000_000.0;
        double minMs = min / 1_000_000.0;
        double maxMs = max / 1_000_000.0;

        return new Stat(datasetName, data.size(), medianMs, meanMs, minMs, maxMs);
    }

    private static long measureRemoveNs(List<Integer> data, int batch) {
        int n = data.size();
        long totalNs = 0L;

        for (int b = 0; b < batch; b++) {
            FibonacciHeap<Integer> heap = buildHeap(data);
            int valueToDelete = data.get((b * 9973) % n);

            long start = System.nanoTime();
            heap.delete(valueToDelete);
            long end = System.nanoTime();

            totalNs += (end - start);
        }

        return totalNs / batch;
    }

    private static FibonacciHeap<Integer> buildHeap(List<Integer> data) {
        FibonacciHeap<Integer> heap = new FibonacciHeap<>();
        for (int value : data) {
            heap.add(value, value);
        }
        return heap;
    }

    private static List<Path> readDatasetList(Path datasetDir) throws IOException {
        Path manifest = datasetDir.resolve("manifest.csv");
        List<Path> result = new ArrayList<>();

        if (Files.exists(manifest)) {
            try (BufferedReader reader = Files.newBufferedReader(manifest)) {
                reader.readLine();
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(",", 2);
                    Path file = datasetDir.resolve(parts[0].trim());
                    if (Files.exists(file)) {
                        result.add(file);
                    }
                }
            }
            return result;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(datasetDir, "dataset_*.txt")) {
            for (Path path : stream) {
                result.add(path);
            }
        }
        result.sort(Comparator.comparing(path -> path.getFileName().toString()));
        return result;
    }

    private static List<Integer> readIntegers(Path file) throws IOException {
        List<Integer> values = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    values.add(Integer.parseInt(line));
                }
            }
        }
        return values;
    }

    private static void writeResults(Path outputTxt, List<Stat> stats, int warmupRounds, int repeats, int batch)
            throws IOException {
        Files.createDirectories(outputTxt.getParent());

        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(outputTxt))) {
            out.println("# FibonacciHeap remove benchmark (stable mode)");
            out.printf("# warmup_rounds=%d repeats=%d batch=%d%n", warmupRounds, repeats, batch);
            out.println("# note: one remove per heap; heap rebuilt before each remove");
            out.println("# columns: dataset size median_ms mean_ms min_ms max_ms");

            for (Stat stat : stats) {
                out.printf(
                        "%s %d %.6f %.6f %.6f %.6f%n",
                        stat.dataset,
                        stat.size,
                        stat.medianMs,
                        stat.meanMs,
                        stat.minMs,
                        stat.maxMs
                );
            }
        }
    }
}

