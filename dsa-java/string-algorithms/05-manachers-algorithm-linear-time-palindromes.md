# 05. Manacher's Linear-Time Palindromes

[← Back to Z-Algorithm](./04-z-algorithm-and-longest-common-prefix.md) | [Track Hub](./README.md) | [Next: Aho-Corasick Multi-Pattern Automata →](./06-aho-corasick-and-multi-pattern-automata.md)

---

## 🏛️ 1. Theoretical Foundations: Overcoming the $\mathcal{O}(N^2)$ Palindrome Barrier

The standard expand-around-center approach takes $\mathcal{O}(N^2)$ worst-case time on strings consisting of identical characters (e.g., $S = \text{"aaaa...a"}$).
**Manacher's Algorithm** (1975) solves the Longest Palindromic Substring problem in strictly **linear $\mathcal{O}(N)$ time** by leveraging previously computed palindrome radii across a symmetric center.

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                               THE MANACHER REFLECTION INVARIANT                           │
├───────────────────────────────────────────────────────────────────────────────────────────┤
│ Let C be the center of the rightmost-reaching palindrome, and R its right boundary.       │
│ For any query index i < R, its mirrored reflection across C is i' = 2*C - i.             │
│ By symmetry: P[i] >= min(R - i, P[i'])                                                    │
╰───────────────────────────────────────────────────────────────────────────────────────────╯
```

```
String T:    [ . . . . i' . . . . C . . . . i . . . . R ]
                       │          │         │         │
                       └──────────┴─────────┴─────────┘
                         Mirrored index: i' = 2*C - i
```

---

## ⚡ 2. Virtual Character Padding: Unifying Odd & Even Palindromes

A string of length $N$ has both odd-length palindromes (e.g., `"aba"`) and even-length palindromes (e.g., `"abba"`).
To eliminate separate code branches for even and odd lengths, we interleave the string with a delimiter character `#` and wrap it with unique sentinels `^` and `$`:

$$\text{"aba"} \implies \text{"\textasciicircum\#a\#b\#a\#\$"}$$
$$\text{"abba"} \implies \text{"\textasciicircum\#a\#b\#b\#a\#\$"}$$

- The transformed string length is strictly $2N + 3$, which is always **odd**.
- Every palindrome in the original string corresponds to an **odd-length palindrome centered on a character or `#`** in the padded string.
- If $P[i]$ is the radius of the palindrome centered at $i$ in the padded string, the length of the corresponding palindrome in the original string is **exactly $P[i]$**!

---

## 🚀 3. Manacher's Algorithm Implementation: Strictly $\mathcal{O}(N)$

Because the right boundary $R$ strictly advances from $0$ to $2N + 3$ and never moves backward, the inner expansion loop executes at most $2N + 3$ times across the entire run.

```java
public final class ManachersAlgorithm {

    public static String longestPalindrome(String s) {
        if (s == null || s.isEmpty()) return "";

        // Step 1: Transform string to "^#a#b#a#$"
        char[] t = preprocess(s);
        int n = t.length;
        int[] p = new int[n]; // p[i] = palindrome radius centered at i

        int c = 0; // Center of rightmost palindrome
        int r = 0; // Right boundary of rightmost palindrome

        int maxLen = 0;
        int centerIndex = 0;

        for (int i = 1; i < n - 1; i++) {
            int mirror = 2 * c - i;

            if (i < r) {
                // Mirror value bounded by distance to right boundary
                p[i] = Math.min(r - i, p[mirror]);
            }

            // Attempt to expand palindrome centered at i
            // Sentinels '^' and '$' guarantee loop termination without bounds checks
            while (t[i + 1 + p[i]] == t[i - 1 - p[i]]) {
                p[i]++;
            }

            // If expanded beyond right boundary r, update center and boundary
            if (i + p[i] > r) {
                c = i;
                r = i + p[i];
            }

            // Track global maximum palindrome
            if (p[i] > maxLen) {
                maxLen = p[i];
                centerIndex = i;
            }
        }

        // Map back to original string coordinates
        int start = (centerIndex - 1 - maxLen) / 2;
        return s.substring(start, start + maxLen);
    }

    private static char[] preprocess(String s) {
        char[] t = new char[s.length() * 2 + 3];
        t[0] = '^'; // Start sentinel
        int idx = 1;
        for (char ch : s.toCharArray()) {
            t[idx++] = '#';
            t[idx++] = ch;
        }
        t[idx++] = '#';
        t[idx] = '$'; // End sentinel
        return t;
    }
}
```

---

## 🧮 4. Counting All Palindromic Substrings in $\mathcal{O}(N)$

Given string $s$, return the total number of palindromic substrings.
With Manacher's radius array $P$, each radius $P[i]$ contributes exactly $\lfloor (P[i] + 1) / 2 \rfloor$ distinct palindromic substrings!

```java
public static int countSubstrings(String s) {
    char[] t = preprocess(s);
    int n = t.length;
    int[] p = new int[n];
    int c = 0, r = 0;
    int totalCount = 0;

    for (int i = 1; i < n - 1; i++) {
        int mirror = 2 * c - i;
        if (i < r) {
            p[i] = Math.min(r - i, p[mirror]);
        }

        while (t[i + 1 + p[i]] == t[i - 1 - p[i]]) {
            p[i]++;
        }

        if (i + p[i] > r) {
            c = i;
            r = i + p[i];
        }

        totalCount += (p[i] + 1) / 2;
    }

    return totalCount;
}
```

---

## 🧮 Complexity Analysis

| Metric | Expand Around Center | Dynamic Programming | Manacher's Algorithm |
| :--- | :--- | :--- | :--- |
| **Worst-Case Time** | $\mathcal{O}(N^2)$ | $\mathcal{O}(N^2)$ | $\mathcal{O}(N)$ strictly linear |
| **Best-Case Time** | $\mathcal{O}(N)$ | $\mathcal{O}(N^2)$ | $\mathcal{O}(N)$ |
| **Auxiliary Space** | $\mathcal{O}(1)$ optimal memory | $\mathcal{O}(N^2)$ matrix | $\mathcal{O}(N)$ transformed array |

---

<div align="center">

| [← Back to Z-Algorithm](./04-z-algorithm-and-longest-common-prefix.md) | [Track Hub: Strings](./README.md) | [Next: Aho-Corasick Multi-Pattern Automata →](./06-aho-corasick-and-multi-pattern-automata.md) |
| :--- | :---: | ---: |

</div>
