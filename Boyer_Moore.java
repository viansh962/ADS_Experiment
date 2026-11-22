import java.util.*;
 
public class Boyer_Moore {
 
    static final int CHAR = 65536;           // all possible Java char values
 
    // ---------- Bad Character Heuristic ----------
    // badChar[c] = index of the LAST occurrence of c in the pattern (-1 if absent)
    static int[] badCharacterHeuristic(String pattern) {
        int[] badChar = new int[CHAR];
        Arrays.fill(badChar, -1);
        for (int i = 0; i < pattern.length(); i++)
            badChar[pattern.charAt(i)] = i;
        return badChar;
    }
 
    // ---------- Good Suffix Heuristic ----------
    // Case 1 : strong good suffix (another occurrence of the matched suffix in the pattern)
    static void preprocessStrongSuffix(int[] shift, int[] bpos, String pat) {
        int m = pat.length();
        int i = m, j = m + 1;
        bpos[i] = j;
 
        while (i > 0) {
            while (j <= m && pat.charAt(i - 1) != pat.charAt(j - 1)) {
                if (shift[j] == 0)
                    shift[j] = j - i;
                j = bpos[j];
            }
            i--;
            j--;
            bpos[i] = j;
        }
    }
 
    // Case 2 : a prefix of the pattern matches a suffix of the matched part (widest border)
    static void preprocessCase2(int[] shift, int[] bpos, String pat) {
        int m = pat.length();
        int j = bpos[0];
        for (int i = 0; i <= m; i++) {
            if (shift[i] == 0)
                shift[i] = j;
            if (i == j)
                j = bpos[j];
        }
    }
 
    // ---------- Boyer-Moore Search ----------
    static void boyerMooreSearch(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
 
        if (m == 0 || m > n) {
            System.out.println("Pattern is empty or longer than the text.");
            return;
        }
 
        int[] badChar = badCharacterHeuristic(pattern);
 
        int[] shift = new int[m + 1];
        int[] bpos = new int[m + 1];
        preprocessStrongSuffix(shift, bpos, pattern);
        preprocessCase2(shift, bpos, pattern);
 
        int s = 0;  // shift of the pattern over the text
        boolean found = false;
 
        System.out.println("Matching positions:");
        while (s <= n - m) {
            int j = m - 1;
 
            // Compare from the right end of the pattern
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j))
                j--;
 
            if (j < 0) {                                  // match found
                System.out.println("Pattern found at index " + s);
                found = true;
                s += shift[0];
            } else {                                      // mismatch: take the larger shift
                int bc = j - badChar[text.charAt(s + j)];
                s += Math.max(shift[j + 1], bc);
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
 
        boyerMooreSearch(text, pattern);
    }
}
