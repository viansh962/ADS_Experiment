import java.util.*;
 
public class Hashing_Experiment {
 
    static final int n = 10;                               // Hash table size
    static int[] linearProbingTable    = new int[n];
    static int[] quadraticProbingTable = new int[n];
    static int[] doubleHashTable       = new int[n];
 
    @SuppressWarnings("unchecked")
    static LinkedList<Integer>[] chainingTable = new LinkedList[n];
 
    public static void main(String[] args) {
        Arrays.fill(linearProbingTable, -1);                // -1 means "empty slot"
        Arrays.fill(quadraticProbingTable, -1);
        Arrays.fill(doubleHashTable, -1);
        for (int i = 0; i < n; i++)
            chainingTable[i] = new LinkedList<>();
 
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of keys k (1 <= k <= " + n + "): ");
        int k = sc.nextInt();
        if (k < 1 || k > n) {
            System.out.println("Invalid k. It must satisfy 1 <= k <= " + n);
            return;
        }
 
        System.out.println("Enter the keys (positive integers):");
        int[] keys = new int[k];
        for (int i = 0; i < k; i++) {
            keys[i] = sc.nextInt();
            if (keys[i] < 1) {
                System.out.println("Keys must be positive integers (1 to m).");
                return;
            }
        }
 
        System.out.println("\n=== Linear Probing Insertion ===");
        for (int key : keys)
            linearProbingInsert(key);
        printTable(linearProbingTable);
 
        System.out.println("\n=== Quadratic Probing Insertion ===");
        for (int key : keys)
            quadraticProbingInsert(key);
        printTable(quadraticProbingTable);
 
        System.out.println("\n=== Double Hashing Insertion ===");
        for (int key : keys)
            doubleHashInsert(key);
        printTable(doubleHashTable);
 
        System.out.println("\n=== Chaining Insertion ===");
        for (int key : keys)
            chainingInsert(key);
        printChainingTable();
    }
 
    // a) Linear probing : index = (key % n + i) % n
    static void linearProbingInsert(int key) {
        int loc = key % n;
        for (int i = 0; i < n; i++) {
            int idx = (loc + i) % n;
            if (linearProbingTable[idx] == key) {
                System.out.println("Duplicate key ignored: " + key);
                return;
            }
            if (linearProbingTable[idx] == -1) {
                linearProbingTable[idx] = key;
                return;
            }
        }
        System.out.println("Table full, cannot insert key: " + key);
    }
 
    // b) Quadratic probing : index = (key % n + i*i) % n
    static void quadraticProbingInsert(int key) {
        int loc = key % n;
        for (int i = 0; i < n; i++) {
            int idx = (loc + i * i) % n;
            if (quadraticProbingTable[idx] == key) {
                System.out.println("Duplicate key ignored: " + key);
                return;
            }
            if (quadraticProbingTable[idx] == -1) {
                quadraticProbingTable[idx] = key;
                return;
            }
        }
        // Quadratic probing may not visit every slot, so a free slot can still exist.
        System.out.println("No free slot found on probe sequence for key: " + key);
    }
 
    // c) Double hashing : index = (h1(key) + i * h2(key)) % n
    static void doubleHashInsert(int key) {
        int h1 = key % n;
        int h2 = 1 + (key % (n - 1));          // second hash function, value in [1, n-1]
        while (gcd(h2, n) != 1)                // step must be relatively prime to n,
            h2++;                              // otherwise some slots are never probed
        for (int i = 0; i < n; i++) {
            int idx = (h1 + i * h2) % n;
            if (doubleHashTable[idx] == key) {
                System.out.println("Duplicate key ignored: " + key);
                return;
            }
            if (doubleHashTable[idx] == -1) {
                doubleHashTable[idx] = key;
                return;
            }
        }
        System.out.println("Table full, cannot insert key: " + key);
    }
 
    // d) Separate chaining : each slot holds a linked list
    static void chainingInsert(int key) {
        int loc = key % n;
        if (chainingTable[loc].contains(key)) {
            System.out.println("Duplicate key ignored: " + key);
            return;
        }
        chainingTable[loc].add(key);
    }
 
    static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
 
    static void printTable(int[] table) {
        for (int i = 0; i < n; i++)
            System.out.println(i + " -> " + (table[i] == -1 ? "Empty" : table[i]));
    }
 
    static void printChainingTable() {
        for (int i = 0; i < n; i++) {
            System.out.print(i + " -> ");
            for (int key : chainingTable[i])
                System.out.print(key + " -> ");
            System.out.println("NULL");
        }
    }
}
