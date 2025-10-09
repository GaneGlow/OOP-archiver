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

    private static String generateCompressedFileName(String originalName) {
        String safeName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return safeName + ".huf";
    }
}
