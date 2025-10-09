package SimpleArchiver;

import HuffmanTree.HuffmanTree;
import HuffmanTree.HuffmanTreeNode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Archiver extends HuffmanTree {

    private static Map<Byte, Integer> frequencyMap(byte[] data) {
        Map<Byte, Integer> frequency = new HashMap<>();

        for (byte b : data) {
            frequency.put(b, frequency.getOrDefault(b, 0) + 1);
        }

        return frequency;
    }

    private static void writeCompressFile(String outputFile, String encodeData,
                                          int origSize, Map<Byte, String> huffmanCodes) throws IOException {

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(outputFile))) {
            dos.writeInt(origSize);

            dos.writeInt(huffmanCodes.size());

            for (Map.Entry<Byte, String> huffmanCode : huffmanCodes.entrySet()) {
                dos.writeByte(huffmanCode.getKey());
                String code = huffmanCode.getValue();
                dos.writeByte(code.length());
                for (char c : code.toCharArray()) {
                    dos.writeBoolean(c == '1');
                }
            }

            for (char c : encodeData.toCharArray()) {
                dos.writeBoolean(c == '1');
            }
        }
    }

    private static byte[] readFile(String fileName) throws IOException {
        File file = new File(fileName);
        byte[] data = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(data);//?
        }
        return data;
    }

    public static void compress(String inputFile, String outputFile) throws IOException {
        byte[] data = readFile(inputFile);

        Map<Byte, Integer> frequency = frequencyMap(data);

        HuffmanTreeNode root = buildTree(frequency);

        Map<Byte, String> huffmanCodes = codeGenerator(root);

        String encodeData = DataCoder.encode(data, huffmanCodes);

        writeCompressFile(outputFile, encodeData, data.length, huffmanCodes);
    }

    private static void writeDecompressFile(String file, byte[] data) throws IOException{
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
    }

    public static void decompress(String inputFile, String outputFile) throws IOException{
        try (DataInputStream dis = new DataInputStream(new FileInputStream(inputFile))) {

            int origSize = dis.readInt();
            int codesSize = dis.readInt();

            Map<String, Byte> huffmanCodes = new HashMap<>();
            for (int i = 0; i < codesSize; i++) {
                byte character = dis.readByte();
                int codeLength = dis.readByte();
                StringBuilder code =new StringBuilder();
                for (int j = 0; j < codeLength; j++) {
                    code.append(dis.readBoolean() ? '1' : '0');
                }
                huffmanCodes.put(code.toString(), character);
            }
            byte[] decodeData = DataCoder.decode(dis, huffmanCodes, origSize);

            writeDecompressFile(outputFile, decodeData);
        }
    }
}