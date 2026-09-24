#!/usr/bin/env python3
"""
Interactive Study Plan Progress Tracker
Tracks user completion across 30-Day, 60-Day, and 90-Day study roadmaps.
Saves progress locally to .progress.json (ignored by git).
"""

import sys
import os
import json
import re
import argparse

# ANSI Terminal Colors
CYAN = "\033[96m"
GREEN = "\033[92m"
YELLOW = "\033[93m"
RED = "\033[91m"
BOLD = "\033[1m"
DIM = "\033[2m"
RESET = "\033[0m"

REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
PROGRESS_FILE = os.path.join(REPO_ROOT, ".progress.json")

PLANS = {
    "1": ("30-Day FAANG Sprint", os.path.join(REPO_ROOT, "study-plans", "01-the-30-day-faang-sprint-roadmap.md")),
    "2": ("60-Day SDE-2 Track", os.path.join(REPO_ROOT, "study-plans", "02-the-60-day-comprehensive-sde2-backend-roadmap.md")),
    "3": ("90-Day Senior Architect", os.path.join(REPO_ROOT, "study-plans", "03-the-90-day-senior-and-staff-architect-blueprint.md"))
}

def load_progress():
    if os.path.exists(PROGRESS_FILE):
        try:
            with open(PROGRESS_FILE, 'r', encoding='utf-8') as f:
                return json.load(f)
        except Exception:
            return {}
    return {}

def save_progress(data):
    with open(PROGRESS_FILE, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2)

def parse_milestones(filepath):
    if not os.path.exists(filepath):
        return []
    with open(filepath, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    milestones = []
    for line in lines:
        m = re.match(r'^###\s+((?:Day[s]?|Week[s]?)\s+[\d\s–—&]+:\s*.*)', line)
        if m:
            milestones.append(m.group(1).strip())
    return milestones

def render_progress_bar(completed, total, width=30):
    percent = (completed / total * 100) if total > 0 else 0
    filled = int(width * completed // total) if total > 0 else 0
    bar = "█" * filled + "░" * (width - filled)
    return f"[{bar}] {percent:.1f}% ({completed}/{total} Days)"

def show_status(plan_id="1"):
    plan_name, filepath = PLANS.get(plan_id, PLANS["1"])
    milestones = parse_milestones(filepath)
    if not milestones:
        print(f"{RED}Error: Could not parse milestones for {plan_name}{RESET}")
        return

    progress = load_progress().get(plan_id, [])
    completed_set = set(progress)

    print(f"\n{BOLD}{CYAN}=== 📅 STUDY PLAN TRACKER: {plan_name} ==={RESET}\n")
    print(render_progress_bar(len(completed_set), len(milestones)))
    print(f"\n{BOLD}Milestones Checklist:{RESET}\n")

    for idx, m in enumerate(milestones, 1):
        status = f"{GREEN}✅ [DONE]{RESET}" if idx in completed_set else f"{DIM}⬜ [TODO]{RESET}"
        print(f" {status} {BOLD}#{idx:02d}{RESET}: {m}")
    print()

def toggle_day(plan_id, day_idx):
    progress = load_progress()
    plan_progress = set(progress.get(plan_id, []))
    if day_idx in plan_progress:
        plan_progress.remove(day_idx)
        print(f"{YELLOW}Unmarked Day #{day_idx} as completed.{RESET}")
    else:
        plan_progress.add(day_idx)
        print(f"{GREEN}Marked Day #{day_idx} as completed!{RESET}")
    progress[plan_id] = sorted(list(plan_progress))
    save_progress(progress)

def main():
    parser = argparse.ArgumentParser(description="Study Plan Progress Tracker")
    parser.add_argument("--plan", "-p", choices=["1", "2", "3"], default="1", help="Plan selection: 1 (30-Day), 2 (60-Day), 3 (90-Day)")
    parser.add_argument("--status", "-s", action="store_true", help="Display current status and progress bar")
    parser.add_argument("--toggle", "-t", type=int, help="Toggle milestone day number as completed/todo")
    parser.add_argument("--test", action="store_true", help="Quick self-test mode")
    args = parser.parse_args()

    if args.test:
        for pid, (pname, pfile) in PLANS.items():
            ms = parse_milestones(pfile)
            assert len(ms) > 0, f"No milestones found for {pname}"
        print(f"Study Plan Tracker test passed: Successfully parsed all 3 roadmaps.")
        sys.exit(0)

    if args.toggle:
        toggle_day(args.plan, args.toggle)
        show_status(args.plan)
    else:
        show_status(args.plan)

if __name__ == "__main__":
    main()
