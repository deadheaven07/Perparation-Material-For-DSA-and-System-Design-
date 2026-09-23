# 06. Advanced Math & Randomized Algorithms

[← Back to Modular Arithmetic](./05-modular-arithmetic-and-fast-exponentiation.md) | [Track Hub](./README.md) | [Java Track Home](../README.md)

---

## 🏛️ 1. Theoretical Foundations: Randomized Algorithms & Matrix Spaces

In production distributed systems, big-data streaming architectures, and algorithmic interviews, two mathematical patterns solve otherwise intractable problems:
1. **Streaming Randomization (Reservoir Sampling)**: Sampling uniformly when the dataset size $N$ is unknown or too massive to fit into RAM.
2. **Matrix Exponentiation**: Accelerating linear recurrences from linear $\mathcal{O}(N)$ time to logarithmic $\mathcal{O}(\log N)$ time.

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                          ADVANCED MATHEMATICAL ALGORITHMS MATRIX                          │
├────────────────────┬──────────────────────┬──────────────────────┬────────────────────────┤
│ Paradigm           │ Primary Invariant    │ Time Complexity      │ Space Complexity       │
├────────────────────┼──────────────────────┼──────────────────────┼────────────────────────┤
│ Reservoir Sampling │ Exact k/N probability│ O(N) streaming       │ O(k) buffer in RAM     │
│ Fisher-Yates       │ Uniform 1/N! perms   │ O(N) in-place        │ O(1) auxiliary         │
│ Matrix Power       │ Recurrence State     │ O(D^3 log N)         │ O(D^2) matrix space    │
╰────────────────────┴──────────────────────┴──────────────────────┴────────────────────────╯
```

---

## ⚡ 2. Reservoir Sampling: Uniform Sampling on Infinite Streams

Given an unbounded stream of elements arriving one by one, choose $k$ elements such that every element has an **equal probability of $\frac{k}{N}$** of being selected at any point $N$.

### 2.1 The Probability Proof by Induction
- For the first $k$ elements, add them directly to the reservoir (probability $= 1 = k / k$).
- For the $i$-th element ($i > k$), generate a random integer $r \in [0, i - 1]$.
  - If $r < k$, replace `reservoir[r]` with the new element.
- **Induction**:
  $$P(\text{element } j \text{ survives after step } i) = \frac{k}{i - 1} \times \left(1 - \frac{k}{i} \times \frac{1}{k}\right) = \frac{k}{i - 1} \times \frac{i - 1}{i} = \frac{k}{i}$$

```java
import java.util.*;

public final class ReservoirSampler<T> {

    private final Random random = new Random();

    public List<T> sample(Iterator<T> stream, int k) {
        List<T> reservoir = new ArrayList<>(k);

        // Fill initial k items
        int i = 0;
        while (stream.hasNext() && i < k) {
            reservoir.add(stream.next());
            i++;
        }

        // Process remaining streaming elements
        while (stream.hasNext()) {
            T item = stream.next();
            i++;

            // Pick a random index in [0, i - 1]
            int r = random.nextInt(i);
            if (r < k) {
                reservoir.set(r, item); // Replace element in reservoir
            }
        }

        return reservoir;
    }
}
```

---

## 🎲 3. Fisher-Yates (Knuth) Shuffle: $\mathcal{O}(N)$ In-Place

Generates an unbiased, uniformly distributed random permutation where each of the $N!$ possible permutations has probability $\frac{1}{N!}$.

```java
public final class FisherYatesShuffle {

    private final Random random = new Random();

    public void shuffle(int[] nums) {
        // Iterate backward from n - 1 down to 1
        for (int i = nums.length - 1; i > 0; i--) {
            // Pick a uniform random index j in [0, i] inclusive
            int j = random.nextInt(i + 1);
            swap(nums, i, j);
        }
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
}
```

---

## 🧮 4. Fast Matrix Exponentiation: Computing Fibonacci in $\mathcal{O}(\log N)$

The linear recurrence $F(n) = F(n - 1) + F(n - 2)$ can be expressed as a matrix multiplication:

$$\begin{pmatrix} F(n+1) \\ F(n) \end{pmatrix} = \begin{pmatrix} 1 & 1 \\ 1 & 0 \end{pmatrix} \begin{pmatrix} F(n) \\ F(n-1) \end{pmatrix} \implies \begin{pmatrix} F(n+1) \\ F(n) \end{pmatrix} = \begin{pmatrix} 1 & 1 \\ 1 & 0 \end{pmatrix}^n \begin{pmatrix} F(1) \\ F(0) \end{pmatrix}$$

By applying binary exponentiation to the $2 \times 2$ transition matrix, $F(N) \pmod M$ is computed in strictly $\mathcal{O}(\log N)$ time.

```java
public final class MatrixFibonacci {

    private static final long MOD = 1_000_000_007L;

    public static long fib(long n) {
        if (n <= 0) return 0;
        if (n == 1) return 1;

        long[][] t = {{1, 1}, {1, 0}};
        long[][] result = power(t, n - 1);

        // F(n) = result[0][0] * F(1) + result[0][1] * F(0)
        return result[0][0];
    }

    private static long[][] power(long[][] a, long exp) {
        long[][] res = {{1, 0}, {0, 1}}; // Identity matrix
        long[][] base = a;

        while (exp > 0) {
            if ((exp & 1) == 1) {
                res = multiply(res, base);
            }
            base = multiply(base, base);
            exp >>= 1;
        }

        return res;
    }

    private static long[][] multiply(long[][] a, long[][] b) {
        long[][] c = new long[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    c[i][j] = (c[i][j] + (a[i][k] * b[k][j]) % MOD) % MOD;
                }
            }
        }
        return c;
    }
}
```

---

## 🧮 Complexity Analysis

| Algorithm | Time Complexity | Auxiliary Space | Determinism |
| :--- | :--- | :--- | :--- |
| **`Reservoir Sampling`** | $\mathcal{O}(N)$ stream scan | $\mathcal{O}(k)$ buffer | Randomized (Uniform) |
| **`Fisher-Yates Shuffle`**| $\mathcal{O}(N)$ in-place | $\mathcal{O}(1)$ | Randomized (Uniform $1/N!$) |
| **`Matrix Fibonacci`** | $\mathcal{O}(\log N)$ | $\mathcal{O}(1)$ | Deterministic exact |

---

<div align="center">

| [← Back to Modular Arithmetic](./05-modular-arithmetic-and-fast-exponentiation.md) | [Track Hub: Bit Manipulation & Math](./README.md) | [Java Track Home](../README.md) |
| :--- | :---: | ---: |

</div>
