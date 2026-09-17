const fs = require('fs');
const s = fs.readFileSync('D:/study/spring-study/src/main/resources/static/users.html', 'utf8');
const scripts = [...s.matchAll(/<script[^>]*>([\s\S]*?)<\/script>/g)].map(m => m[1]).filter(x => x.trim());
if (scripts.length === 0) { console.error('未找到 script 块'); process.exit(1); }
let allOk = true;
scripts.forEach((code, i) => {
  // 用 new Function 仅做语法解析（不执行）
  try {
    new Function(code);
    console.log(`script#${i + 1} 语法检查通过 (${code.length} 字符)`);
  } catch (e) {
    allOk = false;
    console.error(`script#${i + 1} 语法错误: ${e.message}`);
  }
});
// 检查关键函数是否都已定义并在 setup 中返回
const mainScript = scripts[scripts.length - 1];
const requiredDefs = [
  'openCreateDialog', 'openEditDialog', 'submitForm', 'confirmDelete',
  'toggleStatus', 'fetchUserList', 'fetchCurrentUser', 'formatTime',
  'resetForm', 'handleMenuSelect', 'goHome'
];
for (const fn of requiredDefs) {
  const defined = new RegExp(`const\\s+${fn}\\s*=`).test(mainScript) || new RegExp(`function\\s+${fn}\\b`).test(mainScript);
  const returned = new RegExp(`\\b${fn}\\b`).test(mainScript.split('return {')[1] || '');
  if (!defined) { console.error(`缺少定义: ${fn}`); allOk = false; }
  if (!returned) { console.error(`setup 未返回: ${fn}`); allOk = false; }
}
if (allOk) console.log('JS 语法与函数完整性校验通过');
else process.exit(1);