# 01. Bitwise Fundamentals & Two's Complement in Java

[← Back to Bit Manipulation Hub](./README.md) | [Track Hub](./README.md) | [Next: The XOR Family & Frequency Cancellation →](./02-the-xor-family-and-frequency-cancellation.md)

---

## 🏛️ 1. Hardware Architecture & Register Mechanics

At the CPU hardware level, bitwise operations are executed by the **Arithmetic Logic Unit (ALU)** in a single clock cycle ($< 0.5\text{ ns}$). Because they operate directly on processor registers, they avoid all memory bus traffic, cache invalidation, and object pointer indirection.

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                                MASTER BITWISE OPERATORS IN JAVA                           │
├───────────┬──────────────┬───────────────────────────────────┬────────────────────────────┤
│ Operator  │ Name         │ Definition                        │ Hardware Invariant         │
├───────────┼──────────────┼───────────────────────────────────┼────────────────────────────┤
│ &         │ Bitwise AND  │ 1 if both bits are 1, else 0      │ Bit masking & testing      │
│ |         │ Bitwise OR   │ 1 if either bit is 1, else 0      │ Bit setting & union        │
│ ^         │ Bitwise XOR  │ 1 if bits differ, 0 if identical  │ Parity & cancellation      │
│ ~         │ Bitwise NOT  │ Inverts every bit (1->0, 0->1)    │ Bitwise complement         │
│ <<        │ Left Shift   │ Shifts bits left; fills with 0s   │ Multiplies by 2^k          │
│ >>        │ Signed Right │ Shifts right; preserves sign bit  │ Floor division by 2^k      │
│ >>>       │ Unsigned R.  │ Shifts right; zero-fills MSB      │ Unsigned logical shift     │
╰───────────┴──────────────┴───────────────────────────────────┴────────────────────────────╯
```

---

## ⚡ 2. The 6 Universal Silicon Bit Hacks

```java
public final class BitHacks {

    // 1. Check if k-th bit is set (0-indexed)
    public static boolean isKthBitSet(int n, int k) {
        return ((n >> k) & 1) == 1;
    }

    // 2. Set k-th bit
    public static int setKthBit(int n, int k) {
        return n | (1 << k);
    }

    // 3. Clear k-th bit
    public static int clearKthBit(int n, int k) {
        return n & ~(1 << k);
    }

    // 4. Toggle k-th bit
    public static int toggleKthBit(int n, int k) {
        return n ^ (1 << k);
    }

    // 5. Clear the Lowest Set Bit (LSB) -> Brian Kernighan's Fundamental Step
    // Example: 01011000 & 01010111 = 01010000
    public static int clearLowestSetBit(int n) {
        return n & (n - 1);
    }

    // 6. Isolate the Lowest Set Bit (LSB)
    // Example: n = 12 (1100) -> -n = -12 (...0100) -> 1100 & ...0100 = 0100 (4)
    public static int isolateLowestSetBit(int n) {
        return n & (-n);
    }

    // 7. Check if number is an exact Power of Two
    // A power of two in binary has exactly one '1' bit (e.g. 16 = 10000). Clearing it yields 0.
    public static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }
}
```

---

## 🧮 3. Hamming Weight: Counting Set Bits ($O(\text{set bits})$ vs $O(1)$)

Given a 32-bit integer, count the number of `1` bits it contains.

### 3.1 Brian Kernighan's Algorithm: $\mathcal{O}(\text{number of set bits})$
Instead of looping 32 times, each iteration of `n &= (n - 1)` zeroes out the lowest set bit. Thus, the loop runs strictly equal to the number of set bits.

```java
public static int countSetBitsKernighan(int n) {
    int count = 0;
    while (n != 0) {
        n &= (n - 1); // Clears the lowest set bit
        count++;
    }
    return count;
}
```

### 3.2 SWAR (SIMD Within A Register) Parallel Reduction: $\mathcal{O}(1)$
How the JVM's `Integer.bitCount(int n)` implements hardware popcount without looping:

```java
public static int bitCountParallel(int i) {
    // HD, Figure 5-2
    i = i - ((i >>> 1) & 0x55555555);
    i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
    i = (i + (i >>> 4)) & 0x0f0f0f0f;
    i = i + (i >>> 8);
    i = i + (i >>> 16);
    return i & 0x3f;
}
```

---

## 🔄 4. Reversing a 32-Bit Integer's Bits in $\mathcal{O}(1)$

Reverse bits of a given 32-bit unsigned integer.

```java
public static int reverseBits(int n) {
    // Divide and conquer: swap 16-bit blocks, then 8-bit, 4-bit, 2-bit, 1-bit
    n = ((n >>> 16) | (n << 16));
    n = (((n & 0xff00ff00) >>> 8) | ((n & 0x00ff00ff) << 8));
    n = (((n & 0xf0f0f0f0) >>> 4) | ((n & 0x0f0f0f0f) << 4));
    n = (((n & 0xcccccccc) >>> 2) | ((n & 0x33333333) << 2));
    n = (((n & 0xaaaaaaaa) >>> 1) | ((n & 0x55555555) << 1));
    return n;
}
```

---

## 🧮 Complexity Analysis

| Operation | Time Complexity | Auxiliary Space | Clock Cycles |
| :--- | :--- | :--- | :--- |
| **`n & (n - 1)`** | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ | 1 cycle |
| **`n & -n`** | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ | 1 cycle |
| **`Kernighan bitCount`** | $\mathcal{O}(K)$ where $K = \text{set bits}$ | $\mathcal{O}(1)$ | $\le 32$ cycles |
| **`SWAR bitCount`** | $\mathcal{O}(1)$ branchless | $\mathcal{O}(1)$ | 12 cycles |
| **`Reverse Bits`** | $\mathcal{O}(1)$ branchless | $\mathcal{O}(1)$ | 15 cycles |

---

<div align="center">

| [← Back to Bit Manipulation Hub](./README.md) | [Track Hub: Bit Manipulation & Math](./README.md) | [Next: The XOR Family & Frequency Cancellation →](./02-the-xor-family-and-frequency-cancellation.md) |
| :--- | :---: | ---: |

</div>
