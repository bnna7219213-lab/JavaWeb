/**
 * API Client - 微前端共享数据请求工具
 * 封装 fetch，统一错误处理和请求前缀配置
 */
export var API_BASE = '/api';

export function setApiBase(base) {
  API_BASE = base;
}

async function request(method, path, body) {
  var url = API_BASE + path;
  var options = {
    method: method,
    headers: { 'Content-Type': 'application/json' }
  };
  if (body) {
    options.body = JSON.stringify(body);
  }

  var resp = await fetch(url, options);
  if (!resp.ok) {
    var errorText = await resp.text().catch(function() { return resp.statusText; });
    throw new Error('API ' + resp.status + ': ' + errorText);
  }
  return resp.json();
}

export var api = {
  getUsers: function(department) {
    var path = '/users';
    if (department) path += '?department=' + encodeURIComponent(department);
    return request('GET', path);
  },
  getUser: function(id) {
    return request('GET', '/users/' + id);
  },
  createUser: function(user) {
    return request('POST', '/users', user);
  },
  updateUser: function(id, user) {
    return request('PUT', '/users/' + id, user);
  },
  deleteUser: function(id) {
    return request('DELETE', '/users/' + id);
  },
  getUserStats: function() {
    return request('GET', '/users/stats/departments');
  },
  getProducts: function(category) {
    var path = '/products';
    if (category) path += '?category=' + encodeURIComponent(category);
    return request('GET', path);
  },
  getProduct: function(id) {
    return request('GET', '/products/' + id);
  },
  createProduct: function(product) {
    return request('POST', '/products', product);
  },
  deleteProduct: function(id) {
    return request('DELETE', '/products/' + id);
  },
  getProductStats: function() {
    return request('GET', '/products/stats/categories');
  }
};
