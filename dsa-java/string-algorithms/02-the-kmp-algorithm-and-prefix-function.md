# 02. The KMP Algorithm & Prefix Function

[← Back to String Internals](./01-string-internals-and-two-pointer-palindromes.md) | [Track Hub](./README.md) | [Next: Rabin-Karp Rolling Hash →](./03-rabin-karp-rolling-hash-and-string-matching.md)

---

## 🏛️ 1. Theoretical Foundations: Overcoming Quadratic Degradation

In naive string searching, when searching for pattern $P$ of length $M$ in text $T$ of length $N$, a mismatch forces the text pointer to reset backward by $j - 1$ steps.
- **Worst-Case Pathological Example**: $T = \text{"aaaaaaaaab"}$, $P = \text{"aaab"}$.
- Naive algorithm takes $\mathcal{O}(N \cdot M)$ time.

The **Knuth-Morris-Pratt (KMP)** algorithm eliminates backward backtracking of the text pointer by extracting the **prefix-suffix symmetry** of the pattern into the **$\pi$ array** (Longest Proper Prefix which is also a Suffix, or **LPS**).

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                                  THE LPS ARRAY INVARIANT                                  │
├───────────────────────────────────────────────────────────────────────────────────────────┤
│ lps[i] is the length of the longest proper prefix of P[0..i] that matches a suffix of P[0..i]│
│ Proper Prefix: A prefix that is NOT equal to the entire string itself.                    │
╰───────────────────────────────────────────────────────────────────────────────────────────╯
```

```
Pattern:  A  B  A  B  A  C
Index:    0  1  2  3  4  5
LPS:     [0, 0, 1, 2, 3, 0]
                 │  │  │
                 │  │  └─ "ABA" matches prefix "ABA" (length 3)
                 │  └──── "AB" matches prefix "AB"   (length 2)
                 └─────── "A" matches prefix "A"     (length 1)
```

---

## ⚡ 2. Constructing the LPS Array in $\mathcal{O}(M)$ Time

```java
public final class KmpPatternMatcher {

    public static int[] computeLps(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0; // Length of previous longest prefix suffix
        int i = 1;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    // Fall back to previous prefix length without advancing i
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }
}
```

---

## 🚀 3. KMP Search Execution: $\mathcal{O}(N + M)$ Time

Because text pointer `i` strictly advances and never decrements, and pattern pointer `j` drops by at most $j$ via the LPS table, the total number of operations across both loops is bounded by $2N$.

```java
public static int strStr(String haystack, String needle) {
    if (needle.isEmpty()) return 0;
    if (haystack.length() < needle.length()) return -1;

    int[] lps = computeLps(needle);
    int i = 0; // Pointer for haystack
    int j = 0; // Pointer for needle

    while (i < haystack.length()) {
        if (haystack.charAt(i) == needle.charAt(j)) {
            i++;
            j++;
        }

        // Full pattern match found
        if (j == needle.length()) {
            return i - j; // Starting index of match
        } else if (i < haystack.length() && haystack.charAt(i) != needle.charAt(j)) {
            if (j != 0) {
                // Shift pattern forward based on LPS table
                j = lps[j - 1];
            } else {
                i++;
            }
        }
    }

    return -1; // Needle not found
}
```

---

## 📐 4. Repeated Substring Pattern via Periodicity Arithmetic

Given a string `s`, check if it can be constructed by taking a substring of it and appending one or more copies of the substring together.

### 4.1 The Period Invariant Theorem
Let $n = \text{length}(s)$ and $L = \text{lps}[n - 1]$:
1. If $s$ is periodic with period $P$, then the suffix of length $n - P$ must match the prefix of length $n - P$.
2. Thus, the longest proper prefix-suffix has length $L = n - P$, which implies $P = n - L$.
3. If $L > 0$ and $n \pmod{(n - L)} == 0$, the string is formed by repeating the prefix of length $n - L$!

```java
public static boolean repeatedSubstringPattern(String s) {
    int n = s.length();
    int[] lps = computeLps(s);
    int len = lps[n - 1];

    // Period length is (n - len). If n is divisible by this period and len > 0:
    return len > 0 && (n % (n - len) == 0);
}
```

---

## 🧮 Complexity Analysis

| Step | Time Complexity | Auxiliary Space | Determinism |
| :--- | :--- | :--- | :--- |
| **`computeLps(P)`** | $\mathcal{O}(M)$ linear | $\mathcal{O}(M)$ integer array | Deterministic |
| **`KMP Search`** | $\mathcal{O}(N)$ linear | $\mathcal{O}(1)$ beyond LPS table | Deterministic (Zero Backtracking) |
| **`Repeated Pattern`**| $\mathcal{O}(N)$ | $\mathcal{O}(N)$ | Mathematical Exactness |

---

<div align="center">

| [← Back to String Internals](./01-string-internals-and-two-pointer-palindromes.md) | [Track Hub: Strings](./README.md) | [Next: Rabin-Karp Rolling Hash →](./03-rabin-karp-rolling-hash-and-string-matching.md) |
| :--- | :---: | ---: |

</div>
