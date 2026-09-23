# 03. Rabin-Karp Rolling Hash & String Matching

[← Back to KMP Algorithm](./02-the-kmp-algorithm-and-prefix-function.md) | [Track Hub](./README.md) | [Next: The Z-Algorithm & LCP →](./04-z-algorithm-and-longest-common-prefix.md)

---

## 🏛️ 1. Theoretical Foundations: Polynomial Rolling Hashes

The **Rabin-Karp** algorithm treats strings as numbers in a positional base-$B$ numeral system, computing hash values modulo a large prime $M$.
Instead of recomputing the hash of an $L$-length substring in $\mathcal{O}(L)$ time, a **Rolling Hash** updates the hash value when the sliding window shifts by one position in strictly **$\mathcal{O}(1)$ time**.

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                              THE POLYNOMIAL ROLLING HASH FORMULA                          │
├───────────────────────────────────────────────────────────────────────────────────────────┤
│ Hash(S[0..L-1]) = (S[0]*B^(L-1) + S[1]*B^(L-2) + ... + S[L-1]*B^0) mod M                 │
├───────────────────────────────────────────────────────────────────────────────────────────┤
│ Sliding Window Shift (Drop leftmost S[left], Add incoming S[right]):                       │
│ Hash_new = ((Hash_old - S[left] * B^(L-1)) * B + S[right]) mod M                          │
╰───────────────────────────────────────────────────────────────────────────────────────────╯
```

```
Window slides: [ A  B  C ] D  ->  A [ B  C  D ]
Subtract:       A * B^2
Multiply:      (B * B + C) * B = B * B^2 + C * B
Add:            D * B^0
Result:         B * B^2 + C * B + D  (Exact match for "BCD" in O(1)!)
```

---

## 🛡️ 2. Collision Mitigation: Double Hashing

With a single 32-bit modulus $M \approx 10^9$, by the **Birthday Paradox**, a hash collision is expected after scanning approximately $\sqrt{M} \approx 3 \times 10^4$ substrings.
To achieve cryptographically secure, zero-collision guarantees without full string comparisons:
- **Hash 1**: $B_1 = 31$, $M_1 = 1{,}000{,}000{,}007$
- **Hash 2**: $B_2 = 37$, $M_2 = 1{,}000{,}000{,}009$
- Combined Hash: Packing both into a single 64-bit `long`: `(hash1 << 32) | hash2`.
- Collision Probability: $\approx \frac{1}{M_1 \cdot M_2} \approx 10^{-18}$ (smaller than cosmic ray bit-flip probability).

---

## ⚡ 3. Canonical Problem: Repeated DNA Sequences

Given a string `s` representing a DNA sequence containing characters `'A'`, `'C'`, `'G'`, `'T'`, return all the 10-letter-long sequences that occur more than once.

```java
import java.util.*;

public final class RepeatedDna {

    private static final int L = 10;
    private static final int BASE = 4; // Only 4 characters: A=0, C=1, G=2, T=3
    private static final int BASE_POW = (int) Math.pow(BASE, L - 1); // 4^9

    public static List<String> findRepeatedDnaSequences(String s) {
        if (s == null || s.length() <= L) {
            return Collections.emptyList();
        }

        // Map characters to 2-bit integers
        int[] charMap = new int[26];
        charMap['A' - 'A'] = 0;
        charMap['C' - 'A'] = 1;
        charMap['G' - 'A'] = 2;
        charMap['T' - 'A'] = 3;

        int hash = 0;
        for (int i = 0; i < L; i++) {
            hash = hash * BASE + charMap[s.charAt(i) - 'A'];
        }

        Set<Integer> seen = new HashSet<>();
        Set<String> repeated = new HashSet<>();
        seen.add(hash);

        // Slide window of length L across string
        for (int i = L; i < s.length(); i++) {
            // Drop leftmost character and add incoming character
            int leftChar = charMap[s.charAt(i - L) - 'A'];
            int rightChar = charMap[s.charAt(i) - 'A'];

            hash = (hash - leftChar * BASE_POW) * BASE + rightChar;

            if (!seen.add(hash)) {
                repeated.add(s.substring(i - L + 1, i + 1));
            }
        }

        return new ArrayList<>(repeated);
    }
}
```

---

## 🚀 4. Hard-Tier: Longest Duplicate Substring in $\mathcal{O}(N \log N)$

Find the longest substring that occurs at least twice in string $s$.

### 4.1 Monotonicity & Binary Search on Length
If a duplicate substring of length $L$ exists, then duplicate substrings of all lengths $< L$ are guaranteed to exist. We binary search the answer-space $L \in [1, N - 1]$. For each candidate length $L$, we test existence in $\mathcal{O}(N)$ using Rabin-Karp with Double Hashing.

```java
public final class LongestDuplicateSubstring {

    private static final long MOD1 = 1_000_000_007L;
    private static final long MOD2 = 1_000_000_009L;
    private static final long BASE1 = 31L;
    private static final long BASE2 = 37L;

    public static String longestDupSubstring(String s) {
        int n = s.length();
        int low = 1, high = n - 1;
        String best = "";

        while (low <= high) {
            int mid = low + (high - low) / 2;
            String dup = searchDupOfLength(s, mid);
            if (dup != null) {
                best = dup;
                low = mid + 1; // Try longer substring
            } else {
                high = mid - 1; // Try shorter substring
            }
        }

        return best;
    }

    private static String searchDupOfLength(String s, int len) {
        long h1 = 0, h2 = 0;
        long pow1 = 1, pow2 = 1;

        for (int i = 0; i < len; i++) {
            h1 = (h1 * BASE1 + (s.charAt(i) - 'a')) % MOD1;
            h2 = (h2 * BASE2 + (s.charAt(i) - 'a')) % MOD2;
            if (i > 0) {
                pow1 = (pow1 * BASE1) % MOD1;
                pow2 = (pow2 * BASE2) % MOD2;
            }
        }

        Set<Long> seen = new HashSet<>();
        seen.add((h1 << 32) | h2);

        for (int i = len; i < s.length(); i++) {
            long leftVal = s.charAt(i - len) - 'a';
            long rightVal = s.charAt(i) - 'a';

            h1 = ((h1 - leftVal * pow1 % MOD1 + MOD1) * BASE1 + rightVal) % MOD1;
            h2 = ((h2 - leftVal * pow2 % MOD2 + MOD2) * BASE2 + rightVal) % MOD2;

            long combined = (h1 << 32) | h2;
            if (!seen.add(combined)) {
                return s.substring(i - len + 1, i + 1);
            }
        }

        return null;
    }
}
```

---

## 🧮 Complexity Analysis

| Algorithm | Average Time | Worst-Case Time | Auxiliary Space |
| :--- | :--- | :--- | :--- |
| **`Repeated DNA Sequences`**| $\mathcal{O}(N)$ | $\mathcal{O}(N)$ | $\mathcal{O}(N)$ hash set |
| **`Rabin-Karp Pattern Match`**| $\mathcal{O}(N + M)$ | $\mathcal{O}(N \cdot M)$ single hash / $\mathcal{O}(N + M)$ double | $\mathcal{O}(1)$ |
| **`Longest Duplicate Substr`**| $\mathcal{O}(N \log N)$ | $\mathcal{O}(N \log N)$ | $\mathcal{O}(N)$ |

---

<div align="center">

| [← Back to KMP Algorithm](./02-the-kmp-algorithm-and-prefix-function.md) | [Track Hub: Strings](./README.md) | [Next: The Z-Algorithm & LCP →](./04-z-algorithm-and-longest-common-prefix.md) |
| :--- | :---: | ---: |

</div>
