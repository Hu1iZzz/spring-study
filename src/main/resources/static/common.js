/* 公共组件与工具函数 */
const { createApp, ref, onMounted, reactive, nextTick, computed } = Vue;

/* 全局用户权限和菜单 */
const userPermissions = ref([]);
const userMenus = ref([]);

/* 判断当前用户是否拥有权限 */
function hasPermission(code) {
  return userPermissions.value.includes(code);
}

/* 注入全局公共样式 - 扁平 Mac 风格 */
(function() {
  const style = document.createElement('style');
  style.textContent = `
    /* ====== 设计变量（扁平 Mac / Apple 风） ====== */
    :root {
      --mac-bg: #f5f5f7;              /* 页面底色 */
      --mac-text: #1d1d1f;            /* 主文字 */
      --mac-text2: #6e6e73;           /* 次要文字 */
      --mac-text3: #86868b;           /* 弱文字 */
      --mac-blue: #0071e3;            /* 苹果蓝 */
      --mac-blue-hover: #0077ed;
      --mac-green: #34c759;
      --mac-orange: #ff9500;
      --mac-red: #ff3b30;
      --mac-purple: #af52de;
      --mac-hairline: rgba(0, 0, 0, 0.08);   /* 细分隔线 */
      --mac-fill: #f5f5f7;                   /* 灰色填充（按钮/表头） */
      --mac-fill2: #e8e8ed;                  /* 深一档的灰色填充 */
      --mac-ease: cubic-bezier(0.25, 0.1, 0.25, 1);
    }

    /* Element Plus 主题色对齐苹果蓝 */
    :root {
      --el-color-primary: #0071e3;
      --el-color-primary-light-3: #5aa2ec;
      --el-color-primary-light-5: #8abdf2;
      --el-color-primary-light-7: #bcd8f7;
      --el-color-primary-light-8: #d5e6fa;
      --el-color-primary-light-9: #eaf2fd;
      --el-color-primary-dark-2: #005bb4;
      --el-border-radius-base: 10px;
      --el-text-color-primary: #1d1d1f;
      --el-text-color-regular: #424245;
      --el-border-color: #d2d2d7;
      --el-fill-color-light: #f5f5f7;
    }

    * { margin: 0; padding: 0; box-sizing: border-box; }
    body {
      font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', 'Segoe UI', Roboto, sans-serif;
      background: var(--mac-bg); color: var(--mac-text); font-size: 14px;
      -webkit-font-smoothing: antialiased;
    }
    a { text-decoration: none; }

    /* 细滚动条 */
    ::-webkit-scrollbar { width: 8px; height: 8px; }
    ::-webkit-scrollbar-thumb { background: rgba(0,0,0,0.18); border-radius: 999px; border: 2px solid transparent; background-clip: content-box; }
    ::-webkit-scrollbar-thumb:hover { background: rgba(0,0,0,0.3); border: 2px solid transparent; background-clip: content-box; }
    ::-webkit-scrollbar-track { background: transparent; }

    /* ====== 导航栏（毛玻璃，保留 Mac 质感） ====== */
    .navbar {
      background: rgba(255, 255, 255, 0.72);
      backdrop-filter: saturate(180%) blur(20px);
      -webkit-backdrop-filter: saturate(180%) blur(20px);
      height: 52px; display: flex; align-items: center; padding: 0 24px;
      position: sticky; top: 0; z-index: 100;
      border-bottom: 1px solid var(--mac-hairline);
    }
    .navbar-brand { font-size: 17px; font-weight: 600; color: var(--mac-text); cursor: pointer; margin-right: 32px; white-space: nowrap; letter-spacing: -.02em; }
    .navbar-menu { flex: 1; }
    .navbar-menu .el-menu { background: transparent !important; border-bottom: none !important; }
    .navbar-menu .el-menu-item {
      height: 52px; line-height: 52px; color: var(--mac-text2) !important;
      border-bottom: none !important; font-size: 14px; border-radius: 10px; margin: 0 2px;
    }
    .navbar-menu .el-menu-item:hover { background: rgba(0,0,0,0.04) !important; color: var(--mac-text) !important; }
    .navbar-menu .el-menu-item.is-active { color: var(--mac-blue) !important; background: rgba(0,113,227,0.08) !important; }
    .navbar-menu .el-sub-menu__title {
      height: 52px; line-height: 52px; color: var(--mac-text2) !important;
      border-bottom: none !important; font-size: 14px; border-radius: 10px; margin: 0 2px;
    }
    .navbar-menu .el-sub-menu__title:hover { background: rgba(0,0,0,0.04) !important; color: var(--mac-text) !important; }
    .navbar-menu .el-sub-menu.is-active > .el-sub-menu__title { color: var(--mac-blue) !important; }
    .navbar-right { display: flex; align-items: center; gap: 14px; color: var(--mac-text2); font-size: 14px; }
    .navbar-right .el-button { color: var(--mac-text2); }
    .navbar-right .el-button:hover { color: var(--mac-text); }

    /* ====== 内容区 ====== */
    .content { max-width: 1280px; margin: 32px auto; padding: 0 28px; }

    /* ====== 卡片（扁平：无阴影，白底直接浮在灰底上） ====== */
    .card {
      background: #fff; border-radius: 18px;
      box-shadow: none; overflow: hidden;
    }
    .card-header { display: flex; justify-content: space-between; align-items: center; padding: 18px 22px; border-bottom: 1px solid rgba(0,0,0,0.05); }
    .card-header h2 { font-size: 15px; font-weight: 600; color: var(--mac-text); }

    /* ====== 表格（扁平细分隔线） ====== */
    .el-table { border-radius: 14px; }
    .el-table th.el-table__cell { background: var(--mac-fill) !important; font-weight: 600; color: var(--mac-text2); font-size: 13px; padding: 12px 0 !important; }
    .el-table td.el-table__cell { border-bottom: 1px solid rgba(0,0,0,0.05) !important; padding: 12px 0 !important; font-size: 13px; }
    .el-table--border { border: none !important; }
    .el-table--border::after, .el-table::before { display: none !important; }
    .el-table .el-button + .el-button { margin-left: 4px; }
    .el-table .el-button--small { padding: 5px 8px; }
    .el-empty__description p { color: var(--mac-text3); font-size: 14px; }
    .pager { display: flex; justify-content: flex-end; padding: 16px 20px; }

    /* ====== 工具栏 ====== */
    .toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
    .toolbar h2 { font-size: 24px; font-weight: 700; color: var(--mac-text); letter-spacing: -.02em; }

    /* ====== 统计卡片（扁平） ====== */
    .welcome {
      background: #fff; border-radius: 22px; padding: 56px 48px; text-align: center;
      box-shadow: none;
    }
    .welcome h1 { font-size: 28px; color: var(--mac-text); margin: 16px 0 8px; font-weight: 600; letter-spacing: -.02em; }
    .welcome p { color: var(--mac-text3); margin: 0 0 28px; font-size: 15px; }
    .welcome-stats { display: flex; justify-content: center; gap: 32px; margin: 32px 0; }
    .welcome-stat { text-align: center; }
    .welcome-stat .num { font-size: 32px; font-weight: 600; color: var(--mac-blue); }
    .welcome-stat .label { font-size: 13px; color: var(--mac-text3); margin-top: 4px; }
    .stat-card {
      flex: 1; background: #fff; border-radius: 18px; padding: 26px; text-align: center;
      box-shadow: none;
    }
    .stat-num { font-size: 36px; font-weight: 700; letter-spacing: -.02em; }
    .stat-label { font-size: 13px; color: var(--mac-text3); margin-top: 6px; }

    /* ====== 按钮（扁平填充 + 按压缩放，Mac 手感） ====== */
    .el-button { border-radius: 12px !important; font-weight: 500; border: none !important; transition: all .2s var(--mac-ease); }
    .el-button:active { transform: scale(.97); }
    .el-button--default { background: var(--mac-fill2); color: var(--mac-text); }
    .el-button--default:hover { background: #dcdce1; color: var(--mac-text); }
    .el-button--primary { background: var(--mac-blue); }
    .el-button--primary:hover { background: var(--mac-blue-hover); }
    .el-button--danger { background: var(--mac-red); }
    .el-button--danger:hover { background: #ff4d42; }
    .el-button--success { background: var(--mac-green); }
    .el-button--warning { background: var(--mac-orange); }
    .el-button.is-text, .el-button.is-link { background: transparent !important; }
    .el-button--small { border-radius: 9px !important; }

    /* ====== 输入框（细描边 + 聚焦光环） ====== */
    .el-input__wrapper, .el-textarea__inner { border-radius: 10px !important; }
    .el-input__wrapper { box-shadow: 0 0 0 1px rgba(0,0,0,0.14) inset !important; }
    .el-input__wrapper:hover { box-shadow: 0 0 0 1px rgba(0,0,0,0.26) inset !important; }
    .el-input__wrapper.is-focus { box-shadow: 0 0 0 1px var(--mac-blue) inset, 0 0 0 4px rgba(0,113,227,0.15) !important; }
    .el-textarea__inner { box-shadow: 0 0 0 1px rgba(0,0,0,0.14) inset !important; }
    .el-textarea__inner:focus { box-shadow: 0 0 0 1px var(--mac-blue) inset, 0 0 0 4px rgba(0,113,227,0.15) !important; }
    .el-select .el-input__wrapper { border-radius: 10px !important; }

    /* ====== 弹窗（大圆角 + 轻投影，扁平页面里保留一点悬浮感） ====== */
    .el-overlay { background: rgba(0,0,0,0.28); }
    .el-dialog { border-radius: 20px !important; overflow: hidden; box-shadow: 0 24px 80px rgba(0,0,0,0.18) !important; }
    .el-dialog__header { padding: 22px 24px 14px; }
    .el-dialog__title { font-weight: 600; letter-spacing: -.01em; }
    .el-dialog__body { padding: 16px 24px; }
    .el-dialog__footer { padding: 14px 24px 22px; }
    .el-form-item { margin-bottom: 20px; }
    .el-form-item__label { font-weight: 500; color: #424245; }

    /* ====== 标签（圆角胶囊、去描边） ====== */
    .el-tag { border-radius: 999px !important; border: none !important; font-weight: 500; }

    /* ====== 分页（扁平，激活项浅色填充） ====== */
    .el-pagination { --el-pagination-button-color: var(--mac-text2); }
    .el-pagination .el-pager li { border-radius: 8px !important; background: transparent !important; font-weight: 500; }
    .el-pagination .el-pager li:hover { background: rgba(0,0,0,0.04) !important; }
    .el-pagination .el-pager li.is-active { background: var(--mac-fill2) !important; color: var(--mac-text) !important; }
    .el-pagination .btn-prev, .el-pagination .btn-next { border-radius: 8px !important; background: transparent !important; }

    /* ====== 其他组件圆角 ====== */
    .el-checkbox { border-radius: 4px; }
    .el-switch__core { border-radius: 999px !important; }
    .el-tree-node__content { border-radius: 8px; }
    .el-message { border-radius: 999px !important; border: none !important; box-shadow: 0 8px 30px rgba(0,0,0,0.12) !important; }
    .el-message-box { border-radius: 18px !important; }
    .el-tree-select .el-input__wrapper { border-radius: 10px !important; }
    .el-date-editor.el-input .el-input__wrapper { border-radius: 10px !important; }
    .el-input-number .el-input__wrapper { border-radius: 10px !important; }

    /* ====== 下拉面板（细描边 + 轻投影） ====== */
    .el-popper { border-radius: 12px !important; box-shadow: 0 12px 40px rgba(0,0,0,0.1), 0 0 0 1px rgba(0,0,0,0.05) !important; }
    .el-select-dropdown__item { border-radius: 8px; margin: 2px 4px; }
    .el-select-dropdown__item.is-selected { color: var(--mac-blue); font-weight: 600; }

    /* ====== 导航子菜单（弹出下拉） ====== */
    .el-menu--popup {
      border-radius: 14px !important;
      box-shadow: 0 12px 40px rgba(0,0,0,0.1), 0 0 0 1px rgba(0,0,0,0.05) !important;
      border: none !important;
      padding: 6px !important;
      min-width: 180px !important;
    }
    .el-menu--popup .el-menu-item {
      height: 38px !important; line-height: 38px !important;
      border-radius: 9px !important; margin: 2px 0 !important;
      color: var(--mac-text2) !important; font-size: 14px;
    }
    .el-menu--popup .el-menu-item:hover { background: rgba(0,0,0,0.04) !important; color: var(--mac-text) !important; }
    .el-menu--popup .el-menu-item.is-active { color: var(--mac-blue) !important; background: rgba(0,113,227,0.08) !important; }
    .el-menu--popup .el-sub-menu__title {
      height: 38px !important; line-height: 38px !important;
      border-radius: 9px !important; margin: 2px 0 !important;
      color: var(--mac-text2) !important; font-size: 14px;
    }
    .el-menu--popup .el-sub-menu__title:hover { background: rgba(0,0,0,0.04) !important; color: var(--mac-text) !important; }
  `;
  document.head.appendChild(style);
})();

/* API 请求封装：自动处理 401/403 */
async function api(url, opts) {
  const res = await fetch(url, { ...opts, redirect: 'manual' });
  if (res.status === 401 || res.type === 'opaqueredirect') {
    ElementPlus.ElMessage.warning('请先登录');
    setTimeout(() => location.href = '/login.html', 800);
    return null;
  }
  if (res.status === 403) {
    ElementPlus.ElMessage.error('无权限执行此操作');
    return null;
  }
  return res;
}

/* 获取当前登录用户 */
async function loadCurrentUser() {
  try {
    const res = await api('/api/users/currentUser');
    if (res && res.ok) {
      const u = await res.json();
      if (u && u.username) {
        localStorage.setItem('user', JSON.stringify(u));
        userPermissions.value = u.permissions || [];
        userMenus.value = u.menus || [];
        return u.nickname || u.username;
      }
    }
    const s = localStorage.getItem('user');
    if (s) { const u = JSON.parse(s); userPermissions.value = u.permissions || []; userMenus.value = u.menus || []; return u.nickname || u.username; }
  } catch {}
  return '未登录';
}

/* 递归菜单渲染组件 */
const MenuRenderer = {
  name: 'MenuRenderer',
  props: ['menus'],
  template: `
    <template v-for="m in menus" :key="m.id">
      <el-sub-menu v-if="m.children && m.children.length" :index="'sub-' + m.id">
        <template #title>
          <el-icon v-if="m.icon"><component :is="m.icon" /></el-icon>
          <span>{{ m.name }}</span>
        </template>
        <menu-renderer :menus="m.children" />
      </el-sub-menu>
      <el-menu-item v-else :index="m.path">
        <el-icon v-if="m.icon"><component :is="m.icon" /></el-icon>
        <span>{{ m.name }}</span>
      </el-menu-item>
    </template>
  `
};

/* 导航栏布局组件 */
const AppLayout = {
  components: { MenuRenderer },
  template: `
    <div>
      <div class="navbar">
        <span class="navbar-brand" @click="go('/index.html')">📚 学习平台</span>
        <div class="navbar-menu">
          <el-menu :default-active="active" mode="horizontal" @select="go">
            <menu-renderer :menus="menus" />
          </el-menu>
        </div>
        <div class="navbar-right">
          <span>👤 {{ username }}</span>
          <el-button text @click="go('/logout.html')">退出</el-button>
        </div>
      </div>
      <div class="content"><slot></slot></div>
    </div>
  `,
  setup() {
    const username = ref('加载中...');
    const active = ref(location.pathname);
    const go = (i) => { if (i && i.startsWith('/')) location.href = i; };
    const menus = computed(() => userMenus.value);
    onMounted(async () => { username.value = await loadCurrentUser(); });
    return { username, active, go, menus };
  }
};

/* 初始化应用：注册图标 + Element Plus + 挂载 */
function initApp(options) {
  const app = createApp(options);
  app.component('MenuRenderer', MenuRenderer);
  for (const [k, c] of Object.entries(ElementPlusIconsVue)) app.component(k, c);
  app.use(ElementPlus);
  app.mount('#app');
}
