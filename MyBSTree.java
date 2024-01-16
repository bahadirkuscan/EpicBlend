public class MyBSTree {
    public TreeNode root;
    MyBSTree(){}
    public class TreeNode{
        public Song song;
        public TreeNode left;
        public TreeNode right;

        TreeNode(Song song){
            this.song = song;
        }
    }

    public void add(Song song){
        if (root == null){
            root = new TreeNode(song);
            return;
        }
        TreeNode node = root;
        while (true){
            if (node.song.comparePlaycount(song) > 0){
                if (node.left == null){
                    node.left = new TreeNode(song);
                    return;
                }
                node = node.left;
            }
            else if (node.song.comparePlaycount(song) < 0){
                if (node.right == null){
                    node.right = new TreeNode(song);
                    return;
                }
                node = node.right;
            }
            else {
                return;
            }
        }
    }
}
