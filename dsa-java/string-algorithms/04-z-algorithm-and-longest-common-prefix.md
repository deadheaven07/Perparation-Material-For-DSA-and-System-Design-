# 04. The Z-Algorithm & Longest Common Prefix

[← Back to Rabin-Karp](./03-rabin-karp-rolling-hash-and-string-matching.md) | [Track Hub](./README.md) | [Next: Manacher's Linear-Time Palindromes →](./05-manachers-algorithm-linear-time-palindromes.md)

---

## 🏛️ 1. Theoretical Foundations: The $Z$-Array Invariant

Given a string $S$ of length $N$, the **$Z$-array** is an integer array where $Z[i]$ represents the length of the **Longest Common Prefix (LCP)** between the entire string $S$ and the suffix of $S$ starting at index $i$:
$$Z[i] = \max \{ k \mid S[0..k-1] == S[i..i+k-1] \}$$

```
Index:    0   1   2   3   4   5   6   7
String:   a   a   b   x   a   a   b   a
Z-Array: [0,  1,  0,  0,  3,  1,  0,  1]
                          │
                          └─ Substring starting at index 4 is "aab...",
                             matching prefix "aab" of length 3!
```

---

## ⚡ 2. The $[L, R]$ Z-Box Window Invariant: Strictly $\mathcal{O}(N)$

To compute the $Z$-array in linear time without comparing characters repeatedly, we maintain a **Z-box** $[L, R]$, which is the interval with the maximum $R$ such that $S[L..R]$ matches a prefix of $S$ ($S[0..R - L]$).

```
String S:   [ 0 . . . . . . R - L ] . . . . [ L . . . . . . R ]
              │             │                 │             │
              └─────────────┴─────────────────┴─────────────┘
                Prefix matching substring inside Z-box [L, R]
```

### 2.1 The Two Invariant Cases
For current index $i$:
1. **Case 1: $i > R$ (Outside Z-Box)**:
   We have no prior information. Compare characters $S[i..]$ with $S[0..]$ directly. If matches found, establish new $[L, R]$.
2. **Case 2: $i \le R$ (Inside Z-Box)**:
   Let $k = i - L$ be the corresponding prefix index.
   - **Subcase 2a**: If $Z[k] < R - i + 1$, the prefix does not reach the right boundary $R$. By symmetry, $Z[i] = Z[k]$ in $\mathcal{O}(1)$ without any comparisons!
   - **Subcase 2b**: If $Z[k] \ge R - i + 1$, the match extends to at least $R$. We start comparing characters only from $R + 1$ onward, updating $L = i$ and increasing $R$.

Because $R$ strictly increases from $0$ to $N$, the algorithm makes at most $2N$ comparisons, running in strictly **$\mathcal{O}(N)$ time**.

```java
public final class ZAlgorithm {

    public static int[] computeZArray(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;

        for (int i = 1; i < n; i++) {
            if (i <= r) {
                // Inside Z-box: mirror from prefix index i - l
                z[i] = Math.min(r - i + 1, z[i - l]);
            }

            // Attempt to expand beyond current known boundary
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }

            // Update Z-box boundaries if new rightmost boundary reached
            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }

        return z;
    }
}
```

---

## 🎯 3. Pattern Matching via Concatenation: $P + \text{"\$"} + T$

To find all occurrences of pattern $P$ (length $M$) in text $T$ (length $N$):
1. Concatenate $S = P + \text{"\$"} + T$, where `$` is a delimiter character not present in $P$ or $T$.
2. Compute the $Z$-array of $S$.
3. Any index $i > M$ where $Z[i] == M$ corresponds to an exact match in $T$ starting at index $i - M - 1$!

```java
public static List<Integer> searchPattern(String text, String pattern) {
    List<Integer> matches = new ArrayList<>();
    if (pattern.isEmpty() || text.length() < pattern.length()) {
        return matches;
    }

    String concat = pattern + "$" + text;
    int[] z = computeZArray(concat);
    int m = pattern.length();

    for (int i = m + 1; i < concat.length(); i++) {
        if (z[i] == m) {
            matches.add(i - m - 1); // Exact match index in original text
        }
    }

    return matches;
}
```

---

## 🧮 4. Comparison: KMP vs. Z-Algorithm

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                                 KMP VS. Z-ALGORITHM COMPARISON                            │
├────────────────────┬───────────────────────────────┬──────────────────────────────────────┤
│ Metric             │ KMP Algorithm (LPS π)         │ Z-Algorithm (Z-Array)                │
├────────────────────┼───────────────────────────────┼──────────────────────────────────────┤
│ State Array Focus  │ Longest Prefix-Suffix of P[0..i]│ Longest Common Prefix of S[i..N-1]  │
│ Delimiter Usage    │ None (searches streamed text) │ Requires P + "$" + T concatenation   │
│ Streaming Friendly │ Yes (O(1) memory state)       │ No (requires entire string in RAM)   │
│ Code Complexity    │ Subtle index edge cases       │ Extremely clean Z-box window         │
│ Time Complexity    │ Strictly O(N + M)             │ Strictly O(N + M)                    │
╰────────────────────┴───────────────────────────────┴──────────────────────────────────────╯
```

---

## 🧮 Complexity Analysis

| Algorithm | Precomputation | Search Time | Auxiliary Space |
| :--- | :--- | :--- | :--- |
| **`Z-Array Construction`** | None | $\mathcal{O}(N)$ | $\mathcal{O}(N)$ integer array |
| **`Z Pattern Matching`** | None | $\mathcal{O}(N + M)$ | $\mathcal{O}(N + M)$ concatenated string |

---

<div align="center">

| [← Back to Rabin-Karp](./03-rabin-karp-rolling-hash-and-string-matching.md) | [Track Hub: Strings](./README.md) | [Next: Manacher's Linear-Time Palindromes →](./05-manachers-algorithm-linear-time-palindromes.md) |
| :--- | :---: | ---: |

</div>
