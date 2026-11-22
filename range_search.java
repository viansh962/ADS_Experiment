import java.util.*;
 
// Class to store a 2D point
class Point {
    int x, y;
 
    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
 
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
 
// KD Tree Node
class KDNode {
    Point point;
    KDNode left, right;
 
    KDNode(Point point) {
        this.point = point;
        this.left = null;
        this.right = null;
    }
}
 
// KD Tree for 2D Range Search
class KDTree {
    KDNode root;
 
    // Insert a point (recursive). Points with an equal coordinate go to the right subtree.
    KDNode insertRec(KDNode root, Point point, int depth) {
        if (root == null)
            return new KDNode(point);
 
        int cd = depth % 2;                       // cd = current dimension (0 = x, 1 = y)
 
        if (cd == 0) {
            if (point.x < root.point.x)
                root.left = insertRec(root.left, point, depth + 1);
            else
                root.right = insertRec(root.right, point, depth + 1);
        } else {
            if (point.y < root.point.y)
                root.left = insertRec(root.left, point, depth + 1);
            else
                root.right = insertRec(root.right, point, depth + 1);
        }
        return root;
    }
 
    void insert(Point point) {
        root = insertRec(root, point, 0);
    }
 
    // Range Search : report all points with x1 <= x <= x2 and y1 <= y <= y2
    void rangeSearch(KDNode root, int x1, int y1, int x2, int y2,
                     int depth, List<Point> result) {
        if (root == null)
            return;
 
        int x = root.point.x;
        int y = root.point.y;
 
        // Check if this point lies inside the rectangle
        if (x >= x1 && x <= x2 && y >= y1 && y <= y2)
            result.add(root.point);
 
        int cd = depth % 2;
 
        // Left: coordinates < split value; right: coordinates >= split value
        if (cd == 0) {
            if (x1 < x)
                rangeSearch(root.left, x1, y1, x2, y2, depth + 1, result);
            if (x2 >= x)
                rangeSearch(root.right, x1, y1, x2, y2, depth + 1, result);
        } else {
            if (y1 < y)
                rangeSearch(root.left, x1, y1, x2, y2, depth + 1, result);
            if (y2 >= y)
                rangeSearch(root.right, x1, y1, x2, y2, depth + 1, result);
        }
    }
 
    // Display points (preorder)
    void display(KDNode root) {
        if (root == null)
            return;
        System.out.print(root.point + " ");
        display(root.left);
        display(root.right);
    }
}
 
public class range_search 1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        KDTree tree = new KDTree();
 
        while (true) {
            System.out.println("\n1. Insert Point  2. Range Search  3. Display  4. Exit");
            System.out.print("Enter choice: ");
            int ch = sc.nextInt();
 
            switch (ch) {
                case 1:
                    System.out.print("Enter x y: ");
                    int x = sc.nextInt();
                    int y = sc.nextInt();
                    tree.insert(new Point(x, y));
                    break;
 
                case 2:
                    System.out.print("Enter rectangle (x1 y1 x2 y2): ");
                    int x1 = sc.nextInt();
                    int y1 = sc.nextInt();
                    int x2 = sc.nextInt();
                    int y2 = sc.nextInt();
 
                    // allow the corners to be entered in any order
                    int lx = Math.min(x1, x2), hx = Math.max(x1, x2);
                    int ly = Math.min(y1, y2), hy = Math.max(y1, y2);
 
                    List<Point> result = new ArrayList<>();
                    tree.rangeSearch(tree.root, lx, ly, hx, hy, 0, result);
 
                    System.out.println("Points inside range:");
                    if (result.isEmpty())
                        System.out.println("(none)");
                    for (Point p : result)
                        System.out.println(p);
                    break;
 
                case 3:
                    System.out.println("KD-Tree Points:");
                    tree.display(tree.root);
                    System.out.println();
                    break;
 
                case 4:
                    return;
 
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
