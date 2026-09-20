import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import { JSDOM } from 'jsdom';

// Setup JSDOM environment so Mermaid works in Node
const dom = new JSDOM('<!DOCTYPE html><html><body></body></html>');
global.window = dom.window;
global.document = dom.window.document;
try {
  Object.defineProperty(global, 'navigator', {
    value: dom.window.navigator,
    configurable: true,
    writable: true
  });
} catch (e) {
  // navigator might already be partially defined
}

// Import mermaid dynamically after window is polyfilled
const { default: mermaid } = await import('mermaid');

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const WORKSPACE = path.resolve(__dirname, '..');

mermaid.initialize({ startOnLoad: false });

function getFiles(dir, fileList = []) {
  const files = fs.readdirSync(dir);
  for (const file of files) {
    if (file === '.git' || file === 'node_modules' || file === '.github') continue;
    const fullPath = path.join(dir, file);
    if (fs.statSync(fullPath).isDirectory()) {
      getFiles(fullPath, fileList);
    } else if (file.endsWith('.md')) {
      fileList.push(fullPath);
    }
  }
  return fileList;
}

async function validateAll() {
  const mdFiles = getFiles(WORKSPACE);
  let totalDiagrams = 0;
  let errors = [];

  for (const file of mdFiles) {
    const content = fs.readFileSync(file, 'utf-8');
    const rel = path.relative(WORKSPACE, file);
    const regex = /```mermaid\s*\n([\s\S]*?)\n```/g;
    let match;
    let index = 0;

    while ((match = regex.exec(content)) !== null) {
      index++;
      totalDiagrams++;
      const diagramCode = match[1];
      try {
        await mermaid.parse(diagramCode);
      } catch (err) {
        errors.push({
          file: rel,
          index,
          error: err.message || err.str || String(err),
          snippet: diagramCode.split('\n').slice(0, 15).join('\n')
        });
      }
    }
  }

  console.log(`Scanned ${totalDiagrams} Mermaid diagrams across all Markdown files.`);
  if (errors.length > 0) {
    console.log(`\n❌ FOUND ${errors.length} MERMAID PARSE ERRORS:\n`);
    for (const e of errors) {
      console.log(`File: ${e.file} (Diagram #${e.index})`);
      console.log(`Error: ${e.error}`);
      console.log(`Snippet:\n${e.snippet}\n-------------------------------------------\n`);
    }
    process.exit(1);
  } else {
    console.log('\n✅ ALL MERMAID DIAGRAMS PARSED SUCCESSFULLY WITH 0 ERRORS!');
  }
}

validateAll().catch(err => {
  console.error('Validation script error:', err);
  process.exit(1);
});
