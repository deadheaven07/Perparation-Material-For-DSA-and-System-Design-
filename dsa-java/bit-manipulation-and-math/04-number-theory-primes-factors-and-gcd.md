# 04. Number Theory: Primes, Factors & GCD

[← Back to Bitmask Subsets](./03-bitmask-subsets-and-state-representations.md) | [Track Hub](./README.md) | [Next: Modular Arithmetic & Fast Exponentiation →](./05-modular-arithmetic-and-fast-exponentiation.md)

---

## 🏛️ 1. Theoretical Foundations: The Fundamental Theorem of Arithmetic

Every integer $N > 1$ can be uniquely represented as a product of prime powers:
$$N = p_1^{a_1} \cdot p_2^{a_2} \cdots p_k^{a_k}$$

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                              PRIMALITY & FACTORING THEOREMS                               │
├────────────────────┬──────────────────────┬──────────────────────┬────────────────────────┤
│ Operation          │ Small Scale (N <= 10^9)│ Large Scale Range    │ Complexity             │
├────────────────────┼──────────────────────┼──────────────────────┼────────────────────────┤
│ Single Prime Test  │ Trial Division       │ Miller-Rabin         │ O(sqrt(N))             │
│ All Primes <= N    │ Sieve of Eratosthenes│ Segmented Sieve      │ O(N log log N)         │
│ Linear Sieve (SPF) │ Euler's Sieve        │ Multiplicative Funcs │ O(N) strictly linear   │
│ GCD / LCM          │ Euclidean Algorithm  │ Binary GCD           │ O(log(min(a, b)))      │
╰────────────────────┴──────────────────────┴──────────────────────┴────────────────────────╯
```

---

## ⚡ 2. Primality Testing: Trial Division in $O(\sqrt{N})$

If $N$ is composite, it must have a factor $d \le \sqrt{N}$. If all numbers up to $\sqrt{N}$ fail to divide $N$, $N$ is guaranteed to be prime.

```java
public final class PrimalityTest {

    public static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;

        // All primes > 3 are of the form 6k ± 1
        for (int i = 5; (long) i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
}
```

---

## 🧮 3. Sieve of Eratosthenes vs. Euler's Linear Sieve

### 3.1 Sieve of Eratosthenes: $\mathcal{O}(N \log \log N)$
Iteratively cross out multiples of primes starting from $p^2$.

```java
public static int countPrimes(int n) {
    if (n <= 2) return 0;

    boolean[] isComposite = new boolean[n];
    int count = 0;

    for (int p = 2; (long) p * p < n; p++) {
        if (!isComposite[p]) {
            // Start crossing out from p^2 (all smaller multiples already crossed out)
            for (int multiple = p * p; multiple < n; multiple += p) {
                isComposite[multiple] = true;
            }
        }
    }

    for (int i = 2; i < n; i++) {
        if (!isComposite[i]) count++;
    }
    return count;
}
```

### 3.2 Euler's Linear Sieve with Smallest Prime Factor (SPF): Strictly $\mathcal{O}(N)$
In the standard sieve, a composite number like $12$ is crossed out multiple times (by 2, then by 3).
Euler's Sieve guarantees that **every composite number is crossed out exactly once by its smallest prime factor**.

```java
public final class LinearSieve {

    private final int[] spf; // Smallest Prime Factor for each number
    private final List<Integer> primes;

    public LinearSieve(int maxN) {
        this.spf = new int[maxN + 1];
        this.primes = new ArrayList<>();

        for (int i = 2; i <= maxN; i++) {
            if (spf[i] == 0) {
                spf[i] = i; // i is prime
                primes.add(i);
            }

            for (int p : primes) {
                if (p > spf[i] || (long) i * p > maxN) {
                    break; // CRITICAL: Guarantees each composite is marked only by its SPF
                }
                spf[i * p] = p;
            }
        }
    }

    // Instant O(log N) prime factorization using precomputed SPF array
    public List<Integer> getPrimeFactors(int n) {
        List<Integer> factors = new ArrayList<>();
        while (n > 1) {
            factors.add(spf[n]);
            n /= spf[n];
        }
        return factors;
    }
}
```

---

## 📐 4. Greatest Common Divisor (GCD) & Least Common Multiple (LCM)

### 4.1 The Euclidean Invariant: $\gcd(a, b) = \gcd(b, a \bmod b)$
By Lamé's Theorem, the number of division steps in the Euclidean algorithm never exceeds $5 \times \text{number of digits of } \min(a, b)$, running in strictly $\mathcal{O}(\log(\min(a, b)))$.

```java
public final class GcdLcm {

    // Greatest Common Divisor
    public static long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }

    // Least Common Multiple with 64-bit Overflow Prevention
    public static long lcm(long a, long b) {
        if (a == 0 || b == 0) return 0;
        // Divide before multiplying to avoid premature integer overflow
        return Math.abs((a / gcd(a, b)) * b);
    }
}
```

---

## 🧮 Complexity Analysis

| Algorithm | Precomputation Time | Query Time | Auxiliary Space |
| :--- | :--- | :--- | :--- |
| **`Trial Division`** | None | $\mathcal{O}(\sqrt{N})$ | $\mathcal{O}(1)$ |
| **`Sieve of Eratosthenes`** | $\mathcal{O}(N \log \log N)$ | $\mathcal{O}(1)$ lookup | $\mathcal{O}(N)$ |
| **`Linear Sieve (SPF)`** | $\mathcal{O}(N)$ strictly linear | $\mathcal{O}(\log N)$ factorization | $\mathcal{O}(N)$ |
| **`Euclidean GCD`** | None | $\mathcal{O}(\log(\min(a, b)))$ | $\mathcal{O}(1)$ |

---

<div align="center">

| [← Back to Bitmask Subsets](./03-bitmask-subsets-and-state-representations.md) | [Track Hub: Bit Manipulation & Math](./README.md) | [Next: Modular Arithmetic & Fast Exponentiation →](./05-modular-arithmetic-and-fast-exponentiation.md) |
| :--- | :---: | ---: |

</div>
