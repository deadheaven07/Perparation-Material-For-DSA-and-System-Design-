# 04. Strings, Sequences & Edit Distance

[← Back to Knapsack Family](./03-the-knapsack-family-and-subset-sums.md) | [Track Hub](./README.md) | [Next: Interval DP & Game Theory →](./05-interval-dp-and-game-theory.md)

---

## 🏛️ 1. Theoretical Foundations: 2-String Prefix Matching DP

String and sequence dynamic programming typically aligns two sequences $s$ (length $M$) and $t$ (length $N$).

### The 2D Prefix Matrix Invariant
We define $dp[i][j]$ as the solution for prefix $s[0 \dots i - 1]$ and prefix $t[0 \dots j - 1]$. 
- Row index $0$ represents the empty string `""` of $s$.
- Column index $0$ represents the empty string `""` of $t$.

```
                     t[j - 1] (Character Match or Insert)
                         │
                         ▼
  dp[i - 1][j - 1] ───────────────► dp[i - 1][j] (Deletion from s)
   (Diagonal Match/Replace)             │
                                        ▼
  dp[i][j - 1] (Insertion into s) ──► dp[i][j] (Current State)
```

---

## 2. Problem 1: Longest Common Subsequence (LCS)

### 2.1 Problem Statement & Constraints

Given two strings `text1` and `text2`, return the length of their **longest common subsequence**. If there is no common subsequence, return `0`.

A **subsequence** of a string is a new string generated from the original string with some characters (can be none) deleted without changing the relative order of the remaining characters.

```
Example 1:
Input: text1 = "abcde", text2 = "ace"
Output: 3
Explanation: The longest common subsequence is "ace" and its length is 3.

Example 2:
Input: text1 = "abc", text2 = "def"
Output: 0
```

#### Constraints:
- $1 \le \text{text1.length}, \text{text2.length} \le 1000$.
- `text1` and `text2` consist of only lowercase English characters.

---

### 2.2 Thought Process & Bellman Transition

```
  Case 1: Characters Match (text1[i - 1] == text2[j - 1])
  Both characters contribute 1 to the common sequence!
  Optimal choice: Take the diagonal predecessor + 1:
  dp[i][j] = dp[i - 1][j - 1] + 1
                         ↓
  Case 2: Characters Mismatch (text1[i - 1] != text2[j - 1])
  At least one of these characters cannot be part of the match at this step:
  Either discard text1[i - 1] (dp[i - 1][j]) OR discard text2[j - 1] (dp[i][j - 1]):
  dp[i][j] = max(dp[i - 1][j], dp[i][j - 1])
                         ↓
  Space Compression:
  Row i depends ONLY on Row i - 1!
  Compress 2D table into two rolling rows (or a single 1D array with diagonal variable):
  Space: O(min(M, N))!
```

---

### 2.3 Production Java 17/21 Implementation

```java
public final class LongestCommonSubsequence {

    /**
     * Finds LCS length with O(min(M, N)) rolling space optimization.
     *
     * Time Complexity:  O(M * N) where M = text1.length(), N = text2.length().
     * Space Complexity: O(min(M, N)) using a 1D array and diagonal tracker.
     */
    public int longestCommonSubsequence(String text1, String text2) {
        // Ensure text2 is the shorter string to minimize space
        if (text1.length() < text2.length()) {
            return longestCommonSubsequence(text2, text1);
        }

        int m = text1.length();
        int n = text2.length();
        int[] dp = new int[n + 1];

        for (int i = 1; i <= m; i++) {
            int prevDiagonal = 0; // Represents dp[i - 1][j - 1]
            char c1 = text1.charAt(i - 1);

            for (int j = 1; j <= n; j++) {
                int temp = dp[j]; // Save dp[i - 1][j] for next column's diagonal

                if (c1 == text2.charAt(j - 1)) {
                    dp[j] = prevDiagonal + 1;
                } else {
                    dp[j] = Math.max(dp[j], dp[j - 1]);
                }

                prevDiagonal = temp;
            }
        }

        return dp[n];
    }
}
```

---

## 3. Problem 2: Edit Distance (Levenshtein Distance — Hard)

### 3.1 Problem Statement & Constraints

Given two strings `word1` and `word2`, return the minimum number of operations required to convert `word1` to `word2`.

You have the following three operations permitted on a word:
- Insert a character
- Delete a character
- Replace a character

```
Example 1:
Input: word1 = "horse", word2 = "ros"
Output: 3
Explanation: 
horse -> rorse (replace 'h' with 'r')
rorse -> rose (remove 'r')
rose -> ros (remove 'e')
```

#### Constraints:
- $0 \le \text{word1.length}, \text{word2.length} \le 500$.
- `word1` and `word2` consist of lowercase English letters.

---

### 3.2 Thought Process & Invariant Derivation

```
  State Definition:
  dp[i][j] = Minimum edits to transform word1[0..i-1] into word2[0..j-1].
                         ↓
  Base Cases:
  dp[i][0] = i (Deleting all i characters of word1 to form empty string).
  dp[0][j] = j (Inserting all j characters of word2 into empty string).
                         ↓
  Transition:
  If word1[i - 1] == word2[j - 1]:
  No operation required! dp[i][j] = dp[i - 1][j - 1].
  
  If word1[i - 1] != word2[j - 1]:
  We must choose the minimum among three possible edit operations:
  1. Insert into word1:   1 + dp[i][j - 1]
  2. Delete from word1:   1 + dp[i - 1][j]
  3. Replace character:   1 + dp[i - 1][j - 1]
  dp[i][j] = 1 + min({ dp[i][j - 1], dp[i - 1][j], dp[i - 1][j - 1] })
```

---

### 3.3 Production Java 17/21 Implementation

```java
public final class EditDistance {

    /**
     * Calculates minimum Levenshtein edit distance in O(min(M, N)) space.
     *
     * Time Complexity:  O(M * N)
     * Space Complexity: O(min(M, N))
     */
    public int minDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();

        // Base cases for empty strings
        if (m == 0) return n;
        if (n == 0) return m;

        int[] dp = new int[n + 1];
        for (int j = 0; j <= n; j++) {
            dp[j] = j; // Converting empty string to word2[0..j-1] requires j insertions
        }

        for (int i = 1; i <= m; i++) {
            int prevDiagonal = dp[0]; // Represents dp[i - 1][0]
            dp[0] = i; // Converting word1[0..i-1] to empty string requires i deletions

            for (int j = 1; j <= n; j++) {
                int temp = dp[j];

                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[j] = prevDiagonal;
                } else {
                    int insertOp = dp[j - 1];
                    int deleteOp = dp[j];
                    int replaceOp = prevDiagonal;

                    dp[j] = 1 + Math.min(replaceOp, Math.min(insertOp, deleteOp));
                }

                prevDiagonal = temp;
            }
        }

        return dp[n];
    }
}
```

---

## 4. Problem 3: Regular Expression Matching (Hard)

### 4.1 Problem Statement & Constraints

Given an input string `s` and a pattern `p`, implement regular expression matching with support for `'.'` and `'*'` where:
- `'.'` Matches any single character.
- `'*'` Matches zero or more of the preceding element.

The matching should cover the **entire** input string (not partial).

```
Example 1:
Input: s = "aa", p = "a*"
Output: true
Explanation: '*' means zero or more of 'a'. "aa" matches.

Example 2:
Input: s = "ab", p = ".*"
Output: true
Explanation: ".*" means "zero or more (*) of any character (.)".
```

#### Constraints:
- $1 \le \text{s.length} \le 20$.
- $1 \le \text{p.length} \le 20$.
- `s` contains only lowercase English letters.
- `p` contains only lowercase English letters, `'.'`, and `'*'`.
- Guaranteed for each appearance of `'*'`, there will be a valid preceding character to match.

---

### 4.2 Thought Process & Invariant Branching

```
  State Definition:
  dp[i][j] = true if s[0..i-1] matches pattern p[0..j-1].
                         ↓
  Branch 1: Normal Character or '.' (p[j - 1] != '*')
  Characters must match: (s[i - 1] == p[j - 1] || p[j - 1] == '.')
  dp[i][j] = match && dp[i - 1][j - 1]
                         ↓
  Branch 2: Wildcard Star (p[j - 1] == '*')
  '*' can act in TWO distinct ways:
  • Case 2A: Zero occurrences of preceding character p[j - 2]:
    Completely ignore p[j - 2] and '*':
    dp[i][j] = dp[i][j - 2]
  • Case 2B: One or more occurrences of preceding character:
    Requires that s[i - 1] matches p[j - 2]:
    dp[i][j] = dp[i][j] || (matches(s[i - 1], p[j - 2]) && dp[i - 1][j])
```

---

### 4.3 Production Java 17/21 Implementation

```java
public final class RegularExpressionMatching {

    /**
     * Evaluates regex matching with '.' and '*' support via 2D DP.
     *
     * Time Complexity:  O(M * N) where M = s.length(), N = p.length().
     * Space Complexity: O(M * N) for boolean DP matrix.
     */
    public boolean isMatch(String s, String p) {
        int m = s.length();
        int n = p.length();

        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true; // Empty pattern matches empty string

        // Handle patterns matching empty string (e.g., "a*", "a*b*")
        for (int j = 2; j <= n; j += 2) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 2];
            }
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char pChar = p.charAt(j - 1);

                if (pChar != '*') {
                    if (pChar == '.' || pChar == s.charAt(i - 1)) {
                        dp[i][j] = dp[i - 1][j - 1];
                    }
                } else {
                    // 1. Zero occurrences of preceding element
                    dp[i][j] = dp[i][j - 2];

                    // 2. One or more occurrences of preceding element
                    char prevChar = p.charAt(j - 2);
                    if (prevChar == '.' || prevChar == s.charAt(i - 1)) {
                        dp[i][j] = dp[i][j] || dp[i - 1][j];
                    }
                }
            }
        }

        return dp[m][n];
    }
}
```

---

<div align="center">

| [← Back to Knapsack Family](./03-the-knapsack-family-and-subset-sums.md) | [Track Hub: Dynamic Programming](./README.md) | [Next: Interval DP & Game Theory →](./05-interval-dp-and-game-theory.md) |
| :--- | :---: | ---: |

</div>
