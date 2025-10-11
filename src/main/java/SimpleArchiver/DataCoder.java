package SimpleArchiver;

import java.io.ByteArrayOutputStream;
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
