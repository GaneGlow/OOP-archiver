package SimpleArchiver;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Map;

public class DataCoder {

    public static String encode(byte[] data, Map<Byte, String> huffmanCodes) {
        StringBuilder encode = new StringBuilder();
        for (byte n : data) {
            encode.append(huffmanCodes.get(n));
        }
        return encode.toString();
    }

    public static byte[] decode(String encodedData, Map<String, Byte> huffmanCodes, int origSize) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StringBuilder currCode = new StringBuilder();

        /*while (baos.size() < origSize) {
            boolean bit = dataInputStream.readBoolean();
            currCode.append(bit ? "1" : "0");

            if (huffmanCodes.containsKey(currCode.toString())) {
                baos.write(huffmanCodes.get(currCode.toString()));
                currCode.setLength(0);
            }
        }*/
        for (int i = 0; i < encodedData.length(); i++) {
            currCode.append(encodedData.charAt(i));

            Byte decodedByte = huffmanCodes.get(currCode.toString());
            if (decodedByte != null) {
                baos.write(decodedByte);
                currCode.setLength(0);

                if (baos.size() == origSize) {
                    break;
                }
            }
        }
        return baos.toByteArray();
    }
}
