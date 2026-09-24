# 02. Amazon Leadership Principles: Engineering Mastery

[← Back to The STAR Method](./01-the-star-method-and-high-impact-storytelling.md) | [Track Hub](./README.md) | [Next: Conflict Resolution & Disagreements →](./03-conflict-resolution-and-technical-disagreements.md)

---

## 🏛️ 1. Theoretical Foundations: The LP Evaluation Engine

Amazon's hiring bar evaluates every candidate against **16 Leadership Principles (LPs)**. In engineering loops, technical questions are explicitly paired with specific LPs. Interviewers are designated to evaluate 2 to 3 specific LPs and assign positive or negative marks.

```
                      ╭────────────────────────────────────────╮
                      │      THE 16 LEADERSHIP PRINCIPLES      │
                      ╰───────────────────┬────────────────────╯
                                          │
                         Are decisions technical or cultural?
                                          │
                         ┌────────────────┴────────────────┐
                         ▼                                 ▼
                 THE 6 TECHNICAL LPs               CULTURAL EXECUTION
                 1. Customer Obsession             7. Invent & Simplify
                 2. Ownership                      8. Are Right, A Lot
                 3. Bias for Action                9. Learn & Be Curious
                 4. Dive Deep                     10. Insist on Highest Standards
                 5. Have Backbone; Disagree/Commit11. Think Big
                 6. Deliver Results               12. Frugality / Earn Trust
```

---

## ⚡ 2. Deep Dive: The 6 Core Technical LPs

### 2.1 Customer Obsession
> *"Leaders start with the customer and work backwards. They work vigorously to earn and keep customer trust."*

- **The Software Engineering Mindset**: Engineers often fall in love with complex architectures (e.g. migrating everything to a distributed event-driven graph database) that add zero value to the customer while increasing latency and failure modes.
- **Positive Signals**: Prioritizing end-user latency, eliminating checkout failure bugs, writing automated resilience tests that safeguard user data, rejecting vanity tech stacks that delay customer features.
- **Classic Prompt**: *"Tell me about a time you pushed back on a product feature or technical decision to protect the customer experience."*

### 2.2 Ownership
> *"Leaders are owners. They think long term and don’t sacrifice long-term value for short-term results. They never say 'that’s not my job'."*

- **The Software Engineering Mindset**: Taking responsibility for unowned legacy services, proactively fixing flaky tests, improving documentation, investigating intermittent production errors that other teams ignored.
- **Positive Signals**: Acting beyond immediate team boundaries, investing in automated CI/CD and monitoring, mentoring junior engineers, paying down technical debt.
- **Classic Prompt**: *"Tell me about a time you took on a problem that was outside your direct team's scope because it was critical to the company."*

### 2.3 Bias for Action (One-Way vs. Two-Way Doors)
> *"Speed matters in business. Many decisions and actions are reversible and do not need extensive study. We value calculated risk taking."*

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                           ONE-WAY VS. TWO-WAY DOOR ARCHITECTURE                           │
├────────────────────┬───────────────────────────────┬──────────────────────────────────────┤
│ Decision Type      │ Characteristics               │ Strategic Action                     │
├────────────────────┼───────────────────────────────┼──────────────────────────────────────┤
│ Type 1 (One-Way)   │ Irreversible, high-cost exit  │ Deep analysis, formal RFC, consensus │
│                    │ (e.g., Core DB selection)     │ proof-of-concept benchmarks          │
├────────────────────┼───────────────────────────────┼──────────────────────────────────────┤
│ Type 2 (Two-Way)   │ Reversible, low-cost exit     │ Bias for action! Decide quickly,     │
│                    │ (e.g., Caching key structure, │ deploy feature flag, test in canary  │
│                    │  API payload field naming)    │                                      │
╰────────────────────┴───────────────────────────────┴──────────────────────────────────────╯
```
- **Classic Prompt**: *"Tell me about a time you had to make a critical architectural decision with incomplete information."*

### 2.4 Dive Deep
> *"Leaders operate at all levels, stay connected to the details, audit frequently, and are skeptical when metrics and anecdote differ."*

- **The Software Engineering Mindset**: Senior engineers must not merely stay at 30,000-foot architecture diagrams. When production breaks, can you inspect JVM bytecode, analyze TCP packet dumps with Wireshark, analyze GC pauses in Prometheus, or profile CPU cache misses?
- **Positive Signals**: Root-cause analysis via the 5 Whys, digging into open-source library source code to find race conditions, validating metrics against telemetry logs.
- **Classic Prompt**: *"Tell me about the most technically complex bug you diagnosed in your career."*

### 2.5 Have Backbone; Disagree and Commit
> *"Leaders are obligated to respectfully challenge decisions when they disagree, even when doing so is uncomfortable or exhausting. Once a decision is determined, they commit wholly."*

- **The Software Engineering Mindset**:
  1. **Phase 1 (Disagree)**: If an architecture proposal is flawed (e.g., storing relational financial transactions in an eventually consistent NoSQL store with no rollback support), you must push back using data, benchmarks, and failure simulations.
  2. **Phase 2 (Commit)**: Once leadership makes the final decision, you do **not** sabotage or say "I told you so." You execute with 100% effort and build defensive safeguards around the chosen path.
- **Classic Prompt**: *"Tell me about a time you strongly disagreed with a Principal Engineer or Product Manager on technical direction."*

### 2.6 Deliver Results
> *"Leaders focus on the key inputs for their business and deliver them with the right quality and in a timely fashion. Despite setbacks, they rise to the occasion and never settle."*

- **The Software Engineering Mindset**: Shipping high-leverage software on time despite sudden blocker dependencies, organizational re-orgs, or scope shifts.
- **Positive Signals**: Ruthless prioritization, de-scoping non-critical features, building modular bridges across cross-team bottlenecks.
- **Classic Prompt**: *"Tell me about a project that was falling behind schedule and how you brought it to successful delivery."*

---

## 🗺️ 3. Master LP Question-to-Story Matrix

```
╭───────────────────────────────────────────────────────────────────────────────────────────╮
│                             AMAZON LP MAPPING MATRIX FOR ENGINEERS                        │
├───────────────────────────────┬───────────────────────────────────────────────────────────┤
│ Leadership Principle          │ Primary Engineering Interview Question                    │
├───────────────────────────────┼───────────────────────────────────────────────────────────┤
│ Customer Obsession            │ "Tell me about a customer bug that changed your roadmap." │
│ Ownership                     │ "Describe a time you saw a problem and fixed it unprompted."│
│ Invent & Simplify             │ "Tell me about a time you simplified a complex subsystem."│
│ Are Right, A Lot              │ "Describe a time your technical judgment was proven right."│
│ Learn & Be Curious            │ "How do you stay abreast of new technologies like AI/gRPC?"│
│ Insist on Highest Standards   │ "Tell me about a time code quality wasn't up to your bar."│
│ Think Big                     │ "Describe an architectural proposal that scaled 10x."     │
│ Bias for Action               │ "When did you take a calculated risk without full data?"  │
│ Frugality                     │ "How did you cut cloud infrastructure or database costs?" │
│ Earn Trust                    │ "Tell me about a time you made a mistake and owned it."   │
│ Dive Deep                     │ "Walk me through the deepest debugging trace of your career."│
│ Have Backbone                 │ "When did you challenge leadership on an engineering flaw?"│
│ Deliver Results               │ "Tell me about shipping a project under tight constraints."│
╰───────────────────────────────┴───────────────────────────────────────────────────────────╯
```

---

<div align="center">

| [← Back to The STAR Method](./01-the-star-method-and-high-impact-storytelling.md) | [Track Hub: Behavioral & Leadership](./README.md) | [Next: Conflict Resolution & Disagreements →](./03-conflict-resolution-and-technical-disagreements.md) |
| :--- | :---: | ---: |

</div>
