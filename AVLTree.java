import java.util.*;
 
public class AVLTree {
 
    // Node class
    class Node {
        int key, height;
        Node left, right;
 
        Node(int d) {
            key = d;
            height = 1;
        }
    }
 
    Node root;
 
    // Get height
    int height(Node n) {
        return (n == null) ? 0 : n.height;
    }
 
    // Get balance factor = height(left) - height(right)
    int getBalance(Node n) {
        return (n == null) ? 0 : height(n.left) - height(n.right);
    }
 
    // Right rotate (LL rotation)
    Node rightRotate(Node y) {
        Node x = y.left;
        Node t2 = x.right;
 
        x.right = y;
        y.left = t2;
 
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;
 
        return x;
    }
 
    // Left rotate (RR rotation)
    Node leftRotate(Node x) {
        Node y = x.right;
        Node t2 = y.left;
 
        y.left = x;
        x.right = t2;
 
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;
 
        return y;
    }
 
    // INSERT
    Node insert(Node node, int key) {
        if (node == null)
            return new Node(key);
 
        if (key < node.key)
            node.left = insert(node.left, key);
        else if (key > node.key)
            node.right = insert(node.right, key);
        else
            return node;                       // duplicates not allowed
 
        // Update height
        node.height = 1 + Math.max(height(node.left), height(node.right));
 
        int balance = getBalance(node);
 
        // *** ROTATION CASES ***
        // LL
        if (balance > 1 && key < node.left.key) {
            System.out.println("   [LL case at node " + node.key + " -> right rotation]");
            return rightRotate(node);
        }
 
        // RR
        if (balance < -1 && key > node.right.key) {
            System.out.println("   [RR case at node " + node.key + " -> left rotation]");
            return leftRotate(node);
        }
 
        // LR
        if (balance > 1 && key > node.left.key) {
            System.out.println("   [LR case at node " + node.key
                    + " -> left rotation on child, then right rotation]");
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
 
        // RL
        if (balance < -1 && key < node.right.key) {
            System.out.println("   [RL case at node " + node.key
                    + " -> right rotation on child, then left rotation]");
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
 
        return node;
    }
 
    // Get minimum node (for deletion)
    Node minValueNode(Node node) {
        Node current = node;
        while (current.left != null)
            current = current.left;
        return current;
    }
 
    // DELETE
    Node delete(Node root, int key) {
        if (root == null)
            return root;
 
        if (key < root.key)
            root.left = delete(root.left, key);
        else if (key > root.key)
            root.right = delete(root.right, key);
        else {
            // CASE: 0 or 1 child
            if (root.left == null || root.right == null) {
                Node temp = (root.left != null) ? root.left : root.right;
                root = temp;                   // temp == null -> no child, else one child
            } else {
                // Node with 2 children: copy inorder successor, then delete it
                Node temp = minValueNode(root.right);
                root.key = temp.key;
                root.right = delete(root.right, temp.key);
            }
        }
 
        if (root == null)
            return root;
 
        // Update height
        root.height = Math.max(height(root.left), height(root.right)) + 1;
 
        // BALANCE FACTOR
        int balance = getBalance(root);
 
        // *** DELETION ROTATION CASES ***
        // Left heavy : deletion happened in the RIGHT subtree  (R0, R1, R-1)
        if (balance > 1) {
            int b = getBalance(root.left);
            System.out.println("   [Case R" + b + " at node " + root.key + "]");
            if (b >= 0)                        // R0 or R1  -> single right rotation
                return rightRotate(root);
            root.left = leftRotate(root.left); // R-1       -> double (LR) rotation
            return rightRotate(root);
        }
 
        // Right heavy : deletion happened in the LEFT subtree  (L0, L1, L-1)
        if (balance < -1) {
            int b = getBalance(root.right);
            System.out.println("   [Case L" + b + " at node " + root.key + "]");
            if (b <= 0)                        // L0 or L-1 -> single left rotation
                return leftRotate(root);
            root.right = rightRotate(root.right); // L1     -> double (RL) rotation
            return leftRotate(root);
        }
 
        return root;
    }
 
    // Display using SET notation : { left , key , right }
    String display(Node node) {
        if (node == null)
            return "{}";
        return "{ " + display(node.left) + " , " + node.key + " , "
               + display(node.right) + " }";
    }
 
    // helpers for the test program
    static AVLTree build(int... keys) {
        AVLTree t = new AVLTree();
        for (int k : keys)
            t.root = t.insert(t.root, k);
        return t;
    }
 
    static void testInsert(String name, int... keys) {
        System.out.println("Insert " + Arrays.toString(keys) + "  (" + name + ")");
        AVLTree t = new AVLTree();
        for (int k : keys)
            t.root = t.insert(t.root, k);
        System.out.println("   Tree: " + t.display(t.root));
    }
 
    static void testDelete(String name, int delKey, int... keys) {
        AVLTree t = build(keys);
        System.out.println("Case " + name + ": tree built from " + Arrays.toString(keys)
                + ", delete " + delKey);
        System.out.println("   Before: " + t.display(t.root));
        t.root = t.delete(t.root, delKey);
        System.out.println("   After : " + t.display(t.root));
    }
 
    // MAIN: TEST FOR ALL CASES
    public static void main(String[] args) {
 
        System.out.println("=== INSERTION TEST ===");
        testInsert("LL", 30, 20, 10);
        testInsert("RR", 10, 20, 30);
        testInsert("LR", 30, 10, 20);
        testInsert("RL", 10, 30, 20);
 
        System.out.println("\nCombined tree (inserted one after another):");
        AVLTree tree = new AVLTree();
        int[] values = { 30, 20, 10, 40, 50, 25, 45 };
        for (int v : values)
            tree.root = tree.insert(tree.root, v);
        System.out.println("AVL Tree (Set Notation):");
        System.out.println(tree.display(tree.root));
 
        System.out.println("\n=== DELETION TEST (all six cases) ===");
        testDelete("R0 ", 40, 30, 20, 40, 10, 25);
        testDelete("R1 ", 40, 30, 20, 40, 10);
        testDelete("R-1", 40, 30, 20, 40, 25);
        testDelete("L0 ", 20, 30, 20, 40, 35, 45);
        testDelete("L-1", 20, 30, 20, 40, 45);
        testDelete("L1 ", 20, 30, 20, 40, 35);
 
        System.out.println("\nDeleting from the combined tree: 10, 50, 20, 40");
        int[] del = { 10, 50, 20, 40 };
        for (int d : del)
            tree.root = tree.delete(tree.root, d);
        System.out.println("AVL Tree After Deletion:");
        System.out.println(tree.display(tree.root));
    }
}
