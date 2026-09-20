import os
import re
import sys

# Dynamically determine workspace root (parent of scripts directory)
workspace = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
broken_links = []
unbalanced_fences = []
missing_navigation = []
total_links = 0
total_files = 0
total_code_blocks = 0

for root, _, files in os.walk(workspace):
    if '.git' in root or 'node_modules' in root or '.github' in root:
        continue
    for f in files:
        if f.endswith('.md'):
            total_files += 1
            filepath = os.path.join(root, f)
            with open(filepath, 'r', encoding='utf-8') as handle:
                content = handle.read()

            # 1. Code fence check
            lines = content.split('\n')
            fence_count = 0
            for idx, line in enumerate(lines, 1):
                if line.strip().startswith('```'):
                    fence_count += 1
            if fence_count % 2 != 0:
                unbalanced_fences.append((filepath, fence_count))
            total_code_blocks += fence_count // 2

            # 2. Navigation footer check (non-root READMEs, CONTRIBUTING, and numbered files)
            if not f.endswith('README.md') and not f.endswith('CONTRIBUTING.md') and '---' in content:
                if 'Continue Learning' not in content and 'Track Hub' not in content and 'Previous' not in content:
                    missing_navigation.append(filepath)

            # 3. Relative Link check
            for m in re.finditer(r'\[([^\]]+)\]\(([^)]+)\)', content):
                link = m.group(2)
                if link.startswith(('http://', 'https://', '#', 'mailto:')):
                    continue
                clean_link = link.split('#')[0]
                if not clean_link:
                    continue
                total_links += 1
                target_path = os.path.normpath(os.path.join(root, clean_link))
                if not os.path.exists(target_path):
                    broken_links.append((filepath, link, target_path))

print("==================================================")
print("       REPOSITORY COMPREHENSIVE INTEGRITY AUDIT   ")
print("==================================================")
print(f"Total Markdown Documents:    {total_files}")
print(f"Total Code Blocks:           {total_code_blocks}")
print(f"Total Relative Links:        {total_links}")
print("--------------------------------------------------")

error_found = False

if unbalanced_fences:
    error_found = True
    print(f"❌ Unbalanced code fences in {len(unbalanced_fences)} files:")
    for fp, count in unbalanced_fences:
        print(f"   {fp} (count: {count})")
else:
    print("✅ Code Fences: 100% Balanced and Well-formed")

if missing_navigation:
    print(f"⚠️ Missing navigation footers in {len(missing_navigation)} files:")
    for fp in missing_navigation:
        print(f"   {fp}")
else:
    print("✅ Navigation Footers: Present across all content pages")

if broken_links:
    error_found = True
    print(f"❌ Broken Links found: {len(broken_links)}")
    for fp, l, tgt in broken_links:
        print(f"   In {fp}: {l} -> {tgt}")
else:
    print("✅ Relative Markdown Links: 100% Valid (0 Broken Links)")

print("==================================================")
if error_found:
    sys.exit(1)
else:
    print("🎉 AUDIT PASSED WITH ZERO INTEGRITY DEFECTS!")
    sys.exit(0)
