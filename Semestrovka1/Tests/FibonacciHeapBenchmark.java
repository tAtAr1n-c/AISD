package Semestrovka.Tests;

import Semestrovka.FibonacciHeap;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FibonacciHeapBenchmark {

    private static final int DEFAULT_REPEATS = 5;
    private static final String DEFAULT_DATASET_DIR = "src/Semestrovka/input_sets";

    private static final class Result {
        private long addNsTotal;
        private long searchNsTotal;
        private long deleteNsTotal;
    }

    public static void main(String[] args) throws Exception {
        Path datasetDir = args.length > 0 ? Paths.get(args[0]) : Paths.get(DEFAULT_DATASET_DIR);
        int repeats = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_REPEATS;

        if (!Files.isDirectory(datasetDir)) {
            System.err.println("Dataset directory not found: " + datasetDir.toAbsolutePath());
            return;
        }

        List<Path> datasets = readDatasetList(datasetDir);
        if (datasets.isEmpty()) {
            System.err.println("No datasets found in: " + datasetDir.toAbsolutePath());
            return;
        }

        System.out.println("Datasets: " + datasets.size() + ", repeats per dataset: " + repeats);
        System.out.println("Timing unit: nanoseconds (also shown in milliseconds)");
        System.out.println();

        for (Path dataset : datasets) {
            List<Integer> data = readIntegers(dataset);
            int n = data.size();
            int searchOps = Math.min(200, Math.max(20, n / 20));
            int deleteOps = Math.min(50, Math.max(10, n / 200));

            Result result = new Result();
            for (int i = 0; i < repeats; i++) {
                result.addNsTotal += measureAdd(data);
                result.searchNsTotal += measureSearch(data, searchOps);
                result.deleteNsTotal += measureDelete(data, deleteOps);
            }

            double addAvgMs = result.addNsTotal / (double) repeats / 1_000_000.0;
            double searchAvgMs = result.searchNsTotal / (double) repeats / 1_000_000.0;
            double deleteAvgMs = result.deleteNsTotal / (double) repeats / 1_000_000.0;

            System.out.printf(
                    "%-26s n=%5d | add=%.3f ms | search(%3d)=%.3f ms | delete(%2d)=%.3f ms%n",
                    dataset.getFileName(),
                    n,
                    addAvgMs,
                    searchOps,
                    searchAvgMs,
                    deleteOps,
                    deleteAvgMs
            );
        }
    }

    private static long measureAdd(List<Integer> data) {
        FibonacciHeap heap = new FibonacciHeap();
        long start = System.nanoTime();
        for (int value : data) {
            heap.add(value);
        }
        return System.nanoTime() - start;
    }

    private static long measureSearch(List<Integer> data, int searchOps) {
        FibonacciHeap heap = buildHeap(data);
        long start = System.nanoTime();
        int n = data.size();

        for (int i = 0; i < searchOps; i++) {
            int query;
            if ((i & 1) == 0) {
                query = data.get((i * 9973) % n);
            } else {
                query = 3_000_000 + i;
            }
            heap.search(query);
        }
        return System.nanoTime() - start;
    }

    private static long measureDelete(List<Integer> data, int deleteOps) {
        FibonacciHeap heap = buildHeap(data);
        long start = System.nanoTime();
        int n = data.size();

        for (int i = 0; i < deleteOps; i++) {
            int value = data.get((i * 31) % n);
            heap.delete(value);
        }
        return System.nanoTime() - start;
    }

    private static FibonacciHeap buildHeap(List<Integer> data) {
        FibonacciHeap heap = new FibonacciHeap();
        for (int value : data) {
            heap.add(value);
        }
        return heap;
    }

    private static List<Path> readDatasetList(Path datasetDir) throws IOException {
        Path manifest = datasetDir.resolve("manifest.csv");
        List<Path> result = new ArrayList<>();

        if (Files.exists(manifest)) {
            try (BufferedReader reader = Files.newBufferedReader(manifest)) {
                String line = reader.readLine();
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(",", 2);
                    if (parts.length < 1 || parts[0].isEmpty()) {
                        continue;
                    }
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
}

