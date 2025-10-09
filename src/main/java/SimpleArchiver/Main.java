package SimpleArchiver;

import java.io.PrintStream;

public class Main {

    public static class CmdParams {
        public String inputFile;
        public String outputFile;
        public boolean compress;
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
            if (!args[0].equals("-d") && !args[0].equals("-c")) {
                params.error = true;
                params.help = true;
                return params;
            }
            if (args[0].equals("-d")) {
                params.decompress = true;
            } else {
                params.compress = true;
            }
            if (args.length < 2) {
                params.help = true;
                params.error = true;
                return params;
            }

            params.inputFile = args[1];
            if (args.length > 2) {
                params.outputFile = args[2];
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
            out.println("  <cmd> args <input-file> (<output-file>)");
            out.println("    -d  // decompress file");
            out.println("    -c  // compress file");
            out.println("  <cmd> --help");
            System.exit(params.error ? 1 : 0);
        }
            if (params.compress) {
                Archiver.compress(args[1], args[2]);
                System.out.println("Archiving completed successfully!");
            }
            if (params.decompress) {
                Archiver.decompress(args[1], args[2]);
                System.out.println("Unpacking completed successfully!");
            }
    }
}
