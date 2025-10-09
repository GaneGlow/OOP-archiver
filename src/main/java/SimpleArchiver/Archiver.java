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
                /*for (char c : code.toCharArray()) {
                    dos.writeBoolean(c == '1');
                }
            }

            for (char c : encodeData.toCharArray()) {
                dos.writeBoolean(c == '1');
            }
        }*/
                dos.writeUTF(code);
            }

            int bitLength = encodeData.length();
            dos.writeInt(bitLength);

            byte currentByte = 0;
            int bitCount = 0;

            for (int i = 0; i < bitLength; i++) {
                char bitChar = encodeData.charAt(i);
                if (bitChar == '1') {
                    currentByte |= (byte) (1 << (7 - bitCount));
                }
                bitCount++;

                if (bitCount == 8) {
                    dos.writeByte(currentByte);
                    currentByte = 0;
                    bitCount = 0;
                }
            }
            if (bitCount > 0) {
                dos.writeByte(currentByte);
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
                //StringBuilder code =new StringBuilder();
                /*for (int j = 0; j < codeLength; j++) {
                    code.append(dis.readBoolean() ? '1' : '0');
                }*/

                String code = dis.readUTF();
                huffmanCodes.put(code, character);
            }

            int bitLength = dis.readInt();

            StringBuilder encodedData = new StringBuilder();
            int totalBytes = (bitLength + 7) / 8;

            for (int i = 0; i < totalBytes; i++) {
                byte currentByte = dis.readByte();
                int bitsInThisByte = (i == totalBytes - 1) ?
                        bitLength - (i * 8) : 8;

                for (int j = 0; j < bitsInThisByte; j++) {
                    if ((currentByte & (1 << (7 - j))) != 0) {
                        encodedData.append('1');
                    } else {
                        encodedData.append('0');
                    }
                }
            }

            byte[] decodeData = DataCoder.decode(encodedData.toString(), huffmanCodes, origSize);
            writeDecompressFile(outputFile, decodeData);
        }
    }
}