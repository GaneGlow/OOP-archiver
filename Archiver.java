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
}
