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

public class ExtractMinAfterConsolidateBenchmark {

    private static final int DEFAULT_WARMUP = 5;
    private static final int DEFAULT_REPEATS = 20;
    private static final int DEFAULT_BATCH = 10;

    private static final class Stat {
        private final String dataset;
        private final int size;
        private final double medianNs;
        private final double meanNs;
        private final double minNs;
        private final double maxNs;

        private Stat(String dataset, int size, double medianNs, double meanNs, double minNs, double maxNs) {
            this.dataset = dataset;
            this.size = size;
            this.medianNs = medianNs;
            this.meanNs = meanNs;
            this.minNs = minNs;
            this.maxNs = maxNs;
        }
    }

    public static void main(String[] args) throws Exception {
        Path datasetDir = args.length > 0 ? Paths.get(args[0]) : Paths.get("src/Semestrovka/input_sets");
        Path outputTxt = args.length > 1 ? Paths.get(args[1]) : datasetDir.resolve("extract_min_after_consolidate_results.txt");
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
            if (data.size() < 2) {
                continue;
            }
            Stat stat = benchmarkOne(dataset.getFileName().toString(), data, warmupRounds, repeats, batch);
            stats.add(stat);
            System.out.printf("Done %-26s n=%5d median=%.1f ns%n", stat.dataset, stat.size, stat.medianNs);
        }

        writeResults(outputTxt, stats, warmupRounds, repeats, batch);
        System.out.println("Saved: " + outputTxt.toAbsolutePath());
    }

    private static Stat benchmarkOne(String datasetName, List<Integer> data, int warmupRounds, int repeats, int batch) {
        for (int i = 0; i < warmupRounds; i++) {
            measureExtractMinNsAfterConsolidate(data, batch);
        }

        List<Long> samplesNs = new ArrayList<>(repeats);
        long sum = 0L;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        for (int i = 0; i < repeats; i++) {
            long ns = measureExtractMinNsAfterConsolidate(data, batch);
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

        double meanNs = sum / (double) repeats;
        return new Stat(datasetName, data.size(), medianNs, meanNs, min, max);
    }

    private static long measureExtractMinNsAfterConsolidate(List<Integer> data, int batch) {
        long totalNs = 0L;

        for (int b = 0; b < batch; b++) {
            FibonacciHeap<Integer> heap = new FibonacciHeap<>();
            for (int value : data) {
                heap.add(value, value);
            }

            // Pre-step: force consolidate as requested (first extract-min call).
//            heap.extractMinConsolidateOnlyNs();

            long start = System.nanoTime();
            heap.extractMinValue();
            long end = System.nanoTime();
            totalNs += (end - start);
        }

        return totalNs / batch;
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
            out.println("# FibonacciHeap extractMin benchmark after pre-consolidate");
            out.printf("# warmup_rounds=%d repeats=%d batch=%d%n", warmupRounds, repeats, batch);
            out.println("# note: scenario is add n -> pre-consolidate -> timed single extractMin");
            out.println("# columns: dataset size median_ns mean_ns min_ns max_ns");

            for (Stat stat : stats) {
                out.printf(
                        "%s %d %.3f %.3f %.3f %.3f%n",
                        stat.dataset,
                        stat.size,
                        stat.medianNs,
                        stat.meanNs,
                        stat.minNs,
                        stat.maxNs
                );
            }
        }
    }
}

