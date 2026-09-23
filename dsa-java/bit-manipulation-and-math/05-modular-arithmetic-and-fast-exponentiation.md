# 05. Modular Arithmetic & Fast Exponentiation

[← Back to Number Theory](./04-number-theory-primes-factors-and-gcd.md) | [Track Hub](./README.md) | [Next: Advanced Math & Randomized Algorithms →](./06-advanced-math-and-randomized-algorithms.md)

---

## 🏛️ 1. Theoretical Foundations: Modular Arithmetic Axioms

In competitive programming, enterprise cryptography, and hashing algorithms, calculations frequently produce numbers far larger than 64-bit registers. We compute results modulo a large prime $M$ (typically $10^9 + 7 = 1{,}000{,}000{,}007$ or $998{,}244{,}353$).

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                               MODULAR ARITHMETIC RULES IN JAVA                            │
├───────────────┬───────────────────────────────────┬───────────────────────────────────────┤
│ Operation     │ Mathematical Identity            │ Java Implementation Invariant         │
├───────────────┼───────────────────────────────────┼───────────────────────────────────────┤
│ Addition      │ (a + b) mod M                     │ ((a % M) + (b % M)) % M               │
│ Subtraction   │ (a - b) mod M                     │ ((a % M) - (b % M) + M) % M  <-- +M!  │
│ Multiplication│ (a * b) mod M                     │ ((long) a * b) % M           <-- long!│
│ Division      │ (a / b) mod M = a * b^(-1) mod M  │ a * power(b, M - 2, M) % M            │
╰───────────────┴───────────────────────────────────┴───────────────────────────────────────╯
```

> [!WARNING]
> **Java Modulo Operator `%` Preserves Negative Sign**:
> In Java, `-7 % 5 == -2`, not `+3`. To ensure all modulo operations yield positive remainders in range $[0, M - 1]$, always add $M$:
> `int result = (val % M + M) % M;`

---

## ⚡ 2. Binary Exponentiation: Computing $a^b \pmod M$ in $\mathcal{O}(\log b)$

```java
public final class ModularMath {

    public static long powerMod(long base, long exp, long mod) {
        long result = 1;
        base %= mod;

        while (exp > 0) {
            // If exp is odd, multiply base with result
            if ((exp & 1) == 1) {
                result = (result * base) % mod;
            }
            // exp must be even now; square the base
            base = (base * base) % mod;
            exp >>= 1;
        }

        return result;
    }
}
```

---

## 🧮 3. Modular Multiplicative Inverse via Fermat's Little Theorem

### 3.1 The Fermat Invariant
If $M$ is a prime number and $\gcd(a, M) = 1$, then:
$$a^{M - 1} \equiv 1 \pmod M$$
Multiplying both sides by $a^{-1}$:
$$a^{-1} \equiv a^{M - 2} \pmod M$$
Therefore, division $\frac{a}{b} \pmod M$ is computed as:
$$\frac{a}{b} \equiv a \cdot b^{M - 2} \pmod M$$

```java
public static long modInverse(long a, long primeMod) {
    return powerMod(a, primeMod - 2, primeMod);
}
```

---

## 🚀 4. Combinatorics at Scale: $n\text{C}r \pmod M$ in $\mathcal{O}(1)$ Query Time

Computing $\binom{n}{r} = \frac{n!}{r! (n - r)!} \pmod M$ for multiple queries up to $N = 10^6$.

```java
public final class Combinatorics {

    private final int mod;
    private final long[] fact;
    private final long[] invFact;

    public Combinatorics(int maxN, int mod) {
        this.mod = mod;
        this.fact = new long[maxN + 1];
        this.invFact = new long[maxN + 1];

        // Step 1: Precompute factorials in O(N)
        fact[0] = 1;
        for (int i = 1; i <= maxN; i++) {
            fact[i] = (fact[i - 1] * i) % mod;
        }

        // Step 2: Compute invFact[maxN] using Fermat's Little Theorem in O(log mod)
        invFact[maxN] = ModularMath.powerMod(fact[maxN], mod - 2, mod);

        // Step 3: Precompute all inverse factorials backward in O(N)
        // Since (n!)^(-1) * n = ((n-1)!)^(-1)
        for (int i = maxN - 1; i >= 0; i--) {
            invFact[i] = (invFact[i + 1] * (i + 1)) % mod;
        }
    }

    // Instant O(1) combinations query
    public long nCr(int n, int r) {
        if (r < 0 || r > n) return 0;
        return (((fact[n] * invFact[r]) % mod) * invFact[n - r]) % mod;
    }
}
```

---

## 🧮 Complexity Analysis

| Algorithm | Precomputation | Query Time | Auxiliary Space |
| :--- | :--- | :--- | :--- |
| **`powerMod(a, b, M)`** | None | $\mathcal{O}(\log b)$ | $\mathcal{O}(1)$ |
| **`modInverse(a, M)`** | None | $\mathcal{O}(\log M)$ | $\mathcal{O}(1)$ |
| **`Combinatorics nCr`** | $\mathcal{O}(N)$ | $\mathcal{O}(1)$ | $\mathcal{O}(N)$ |

---

<div align="center">

| [← Back to Number Theory](./04-number-theory-primes-factors-and-gcd.md) | [Track Hub: Bit Manipulation & Math](./README.md) | [Next: Advanced Math & Randomized Algorithms →](./06-advanced-math-and-randomized-algorithms.md) |
| :--- | :---: | ---: |

</div>
