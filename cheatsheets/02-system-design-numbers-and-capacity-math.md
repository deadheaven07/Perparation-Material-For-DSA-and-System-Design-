# 02. System Design Numbers & Capacity Estimations Cheatsheet

[← Back to DSA Formulas](./01-dsa-formulas-and-invariants-cheatsheet.md) | [Cheatsheets Hub](./README.md) | [Next: GoF Patterns & Concurrency →](./03-gof-patterns-and-concurrency-cheatsheet.md)

---

## 1. Latency Numbers Every Systems Engineer Should Know

*Normalized from Dr. Jeff Dean's canonical latency hierarchy:*

| Operation | Real Time | Scaled Human Analog (1 ns = 1 sec) |
| :--- | :---: | :---: |
| **L1 Cache Reference** | $0.5\text{ ns}$ | $0.5\text{ seconds}$ (Heartbeat) |
| **Branch Mispredict** | $5\text{ ns}$ | $5\text{ seconds}$ |
| **L2 Cache Reference** | $7\text{ ns}$ | $7\text{ seconds}$ |
| **Mutex Lock / Unlock** | $25\text{ ns}$ | $25\text{ seconds}$ |
| **Main Memory Access (RAM)** | $100\text{ ns}$ | $1.7\text{ minutes}$ |
| **Compress 1KB with Snappy** | $10,000\text{ ns} \ (10\ \mu\text{s})$ | $2.8\text{ hours}$ |
| **Read 1MB Sequentially from Memory** | $250,000\text{ ns} \ (250\ \mu\text{s})$ | $2.9\text{ days}$ |
| **Same Datacenter Round Trip (RTT)** | $500,000\text{ ns} \ (500\ \mu\text{s})$ | $5.8\text{ days}$ |
| **Read 1MB Sequentially from NVMe SSD** | $1,000,000\text{ ns} \ (1\text{ ms})$ | $11.6\text{ days}$ |
| **Read 1MB Sequentially from HDD** | $20,000,000\text{ ns} \ (20\text{ ms})$ | $7.7\text{ months}$ |
| **Cross-Continental Internet Round Trip (NYC to London)**| $150,000,000\text{ ns} \ (150\text{ ms})$| $4.8\text{ years}$ |

> [!TIP]
> **The 10x Golden Rule:** RAM access is $\sim 10,000\times$ faster than an SSD read, and an SSD read is $\sim 20\times$ faster than spinning HDD disk seek.

---

## 2. Capacity Shorthand: Powers of 2 vs. Powers of 10

| Power of 2 | Exact Bytes | Approximate Decimal | Standard Name |
| :---: | :---: | :---: | :---: |
| $2^{10}$ | $1,024\text{ B}$ | $10^3 = 1,000$ | **1 Kilobyte (KB)** |
| $2^{20}$ | $1,048,576\text{ B}$ | $10^6 = 1,000,000$ | **1 Megabyte (MB)** |
| $2^{30}$ | $1,073,741,824\text{ B}$ | $10^9 = 1,000,000,000$ | **1 Gigabyte (GB)** |
| $2^{40}$ | $1,099,511,627,776\text{ B}$ | $10^{12} = 1\text{ Trillion}$ | **1 Terabyte (TB)** |
| $2^{50}$ | $1.125 \times 10^{15}\text{ B}$ | $10^{15} = 1\text{ Quadrillion}$ | **1 Petabyte (PB)** |
| $2^{60}$ | $1.152 \times 10^{18}\text{ B}$ | $10^{18} = 1\text{ Quintillion}$ | **1 Exabyte (EB)** |

---

## 3. Seconds in a Day & QPS Quick Conversions

$$\text{Seconds in a Day} = 24 \times 60 \times 60 = \mathbf{86,400\text{ seconds}} \approx \mathbf{10^5\text{ seconds}}$$

*Use this $10^5$ approximation during live interview capacity estimations for rapid mental math:*

| Daily Volume | Exact QPS | Mental Math Approximation ($/10^5$) | Peak QPS ($2.5\times$) |
| :---: | :---: | :---: | :---: |
| **1 Million req / day** | $11.6\text{ QPS}$ | $\approx 10\text{ QPS}$ | $\approx 30\text{ QPS}$ |
| **10 Million req / day** | $115.7\text{ QPS}$ | $\approx 100\text{ QPS}$ | $\approx 300\text{ QPS}$ |
| **100 Million req / day** | $1,157\text{ QPS}$ | $\approx 1,000\text{ QPS}$ | $\approx 3,000\text{ QPS}$ |
| **1 Billion req / day** | $11,574\text{ QPS}$ | $\approx 10,000\text{ QPS}$ | $\approx 30,000\text{ QPS}$ |
| **10 Billion req / day** | $115,740\text{ QPS}$ | $\approx 100,000\text{ QPS}$ | $\approx 300,000\text{ QPS}$ |

---

## 4. High Availability SLAs & Allowable Downtime

$$\text{Availability} = \frac{\text{Uptime}}{\text{Uptime} + \text{Downtime}}$$

| Availability Level | Downtime per Year | Downtime per Month | Downtime per Day |
| :--- | :---: | :---: | :---: |
| **99% (Two Nines)** | $3.65\text{ days}$ | $7.31\text{ hours}$ | $14.4\text{ minutes}$ |
| **99.9% (Three Nines)** | $8.77\text{ hours}$ | $43.8\text{ minutes}$ | $1.44\text{ minutes}$ |
| **99.99% (Four Nines)** | $52.6\text{ minutes}$ | $4.38\text{ minutes}$ | $8.66\text{ seconds}$ |
| **99.999% (Five Nines)** | $5.26\text{ minutes}$ | $26.3\text{ seconds}$ | $0.86\text{ seconds}$ |
| **99.9999% (Six Nines)** | $31.5\text{ seconds}$ | $2.63\text{ seconds}$ | $0.086\text{ seconds}$ |

---

## 5. Network Bandwidth & Hardware Sizing Rules

- **Bits vs. Bytes:** $1\text{ Byte} = 8\text{ bits}$. Network cards are rated in **Gbps (bits)**; storage in **GB (bytes)**.
- **1 Gbps Network Card Throughput:**
  $$\frac{1,000\text{ Mbps}}{8} = \mathbf{125\text{ MB/second}}$$
- **10 Gbps Enterprise NIC:**
  $$\frac{10,000\text{ Mbps}}{8} = \mathbf{1.25\text{ GB/second}}$$
- **80/20 Pareto Rule for Caching:**
  Caching $20\%$ of hot daily read requests serves $80\%$ of total read traffic.
  $$\text{Cache RAM} = \text{Daily Read Requests} \times 0.20 \times \text{Average Object Size}$$

---

<div align="center">

| [← Back to DSA Formulas](./01-dsa-formulas-and-invariants-cheatsheet.md) | [Track Hub: Cheatsheets](./README.md) | [Next: GoF Patterns & Concurrency →](./03-gof-patterns-and-concurrency-cheatsheet.md) |
| :--- | :---: | ---: |

</div>
