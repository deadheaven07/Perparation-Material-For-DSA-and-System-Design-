# Page 01: 1D Arrays & Memory Architecture

To write high-performance algorithms, an engineer must understand not just the syntax of arrays, but what happens physically inside **RAM, the CPU cache hierarchy, and the JVM heap** when an array is declared, accessed, or resized.

---

## 1. What is an Array? Mathematical Foundation

An **Array** is a linear data structure consisting of a collection of elements of the same data type, stored in a **contiguous block of physical memory**, where each element is identifiable by an integer index.

### The Random Access Addressing Formula
The defining superpower of an array is **$O(1)$ random access**. Given any valid index $i$, the CPU does not iterate through prior elements; it computes the exact memory address in a single arithmetic clock cycle:

$$\text{Address}(A[i]) = \text{Base Address} + (i \times \text{Size of Single Element})$$

```text
Logical View:  Index:    0      1      2      3      4
               Value:  [ 42 ] [ 99 ] [ 15 ] [ 88 ] [ 73 ]
                         │      │      │      │      │
Physical RAM:  Address: 0x1000 0x1004 0x1008 0x100C 0x1010  (Assuming 4-byte integers)

Calculation for A[3]:
Base Address       = 0x1000
Index (i)          = 3
Element Size       = 4 bytes
Target Address     = 0x1000 + (3 * 4) = 0x1000 + 12 (0x0C) = 0x100C
```

Because memory hardware (RAM) operates via direct address lines, looking up index `0` and looking up index `1,000,000` takes the exact same physical time ($O(1)$).

---

## 2. JVM Heap Layout: What Does a Java Array Look Like in Memory?

In Java, arrays are **first-class heap objects**. Unlike C/C++ where a primitive array is merely a raw memory pointer, a Java array carries metadata enforced by the runtime.

### The Memory Footprint of `int[] arr = new int[4];`

On a 64-bit JVM with Compressed OOPs (Ordinary Object Pointers, default for heaps $< 32\text{ GB}$):

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                         JVM Object Layout: int[4]                           │
├────────────────────────────────────────────────────────┬────────────────────┤
│ Component                                              │ Size               │
├────────────────────────────────────────────────────────┼────────────────────┤
│ 1. Mark Word (Hashcode, GC age, Locking state)         │ 8 bytes            │
│ 2. Klass Word (Pointer to class metadata: [I.class)    │ 4 bytes (comp-oops)│
│ 3. Array Length field (stores array.length = 4)         │ 4 bytes            │
│ 4. Element Payload: 4 integers × 4 bytes               │ 16 bytes           │
│ 5. Alignment Padding (JVM aligns objects to 8 bytes)   │ 0 bytes (32 = 8×4) │
├────────────────────────────────────────────────────────┼────────────────────┤
│ Total Heap Memory Occupied                             │ 32 bytes           │
╰────────────────────────────────────────────────────────┴────────────────────╯
```

```text
Byte Offset: 0        8       12      16             20             24             28            32
             ┌────────┬───────┬───────┬──────────────┬──────────────┬──────────────┬──────────────┐
Content:     │ Mark   │ Klass │ Array │ Element 0    │ Element 1    │ Element 2    │ Element 3    │
             │ Word   │ Word  │ Length│ (int: 4B)    │ (int: 4B)    │ (int: 4B)    │ (int: 4B)    │
             └────────┴───────┴───────┴──────────────┴──────────────┴──────────────┴──────────────┘
             ◄── 16-Byte Header ─────►◄──────────────── 16-Byte Payload ─────────────────────────►
```

> [!NOTE]
> Because the `Array Length` field is a 32-bit signed integer (`int`), the maximum theoretical length of any array in Java is $2^{31} - 1 = 2,147,483,647$. In practice, most JVM implementations reserve $8$ header bytes, capping the safe maximum array size at `Integer.MAX_VALUE - 8`.

---

## 3. Primitives vs. Boxed References: Memory Bloat & Cache Misses

One of the most critical performance mistakes in Java is confusing primitive arrays with boxed arrays:

```java
int[] primitiveArr = new int[1_000_000];       // ~4 MB heap footprint
Integer[] boxedArr = new Integer[1_000_000];   // ~20-24 MB heap footprint!
```

```text
1. Primitive Array: int[4] (Direct contiguous values)
   [Header: 16B] ──► [ 10 ] [ 20 ] [ 30 ] [ 40 ] (Values stored inline)

2. Boxed Object Array: Integer[4] (Array of pointers!)
   [Header: 16B] ──► [ Pointer 0 ] [ Pointer 1 ] [ Pointer 2 ] [ Pointer 3 ]
                            │             │             │             │
                            ▼             ▼             ▼             ▼
                       [Integer:16B] [Integer:16B] [Integer:16B] [Integer:16B]
                       val = 10      val = 20      val = 30      val = 40
                       (Scattered arbitrarily across heap memory!)
```

### Why `Integer[]` Kills CPU Cache Performance
1. **Memory Bloat**: Each `Integer` wrapper object requires its own 16-byte object header plus 4 bytes of data, aligned to 24 bytes, plus the 4-byte pointer in the main array. That is $28$ bytes per number instead of $4$ bytes ($7\times$ memory consumption).
2. **Pointer Chasing**: To read `boxedArr[i]`, the CPU must fetch the pointer, then make a separate memory lookup to find where the `Integer` object lives in heap memory.
3. **Cache Line Thrashing**: Primitive arrays guarantee spatial contiguity. Boxed integers can be scattered across different memory pages, guaranteeing CPU L1/L2 cache misses.

---

## 4. CPU Cache Lines & Spatial Locality

Modern CPUs do not fetch memory one byte at a time. When the CPU requests memory address `0x1000`, the hardware memory controller fetches a full **Cache Line (typically 64 bytes)** into the L1 data cache.

```text
Memory Request: A[0] (Address 0x1000)
Hardware Action: Fetches 64 bytes (0x1000 to 0x103F) into L1 Cache!
Result: If element size is 4 bytes (int), fetching A[0] brings A[0] through A[15] 
        into L1 cache simultaneously for FREE!
```

### Benchmark: Sequential Access vs. Stride Jump
```java
// Sequential traversal: 100% Cache Friendly
// 1 Cache Miss followed by 15 Cache Hits per 64-byte line
long sum = 0;
for (int i = 0; i < n; i++) {
    sum += arr[i]; // Ultra-fast (~0.5 ns per element)
}

// Strided traversal (e.g. step size 16 ints = 64 bytes):
// Every single read forces a new L1/L2 Cache Miss and RAM fetch!
for (int i = 0; i < n; i += 16) {
    sum += arr[i]; // ~10x to 20x slower (~10-50 ns per element)
}
```

---

## 5. Core Array Operations & Complexity

| Operation | Best Case | Average Case | Worst Case | Space | Hardware Mechanism |
| :--- | :---: | :---: | :---: | :---: | :--- |
| **Lookup by Index (`arr[i]`)** | $O(1)$ | $O(1)$ | $O(1)$ | $O(1)$ | Direct pointer arithmetic offset |
| **Update (`arr[i] = val`)** | $O(1)$ | $O(1)$ | $O(1)$ | $O(1)$ | Direct memory overwrite |
| **Insert at End (Static)** | N/A | N/A | N/A | N/A | Fixed size; cannot expand |
| **Insert at End (Dynamic `ArrayList`)** | $O(1)$ | $O(1)$ amortized | $O(N)$ (when resizing) | $O(1)$ | Amortized constant allocation |
| **Insert at Index `0`** | $O(N)$ | $O(N)$ | $O(N)$ | $O(1)$ | Must shift all $N$ elements right |
| **Delete at Index `0`** | $O(N)$ | $O(N)$ | $O(N)$ | $O(1)$ | Must shift all $N-1$ elements left |
| **Linear Search (Unsorted)** | $O(1)$ | $O(N)$ | $O(N)$ | $O(1)$ | Sequential scan until match |
| **Binary Search (Sorted)** | $O(1)$ | $O(\log N)$ | $O(\log N)$ | $O(1)$ | Halving search space |

---

## 6. Dynamic Arrays: How `ArrayList<T>` Works Under the Hood

Because native Java arrays have fixed length at creation, the Java Collections Framework provides `java.util.ArrayList<E>`, which wraps a resizable native array `Object[] elementData`.

### The Growth Factor Formula
When an element is added and the internal array is full (`size == elementData.length`):

```java
// Simplified JDK ArrayList growth logic:
private void grow(int minCapacity) {
    int oldCapacity = elementData.length;
    // New capacity = oldCapacity + (oldCapacity >> 1) = 1.5 * oldCapacity
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity;
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

```text
Capacity Progression:
Initial (default):  10
After 1st resize:   10 + (10 >> 1) = 10 + 5  = 15
After 2nd resize:   15 + (15 >> 1) = 15 + 7  = 22
After 3rd resize:   22 + (22 >> 1) = 22 + 11 = 33
After 4th resize:   33 + (33 >> 1) = 33 + 16 = 49
```

> [!TIP]
> **Why $1.5\times$ growth instead of $2.0\times$?**
> If you double ($2.0\times$), the new array size $2^k$ is always strictly greater than the sum of all previously freed memory chunks ($1 + 2 + 4 + \dots + 2^{k-1} = 2^k - 1$). The JVM memory allocator can never reuse the contiguous block of memory it just abandoned! With a growth factor of $1.5\times$, previous memory blocks can eventually be consolidated and reused.

### The Amortized $O(1)$ Mathematical Proof
Although a single resize operation costs $O(N)$ (allocating a new array and copying all elements), resizes occur exponentially infrequently.

Using the **Accounting / Banker's Method**:
- Assign a cost of **$3$ tokens** to every standard `add()` operation:
  - $1$ token pays for inserting the current element into the array.
  - $1$ token is saved on the newly inserted element to pay for its future move during the next resize.
  - $1$ token is saved to pay for moving an older element that has already exhausted its tokens.
- When the array doubles/expands, the accumulated saved tokens pay for the entire $O(N)$ copy cost. Thus, every append operates in **Amortized $O(1)$** time.

---

## 7. Fast Array Copying: `System.arraycopy` vs. Loops

When copying elements or shifting elements during insertions/deletions, never write a manual `for` loop:

```java
// ❌ SLOW: Manual loop (bounds-checked on every iteration, no SIMD vectorization)
for (int i = 0; i < src.length; i++) {
    dest[i] = src[i];
}

// ✅ ULTRA-FAST: JVM Native Intrinsics
System.arraycopy(src, srcPos, dest, destPos, length);
```

```text
╭─────────────────────────────────────────────────────────────────────────────╮
│                        Why System.arraycopy is Faster                       │
├─────────────────────────────────────────────────────────────────────────────┤
│ 1. Native C++ Assembly: It compiles into a direct rep movsb or SIMD AVX-512 │
│    vector register instruction.                                             │
│ 2. Single Bounds Check: The JVM validates array bounds once at the start    │
│    rather than per element.                                                 │
│ 3. Overlap-Safe: Handles overlapping memory regions within the same array  │
│    without corrupting data (similar to C's memmove).                        │
╰─────────────────────────────────────────────────────────────────────────────╯
```

---

## 8. Self-Check & Active Recall

1. **Q**: What is the exact mathematical formula used by hardware to look up `arr[k]` in an array of 8-byte `long` primitives starting at address `0x4000`?
   - *A*: $\text{Address} = 0\text{x}4000 + (k \times 8)$.

2. **Q**: Why does an `int[1000]` consume significantly less memory than an `Integer[1000]`?
   - *A*: `int[1000]` is a single contiguous block containing a 16-byte header and 4,000 bytes of primitive data ($\approx 4$ KB). `Integer[1000]` creates an array of 1,000 reference pointers ($\approx 4$ KB) pointing to 1,000 separate `Integer` heap objects, each with its own 16-byte header and 8-byte aligned footprint ($24\text{ B} \times 1000 = 24$ KB), totaling $\approx 28$ KB ($7\times$ bloat).

3. **Q**: What is the amortized time complexity of appending $N$ elements to an empty `ArrayList` with an initial capacity of $1$?
   - *A*: Amortized $O(1)$ per append, totaling $O(N)$ cumulative time.

---

## 🧭 Continue Learning

| ◀️ Previous Topic | 🧭 Track Hub | Next Topic ▶️ |
| :--- | :---: | ---: |
| [**Page 00: Problem Types & Taxonomy**](00-array-problem-types-and-algorithm-taxonomy.md)<br><sub>*Question Archetypes & Algorithm Matrix*</sub> | [**Arrays Index**](README.md)<br><sub>*All 9 Modules*</sub> | [**Page 02: 2D & 3D Arrays**](02-multidimensional-arrays-2d-and-3d.md)<br><sub>*Matrix Layout, Jagged Arrays & Flat Math*</sub> |
