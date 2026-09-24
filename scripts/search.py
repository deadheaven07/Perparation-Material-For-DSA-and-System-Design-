#!/usr/bin/env python3
"""
Fast Knowledge Search Engine for Prep_DSA_SystemDesign
Searches across all 188+ Markdown guides, code examples, and cheatsheets.
"""

import sys
import os
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

def search_repo(query, max_results=20):
    if not query:
        print(f"{YELLOW}Please provide a search term.{RESET}")
        return []

    pattern = re.compile(re.escape(query), re.IGNORECASE)
    results = []

    for root, dirs, files in os.walk(REPO_ROOT):
        # Exclude hidden directories and node_modules
        dirs[:] = [d for d in dirs if not d.startswith('.') and d != 'node_modules']
        for file in files:
            if file.endswith('.md'):
                filepath = os.path.join(root, file)
                rel_path = os.path.relpath(filepath, REPO_ROOT)
                try:
                    with open(filepath, 'r', encoding='utf-8') as f:
                        for line_num, line in enumerate(f, 1):
                            if pattern.search(line):
                                snippet = line.strip()
                                # Highlight the matched term
                                highlighted = pattern.sub(f"{BOLD}{YELLOW}\\g<0>{RESET}", snippet)
                                results.append({
                                    "file": rel_path,
                                    "line": line_num,
                                    "snippet": highlighted
                                })
                except Exception:
                    continue

    return results

def main():
    parser = argparse.ArgumentParser(description="Repository Fast Knowledge Search")
    parser.add_argument("query", nargs="?", default="", help="Keyword or concept to search for")
    parser.add_argument("--limit", "-l", type=int, default=15, help="Maximum number of results to display")
    parser.add_argument("--test", action="store_true", help="Quick self-test mode")
    args = parser.parse_args()

    if args.test:
        test_res = search_repo("Raft", max_results=5)
        assert len(test_res) > 0, "Search self-test failed to find 'Raft'"
        print(f"Search engine test passed: Found {len(test_res)} matches for 'Raft'.")
        sys.exit(0)

    if not args.query:
        print(f"{BOLD}{CYAN}=== 🔍 REPOSITORY KNOWLEDGE SEARCH ==={RESET}")
        print("Usage: npm run search <query>")
        print("Example: npm run search \"Token Bucket\"")
        sys.exit(0)

    print(f"\n{BOLD}{CYAN}=== 🔍 SEARCH RESULTS FOR: '{args.query}' ==={RESET}\n")
    matches = search_repo(args.query)

    if not matches:
        print(f"{YELLOW}No matches found for '{args.query}'. Try broader keywords.{RESET}\n")
        return

    print(f"Found {BOLD}{len(matches)}{RESET} matches across repository (showing top {min(len(matches), args.limit)}):\n")
    for res in matches[:args.limit]:
        print(f"📄 {GREEN}{res['file']}{RESET}:{CYAN}{res['line']}{RESET}")
        print(f"   {res['snippet']}\n")

if __name__ == "__main__":
    main()
