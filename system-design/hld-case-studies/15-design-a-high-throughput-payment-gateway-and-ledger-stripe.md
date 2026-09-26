# 15. Design a High-Throughput Payment Gateway & Double-Entry Ledger (Stripe / PayPal)

[← Back to Distributed Vector Database](./14-design-a-distributed-vector-database-pinecone-milvus.md) | [Track Hub](./README.md) | [Next: Real-Time Ad Click Aggregator →](./16-design-a-real-time-ad-click-event-aggregator-google-meta.md)

---

## 🏛️ 1. Requirements & System Scope

A modern payment gateway and financial ledger orchestrates credit card, bank, and digital wallet transactions between consumers, merchants, and Payment Service Providers (PSPs / Card Networks like Visa, Mastercard, and banks). It must guarantee strictly zero money loss, exactly-once processing, multi-region high availability, and mathematically auditable double-entry accounting.

```mermaid
graph LR
    User["Consumer Checkout"] --> Gateway["Payment Gateway"]
    Gateway --> Idemp["Idempotency Filter"]
    Idemp --> Orchestrator["Payment Orchestrator"]
    Orchestrator --> PSP["External PSP (Visa / Stripe / Adyen)"]
    PSP --> Ledger["Double-Entry Ledger"]
    Ledger --> Recon["End-of-Day Reconciliation"]
```

### 1.1 Functional Requirements
1. **Payment Authorization & Capture:** Support two-phase payments: (1) `Authorize` (reserve funds on card) and (2) `Capture` (transfer settled funds).
2. **Idempotent API Execution:** Guarantee that network retries or double-clicked buttons never result in duplicate charges.
3. **Card Tokenization (PCI-DSS Compliance):** Sensitive Primary Account Numbers (PAN) and CVVs are tokenized in an isolated vault; application services only process opaque tokens.
4. **Immutable Double-Entry Ledger:** Every financial movement is recorded as balanced debit/credit journal entries where $\sum \text{Debits} = \sum \text{Credits}$.
5. **Automated Reconciliation:** Ingest end-of-day bank settlement reports (e.g., BAI2, MT940, ISO 20022 CAMT.053) and reconcile internal ledger transactions against external bank truth.

### 1.2 Non-Functional Requirements
- **Financial Durability & Zero Data Loss:** Recovery Point Objective (RPO) = 0. Transactions must never be dropped or silently corrupted.
- **Strict Exactly-Once Semantics:** Network failures must never produce duplicate transfers.
- **Ultra-High Availability:** 99.999% uptime ($< 5.26\text{ minutes}$ downtime/year).
- **Auditability & Traceability:** Immutable audit trail with cryptographic hashing for financial compliance (SOC 1/2, PCI-DSS Level 1).

---

## 🔢 2. Capacity & Scale Estimations

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                              BACK-OF-THE-ENVELOPE SCALE MATH                              │
├───────────────────────────────┬───────────────────────────────┬───────────────────────────┤
│ Metric                        │ Raw Value                     │ Engineering Shorthand     │
├───────────────────────────────┼───────────────────────────────┼───────────────────────────┤
│ Daily Payment Volume          │ 100 Million transactions/day  │ 100M Txns/day             │
│ Average Processing Throughput │ 100M / 86,400 sec             │ ~1,200 TPS                │
│ Peak Traffic (Black Friday 8x)│ 1,200 × 8                     │ ~10,000 TPS peak          │
│ Ledger Journal Entry Size     │ ~1 KB per balanced entry      │ 1 KB                      │
│ Daily Ledger Storage Growth   │ 100M × 1 KB                   │ 100 GB / day              │
│ Annual Ledger Storage         │ 100 GB × 365                  │ ~36.5 TB / year (Replicas)│
│ API Latency SLA (p99)         │ Sub-1.5s total (PSP bounded)  │ < 1.5s                    │
╰───────────────────────────────┴───────────────────────────────┴───────────────────────────╯
```

---

## 🏗️ 3. High-Level Architecture

```mermaid
flowchart TD
    subgraph "Clients"
        Browser["Mobile / Web Checkout SDK"] --> LB["Global Edge Load Balancer"]
    end

    LB --> APIGateway["Payment API Gateway"]

    subgraph "Security & Vault Zone (PCI-DSS Scope)"
        APIGateway --> Vault["Card Tokenization Vault"]
        Vault <--> HSM["Hardware Security Module (HSM)"]
    end

    subgraph "Core Payment Processing Tier"
        APIGateway --> Idemp["Idempotency Filter (Redis + Postgres)"]
        Idemp --> Orch["Payment Orchestrator"]
        Orch --> Routing["Smart PSP Routing Service"]
        Routing --> PSP["Card Network / External PSPs"]
    end

    subgraph "Financial Ledger Tier"
        Orch --> Outbox[("Transactional Outbox")]
        Outbox --> CDC["Debezium / Kafka CDC"]
        CDC --> LedgerService["Ledger Service"]
        LedgerService --> LedgerDB[("Double-Entry Ledger DB (PostgreSQL / CockroachDB)")]
    end

    subgraph "Reconciliation Tier"
        BankFiles["Bank Settlement Files (MT940/BAI2)"] --> S3["Secure Storage (S3 / SFTP)"]
        S3 --> ReconEngine["Reconciliation Engine"]
        LedgerDB --> ReconEngine
        ReconEngine --> DiscrepancyQ["Discrepancy / Exception Queue"]
    end
```

---

## 🔑 4. Deep-Dive: Exactly-Once Processing via Idempotency Keys

Every mutating payment request includes an `Idempotency-Key: <UUID>` header generated on the client.

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Gateway as API Gateway
    participant Cache as Redis (Lock & Cache)
    participant DB as Postgres Idempotency Table
    participant PSP as Card Acquirer / PSP

    Client->>Gateway: POST /v1/charges (Key: "abc-123", Amount: $100)
    Gateway->>Cache: SET charge:abc-123 IN_FLIGHT NX EX 120
    alt Key Already Acquired / Finished
        Cache-->>Gateway: Key exists (Return cached response or reject)
        Gateway-->>Client: 200 OK (Cached charge object)
    else First Attempt
        Cache-->>Gateway: OK (Lock acquired)
        Gateway->>DB: INSERT INTO idempotency_records (key, status='STARTED')
        Gateway->>PSP: Authorize & Capture $100
        PSP-->>Gateway: Charge Success (PSP_Txn_ID: "ch_987")
        Gateway->>DB: UPDATE idempotency_records SET status='COMPLETED', response_body=...
        Gateway->>Cache: SET charge:abc-123 COMPLETED (TTL: 24h)
        Gateway-->>Client: 200 OK (Charge Object)
    end
```

---

## ⚖️ 5. Deep-Dive: Double-Entry Bookkeeping Principles

In financial accounting, money cannot be created or destroyed—it only moves between accounts. Every transaction consists of balanced **Debits** and **Credits**:

$$\sum \text{Debits} - \sum \text{Credits} = 0$$

### 5.1 Account Categories & Sign Conventions
- **Assets (Bank Accounts, Cash):** Debits increase balance, Credits decrease balance.
- **Liabilities (Merchant Payables, User Balances):** Credits increase balance, Debits decrease balance.
- **Equity / Revenue (Platform Fees):** Credits increase balance, Debits decrease balance.

### 5.2 Real-World E-Commerce Example
Customer purchases an item for **$100.00**. The platform charges a **3% processing fee ($3.00)**, and the merchant receives **$97.00**:

```mermaid
graph TD
    subgraph "Journal Entry: Txn_88291"
        D1["Debit: Acquirer Clearing Account ($100.00)<br/>(Asset: Acquirer owes us $100)"]
        C1["Credit: Merchant Payable ($97.00)<br/>(Liability: We owe merchant $97)"]
        C2["Credit: Platform Revenue ($3.00)<br/>(Revenue: Our fee earned)"]
    end

    D1 --- C1
    D1 --- C2
```

```sql
-- Immutable Ledger Schema
CREATE TABLE ledger_entries (
    entry_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id VARCHAR(64) NOT NULL,
    account_id VARCHAR(64) NOT NULL,
    direction VARCHAR(6) NOT NULL CHECK (direction IN ('DEBIT', 'CREDIT')),
    amount_cents BIGINT NOT NULL CHECK (amount_cents > 0),
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Zero-Sum Validation Constraint (checked atomically per transaction)
-- SUM(amount_cents WHERE direction = 'DEBIT') == SUM(amount_cents WHERE direction = 'CREDIT')
```

> [!IMPORTANT]
> **Immutability Invariant:** Ledger rows are **NEVER updated or deleted**. If a mistake or refund occurs, a new compensating journal entry is appended with inverse debits and credits.

---

## 🔄 6. Automated Reconciliation Engine

At $T = \text{Midnight}$, banks generate clearing files (BAI2 / MT940 / CAMT.053) detailing every settled transaction:

```mermaid
flowchart LR
    A["Internal Ledger Transactions"] --> C{"3-Way Matching Engine"}
    B["Bank Clearing File (BAI2)"] --> C
    D["PSP Webhooks (Async)"] --> C

    C -->|"Matched: Amount and Time agree"| Matched["Settled / Reconciled ✅"]
    C -->|"Discrepancy: Missing in Bank"| Alert1["Investigate Unsettled Charge ⚠️"]
    C -->|"Discrepancy: Amount Mismatch"| Alert2["Fee Variance Alert ⚠️"]
```

---

## 🛡️ 7. Edge Cases & Resilience Mechanisms

| Failure Mode / Edge Case | System Impact | Architectural Mitigation |
| :--- | :--- | :--- |
| **PSP Network Timeout** | Gateway sent request, but connection timed out before receiving approval | **Ambiguous State Resolution:** Do NOT retry blindly. Poll the PSP's Inquiry API using the internal transaction reference to determine actual transaction outcome before failing or capturing. |
| **Concurrent Double-Clicks** | User submits checkout twice simultaneously | **Redis Atomic NX Lock:** First thread acquires lock; second thread receives `409 Conflict` or blocks on condition variable awaiting the first thread's result. |
| **Partial Reversals / Split Refunds** | Customer returns 1 item of a 3-item order | Each partial refund creates a distinct compensating ledger journal entry referencing the original parent transaction with its own balance validation. |
| **Multi-Region Ledger Split-Brain** | Network partition between US-East and US-West | **Deterministic Region Partitioning:** Shard payment accounts strictly by Merchant ID with multi-region CockroachDB consensus (Raft). Cross-region accounts route to the owning home region. |

---

<div align="center">

| [← Back to Distributed Vector Database](./14-design-a-distributed-vector-database-pinecone-milvus.md) | [Track Hub: HLD](./README.md) | [Next: Real-Time Ad Click Aggregator →](./16-design-a-real-time-ad-click-event-aggregator-google-meta.md) |
| :--- | :---: | ---: |

</div>
