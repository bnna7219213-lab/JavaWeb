/**
 * MicroApp: ProductModule - 商品管理业务子应用
 * 独立 ES Module，可按需动态加载
 * 功能：商品 CRUD、分类筛选、商品创建事件广播给其他子应用
 *
 * 生命周期协议：
 *   mount(container, opts) -> instance
 *   unmount(instance)      -> void
 */

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

var _root = null;
var _container = null;
var _products = [];
var _unsubscribes = [];

/**
 * 向其他子应用和 Host 发送事件
 */
function emit(eventName, data) {
  window.dispatchEvent(new CustomEvent('microapp-event', {
    detail: { source: 'ProductModule', eventName: eventName, data: data }
  }));
}

export async function mount(container, opts) {
  _container = container;
  opts = opts || {};

  await loadDeps();
  _products = await fetchProducts();

  _root = createRoot(container);
  render();

  // 监听 UserModule 发出的事件
  var unsubUser = onMicroAppEvent('UserModule', 'user:selected', function(data) {
    console.log('[ProductModule] 收到用户选中:', data);
    // 可以联动显示该用户创建的商品
  });
  _unsubscribes.push(unsubUser);

  emit('product:data-loaded', { count: _products.length });

  console.log('[ProductModule] Mount complete, products:', _products.length);

  return {
    name: 'ProductModule',
    version: '2.0.0',
    products: _products,
    getProducts: function() { return _products; }
  };
}

export function unmount(instance) {
  console.log('[ProductModule] Unmounting...');

  for (var i = 0; i < _unsubscribes.length; i++) {
    try { _unsubscribes[i](); } catch (e) {}
  }
  _unsubscribes = [];

  if (_root) {
    _root.unmount();
    _root = null;
  }

  _container = null;
  _products = [];

  console.log('[ProductModule] Unmount complete');
}

// ===========================
// 数据层
// ===========================
async function fetchProducts() {
  try {
    var resp = await fetch('/api/products');
    return await resp.json();
  } catch (err) {
    console.error('[ProductModule] fetchProducts error:', err);
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
// UI 层
// ===========================
function render() {
  _root.render(React.createElement(AppComponent));
}

function AppComponent() {
  var _s = React.useState(_products);
  var products = _s[0];
  var setProducts = _s[1];
  var _filter = React.useState('');
  var catFilter = _filter[0];
  var setCatFilter = _filter[1];

  var catCounts = React.useMemo(function() {
    var map = {};
    for (var i = 0; i < products.length; i++) {
      map[products[i].category] = (map[products[i].category] || 0) + 1;
    }
    return map;
  }, [products]);

  var hasFilter = catFilter.length > 0;

  var handleSelectProduct = function(product) {
    emit('product:selected', product);
  };

  var handleDelete = async function(id, name) {
    if (!confirm('确定删除商品 "' + name + '"?')) return;
    try {
      var resp = await fetch('/api/products/' + id, { method: 'DELETE' });
      if (resp.ok) {
        var updated = await fetchProducts();
        setProducts(updated);
        emit('product:deleted', { id: id, name: name });
      }
    } catch (err) {
      console.error('[ProductModule] Delete error:', err);
    }
  };

  return React.createElement('div', { className: 'ma-shell' },
    React.createElement('h3', null,
      React.createElement('span', { style: { marginRight: '8px', fontSize: '18px' } }, '\uD83D\uDCE6'),
      '商品管理模块'
    ),

    // 分类筛选
    React.createElement('div', { style: { display: 'flex', gap: '8px', marginBottom: '14px', flexWrap: 'wrap' } },
      React.createElement('span', {
        onClick: function() { setCatFilter(''); },
        style: {
          display: 'inline-block', padding: '4px 10px', borderRadius: '12px', fontSize: '12px',
          background: !hasFilter ? '#2563eb' : '#334155',
          color: !hasFilter ? 'white' : '#94a3b8',
          cursor: 'pointer'
        }
      }, '全部分类 (' + products.length + ')'),
      Object.entries(catCounts).map(function(entry) {
        var cat = entry[0], count = entry[1];
        var active = catFilter === cat;
        return React.createElement('span', {
          key: cat,
          onClick: function() { setCatFilter(active ? '' : cat); },
          style: {
            display: 'inline-block', padding: '4px 10px', borderRadius: '12px', fontSize: '12px',
            background: active ? '#2563eb' : '#334155',
            color: active ? 'white' : '#94a3b8',
            cursor: 'pointer'
          }
        }, cat + ' (' + count + ')');
      })
    ),

    // 商品表格
    React.createElement('table', { className: 'ma-table' },
      React.createElement('thead', null,
        React.createElement('tr', null,
          React.createElement('th', null, 'ID'),
          React.createElement('th', null, '名称'),
          React.createElement('th', null, '分类'),
          React.createElement('th', null, '价格'),
          React.createElement('th', null, '库存'),
          React.createElement('th', null, '操作')
        )
      ),
      React.createElement('tbody', null,
        products.filter(function(p) {
          return !hasFilter || p.category === catFilter;
        }).map(function(product) {
          return React.createElement('tr', { key: product.id },
            React.createElement('td', { className: 'id-col' }, product.id),
            React.createElement('td', null,
              React.createElement('a', {
                onClick: function() { handleSelectProduct(product); },
                style: { color: '#60a5fa', cursor: 'pointer' }
              }, product.name)
            ),
            React.createElement('td', null,
              React.createElement('span', {
                style: { padding: '2px 8px', background: '#1e3a5f', borderRadius: '10px', fontSize: '11px' }
              }, product.category)
            ),
            React.createElement('td', null,
              React.createElement('span', { style: { color: '#fbbf24', fontWeight: '600' } },
                '\u00A5' + product.price
              )
            ),
            React.createElement('td', null, product.stock),
            React.createElement('td', null,
              React.createElement('button', {
                onClick: function() { handleDelete(product.id, product.name); },
                style: { background: '#7f1d1d', color: 'white', border: 'none', padding: '3px 8px', borderRadius: '3px', fontSize: '11px', cursor: 'pointer' }
              }, '删除')
            )
          );
        })
      )
    )
  );
}
