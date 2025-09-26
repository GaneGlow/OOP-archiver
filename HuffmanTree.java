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
}
