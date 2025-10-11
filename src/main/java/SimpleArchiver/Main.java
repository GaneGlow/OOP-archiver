package SimpleArchiver;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static class CmdParams {
        public String[] inputFiles;
        public String outputFile;
        public boolean compress;
        public boolean multiFile;
        public boolean decompress;
        public boolean error;
        public boolean help;
    }

    public static CmdParams parseArgs(String[] args) {
        CmdParams params = new CmdParams();
        if (args.length > 0) {
            if (args[0].equals("--help")) {
                params.help = true;
                return params;
            }
            if (!args[0].equals("-d") && !args[0].equals("-c") && !args[0].equals("-m")) {
                params.error = true;
                params.help = true;
                return params;
            }

            if (args[0].equals("-d")) {
                params.decompress = true;
                if (args.length >= 3) {
                    params.inputFiles = new String[]{args[1]};
                    params.outputFile = args[2];
                } else {
                    params.error = true;
                    params.help = true;
                }
            } else if (args[0].equals("-c")) {
                params.compress = true;
                if (args.length >= 3) {
                    params.inputFiles = new String[]{args[1]};
                    params.outputFile = args[2];
                } else {
                    params.error = true;
                    params.help = true;
                }
            } else {
                params.compress = true;
                params.multiFile = true;

                if (args.length >= 3) {
                    List<String> inputFilesList = new ArrayList<>();
                    for (int i = 1; i < args.length - 1; i++) {
                        inputFilesList.add(args[i]);
                    }
                    params.inputFiles = inputFilesList.toArray(new String[0]);
                    params.outputFile = args[args.length - 1];
                } else {
                    params.error = true;
                    params.help = true;
                }
            }
        } else {
            params.help = true;
            params.error = true;
        }
        return params;
    }

    public static void main(String[] args) throws Exception {
        CmdParams params = parseArgs(args);
        if (params.help) {
            PrintStream out = params.error ? System.err : System.out;
            out.println("Usage:");
            out.println("  <cmd> -c <input-file> <output-file>           // compress one file");
            out.println("  <cmd> -m <file1> <file2> ... <output-folder>  // compress several files to folder archive");
            out.println("  <cmd> -m <folder> <output-folder>             // compress folder to archive");
            out.println("  <cmd> -d <input-folder> <output-folder>       // decompress folder archive");
            out.println("  --help                                        // short instructions");

            System.exit(params.error ? 1 : 0);
        }

        if (params.compress) {
            if (params.multiFile) {
                if (params.inputFiles.length == 1) {
                    java.io.File file = new java.io.File(params.inputFiles[0]);
                    if (file.isDirectory()) {
                        FolderFileArchiver.compressFolder(params.inputFiles[0], params.outputFile);
                        System.out.println("Folder archiving completed successfully!");
                    } else {
                        FolderFileArchiver.compressFiles(params.inputFiles, params.outputFile);
                        System.out.println("Files archiving completed successfully!");
                    }
                } else {
                    FolderFileArchiver.compressFiles(params.inputFiles, params.outputFile);
                    System.out.println("Files archiving completed successfully!");
                }
            } else {
                Archiver.compress(params.inputFiles[0], params.outputFile);
                System.out.println("File archiving completed successfully!");
            }
        }
        if (params.decompress) {
            FolderFileArchiver.decompressFolder(params.inputFiles[0], params.outputFile);
            System.out.println("Folder unpacking completed successfully!");
        }
    }
}
