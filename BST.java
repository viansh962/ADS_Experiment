import java.util.*;
 
public class BST {
 
    // Node structure
    class Node {
        int data;
        Node left, right;
 
        Node(int d) {
            data = d;
            left = right = null;
        }
    }
 
    Node root;
 
    // a) Insert a node (duplicates are ignored)
    Node insert(Node root, int key) {
        if (root == null)
            return new Node(key);
 
        if (key < root.data)
            root.left = insert(root.left, key);
        else if (key > root.data)
            root.right = insert(root.right, key);
 
        return root;
    }
 
    // c) Search a key
    boolean search(Node root, int key) {
        if (root == null)
            return false;
        if (root.data == key)
            return true;
        if (key < root.data)
            return search(root.left, key);
        return search(root.right, key);
    }
 
    // d) Find minimum
    int findMin(Node root) {
        if (root == null)
            return -1;
        while (root.left != null)
            root = root.left;
        return root.data;
    }
 
    // e) Find maximum
    int findMax(Node root) {
        if (root == null)
            return -1;
        while (root.right != null)
            root = root.right;
        return root.data;
    }
 
    // Find node reference
    Node findNode(Node root, int key) {
        if (root == null)
            return null;
        if (root.data == key)
            return root;
        if (key < root.data)
            return findNode(root.left, key);
        return findNode(root.right, key);
    }
 
    // b) Delete a node
    Node delete(Node root, int key) {
        if (root == null)
            return null;
 
        if (key < root.data)
            root.left = delete(root.left, key);
        else if (key > root.data)
            root.right = delete(root.right, key);
        else {
            // Case 1 and 2: leaf node or node with only one child
            if (root.left == null)
                return root.right;
            else if (root.right == null)
                return root.left;
 
            // Case 3: node with two children -> copy inorder successor, then delete it
            root.data = findMin(root.right);
            root.right = delete(root.right, root.data);
        }
        return root;
    }
 
    // f) Left child of a node
    Integer leftChild(int key) {
        Node n = findNode(root, key);
        if (n != null && n.left != null)
            return n.left.data;
        return null;
    }
 
    // g) Right child of a node
    Integer rightChild(int key) {
        Node n = findNode(root, key);
        if (n != null && n.right != null)
            return n.right.data;
        return null;
    }
 
    // h) Count nodes
    int countNodes(Node root) {
        if (root == null)
            return 0;
        return 1 + countNodes(root.left) + countNodes(root.right);
    }
 
    // Count leaf nodes
    int countLeaves(Node root) {
        if (root == null)
            return 0;
        if (root.left == null && root.right == null)
            return 1;
        return countLeaves(root.left) + countLeaves(root.right);
    }
 
    // Count full nodes (nodes having both children)
    int countFullNodes(Node root) {
        if (root == null)
            return 0;
        int count = (root.left != null && root.right != null) ? 1 : 0;
        return count + countFullNodes(root.left) + countFullNodes(root.right);
    }
 
    // Count ancestors (nodes on the path from the root to the key, excluding the key)
    // Returns -1 if the key is not present.
    int countAncestors(int key) {
        int count = 0;
        Node cur = root;
        while (cur != null) {
            if (cur.data == key)
                return count;
            count++;
            cur = (key < cur.data) ? cur.left : cur.right;
        }
        return -1;
    }
 
    // Count descendants = (nodes in the subtree of key) - 1. Returns -1 if key not present.
    int countDescendants(int key) {
        Node n = findNode(root, key);
        if (n == null)
            return -1;
        return countNodes(n) - 1;
    }
 
    // Inorder traversal
    void inorder(Node root) {
        if (root != null) {
            inorder(root.left);
            System.out.print(root.data + " ");
            inorder(root.right);
        }
    }
 
    void show(String msg) {
        System.out.print(msg);
        inorder(root);
        System.out.println();
    }
 
    // Main driver code
    public static void main(String[] args) {
 
        BST tree = new BST();
        int[] values = { 50, 30, 20, 40, 70, 60, 80, 65 };
 
        for (int v : values)
            tree.root = tree.insert(tree.root, v);
 
        tree.show("Inorder Traversal: ");
        System.out.println("Min: " + tree.findMin(tree.root));
        System.out.println("Max: " + tree.findMax(tree.root));
 
        System.out.println("Search 40: " + tree.search(tree.root, 40));
        System.out.println("Search 99: " + tree.search(tree.root, 99));
 
        System.out.println("Left child of 30: " + tree.leftChild(30));
        System.out.println("Right child of 30: " + tree.rightChild(30));
        System.out.println("Left child of 20: " + tree.leftChild(20));
 
        System.out.println("Total nodes: " + tree.countNodes(tree.root));
        System.out.println("Leaf nodes: " + tree.countLeaves(tree.root));
        System.out.println("Full nodes: " + tree.countFullNodes(tree.root));
        System.out.println("Ancestors of 65: " + tree.countAncestors(65));
        System.out.println("Descendants of 70: " + tree.countDescendants(70));
 
        // Deletion: every case
        tree.root = tree.delete(tree.root, 20);      // leaf node
        tree.show("After deleting 20 (leaf)           : ");
 
        tree.root = tree.delete(tree.root, 30);      // only one child (40)
        tree.show("After deleting 30 (one child)      : ");
 
        tree.root = tree.delete(tree.root, 70);      // two children (60 and 80)
        tree.show("After deleting 70 (two children)   : ");
 
        tree.root = tree.delete(tree.root, 50);      // root with two children
        tree.show("After deleting 50 (root, 2 child.) : ");
    }
}
