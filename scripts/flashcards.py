#!/usr/bin/env python3
"""
Interactive Flashcard Quiz Runner for Tech Interview Preparation
Parses active recall flashcards from cheatsheets/04-200-high-yield-interview-flashcards.md
"""

import sys
import os
import re
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

REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
FLASHCARD_FILE = os.path.join(REPO_ROOT, "cheatsheets", "04-200-high-yield-interview-flashcards.md")

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

            summary_match = re.search(r'<summary><b>(.*?)</b></summary>', full_block, re.DOTALL)
            p_match = re.search(r'<p>(.*?)</p>', full_block, re.DOTALL)

            if summary_match and p_match:
                q_text = clean_html(summary_match.group(1))
                a_text = clean_html(p_match.group(1))
                cards.append({
                    "pillar": current_pillar,
                    "question": q_text,
                    "answer": a_text
                })
        i += 1

    return cards

def run_quiz(cards, category_filter=None, shuffle=True, non_interactive=False):
    if category_filter:
        filtered = [c for c in cards if category_filter.lower() in c['pillar'].lower()]
        if filtered:
            cards = filtered

    if shuffle:
        random.shuffle(cards)

    total = len(cards)
    print(f"\n{BOLD}{CYAN}=== 🧠 INTERACTIVE ACTIVE RECALL FLASHCARDS ({total} Cards) ==={RESET}\n")

    if non_interactive or not sys.stdin.isatty():
        print(f"Non-interactive run verified: Successfully loaded {total} flashcards.")
        return 0

    scores = {"mastered": 0, "review": 0}
    need_review = []

    for idx, card in enumerate(cards, 1):
        print(f"{BOLD}[Card {idx}/{total}] Category: {YELLOW}{card['pillar']}{RESET}")
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

        try:
            rating = input(f"Rate your recall: [{GREEN}1: Mastered{RESET} / {RED}2: Need Review{RESET}] (default: 1): ").strip()
        except (EOFError, KeyboardInterrupt):
            break

        if rating == '2':
            scores["review"] += 1
            need_review.append(card)
            print(f"{RED}Saved for review later.{RESET}\n")
        else:
            scores["mastered"] += 1
            print(f"{GREEN}Marked as mastered!{RESET}\n")

        print(f"{DIM}{'-'*60}{RESET}\n")

    print(f"\n{BOLD}=== 📊 SESSION SUMMARY ==={RESET}")
    print(f"Total Reviewed: {scores['mastered'] + scores['review']}")
    print(f"{GREEN}Mastered: {scores['mastered']}{RESET}")
    print(f"{RED}Needs Review: {scores['review']}{RESET}")
    return 0

def main():
    parser = argparse.ArgumentParser(description="Interactive Flashcards CLI Runner")
    parser.add_argument("--category", "-c", type=str, help="Filter by pillar or topic keyword")
    parser.add_argument("--no-shuffle", action="store_true", help="Do not randomize card order")
    parser.add_argument("--test", action="store_true", help="Quick self-test mode")
    args = parser.parse_args()

    cards = load_flashcards()
    if args.test:
        print(f"Flashcard test passed: Loaded {len(cards)} valid flashcards.")
        sys.exit(0)

    run_quiz(cards, category_filter=args.category, shuffle=not args.no_shuffle)

if __name__ == "__main__":
    main()
