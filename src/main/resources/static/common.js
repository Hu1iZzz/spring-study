/* 公共组件与工具函数 */
const { createApp, ref, onMounted, reactive, nextTick, computed } = Vue;

/* 全局用户权限和菜单 */
const userPermissions = ref([]);
const userMenus = ref([]);

/* 判断当前用户是否拥有权限 */
function hasPermission(code) {
  return userPermissions.value.includes(code);
}

/* 注入全局公共样式 - Mac 风格 */
(function() {
  const style = document.createElement('style');
  style.textContent = `
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body {
      font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', 'Segoe UI', Roboto, sans-serif;
      background: #f5f5f7; color: #1d1d1f; -webkit-font-smoothing: antialiased;
    }
    a { text-decoration: none; }

    /* ====== 导航栏（毛玻璃） ====== */
    .navbar {
      background: rgba(255, 255, 255, 0.72);
      backdrop-filter: saturate(180%) blur(20px);
      -webkit-backdrop-filter: saturate(180%) blur(20px);
      height: 52px; display: flex; align-items: center; padding: 0 24px;
      position: sticky; top: 0; z-index: 100;
      border-bottom: 1px solid rgba(0, 0, 0, 0.08);
    }
    .navbar-brand { font-size: 17px; font-weight: 600; color: #1d1d1f; cursor: pointer; margin-right: 32px; white-space: nowrap; letter-spacing: -.02em; }
    .navbar-menu { flex: 1; }
    .navbar-menu .el-menu { background: transparent !important; border-bottom: none !important; }
    .navbar-menu .el-menu-item {
      height: 52px; line-height: 52px; color: #424245 !important;
      border-bottom: none !important; font-size: 14px; border-radius: 8px; margin: 0 2px;
    }
    .navbar-menu .el-menu-item:hover { background: rgba(0,0,0,0.04) !important; color: #1d1d1f !important; }
    .navbar-menu .el-menu-item.is-active { color: #0071e3 !important; background: rgba(0,113,227,0.08) !important; }
    .navbar-menu .el-sub-menu__title {
      height: 52px; line-height: 52px; color: #424245 !important;
      border-bottom: none !important; font-size: 14px; border-radius: 8px; margin: 0 2px;
    }
    .navbar-menu .el-sub-menu__title:hover { background: rgba(0,0,0,0.04) !important; color: #1d1d1f !important; }
    .navbar-menu .el-sub-menu.is-active > .el-sub-menu__title { color: #0071e3 !important; }
    .navbar-right { display: flex; align-items: center; gap: 14px; color: #424245; font-size: 14px; }
    .navbar-right .el-button { color: #424245; }
    .navbar-right .el-button:hover { color: #1d1d1f; }

    /* ====== 内容区 ====== */
    .content { max-width: 1280px; margin: 32px auto; padding: 0 28px; }

    /* ====== 卡片 ====== */
    .card {
      background: #fff; border-radius: 14px;
      box-shadow: 0 1px 3px rgba(0,0,0,0.06), 0 1px 2px rgba(0,0,0,0.04);
      overflow: hidden;
    }
    .card-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid rgba(0,0,0,0.06); }
    .card-header h2 { font-size: 15px; font-weight: 600; color: #1d1d1f; }

    /* ====== 表格 ====== */
    .el-table { border-radius: 14px; }
    .el-table th.el-table__cell { background: #fafafa !important; font-weight: 600; color: #1d1d1f; padding: 12px 0 !important; }
    .el-table td.el-table__cell { border-bottom: 1px solid rgba(0,0,0,0.04) !important; padding: 10px 0 !important; }
    .el-table--border { border: none !important; }
    .el-table--border::after, .el-table::before { display: none !important; }
    .el-table .el-button + .el-button { margin-left: 4px; }
    .el-table .el-button--small { padding: 5px 8px; }
    .el-empty__description p { color: #86868b; font-size: 14px; }
    .pager { display: flex; justify-content: flex-end; padding: 16px 20px; }

    /* ====== 工具栏 ====== */
    .toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
    .toolbar h2 { font-size: 22px; font-weight: 600; color: #1d1d1f; letter-spacing: -.02em; }

    /* ====== 欢迎卡片 ====== */
    .welcome {
      background: #fff; border-radius: 20px; padding: 56px 48px; text-align: center;
      box-shadow: 0 1px 3px rgba(0,0,0,0.06), 0 1px 2px rgba(0,0,0,0.04);
    }
    .welcome h1 { font-size: 28px; color: #1d1d1f; margin: 16px 0 8px; font-weight: 600; letter-spacing: -.02em; }
    .welcome p { color: #86868b; margin: 0 0 28px; font-size: 15px; }
    .welcome-stats { display: flex; justify-content: center; gap: 32px; margin: 32px 0; }
    .welcome-stat { text-align: center; }
    .welcome-stat .num { font-size: 32px; font-weight: 600; color: #0071e3; }
    .welcome-stat .label { font-size: 13px; color: #86868b; margin-top: 4px; }
    .stat-card {
      flex: 1; background: #fff; border-radius: 14px; padding: 24px; text-align: center;
      box-shadow: 0 1px 3px rgba(0,0,0,0.06), 0 1px 2px rgba(0,0,0,0.04);
    }
    .stat-num { font-size: 36px; font-weight: 700; letter-spacing: -.02em; }
    .stat-label { font-size: 14px; color: #86868b; margin-top: 6px; }

    /* ====== Element Plus 组件圆角化 ====== */
    .el-button { border-radius: 8px !important; font-weight: 500; }
    .el-button--small { border-radius: 6px !important; }
    .el-input__wrapper { border-radius: 8px !important; }
    .el-textarea__inner { border-radius: 8px !important; }
    .el-select .el-input__wrapper { border-radius: 8px !important; }
    .el-dialog { border-radius: 14px !important; overflow: hidden; }
    .el-dialog__header { padding: 22px 24px 14px; }
    .el-dialog__body { padding: 16px 24px; }
    .el-dialog__footer { padding: 14px 24px 22px; }
    .el-form-item { margin-bottom: 20px; }
    .el-form-item__label { font-weight: 500; color: #424245; }
    .el-tag { border-radius: 6px !important; font-weight: 500; }
    .el-pagination .el-pager li { border-radius: 6px !important; }
    .el-checkbox { border-radius: 4px; }
    .el-switch__core { border-radius: 16px !important; }
    .el-tree-node__content { border-radius: 6px; }
    .el-message { border-radius: 10px !important; }
    .el-message-box { border-radius: 14px !important; }
    .el-tree-select .el-input__wrapper { border-radius: 8px !important; }
    .el-date-editor.el-input .el-input__wrapper { border-radius: 8px !important; }
    .el-input-number .el-input__wrapper { border-radius: 8px !important; }

    /* ====== 下拉菜单圆角 ====== */
    .el-popper { border-radius: 10px !important; }
    .el-select-dropdown__item { border-radius: 6px; margin: 2px 4px; }

    /* ====== 导航子菜单（弹出下拉）扁平化 ====== */
    .el-menu--popup {
      border-radius: 12px !important;
      box-shadow: 0 4px 16px rgba(0,0,0,0.08), 0 1px 4px rgba(0,0,0,0.06) !important;
      border: 1px solid rgba(0,0,0,0.06) !important;
      padding: 6px !important;
      min-width: 180px !important;
    }
    .el-menu--popup .el-menu-item {
      height: 38px !important; line-height: 38px !important;
      border-radius: 8px !important; margin: 2px 0 !important;
      color: #424245 !important; font-size: 14px;
    }
    .el-menu--popup .el-menu-item:hover {
      background: rgba(0,0,0,0.04) !important; color: #1d1d1f !important;
    }
    .el-menu--popup .el-menu-item.is-active {
      color: #0071e3 !important; background: rgba(0,113,227,0.08) !important;
    }
    .el-menu--popup .el-sub-menu__title {
      height: 38px !important; line-height: 38px !important;
      border-radius: 8px !important; margin: 2px 0 !important;
      color: #424245 !important; font-size: 14px;
    }
    .el-menu--popup .el-sub-menu__title:hover {
      background: rgba(0,0,0,0.04) !important; color: #1d1d1f !important;
    }
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
