#!/usr/bin/env python3
"""
Interactive Flashcard Quiz Runner with SuperMemo SM-2 Spaced Repetition Engine
Parses active recall flashcards from cheatsheets/04-200-high-yield-interview-flashcards.md
"""

import sys
import os
import re
import time
import json
import random
import argparse
from datetime import datetime, timedelta

# ANSI Terminal Colors
CYAN = "\033[96m"
GREEN = "\033[92m"
YELLOW = "\033[93m"
RED = "\033[91m"
BOLD = "\033[1m"
DIM = "\033[2m"
MAGENTA = "\033[95m"
RESET = "\033[0m"

REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
FLASHCARD_FILE = os.path.join(REPO_ROOT, "cheatsheets", "04-200-high-yield-interview-flashcards.md")
PROGRESS_FILE = os.path.join(REPO_ROOT, ".flashcards_progress.json")

def clean_html(text):
    """Remove HTML formatting tags and normalize whitespace."""
    text = re.sub(r'<br\s*/?>', '\n   ', text)
    text = re.sub(r'</?(?:b|code|p|summary|details|div|span)>', '', text)
    text = text.replace('&ge;', '≥').replace('&le;', '≤').replace('&times;', '×')
    text = text.replace('&gt;', '>').replace('&lt;', '<').replace('&amp;', '&')
    return text.strip()

def load_flashcards():
    if not os.path.exists(FLASHCARD_FILE):
        print(f"{RED}Error: Flashcard file not found at {FLASHCARD_FILE}{RESET}")
        sys.exit(1)

    with open(FLASHCARD_FILE, 'r', encoding='utf-8') as f:
        content = f.read()

    cards = []
    current_pillar = "General"

    lines = content.split('\n')
    i = 0
    while i < len(lines):
        line = lines[i]
        pillar_match = re.match(r'^##\s+.*?Pillar\s*\d+:\s*(.*)', line)
        if pillar_match:
            current_pillar = pillar_match.group(1).split('(')[0].strip()

        if '<details>' in line:
            block = []
            while i < len(lines) and '</details>' not in lines[i]:
                block.append(lines[i])
                i += 1
            if i < len(lines):
                block.append(lines[i])
            full_block = '\n'.join(block)

            summary_match = re.search(r'<summary><b>(\d+)\.\s*(.*?)</b></summary>', full_block, re.DOTALL)
            p_match = re.search(r'<p>(.*?)</p>', full_block, re.DOTALL)

            if summary_match and p_match:
                card_id = summary_match.group(1)
                q_text = clean_html(summary_match.group(2))
                a_text = clean_html(p_match.group(1))
                cards.append({
                    "id": card_id,
                    "pillar": current_pillar,
                    "question": q_text,
                    "answer": a_text
                })
        i += 1

    return cards

def load_progress():
    if os.path.exists(PROGRESS_FILE):
        try:
            with open(PROGRESS_FILE, 'r', encoding='utf-8') as f:
                return json.load(f)
        except Exception:
            return {}
    return {}

def save_progress(progress):
    try:
        with open(PROGRESS_FILE, 'w', encoding='utf-8') as f:
            json.dump(progress, f, indent=2)
    except Exception as e:
        print(f"{RED}Warning: Could not save progress: {e}{RESET}")

def sm2_update(card_state, quality):
    """
    SuperMemo SM-2 Spaced Repetition Algorithm
    quality: 0 (blackout) to 5 (perfect recall)
    """
    repetitions = card_state.get("repetitions", 0)
    ease_factor = card_state.get("ease_factor", 2.5)
    interval = card_state.get("interval", 1)

    # Calculate new ease factor
    # EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
    ease_factor = ease_factor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))
    ease_factor = max(1.3, ease_factor)

    if quality < 3:
        # Failure: reset repetition streak
        repetitions = 0
        interval = 1
    else:
        # Success
        if repetitions == 0:
            interval = 1
        elif repetitions == 1:
            interval = 6
        else:
            interval = int(round(interval * ease_factor))
        repetitions += 1

    now = time.time()
    next_review = now + (interval * 86400)

    card_state["repetitions"] = repetitions
    card_state["ease_factor"] = round(ease_factor, 2)
    card_state["interval"] = interval
    card_state["last_reviewed"] = datetime.now().isoformat()
    card_state["next_review"] = next_review
    return card_state

def display_stats(cards, progress):
    total = len(cards)
    reviewed = len(progress)
    now = time.time()
    due_today = 0
    learning = 0
    mature = 0
    ef_sum = 0.0

    for cid, state in progress.items():
        if state.get("next_review", 0) <= now:
            due_today += 1
        reps = state.get("repetitions", 0)
        if reps >= 3:
            mature += 1
        else:
            learning += 1
        ef_sum += state.get("ease_factor", 2.5)

    avg_ef = (ef_sum / reviewed) if reviewed > 0 else 2.5
    unseen = total - reviewed

    print(f"\n{BOLD}{CYAN}=== 🧠 SUPERMEMO SM-2 SPACED REPETITION DASHBOARD ==={RESET}\n")
    print(f"Total Master Curriculum:  {BOLD}{total}{RESET} Cards")
    print(f"Reviewed / Tracked:       {BOLD}{reviewed}{RESET} ({reviewed/total*100:.1f}%)")
    print(f"Due for Review Today:     {RED}{BOLD}{due_today}{RESET} Cards")
    print(f"New / Unstudied:          {YELLOW}{unseen}{RESET} Cards")
    print(f"Learning (Reps < 3):      {CYAN}{learning}{RESET} Cards")
    print(f"Mature (Reps ≥ 3):        {GREEN}{mature}{RESET} Cards")
    print(f"Average Ease Factor (EF): {MAGENTA}{avg_ef:.2f}{RESET} (1.30 = hard, 2.50 = standard)\n")

def run_quiz(cards, category_filter=None, shuffle=True, spaced_mode=False, non_interactive=False):
    if category_filter:
        filtered = [c for c in cards if category_filter.lower() in c['pillar'].lower()]
        if filtered:
            cards = filtered

    progress = load_progress()
    now = time.time()

    if spaced_mode:
        # Prioritize cards due for review first, then unstudied cards, then future cards
        def get_priority(card):
            cid = card['id']
            if cid in progress:
                next_rev = progress[cid].get("next_review", 0)
                if next_rev <= now:
                    return (0, next_rev)  # Due right now (highest priority)
                return (2, next_rev)      # Not due yet
            return (1, 0)                 # Unseen card

        cards = sorted(cards, key=get_priority)
    elif shuffle:
        random.shuffle(cards)

    total = len(cards)
    mode_str = f"Spaced Repetition (SM-2)" if spaced_mode else "Active Recall Drill"
    print(f"\n{BOLD}{CYAN}=== 🧠 {mode_str.upper()} ({total} Cards) ==={RESET}\n")

    if non_interactive or not sys.stdin.isatty():
        print(f"Non-interactive run verified: Successfully loaded {total} flashcards.")
        return 0

    reviewed_count = 0
    quality_ratings = []

    for idx, card in enumerate(cards, 1):
        cid = card['id']
        state = progress.get(cid, {"repetitions": 0, "ease_factor": 2.5, "interval": 1})
        rep_status = f"{DIM}(Rep: {state.get('repetitions', 0)}, EF: {state.get('ease_factor', 2.5)}){RESET}"

        print(f"{BOLD}[Card {idx}/{total} - #{cid}] Category: {YELLOW}{card['pillar']}{RESET} {rep_status}")
        print(f"{BOLD}{CYAN}Q: {card['question']}{RESET}\n")

        try:
            cmd = input(f"{DIM}Press [Enter] to reveal answer, or 'q' to quit: {RESET}")
            if cmd.strip().lower() == 'q':
                break
        except (EOFError, KeyboardInterrupt):
            print("\nExiting session.")
            break

        print(f"\n{GREEN}{BOLD}Answer:{RESET}")
        print(f"   {card['answer']}\n")

        # SM-2 Recall Rating Prompt
        prompt = (
            f"Rate recall: [{GREEN}5: Perfect{RESET} | "
            f"{CYAN}4: Good{RESET} | "
            f"{YELLOW}3: Hard{RESET} | "
            f"{RED}2: Failed{RESET} | "
            f"{RED}1: Blank{RESET}] (default: 4): "
        )

        try:
            rating_input = input(prompt).strip()
            if rating_input.lower() == 'q':
                break
            quality = int(rating_input) if rating_input in ["0", "1", "2", "3", "4", "5"] else 4
        except (EOFError, KeyboardInterrupt):
            break

        new_state = sm2_update(state, quality)
        progress[cid] = new_state
        save_progress(progress)

        reviewed_count += 1
        quality_ratings.append(quality)

        interval_days = new_state["interval"]
        next_dt = datetime.fromtimestamp(new_state["next_review"]).strftime('%Y-%m-%d')
        status_color = GREEN if quality >= 3 else RED
        print(f"{status_color}Next review scheduled in {interval_days} day(s) on {next_dt} (EF: {new_state['ease_factor']}){RESET}\n")
        print(f"{DIM}{'-'*65}{RESET}\n")

    print(f"\n{BOLD}=== 📊 SESSION SUMMARY ==={RESET}")
    print(f"Cards Reviewed: {reviewed_count}")
    if quality_ratings:
        avg_q = sum(quality_ratings) / len(quality_ratings)
        print(f"Average Recall Quality: {avg_q:.1f}/5.0")
    print(f"Progress saved to {DIM}.flashcards_progress.json{RESET}\n")
    return 0

def main():
    parser = argparse.ArgumentParser(description="Interactive Flashcards CLI Runner with SM-2 Spaced Repetition")
    parser.add_argument("--category", "-c", type=str, help="Filter by pillar or topic keyword (dsa | java | hld | lld | leadership | storage)")
    parser.add_argument("--no-shuffle", action="store_true", help="Do not randomize card order")
    parser.add_argument("--spaced", "-s", action="store_true", help="Run in SuperMemo SM-2 Spaced Repetition mode")
    parser.add_argument("--stats", action="store_true", help="Display spaced repetition progress dashboard")
    parser.add_argument("--reset", action="store_true", help="Reset all spaced repetition progress")
    parser.add_argument("--test", action="store_true", help="Quick self-test mode for CI validation")
    args = parser.parse_args()

    cards = load_flashcards()

    if args.test:
        assert len(cards) == 205, f"Expected 205 cards, loaded {len(cards)}"
        # Test SM-2 math logic
        dummy = {"repetitions": 0, "ease_factor": 2.5, "interval": 1}
        res5 = sm2_update(dummy.copy(), 5)
        assert res5["repetitions"] == 1 and res5["interval"] == 1
        res4 = sm2_update(res5.copy(), 4)
        assert res4["repetitions"] == 2 and res4["interval"] == 6
        res2 = sm2_update(res4.copy(), 2)
        assert res2["repetitions"] == 0 and res2["interval"] == 1
        print(f"Flashcard test passed: Loaded {len(cards)} valid flashcards and verified SM-2 algorithm.")
        sys.exit(0)

    if args.reset:
        if os.path.exists(PROGRESS_FILE):
            os.remove(PROGRESS_FILE)
            print(f"{GREEN}Spaced repetition progress reset successfully.{RESET}")
        else:
            print("No existing progress file found.")
        sys.exit(0)

    progress = load_progress()

    if args.stats:
        display_stats(cards, progress)
        sys.exit(0)

    run_quiz(cards, category_filter=args.category, shuffle=not args.no_shuffle, spaced_mode=args.spaced)

if __name__ == "__main__":
    main()
