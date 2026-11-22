import java.util.*;
 
public class LCA {
 
    static int LOG;                       // number of binary-lifting levels
    static List<Integer>[] tree;          // adjacency lists
    static int[][] parent;                // parent[v][j] = 2^j-th ancestor of v (0 = none)
    static int[] level;                   // depth of each node (root = 0)
    static int n;                         // number of nodes (nodes are numbered 1..n)
 
    // BFS from the root: computes level[] and parent[][0]
    static void bfs(int root) {
        boolean[] visited = new boolean[n + 1];
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(root);
        visited[root] = true;
        parent[root][0] = 0;
        level[root] = 0;
 
        while (!queue.isEmpty()) {
            int node = queue.poll();
            for (int child : tree[node]) {
                if (!visited[child]) {
                    visited[child] = true;
                    parent[child][0] = node;
                    level[child] = level[node] + 1;
                    queue.add(child);
                }
            }
        }
    }
 
    // Build binary lifting table : parent[i][j] = parent[ parent[i][j-1] ][j-1]
    static void buildLCA() {
        for (int j = 1; j < LOG; j++)
            for (int i = 1; i <= n; i++)
                parent[i][j] = parent[parent[i][j - 1]][j - 1];   // parent[0][*] = 0
    }
 
    // Compute LCA using binary lifting in O(log n)
    static int lca(int u, int v) {
        if (level[u] < level[v]) {
            int temp = u;
            u = v;
            v = temp;
        }
 
        // Lift u up to v's level
        int diff = level[u] - level[v];
        for (int i = 0; i < LOG; i++)
            if ((diff & (1 << i)) != 0)
                u = parent[u][i];
 
        if (u == v)
            return u;
 
        // Lift both until their parents coincide
        for (int i = LOG - 1; i >= 0; i--) {
            if (parent[u][i] != parent[v][i]) {
                u = parent[u][i];
                v = parent[v][i];
            }
        }
 
        return parent[u][0];
    }
 
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
 
        System.out.print("Enter number of nodes: ");
        n = sc.nextInt();
 
        LOG = 1;
        while ((1 << LOG) <= n)
            LOG++;
 
        tree = new ArrayList[n + 1];
        parent = new int[n + 1][LOG];
        level = new int[n + 1];
 
        for (int i = 1; i <= n; i++)
            tree[i] = new ArrayList<>();
 
        System.out.println("Enter edges (u v):");
        for (int i = 0; i < n - 1; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            tree[u].add(v);
            tree[v].add(u);
        }
 
        bfs(1);                           // root of the tree is node 1
        buildLCA();
 
        while (true) {
            System.out.println("\n1. Find LCA  2. Exit");
            System.out.print("Enter choice: ");
            int ch = sc.nextInt();
 
            if (ch == 1) {
                System.out.print("Enter two nodes: ");
                int a = sc.nextInt();
                int b = sc.nextInt();
                if (a < 1 || a > n || b < 1 || b > n)
                    System.out.println("Nodes must be between 1 and " + n);
                else
                    System.out.println("LCA = " + lca(a, b));
            } else {
                break;
            }
        }
    }
}
