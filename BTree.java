import java.util.Scanner;
 
// B-Tree Node Class
class BTreeNode {
    int[] keys;                 // keys stored in sorted order
    BTreeNode[] child;          // child pointers
    int n;                      // current number of keys
    boolean leaf;
 
    BTreeNode(int m, boolean leaf) {
        this.leaf = leaf;
        keys = new int[m];  // at most m-1 keys (+1 spare slot used during a split)
        child = new BTreeNode[m + 1];   // at most m children (+1 spare slot)
        n = 0;
    }
}
 
// m-way B-Tree: every non-leaf node has at most m children and at most m-1 keys.
// Every node except the root has at least ceil(m/2)-1 keys.
public class BTree {
    BTreeNode root;
    final int m;              // order of the tree (max children)
    final int maxKeys;        // m - 1
    final int minKeys;        // ceil(m/2) - 1
 
    BTree(int m) {
        this.m = m;
        this.maxKeys = m - 1;
        this.minKeys = (m + 1) / 2 - 1;
        this.root = null;
    }
 
    // ---------------------------------------------------------------- SEARCH
    boolean contains(int k) {
        BTreeNode x = root;
        while (x != null) {
            int i = 0;
            while (i < x.n && k > x.keys[i])
                i++;
            if (i < x.n && x.keys[i] == k)
                return true;
            if (x.leaf)
                return false;
            x = x.child[i];
        }
        return false;
    }
 
    void search(int k) {
        System.out.println(contains(k) ? "Found!" : "Not Found!");
    }
 
    // ---------------------------------------------------------------- INSERT
    void insert(int k) {
        if (contains(k)) {
            System.out.println("Key " + k + " already exists!");
            return;
        }
        if (root == null) {
            root = new BTreeNode(m, true);
            root.keys[0] = k;
            root.n = 1;
            return;
        }
        insertRec(root, k);
        if (root.n > maxKeys) {                     // root overflow -> tree grows in height
            BTreeNode s = new BTreeNode(m, false);
            s.child[0] = root;
            splitChild(s, 0);
            root = s;
        }
    }
 
    private void insertRec(BTreeNode x, int k) {
        int i = x.n - 1;
        if (x.leaf) {
            while (i >= 0 && x.keys[i] > k) {
                x.keys[i + 1] = x.keys[i];
                i--;
            }
            x.keys[i + 1] = k;
            x.n++;
        } else {
            while (i >= 0 && x.keys[i] > k)
                i--;
            i++;
            insertRec(x.child[i], k);
            if (x.child[i].n > maxKeys)             // child overflow -> split it
                splitChild(x, i);
        }
    }
 
    // Split the overflowing child p.child[i] around its median key
    private void splitChild(BTreeNode p, int i) {
        BTreeNode y = p.child[i];
        int mid = y.n / 2;
        BTreeNode z = new BTreeNode(m, y.leaf);
 
        z.n = y.n - mid - 1;
        for (int j = 0; j < z.n; j++)
            z.keys[j] = y.keys[mid + 1 + j];
        if (!y.leaf)
            for (int j = 0; j <= z.n; j++)
                z.child[j] = y.child[mid + 1 + j];
 
        int median = y.keys[mid];
        y.n = mid;
 
        for (int j = p.n; j > i; j--)
            p.keys[j] = p.keys[j - 1];
        for (int j = p.n + 1; j > i + 1; j--)
            p.child[j] = p.child[j - 1];
 
        p.keys[i] = median;
        p.child[i + 1] = z;
        p.n++;
    }
 
    // ---------------------------------------------------------------- DELETE
    void delete(int k) {
        if (root == null) {
            System.out.println("Tree is empty!");
            return;
        }
        if (!contains(k)) {
            System.out.println("Key " + k + " not found!");
            return;
        }
        remove(root, k);
        if (root.n == 0)                            // root became empty -> tree shrinks
            root = root.leaf ? null : root.child[0];
    }
 
    private void remove(BTreeNode x, int k) {
        int i = 0;
        while (i < x.n && x.keys[i] < k)
            i++;
 
        if (i < x.n && x.keys[i] == k) {
            if (x.leaf) {                           // key is in a leaf: simply remove it
                for (int j = i + 1; j < x.n; j++)
                    x.keys[j - 1] = x.keys[j];
                x.n--;
            } else {  // key is in an internal node: use predecessor
                BTreeNode c = x.child[i];
                while (!c.leaf)
                    c = c.child[c.n];
                int pred = c.keys[c.n - 1];
                x.keys[i] = pred;
                remove(x.child[i], pred);
                fixChild(x, i);
            }
        } else {                                    // key lies in the subtree child[i]
            remove(x.child[i], k);
            fixChild(x, i);
        }
    }
 
    // Repair child[i] if it has fewer than minKeys keys
    private void fixChild(BTreeNode x, int i) {
        if (x.child[i].n >= minKeys)
            return;
        if (i > 0 && x.child[i - 1].n > minKeys)
            borrowFromPrev(x, i);
        else if (i < x.n && x.child[i + 1].n > minKeys)
            borrowFromNext(x, i);
        else if (i > 0)
            merge(x, i - 1);
        else
            merge(x, i);
    }
 
    private void borrowFromPrev(BTreeNode x, int i) {
        BTreeNode c = x.child[i], s = x.child[i - 1];
 
        for (int j = c.n - 1; j >= 0; j--)
            c.keys[j + 1] = c.keys[j];
        if (!c.leaf)
            for (int j = c.n; j >= 0; j--)
                c.child[j + 1] = c.child[j];
 
        c.keys[0] = x.keys[i - 1];
        if (!c.leaf)
            c.child[0] = s.child[s.n];
 
        x.keys[i - 1] = s.keys[s.n - 1];
        c.n++;
        s.n--;
    }
 
    private void borrowFromNext(BTreeNode x, int i) {
        BTreeNode c = x.child[i], s = x.child[i + 1];
 
        c.keys[c.n] = x.keys[i];
        if (!c.leaf)
            c.child[c.n + 1] = s.child[0];
 
        x.keys[i] = s.keys[0];
 
        for (int j = 1; j < s.n; j++)
            s.keys[j - 1] = s.keys[j];
        if (!s.leaf)
            for (int j = 1; j <= s.n; j++)
                s.child[j - 1] = s.child[j];
 
        c.n++;
        s.n--;
    }
 
    // Merge child[i], key[i] and child[i+1] into child[i]
    private void merge(BTreeNode x, int i) {
        BTreeNode c = x.child[i], s = x.child[i + 1];
 
        c.keys[c.n] = x.keys[i];
        for (int j = 0; j < s.n; j++)
            c.keys[c.n + 1 + j] = s.keys[j];
        if (!c.leaf)
            for (int j = 0; j <= s.n; j++)
                c.child[c.n + 1 + j] = s.child[j];
        c.n += s.n + 1;
 
        for (int j = i + 1; j < x.n; j++)
            x.keys[j - 1] = x.keys[j];
        for (int j = i + 2; j <= x.n; j++)
            x.child[j - 1] = x.child[j];
        x.n--;
    }
 
    // --------------------------------------------------------------- DISPLAY
    // Set notation: { child0 key0 child1 key1 ... childN }
    private void display(BTreeNode x) {
        System.out.print("{ ");
        for (int i = 0; i < x.n; i++) {
            if (!x.leaf)
                display(x.child[i]);
            System.out.print(x.keys[i] + " ");
        }
        if (!x.leaf)
            display(x.child[x.n]);
        System.out.print("} ");
    }
 
    void display() {
        if (root == null)
            System.out.println("Tree is empty!");
        else {
            display(root);
            System.out.println();
        }
    }
 
    // ------------------------------------------------------- Main Program
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
 
        System.out.print("Enter order m of the B-tree (m >= 3): ");
        int m = sc.nextInt();
        if (m < 3) {
            System.out.println("Order must be at least 3.");
            return;
        }
 
        BTree tree = new BTree(m);
 
        while (true) {
            System.out.println("\n1.Insert  2.Delete  3.Search  4.Display  5.Exit");
            System.out.print("Enter choice: ");
            int ch = sc.nextInt();
 
            switch (ch) {
                case 1:
                    System.out.print("Enter value: ");
                    tree.insert(sc.nextInt());
                    break;
                case 2:
                    System.out.print("Enter value: ");
                    tree.delete(sc.nextInt());
                    break;
                case 3:
                    System.out.print("Enter value: ");
                    tree.search(sc.nextInt());
                    break;
                case 4:
                    tree.display();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
