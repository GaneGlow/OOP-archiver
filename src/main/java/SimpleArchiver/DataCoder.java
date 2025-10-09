package SimpleArchiver;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
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

    public static byte[] decode(DataInputStream dataInputStream, Map<String, Byte> huffmanCodes, int origSize) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        StringBuilder currCode = new StringBuilder();

        while (byteArrayOutputStream.size() < origSize) {
            boolean bit = dataInputStream.readBoolean();
            currCode.append(bit ? "1" : "0");

            if (huffmanCodes.containsKey(currCode.toString())) {
                byteArrayOutputStream.write(huffmanCodes.get(currCode.toString()));
                currCode.setLength(0);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }
}
