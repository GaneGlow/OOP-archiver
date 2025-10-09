package SimpleArchiver;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FolderFileArchiver {

    public static void compressFolder(String inputFolderPath, String outputFolderPath) throws IOException {
        File inputFolder = new File(inputFolderPath);
        File outputFolder = new File(outputFolderPath);

        if (!inputFolder.exists() || !inputFolder.isDirectory()) {
            throw new IOException("Input folder does not exist or is not a directory: " + inputFolderPath);
        }

        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        File manifestFile = new File(outputFolder, "manifest.mf");
        List<String> manifestLines = new ArrayList<>();

        Files.walkFileTree(inputFolder.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toFile().isFile()) {
                    String relativePath = inputFolder.toPath().relativize(file).toString();
                    String compressedFileName = generateCompressedFileName(relativePath);
                    File compressedFile = new File(outputFolder, compressedFileName);

                    Archiver.compress(file.toString(), compressedFile.toString());

                    manifestLines.add(relativePath + "|" + compressedFileName + "|" + file.toFile().length());

                    System.out.println("Compressed: " + relativePath + " -> " + compressedFileName);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        try (PrintWriter writer = new PrintWriter(new FileWriter(manifestFile))) {
            for (String line : manifestLines) {
                writer.println(line);
            }
        }

        System.out.println("Total files compressed: " + manifestLines.size());
    }

    public static void compressFiles(String[] inputFiles, String outputFolderPath) throws IOException {
        File outputFolder = new File(outputFolderPath);

        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        File manifestFile = new File(outputFolder, "manifest.mf");
        List<String> manifestLines = new ArrayList<>();

        for (String inputFile : inputFiles) {
            File file = new File(inputFile);
            if (!file.exists()) {
                System.err.println("Warning: File does not exist - " + inputFile);
                continue;
            }

            if (file.isFile()) {
                String fileName = file.getName();
                String compressedFileName = generateCompressedFileName(fileName);
                File compressedFile = new File(outputFolder, compressedFileName);

                Archiver.compress(file.toString(), compressedFile.toString());

                manifestLines.add(fileName + "|" + compressedFileName + "|" + file.length());

                System.out.println("Compressed: " + fileName + " -> " + compressedFileName);
            } else if (file.isDirectory()) {
                Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                        if (path.toFile().isFile()) {
                            String relativePath = file.toPath().relativize(path).toString();
                            String compressedFileName = generateCompressedFileName(relativePath);
                            File compressedFile = new File(outputFolder, compressedFileName);

                            Archiver.compress(path.toString(), compressedFile.toString());

                            manifestLines.add(relativePath + "|" + compressedFileName + "|" + path.toFile().length());

                            System.out.println("Compressed: " + relativePath + " -> " + compressedFileName);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(manifestFile))) {
            writer.println("# Multi-file archive manifest");
            writer.println("# Format: original_path|compressed_filename|original_size");
            for (String line : manifestLines) {
                writer.println(line);
            }
        }

        System.out.println("Total files compressed: " + manifestLines.size());
    }

    public static void decompressFolder(String inputFolderPath, String outputFolderPath) throws IOException {
        File inputFolder = new File(inputFolderPath);
        File outputFolder = new File(outputFolderPath);

        if (!inputFolder.exists() || !inputFolder.isDirectory()) {
            throw new IOException("Input archive folder does not exist: " + inputFolderPath);
        }

        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        File manifestFile = new File(inputFolder, "manifest.mf");
        if (!manifestFile.exists()) {
            throw new IOException("Manifest file not found in archive folder: " + manifestFile.getPath());
        }

        List<String> manifestLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(manifestFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#") && !line.trim().isEmpty()) {
                    manifestLines.add(line);
                }
            }
        }

        int restoredCount = 0;
        for (String line : manifestLines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 2) {
                String originalPath = parts[0];
                String compressedFileName = parts[1];

                File compressedFile = new File(inputFolder, compressedFileName);
                File outputFile = new File(outputFolder, originalPath);

                if (!compressedFile.exists()) {
                    System.err.println("Warning: Compressed file not found - " + compressedFileName);
                    continue;
                }

                outputFile.getParentFile().mkdirs();

                Archiver.decompress(compressedFile.toString(), outputFile.toString());
                restoredCount++;

                System.out.println("Decompressed: " + compressedFileName + " -> " + originalPath);
            }
        }

        System.out.println("Total files decompressed: " + restoredCount);
    }

    private static String generateCompressedFileName(String originalName) {
        String safeName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return safeName + ".huf";
    }
}
