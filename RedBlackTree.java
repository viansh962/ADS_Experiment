import java.util.*;
 
public class RedBlackTree<T extends Comparable<T>> {
 
    private static final boolean RED = true;
    private static final boolean BLACK = false;
 
    class Node {
        T data;
        boolean color = RED;                  // a new node is always RED
        Node left, right, parent;
    }
 
    private final Node NIL;  // sentinel node used for all leaves (always BLACK)
    private Node root;
 
    public RedBlackTree() {
        NIL = new Node();
        NIL.color = BLACK;
        NIL.left = NIL.right = NIL.parent = NIL;
        root = NIL;
    }
 
    // Helper: Left Rotate
    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != NIL)
            y.left.parent = x;
 
        y.parent = x.parent;
        if (x.parent == NIL)
            root = y;
        else if (x == x.parent.left)
            x.parent.left = y;
        else
            x.parent.right = y;
 
        y.left = x;
        x.parent = y;
    }
 
    // Helper: Right Rotate
    private void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != NIL)
            y.right.parent = x;
 
        y.parent = x.parent;
        if (x.parent == NIL)
            root = y;
        else if (x == x.parent.right)
            x.parent.right = y;
        else
            x.parent.left = y;
 
        y.right = x;
        x.parent = y;
    }
 
    // INSERT NODE
    public void insert(T data) {
        Node y = NIL;
        Node x = root;
 
        while (x != NIL) {
            y = x;
            int cmp = data.compareTo(x.data);
            if (cmp == 0) {
                System.out.println("Duplicate ignored: " + data);
                return;
            }
            x = (cmp < 0) ? x.left : x.right;
        }
 
        Node z = new Node();
        z.data = data;
        z.parent = y;
        z.left = z.right = NIL;
 
        if (y == NIL)
            root = z;
        else if (data.compareTo(y.data) < 0)
            y.left = z;
        else
            y.right = z;
 
        fixInsert(z);
    }
 
    // Insert Fix-Up
    private void fixInsert(Node z) {
        while (z.parent.color == RED) {
            if (z.parent == z.parent.parent.left) {  // Case A: parent is a left child
                Node uncle = z.parent.parent.right;
 
                if (uncle.color == RED) {                    // Case 1: uncle RED -> recolor
                    z.parent.color = BLACK;
                    uncle.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.right) {  // Case 2: z is right child -> rotate left
                        z = z.parent;
                        leftRotate(z);
                    }
                    z.parent.color = BLACK;  // Case 3: z is left child -> rotate right
                    z.parent.parent.color = RED;
                    rightRotate(z.parent.parent);
                }
            } else {  // Case B: parent is a right child
                Node uncle = z.parent.parent.left;
 
                if (uncle.color == RED) {
                    z.parent.color = BLACK;
                    uncle.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.color = BLACK;
    }
 
    // Get Minimum
    private Node minimum(Node node) {
        while (node.left != NIL)
            node = node.left;
        return node;
    }
 
    // Replace one subtree with another
    private void transplant(Node u, Node v) {
        if (u.parent == NIL)
            root = v;
        else if (u == u.parent.left)
            u.parent.left = v;
        else
            u.parent.right = v;
        v.parent = u.parent;  // also valid when v is NIL (needed by fixDelete)
    }
 
    // DELETE NODE
    public void delete(T data) {
        Node z = root;
        while (z != NIL) {
            int cmp = data.compareTo(z.data);
            if (cmp == 0)
                break;
            z = (cmp < 0) ? z.left : z.right;
        }
 
        if (z == NIL) {
            System.out.println("Key not found: " + data);
            return;
        }
 
        Node y = z;
        boolean yOriginalColor = y.color;
        Node x;
 
        if (z.left == NIL) {                  // Case 1: no left child
            x = z.right;
            transplant(z, z.right);
        } else if (z.right == NIL) {          // Case 2: only left child
            x = z.left;
            transplant(z, z.left);
        } else {  // Case 3: two children -> use min of right subtree
            y = minimum(z.right);
            yOriginalColor = y.color;
            x = y.right;
 
            if (y.parent == z) {
                x.parent = y;
            } else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
 
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }
 
        // Fix double-black violation
        if (yOriginalColor == BLACK)
            fixDelete(x);
    }
 
    // DELETE FIX-UP
    private void fixDelete(Node x) {
        while (x != root && x.color == BLACK) {
            if (x == x.parent.left) {
                Node w = x.parent.right;                     // sibling
 
                if (w.color == RED) {                        // Case 1: sibling RED
                    w.color = BLACK;
                    x.parent.color = RED;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }
 
                if (w.left.color == BLACK && w.right.color == BLACK) {  // Case 2
                    w.color = RED;
                    x = x.parent;
                } else {
                    if (w.right.color == BLACK) {            // Case 3: far nephew BLACK
                        w.left.color = BLACK;
                        w.color = RED;
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    w.color = x.parent.color;                // Case 4: far nephew RED
                    x.parent.color = BLACK;
                    w.right.color = BLACK;
                    leftRotate(x.parent);
                    x = root;
                }
            } else {                                         // mirror image
                Node w = x.parent.left;
 
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    rightRotate(x.parent);
                    w = x.parent.left;
                }
 
                if (w.right.color == BLACK && w.left.color == BLACK) {
                    w.color = RED;
                    x = x.parent;
                } else {
                    if (w.left.color == BLACK) {
                        w.right.color = BLACK;
                        w.color = RED;
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.left.color = BLACK;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.color = BLACK;
    }
 
    // Inorder Traversal
    public void inorder() {
        inorderHelper(root);
        System.out.println();
    }
 
    private void inorderHelper(Node node) {
        if (node != NIL) {
            inorderHelper(node.left);
            System.out.print(node.data + "(" + (node.color == RED ? "R" : "B") + ") ");
            inorderHelper(node.right);
        }
    }
 
    // Verify all Red-Black properties (returns true if the tree is a valid Red-Black tree)
    public boolean isValid() {
        return root.color == BLACK && blackHeight(root) != -1;
    }
 
    private int blackHeight(Node n) {
        if (n == NIL)
            return 1;
        if (n.color == RED && (n.left.color == RED || n.right.color == RED))
            return -1;                                       // two consecutive red nodes
        int l = blackHeight(n.left), r = blackHeight(n.right);
        if (l == -1 || r == -1 || l != r)
            return -1;                                       // unequal black heights
        return l + (n.color == BLACK ? 1 : 0);
    }
 
    // MAIN TEST PROGRAM
    public static void main(String[] args) {
 
        RedBlackTree<Integer> tree = new RedBlackTree<>();
 
        System.out.println("=== INSERTION ===");
        int[] values = { 10, 20, 30, 15, 25, 28, 5, 1, 50 };
        for (int v : values) {
            tree.insert(v);
            tree.inorder();
        }
        System.out.println("Valid Red-Black tree: " + tree.isValid());
 
        System.out.println("\n=== DELETION ===");
        int[] del = { 20, 5, 30, 10 };
        for (int v : del) {
            System.out.println("Deleting: " + v);
            tree.delete(v);
            tree.inorder();
        }
        System.out.println("Valid Red-Black tree: " + tree.isValid());
 
        System.out.println("\n=== STRING TEST ===");
        RedBlackTree<String> tree2 = new RedBlackTree<>();
        tree2.insert("apple");
        tree2.insert("mango");
        tree2.insert("banana");
        tree2.insert("cherry");
        tree2.inorder();                      // sorted order with colors
        System.out.println("Valid Red-Black tree: " + tree2.isValid());
    }
}
