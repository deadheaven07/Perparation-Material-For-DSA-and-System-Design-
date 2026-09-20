# Contributing to Preparation Material for DSA & System Design

Thank you for your interest in contributing to this repository! Our mission is to maintain the most rigorous, high-density, production-grade technical interview preparation resource available.

To maintain our standard of quality, all contributions must adhere to the conventions below.

---

## 🏛️ Contribution Principles

1. **Zero Third-Party / Company Branding:**
   - Do NOT include proprietary company tags (e.g. FAANG, LeetCode company names) in titles, headers, or explanations. Keep problems grounded in theoretical and engineering excellence.
2. **Modern Java (Java 17/21):**
   - Write clean, modern Java using language features such as records, pattern matching switch expressions, `var`, and standard Collections (`ArrayDeque`, `ConcurrentHashMap`, `PriorityQueue`).
3. **100% Relative Links:**
   - All markdown links must use valid relative filepaths pointing to real files. No broken links are permitted.
4. **Mermaid Diagram Safety:**
   - All node labels containing parentheses `()`, bitwise operators `|`, or shifts `<<`, `>>` MUST be enclosed in double quotes (e.g. `Node["mask | (1 << v)"]`).

---

## 📋 The 7-Step DSA Problem Solving Framework

Every new algorithmic problem added must implement the 7-step structure:
1. **Problem Statement & Constraints** (with edge cases and bounds).
2. **Thought Process & Intuition** (from naive brute-force to optimal paradigm).
3. **Mathematical Invariants & Visual Blueprints** (Mermaid diagram visualizing pointers or state transitions).
4. **Architectural Implementation Blueprint** (ASCII data flow / state machine).
5. **Complete Production Java 17/21 Implementation** (clean, compile-ready, well-documented).
6. **Complexity Analysis & Execution Profiles** (formal Time & Space breakdown).
7. **Step-by-Step Dry-Run Table & Interviewer Stress Defenses** (trace table and defense against interviewer curveballs).

---

## 📐 The 4-Step System Design Interview Framework

Every new system design case study must follow the 4-step blueprint:
1. **Step 1: Requirements & Scope Clarification** (FR, NFR, Out of Scope).
2. **Step 2: Back-of-the-Envelope Capacity Estimations** (Traffic QPS, Storage, Memory, Bandwidth).
3. **Step 3: High-Level Architecture & End-to-End Workflows** (Mermaid diagrams, API contracts, DB schema).
4. **Step 4: Deep-Dive Bottlenecks & Failure Modes** (Partitioning, consensus, caching, disaster recovery).

---

## 🧪 Local Pre-Commit Verification

Before submitting a Pull Request, run the local audit scripts from the repository root:

```bash
# 1. Verify link integrity and balanced code fences
python3 scratch/audit_integrity.py

# 2. Compile and validate all Mermaid diagrams
node scratch/validate_mermaid.js
```

Both commands must exit with code `0` and report zero errors.
