import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanTree {

    public static HuffmanTreeNode buildTree(Map<Byte, Integer> treeValues) {
        PriorityQueue<HuffmanTreeNode> queue = new PriorityQueue<>();

        for(Map.Entry<Byte, Integer> entry : treeValues.entrySet()) {
            queue.offer(new HuffmanTreeNode(entry.getKey(), entry.getValue()));
        }

        while (queue.size() > 1) {
            HuffmanTreeNode left = queue.poll();
            HuffmanTreeNode right = queue.poll();
            assert right != null;
            HuffmanTreeNode parent = new HuffmanTreeNode(left.frequency + right.frequency, left, right);

            queue.offer(parent);
        }

        return queue.poll();
    }

    private static void codeGeneratorRec(HuffmanTreeNode node, String code, Map<Byte, String> codes) {
        if (node == null) {
            return;
        }
        if (node.isLeaf()) {
            codes.put(node.code, code.isEmpty() ? "0" : code);
        }
        else {
            codeGeneratorRec(node.left, code + "0", codes);
            codeGeneratorRec(node.right, code + "1", codes);
        }
    }

    public static Map<Byte, String> codeGenerator(HuffmanTreeNode root) {
        Map<Byte, String> codes = new HashMap<>();
        codeGeneratorRec(root, "", codes);
        return codes;
    }
}
