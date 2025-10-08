import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Archiver {

    private static Map<Byte, Integer> frequencyMap(byte[] data) {
        Map<Byte, Integer> frequency = new HashMap<>();

        for (byte b : data) {
            frequency.put(b, frequency.getOrDefault(b, 0) + 1);
        }

        return frequency;
    }

    private static void writeCompressFile(String outputFile, String encodeData, int origSize) throws IOException {

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(outputFile))) {
            dos.writeInt(origSize);

            for (char c : encodeData.toCharArray()) {
                dos.writeBoolean(c == '1');
            }
        }
    }

    private static byte[] readFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        byte[] data = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(data);//?
        } catch (IOException e) {
            throw new FileNotFoundException();
        }

        return data;
    }

    public static void compress(String inputFile, String outputFile) throws IOException {
        byte[] data = readFile(inputFile);

        Map<Byte, Integer> frequency = frequencyMap(data);

        HuffmanTreeNode root = HuffmanTree.buildTree(frequency);

        Map<Byte, String> huffmanCodes = HuffmanTree.codeGenerator(root);

        String encodeData = DataCoder.encode(data, huffmanCodes);

        writeCompressFile(outputFile, encodeData, data.length);
    }
}
