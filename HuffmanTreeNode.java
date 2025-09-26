public class HuffmanTreeNode implements Comparable<HuffmanTreeNode>{
    int frequency;
    byte code;
    HuffmanTreeNode left;
    HuffmanTreeNode right;

    public HuffmanTreeNode(int frequency, HuffmanTreeNode left, HuffmanTreeNode right) {
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }

    public boolean isLeaf() {
        return (left == null) && (right == null);
    }

    @Override
    public int compareTo(HuffmanTreeNode other) {
        return Integer.compare(this. frequency, other.frequency);
    }
}
