import java.util.*;
 
public class KMP {
 
    // Function to build LPS (Longest Prefix Suffix) array
    public static void computeLPS(String pattern, int lps[]) {
        int len = 0;                  // length of the previous longest prefix suffix
        int i = 1;
        lps[0] = 0;
 
        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];      // fall back, do not increment i
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
    }
 
    // KMP Search : prints every starting index where the pattern occurs
    public static void KMPSearch(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
 
        if (m == 0 || m > n) {
            System.out.println("Pattern is empty or longer than the text.");
            return;
        }
 
        int lps[] = new int[m];
        computeLPS(pattern, lps);
        System.out.println("LPS array: " + Arrays.toString(lps));
 
        int i = 0;                    // index for text
        int j = 0;                    // index for pattern
        boolean found = false;
 
        System.out.println("Matching positions:");
        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
 
            if (j == m) {
                System.out.println("Pattern found at index " + (i - j));
                found = true;
                j = lps[j - 1];  // look for the next (overlapping) match
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0)
                    j = lps[j - 1];                      // skip already matched prefix
                else
                    i++;
            }
        }
 
        if (!found)
            System.out.println("Pattern not found");
    }
 
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
 
        System.out.print("Enter text: ");
        String text = sc.nextLine();
 
        System.out.print("Enter pattern: ");
        String pattern = sc.nextLine();
 
        KMPSearch(text, pattern);
    }
}
