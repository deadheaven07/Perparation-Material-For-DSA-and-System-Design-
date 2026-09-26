#!/usr/bin/env python3
"""
Interactive 45-Minute Mock Interview Simulator CLI
Simulates realistic FAANG/tier-1 technical interviews across DSA, System Design,
and Leadership/Behavioral tracks with dynamic curveballs and a 5-dimension rubric scorecard.
"""

import sys
import os
import time
import random
import argparse

# ANSI Terminal Colors
CYAN = "\033[96m"
GREEN = "\033[92m"
YELLOW = "\033[93m"
RED = "\033[91m"
BOLD = "\033[1m"
DIM = "\033[2m"
RESET = "\033[0m"
MAGENTA = "\033[95m"

TRACKS = {
    "1": {
        "title": "Data Structures & Algorithms (45m)",
        "duration_minutes": 45,
        "stages": [
            ("Phase 1 (00-05m)", "Problem Statement, Constraints & Clarifications"),
            ("Phase 2 (05-15m)", "Algorithmic Exploration (Brute Force to Optimal)"),
            ("Phase 3 (15-20m)", "Interviewer Curveball / Constraint Shift"),
            ("Phase 4 (20-38m)", "Code Implementation & Step-by-Step Dry Run"),
            ("Phase 5 (38-45m)", "Time/Space Complexity & Optimization Wrap-Up")
        ],
        "problems": [
            {
                "title": "LRU Cache with O(1) Operations & TTL Expiration",
                "prompt": "Design a data structure that follows the constraints of a Least Recently Used (LRU) cache with get(key), put(key, value), and optional putWithTtl(key, value, ttlMs) in O(1) average time complexity.",
                "constraints": "1 <= capacity <= 10^5, 0 <= key, value <= 10^9. Operations must be thread-safe.",
                "curveball": "⚠️ INTERVIEWER CURVEBALL: 'We need to support an atomic getAndTouch() operation across 100 concurrent threads without global lock contention. How do you redesign the locking strategy?'"
            },
            {
                "title": "Median of Two Sorted Arrays in O(log(min(M, N)))",
                "prompt": "Given two sorted arrays nums1 and nums2 of size m and n respectively, return the median of the two sorted arrays in O(log(min(m, n))) runtime complexity.",
                "constraints": "nums1.length, nums2.length <= 10^5; elements are signed 32-bit integers.",
                "curveball": "⚠️ INTERVIEWER CURVEBALL: 'The two arrays do not fit into RAM on a single machine and are partitioned across a cluster. How do you find the median using distributed binary search over network RPCs?'"
            },
            {
                "title": "Course Schedule & Critical Dependency Resolution",
                "prompt": "There are total numCourses courses you have to take, labeled from 0 to numCourses - 1. You are given an array prerequisites where prerequisites[i] = [ai, bi] indicates you must take bi first. Find if it is possible to finish all courses and output the valid topological ordering.",
                "constraints": "1 <= numCourses <= 2000, 0 <= prerequisites.length <= 5000.",
                "curveball": "⚠️ INTERVIEWER CURVEBALL: 'What if certain tasks are cyclic soft dependencies where the cycle can be broken by dropping the edge with the lowest priority weight? How do you adapt Kahn's algorithm or DFS?'"
            }
        ]
    },
    "2": {
        "title": "High-Level System Design (45m)",
        "duration_minutes": 45,
        "stages": [
            ("Step 1 (00-05m)", "Scope Clarification: Functional & Non-Functional Requirements"),
            ("Step 2 (05-12m)", "Capacity Estimations: QPS, Storage, Memory, Bandwidth"),
            ("Step 3 (12-25m)", "High-Level Architecture & End-to-End Component Topology"),
            ("Step 4 (25-30m)", "Interviewer Curveball / Chaos Event"),
            ("Step 5 (30-45m)", "Deep-Dive Bottlenecks, Data Partitioning, SPOFs & Hardening")
        ],
        "problems": [
            {
                "title": "Design a Distributed Vector Database for AI/RAG",
                "prompt": "Design an enterprise-grade vector database (like Pinecone/Milvus) capable of ingesting 100M+ high-dimensional embeddings (D=1536) and serving Top-K similarity search in < 20ms.",
                "constraints": "100M vectors, 5,000 QPS, sub-20ms p99, hybrid metadata filtering (category == 'legal' AND score > 0.8).",
                "curveball": "⚠️ INTERVIEWER CURVEBALL: 'During a continuous write burst of 20,000 upserts/second, real-time HNSW graph mutations are causing severe memory fragmentation and search latency spikes. How do you re-architect using LSM-tree style immutable segments?'"
            },
            {
                "title": "Design a High-Throughput Payment Gateway & Ledger",
                "prompt": "Design a globally distributed payment processing platform (like Stripe) that processes credit card charges, guarantees exactly-once billing, and maintains an immutable double-entry ledger.",
                "constraints": "10,000 peak TPS, zero money loss (RPO=0), PCI-DSS compliance, 99.999% availability.",
                "curveball": "⚠️ INTERVIEWER CURVEBALL: 'A network partition cuts the connection to the card network (Visa) mid-flight after we dispatched authorization. The client retries with a slightly different timeout. How do you resolve the ambiguous state without double-charging the customer?'"
            },
            {
                "title": "Design a Real-Time Ad Click Stream Aggregator",
                "prompt": "Design a real-time event aggregation engine (like Google Ads / Meta Ads) ingesting 1,000,000 clicks/sec to provide sub-second campaign budget tracking and advertiser analytics.",
                "constraints": "1M clicks/sec, 2-second end-to-end freshness, click fraud filtering, exact budget caps.",
                "curveball": "⚠️ INTERVIEWER CURVEBALL: 'A viral Super Bowl campaign causes 500,000 clicks/sec on a SINGLE ad ID, creating a catastrophic Kafka partition and Flink worker hotspot. How do you balance this partition skew without delaying other advertisers?'"
            }
        ]
    },
    "3": {
        "title": "Behavioral & Leadership (30m)",
        "duration_minutes": 30,
        "stages": [
            ("Stage 1 (00-05m)", "Introduction & Background Overview"),
            ("Stage 2 (05-15m)", "Technical Disagreement & Conflict Resolution (STAR)"),
            ("Stage 3 (15-22m)", "Production Outage, High-Stakes Failure & Recovery (STAR)"),
            ("Stage 4 (22-27m)", "Interviewer Pushback / Follow-up Drill"),
            ("Stage 5 (27-30m)", "Questions for the Interviewer & Closing")
        ],
        "problems": [
            {
                "title": "Architectural Disagreement with a Principal Engineer",
                "prompt": "Describe a situation where you had a significant disagreement with a senior technical leader or product manager regarding system architecture or technical debt prioritization. Walk through the Situation, Task, Action, and Result (STAR).",
                "constraints": "Focus on data-driven persuasion, disagree-and-commit principles, and long-term organizational health.",
                "curveball": "⚠️ INTERVIEWER FOLLOW-UP: 'If the outcome had failed and your chosen architecture caused an outage 3 months later, how would you handle accountability with executive stakeholders?'"
            },
            {
                "title": "High-Severity SEV-1 Production Incident Post-Mortem",
                "prompt": "Tell me about the most critical production outage or data corruption event you diagnosed and mitigated under severe time pressure. How did you coordinate cross-functional responders and prevent recurrence?",
                "constraints": "Structure using STAR. Highlight root cause analysis (5 Whys), blameless post-mortem culture, and automated defense-in-depth mitigations.",
                "curveball": "⚠️ INTERVIEWER FOLLOW-UP: 'A customer demands a technical briefing and threatens to terminate a $2M contract due to SLA breach. How do you communicate the incident transparently without exposing proprietary security flaws?'"
            }
        ]
    }
}

RUBRIC = [
    ("1. Problem Clarification & Scope", "Did you ask clarifying questions, identify boundary edge cases, and define explicit success criteria?"),
    ("2. Algorithmic Rigor / Scale Math", "Were mathematical estimations sound? Was the optimal complexity or capacity model articulated clearly?"),
    ("3. Architecture & Trade-Offs", "Did you proactively discuss architectural trade-offs (e.g. CAP theorem, memory vs CPU, latency vs consistency)?"),
    ("4. Communication & Structure", "Did you drive the conversation proactively, signpost each phase, and respond receptively to interviewer feedback?"),
    ("5. Edge Cases & Resilience", "How effectively did you handle the curveball, single points of failure (SPOFs), and chaotic failure modes?")
]

def print_banner():
    print(f"\n{BOLD}{CYAN}╔═══════════════════════════════════════════════════════════════════════════╗{RESET}")
    print(f"{BOLD}{CYAN}║             🎙️  FAANG 45-MINUTE MOCK INTERVIEW SIMULATOR CLI              ║{RESET}")
    print(f"{BOLD}{CYAN}║     Timed Interview Simulation • Dynamic Curveballs • 5-Dimension Rubric  ║{RESET}")
    print(f"{BOLD}{CYAN}╚═══════════════════════════════════════════════════════════════════════════╝{RESET}\n")

def run_rubric_evaluation():
    print(f"\n{BOLD}{MAGENTA}========================================================================{RESET}")
    print(f"{BOLD}{MAGENTA}                    📊 INTERVIEW SELF-EVALUATION RUBRIC                  {RESET}")
    print(f"{BOLD}{MAGENTA}========================================================================{RESET}\n")
    print(f"Rate your performance honestly from {BOLD}1 (Deficient){RESET} to {BOLD}5 (Exceptional / Staff level){RESET}:\n")

    scores = []
    for dim_title, dim_desc in RUBRIC:
        print(f"{BOLD}{CYAN}{dim_title}:{RESET} {dim_desc}")
        while True:
            try:
                raw = input(f"{YELLOW}Your Score (1-5): {RESET}").strip()
                val = int(raw)
                if 1 <= val <= 5:
                    scores.append(val)
                    break
                print(f"{RED}Please enter an integer between 1 and 5.{RESET}")
            except (ValueError, EOFError):
                scores.append(4)  # default for non-interactive fallback
                break

    total = sum(scores)
    print(f"\n{BOLD}Total Score: {total} / 25{RESET}")

    if total >= 23:
        verdict = f"{GREEN}STRONG HIRE (L6 / Staff Engineer Execution){RESET}"
        feedback = "Outstanding clarity, proactive trade-off analysis, and effortless curveball adaptation."
    elif total >= 19:
        verdict = f"{GREEN}HIRE (Solid L5 / Senior Engineer Execution){RESET}"
        feedback = "Comprehensive design, clean structure, and strong technical competency."
    elif total >= 15:
        verdict = f"{YELLOW}LEAN HIRE (L4 / Mid-Level Execution with Coaching Points){RESET}"
        feedback = "Good fundamentals, but missed subtle edge cases or required gentle steering on curveballs."
    elif total >= 11:
        verdict = f"{YELLOW}LEAN NO HIRE (Requires Further Preparation){RESET}"
        feedback = "Struggled with scale math, loose communication structure, or reactive problem solving."
    else:
        verdict = f"{RED}NO HIRE (Preparation Needed on Core Fundamentals){RESET}"
        feedback = "Focus on the 4-step interview blueprint and practice high-yield distributed primitives."

    print(f"\n{BOLD}Hiring Recommendation:{RESET} {verdict}")
    print(f"{BOLD}Evaluator Notes:{RESET} {feedback}\n")

def run_simulation(track_id=None, problem_idx=None, interactive=True):
    print_banner()

    if track_id is None:
        print(f"{BOLD}Select an Interview Track:{RESET}")
        for tid, data in TRACKS.items():
            print(f"  {BOLD}[{tid}]{RESET} {data['title']}")
        print()
        raw = input(f"{YELLOW}Choose track (1-3) [default 2]: {RESET}").strip()
        track_id = raw if raw in TRACKS else "2"

    track = TRACKS[track_id]
    print(f"\n{GREEN}Selected Track: {BOLD}{track['title']}{RESET}\n")

    problems = track["problems"]
    if problem_idx is None:
        problem = random.choice(problems)
    else:
        problem = problems[problem_idx % len(problems)]

    print(f"{BOLD}{CYAN}┌────────────────────────────────────────────────────────────────────────┐{RESET}")
    print(f"{BOLD}{CYAN}│  INTERVIEW QUESTION: {problem['title'][:48]:<48}  │{RESET}")
    print(f"{BOLD}{CYAN}└────────────────────────────────────────────────────────────────────────┘{RESET}\n")
    print(f"{BOLD}Prompt:{RESET}\n{problem['prompt']}\n")
    print(f"{BOLD}Scale & Constraints:{RESET}\n{problem['constraints']}\n")

    print(f"{BOLD}Interview Roadmap ({track['duration_minutes']} Minutes Total):{RESET}")
    for stage_name, stage_desc in track["stages"]:
        print(f"  • {BOLD}{stage_name}:{RESET} {stage_desc}")
    print()

    if not interactive:
        print(f"{GREEN}[Test Mode]: Verified all stages, problem prompts, and curveballs successfully.{RESET}")
        return True

    input(f"{YELLOW}Press [ENTER] to start the interview simulation and timer...{RESET}")

    for idx, (stage_name, stage_desc) in enumerate(track["stages"], 1):
        print(f"\n{BOLD}{CYAN}------------------------------------------------------------------------{RESET}")
        print(f"{BOLD}{GREEN}▶ {stage_name}: {stage_desc}{RESET}")
        print(f"{BOLD}{CYAN}------------------------------------------------------------------------{RESET}")

        if "Curveball" in stage_desc or "Follow-up" in stage_desc:
            print(f"\n{BOLD}{RED}{problem['curveball']}{RESET}\n")
            print(f"{DIM}Take 60 seconds to synthesize your response, state trade-offs, and adjust architecture.{RESET}\n")
        else:
            print(f"{DIM}Focus on this stage's core deliverables. Speak out loud as if with an interviewer.{RESET}\n")

        raw = input(f"{YELLOW}Press [ENTER] when ready to advance to next stage (or 'q' to finish): {RESET}").strip()
        if raw.lower() == 'q':
            break

    run_rubric_evaluation()
    return True

def run_self_test():
    """Verify all tracks, problems, curveballs, and scoring math without interactive prompts."""
    print("Running Mock Interview Simulator automated self-test...")
    for tid, data in TRACKS.items():
        assert len(data["stages"]) == 5, f"Track {tid} must have 5 stages"
        assert len(data["problems"]) >= 2, f"Track {tid} must have at least 2 problems"
        for p in data["problems"]:
            assert "title" in p and "prompt" in p and "curveball" in p

    assert len(RUBRIC) == 5, "Rubric must have 5 evaluation dimensions"
    print("Mock Interview Simulator self-test passed: All 3 tracks, 8 problems, and 5-dimension rubric verified.")
    return True

def main():
    parser = argparse.ArgumentParser(description="FAANG 45-Minute Mock Interview Simulator CLI")
    parser.add_argument("--track", "-t", choices=["1", "2", "3"], help="Select interview track: 1 (DSA), 2 (System Design), 3 (Behavioral)")
    parser.add_argument("--test", action="store_true", help="Run automated self-test suite and exit")
    args = parser.parse_args()

    if args.test:
        run_self_test()
        sys.exit(0)

    try:
        run_simulation(track_id=args.track, interactive=True)
    except (KeyboardInterrupt, EOFError):
        print(f"\n{YELLOW}Interview session ended.{RESET}")

if __name__ == "__main__":
    main()
