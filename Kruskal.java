import java.util.*;
 
class Edge implements Comparable<Edge> {
    int src, dest, wt;
 
    public Edge(int src, int dest, int wt) {
        this.src = src;
        this.dest = dest;
        this.wt = wt;
    }
 
    public int compareTo(Edge e) {
        return Integer.compare(this.wt, e.wt);          // sort by weight
    }
}
 
public class Kruskal {
 
    int[] parent, rank;
 
    // Disjoint Set (Union-Find) : find with path compression
    int find(int i) {
        if (parent[i] != i)
            parent[i] = find(parent[i]);
        return parent[i];
    }
 
    // Union by rank; returns false if x and y are already in the same set (cycle)
    boolean union(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry)
            return false;
        if (rank[rx] < rank[ry])
            parent[rx] = ry;
        else if (rank[rx] > rank[ry])
            parent[ry] = rx;
        else {
            parent[ry] = rx;
            rank[rx]++;
        }
        return true;
    }
 
    void kruskalMST(int vertices, List<Edge> edges) {
 
        Collections.sort(edges);
 
        parent = new int[vertices];
        rank = new int[vertices];
        for (int i = 0; i < vertices; i++)
            parent[i] = i;
 
        List<Edge> mst = new ArrayList<>();
        int minCost = 0;
 
        for (Edge e : edges) {
            if (union(e.src, e.dest)) {          // edge does not form a cycle
                mst.add(e);
                minCost += e.wt;
            }
            if (mst.size() == vertices - 1)
                break;
        }
 
        if (mst.size() != vertices - 1) {
            System.out.println("\nThe graph is not connected, so no spanning tree exists.");
            return;
        }
 
        System.out.println("\nEdges in Minimum Cost Spanning Tree:");
        for (Edge e : mst)
            System.out.println(e.src + " -- " + e.dest + " == " + e.wt);
        System.out.println("Minimum Cost: " + minCost);
    }
 
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
 
        System.out.print("Enter number of vertices: ");
        int v = sc.nextInt();
 
        System.out.print("Enter number of edges: ");
        int e = sc.nextInt();
 
        List<Edge> edges = new ArrayList<>();
 
        System.out.println("\nEnter edges (src dest weight):");
        for (int i = 0; i < e; i++) {
            int s = sc.nextInt();
            int d = sc.nextInt();
            int w = sc.nextInt();
            if (s < 0 || s >= v || d < 0 || d >= v) {
                System.out.println("Vertex numbers must be between 0 and " + (v - 1));
                return;
            }
            edges.add(new Edge(s, d, w));
        }
 
        Kruskal k = new Kruskal();
        k.kruskalMST(v, edges);
    }
}
