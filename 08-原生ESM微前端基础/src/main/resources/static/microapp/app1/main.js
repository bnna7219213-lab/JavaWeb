/**
 * MicroApp App1 - 用户管理业务模块
 * 独立 ES Module，支持独立开发、独立部署
 * 生命周期协议：mount / unmount
 *
 * 此微前端可以直接移植到任何支持 ESM 的服务器运行
 */

// 使用 Import Map 中共享的 React 引用
const React = await import('react');
const { createRoot } = await import('react-dom/client');

// 微前端内部状态
let root = null;
let containerEl = null;
let config = {};
let users = [];

/**
 * 向 Host App 发送消息
 * 通过 CustomEvent + postMessage 模式
 */
function sendToHost(type, data) {
  window.dispatchEvent(new CustomEvent('microapp-event', {
    detail: { source: 'app1', type, data }
  }));
}

/**
 * 数据请求工具
 */
async function fetchAPI(path) {
  const base = config.apiBase || '/api';
  const resp = await fetch(base + path);
  return resp.json();
}

/**
 * 微前端挂载生命周期
 * @param {HTMLElement} container - Host App 提供的挂载容器
 * @param {Object} opts - 配置选项
 * @returns {Object} 子应用实例
 */
export async function mount(container, opts = {}) {
  containerEl = container;
  config = opts;

  // 获取数据
  users = await fetchAPI('/users');

  // 创建 React Root
  root = createRoot(container);

  // 渲染 UI
  render();

  // 通知 Host App 数据已加载
  sendToHost('data-loaded', { count: users.length, type: 'users' });

  console.log('[App1] Mount 完成, 数据条数:', users.length);

  return { name: 'user-module', version: '1.0.0', users };
}

/**
 * 微前端卸载生命周期
 * 清理所有资源：事件监听、定时器、DOM
 */
export function unmount(instance) {
  if (root) {
    root.unmount();
    root = null;
  }
  containerEl = null;
  users = [];
  console.log('[App1] Unmount 完成, 资源已清理');
}

// ============================
// 内部渲染逻辑（React）
// ============================
function render() {
  const App = React.createElement(AppComponent);
  root.render(App);
}

function AppComponent() {
  const [localUsers, setLocalUsers] = React.useState(users);

  // 处理用户行点击：通过事件通知 Host
  const handleUserClick = (user) => {
    sendToHost('user-selected', user);
  };

  return React.createElement('div', { className: 'app-shell' },
    React.createElement('h3', null, '用户管理 (React + 原生 ESM)'),
    React.createElement('table', { className: 'data-table' },
      React.createElement('thead', null,
        React.createElement('tr', null,
          React.createElement('th', null, 'ID'),
          React.createElement('th', null, '姓名'),
          React.createElement('th', null, '邮箱'),
          React.createElement('th', null, '年龄')
        )
      ),
      React.createElement('tbody', null,
        localUsers.map(user =>
          React.createElement('tr', {
            key: user.id,
            onClick: () => handleUserClick(user),
            style: { cursor: 'pointer' }
          },
            React.createElement('td', { className: 'id-col' }, user.id),
            React.createElement('td', null, user.name),
            React.createElement('td', null, user.email),
            React.createElement('td', null, user.age)
          )
        )
      )
    )
  );
}
