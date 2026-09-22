/**
 * MicroApp: UserModule - 用户管理业务子应用
 * 独立 ES Module，可按需动态加载
 * 功能：用户 CRUD、部门筛选、用户选择事件广播
 *
 * 生命周期协议：
 *   mount(container, opts) -> instance
 *   unmount(instance)      -> void
 */

// 使用 Import Map 中共享的 React
var React = null;
var createRoot = null;
var _depsLoaded = false;

async function loadDeps() {
  if (_depsLoaded) return;
  React = await import('react');
  var rdc = await import('react-dom/client');
  createRoot = rdc.createRoot;
  _depsLoaded = true;
}

// 子应用内部状态
var _root = null;
var _container = null;
var _config = {};
var _users = [];
var _unsubscribes = [];

/**
 * 向其他子应用和 Host 发送事件
 */
function emit(eventName, data) {
  window.dispatchEvent(new CustomEvent('microapp-event', {
    detail: { source: 'UserModule', eventName: eventName, data: data }
  }));
}

/**
 * 挂载生命周期 - 由 Host App 调用
 */
export async function mount(container, opts) {
  _container = container;
  _config = opts || {};

  // 1. 加载依赖
  await loadDeps();

  // 2. 获取数据
  _users = await fetchUsers();

  // 3. 渲染 UI
  _root = createRoot(container);
  render();

  // 4. 注册事件监听 (卸载时需清理)
  // 监听产品子应用发出的事件
  var unsubProduct = onMicroAppEvent('ProductModule', 'product:created', function(data) {
    console.log('[UserModule] 收到产品创建通知:', data);
    // 可以更新内部状态或显示提示
  });
  _unsubscribes.push(unsubProduct);

  // 通知 Host App
  emit('user:data-loaded', { count: _users.length });

  console.log('[UserModule] Mount complete, users:', _users.length);

  return {
    name: 'UserModule',
    version: '2.0.0',
    users: _users,
    getUsers: function() { return _users; }
  };
}

/**
 * 卸载生命周期 - 由 Host App 调用
 */
export function unmount(instance) {
  console.log('[UserModule] Unmounting...');

  // 1. 清理事件监听
  for (var i = 0; i < _unsubscribes.length; i++) {
    try { _unsubscribes[i](); } catch (e) {}
  }
  _unsubscribes = [];

  // 2. 卸载 React
  if (_root) {
    _root.unmount();
    _root = null;
  }

  // 3. 清空状态
  _container = null;
  _users = [];

  console.log('[UserModule] Unmount complete, all resources cleaned');
}

// ===========================
// 内部数据层
// ===========================
async function fetchUsers() {
  try {
    var resp = await fetch('/api/users');
    return await resp.json();
  } catch (err) {
    console.error('[UserModule] fetchUsers error:', err);
    return [];
  }
}

function onMicroAppEvent(source, eventName, handler) {
  var wrapped = function(e) {
    var d = e.detail;
    if (d && d.source === source && d.eventName === eventName) {
      handler(d.data);
    }
  };
  window.addEventListener('microapp-event', wrapped);
  return function() { window.removeEventListener('microapp-event', wrapped); };
}

// ===========================
// UI 渲染层 (React)
// ===========================
function render() {
  _root.render(React.createElement(AppComponent));
}

function AppComponent() {
  var _s = React.useState(_users);
  var users = _s[0];
  var setUsers = _s[1];
  var _filter = React.useState('');
  var deptFilter = _filter[0];
  var setDeptFilter = _filter[1];

  var deptCounts = React.useMemo(function() {
    var map = {};
    for (var i = 0; i < users.length; i++) {
      map[users[i].department] = (map[users[i].department] || 0) + 1;
    }
    return map;
  }, [users]);

  var hasFilter = deptFilter.length > 0;

  var handleSelectUser = function(user) {
    emit('user:selected', user);
  };

  var handleDelete = async function(id, name) {
    if (!confirm('确定用户 "' + name + '" 删除?')) return;
    try {
      var resp = await fetch('/api/users/' + id, { method: 'DELETE' });
      if (resp.ok) {
        var updated = await fetchUsers();
        setUsers(updated);
        emit('user:deleted', { id: id, name: name });
      }
    } catch (err) {
      console.error('[UserModule] Delete error:', err);
    }
  };

  return React.createElement('div', { className: 'ma-shell' },
    React.createElement('h3', null,
      React.createElement('span', { style: { marginRight: '8px', fontSize: '18px' } }, '\uD83D\uDC64'),
      '用户管理模块'
    ),

    // 部门统计栏
    React.createElement('div', { style: { display: 'flex', gap: '8px', marginBottom: '14px', flexWrap: 'wrap' } },
      React.createElement('span', {
        onClick: function() { setDeptFilter(''); },
        style: {
          display: 'inline-block', padding: '4px 10px', borderRadius: '12px', fontSize: '12px',
          background: !hasFilter ? '#2563eb' : '#334155',
          color: !hasFilter ? 'white' : '#94a3b8',
          cursor: 'pointer'
        }
      }, '全体员工 (' + users.length + ')'),
      Object.entries(deptCounts).map(function(entry) {
        var dept = entry[0], count = entry[1];
        var active = deptFilter === dept;
        return React.createElement('span', {
          key: dept,
          onClick: function() { setDeptFilter(active ? '' : dept); },
          style: {
            display: 'inline-block', padding: '4px 10px', borderRadius: '12px', fontSize: '12px',
            background: active ? '#2563eb' : '#334155',
            color: active ? 'white' : '#94a3b8',
            cursor: 'pointer'
          }
        }, dept + ' (' + count + ')');
      })
    ),

    // 用户表格
    React.createElement('table', { className: 'ma-table' },
      React.createElement('thead', null,
        React.createElement('tr', null,
          React.createElement('th', null, 'ID'),
          React.createElement('th', null, '姓名'),
          React.createElement('th', null, '邮箱'),
          React.createElement('th', null, '部门'),
          React.createElement('th', null, '年龄'),
          React.createElement('th', null, '操作')
        )
      ),
      React.createElement('tbody', null,
        users.filter(function(u) {
          return !hasFilter || u.department === deptFilter;
        }).map(function(user) {
          return React.createElement('tr', { key: user.id },
            React.createElement('td', { className: 'id-col' }, user.id),
            React.createElement('td', null,
              React.createElement('a', {
                onClick: function() { handleSelectUser(user); },
                style: { color: '#60a5fa', cursor: 'pointer' }
              }, user.name)
            ),
            React.createElement('td', null, user.email),
            React.createElement('td', null,
              React.createElement('span', {
                style: { padding: '2px 8px', background: '#1e3a5f', borderRadius: '10px', fontSize: '11px' }
              }, user.department)
            ),
            React.createElement('td', null, user.age),
            React.createElement('td', null,
              React.createElement('button', {
                onClick: function() { handleDelete(user.id, user.name); },
                style: { background: '#7f1d1d', color: 'white', border: 'none', padding: '3px 8px', borderRadius: '3px', fontSize: '11px', cursor: 'pointer' }
              }, '删除')
            )
          );
        })
      )
    )
  );
}
