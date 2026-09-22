/**
 * 前端应用入口（编译后版本）
 */

import { UserEndpoint, OrderEndpoint, ApiException } from './endpoints.js';
import { validateUserForm, validateOrderForm, hasErrors, ORDER_STATUS_OPTIONS } from './validation.js';

// ============================================================
// 通用工具
// ============================================================

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function showToast(message, type) {
    type = type || 'info';
    const container = document.getElementById('toast-container');
    if (!container) return;
    const toast = document.createElement('div');
    toast.className = 'toast toast-' + type;
    toast.textContent = message;
    container.appendChild(toast);
    setTimeout(function() { toast.classList.add('toast-fade-out'); }, 2500);
    setTimeout(function() { toast.remove(); }, 3000);
}

function formatError(error) {
    if (error instanceof ApiException) {
        return error.message;
    }
    return String(error);
}

// ============================================================
// 用户管理
// ============================================================

async function loadUsers() {
    const container = document.getElementById('user-list');
    if (!container) return;

    container.innerHTML = '<tr><td colspan="6" style="text-align:center;color:#999;padding:24px">加载中...</td></tr>';

    try {
        const users = await UserEndpoint.listUsers();
        renderUserList(users);
    } catch (error) {
        container.innerHTML = '<tr><td colspan="6" style="text-align:center;color:#e74c3c;padding:24px">加载失败: ' + error + '</td></tr>';
    }
}

function renderUserList(users) {
    const container = document.getElementById('user-list');
    if (!container) return;

    if (users.length === 0) {
        container.innerHTML = '<tr><td colspan="6" style="text-align:center;color:#999">暂无用户数据</td></tr>';
        return;
    }

    container.innerHTML = users.map(function(user) {
        return '<tr><td><span class="id-badge">' + user.id + '</span></td><td>' + escapeHtml(user.name) + '</td><td>' + user.age + '</td><td>' + escapeHtml(user.email || '<span style="color:#bbb">未填写</span>') + '</td><td><button class="btn-xs btn-info" onclick="window.getUserOrders(' + user.id + ')">查看订单</button></td><td><button class="btn-xs btn-danger" onclick="window.deleteUser(' + user.id + ')">删除</button></td></tr>';
    }).join('');

    var countEl = document.getElementById('user-count');
    if (countEl) countEl.textContent = String(users.length);
}

async function handleCreateUser() {
    var nameInput = document.getElementById('user-name');
    var ageInput = document.getElementById('user-age');
    var emailInput = document.getElementById('user-email');

    var formData = {
        name: nameInput.value.trim(),
        age: parseInt(ageInput.value, 10),
        email: emailInput.value.trim() || null,
    };

    var errors = validateUserForm(formData);
    if (hasErrors(errors)) {
        showToast(Object.values(errors).join('; '), 'error');
        return;
    }

    try {
        await UserEndpoint.createUser(formData);
        showToast('用户 "' + formData.name + '" 创建成功', 'success');
        nameInput.value = '';
        ageInput.value = '';
        emailInput.value = '';
        await loadUsers();
    } catch (error) {
        showToast('创建失败: ' + formatError(error), 'error');
    }
}

async function deleteUser(id) {
    if (!confirm('确认删除用户 #' + id + '?')) return;

    try {
        await UserEndpoint.deleteUser(id);
        showToast('用户 #' + id + ' 已删除', 'success');
        await loadUsers();
    } catch (error) {
        showToast('删除失败: ' + formatError(error), 'error');
    }
}

// ============================================================
// 订单管理
// ============================================================

async function getUserOrders(userId) {
    var container = document.getElementById('order-list');
    if (!container) return;

    container.innerHTML = '<tr><td colspan="7" style="text-align:center;color:#999;padding:24px">加载中...</td></tr>';

    try {
        var orders = await OrderEndpoint.listOrdersByUser(userId);
        renderOrderList(orders, userId);
        document.getElementById('order-section').scrollIntoView({ behavior: 'smooth' });
    } catch (error) {
        container.innerHTML = '<tr><td colspan="7" style="text-align:center;color:#e74c3c;padding:24px">加载失败: ' + error + '</td></tr>';
    }
}

async function loadAllOrders() {
    var container = document.getElementById('order-list');
    if (!container) return;

    container.innerHTML = '<tr><td colspan="7" style="text-align:center;color:#999;padding:24px">加载中...</td></tr>';

    try {
        var orders = await OrderEndpoint.listOrders();
        renderOrderList(orders, null);
    } catch (error) {
        container.innerHTML = '<tr><td colspan="7" style="text-align:center;color:#e74c3c;padding:24px">加载失败: ' + error + '</td></tr>';
    }
}

function renderOrderList(orders, userId) {
    var container = document.getElementById('order-list');
    if (!container) return;

    var subtitle = document.getElementById('order-subtitle');
    if (subtitle) {
        subtitle.textContent = userId !== null ? '用户 #' + userId + ' 的订单 (' + orders.length + '条)' : '全部订单 (' + orders.length + '条)';
    }

    if (orders.length === 0) {
        container.innerHTML = '<tr><td colspan="7" style="text-align:center;color:#999">暂无订单数据</td></tr>';
        return;
    }

    container.innerHTML = orders.map(function(order) {
        var statusMap = { PENDING: '待支付', PAID: '已支付', SHIPPED: '已发货', DELIVERED: '已送达', CANCELLED: '已取消' };
        var statusLabel = statusMap[order.status] || order.status;
        return '<tr><td>' + order.id + '</td><td>User #' + order.userId + '</td><td>' + escapeHtml(order.productName) + '</td><td>' + order.quantity + '</td><td>¥' + order.price.toFixed(2) + '</td><td><span class="status-badge status-' + order.status.toLowerCase() + '">' + statusLabel + '</span></td><td><button class="btn-xs btn-danger" onclick="window.deleteOrder(' + order.id + ')">删除</button></td></tr>';
    }).join('');
}

async function handleCreateOrder() {
    var userIdInput = document.getElementById('order-userId');
    var productInput = document.getElementById('order-product');
    var qtyInput = document.getElementById('order-quantity');
    var priceInput = document.getElementById('order-price');
    var statusSelect = document.getElementById('order-status');

    var formData = {
        userId: parseInt(userIdInput.value, 10),
        productName: productInput.value.trim(),
        quantity: parseInt(qtyInput.value, 10),
        price: parseFloat(priceInput.value),
        status: statusSelect.value,
    };

    var errors = validateOrderForm(formData);
    if (hasErrors(errors)) {
        showToast(Object.values(errors).join('; '), 'error');
        return;
    }

    try {
        await OrderEndpoint.createOrder(formData);
        showToast('订单创建成功', 'success');
        productInput.value = '';
        qtyInput.value = '';
        priceInput.value = '';
        await loadAllOrders();
    } catch (error) {
        showToast('创建失败: ' + formatError(error), 'error');
    }
}

async function deleteOrder(id) {
    if (!confirm('确认删除订单 #' + id + '?')) return;

    try {
        await OrderEndpoint.deleteOrder(id);
        showToast('订单 #' + id + ' 已删除', 'success');
        await loadAllOrders();
    } catch (error) {
        showToast('删除失败: ' + formatError(error), 'error');
    }
}

// ============================================================
// 页面初始化
// ============================================================

function renderStatusOptions() {
    var select = document.getElementById('order-status');
    if (!select) return;
    select.innerHTML = ORDER_STATUS_OPTIONS.map(function(opt) {
        return '<option value="' + opt.value + '">' + opt.label + '</option>';
    }).join('');
}

document.addEventListener('DOMContentLoaded', function() {
    renderStatusOptions();

    var createUserBtn = document.getElementById('btn-create-user');
    if (createUserBtn) createUserBtn.addEventListener('click', handleCreateUser);

    var createOrderBtn = document.getElementById('btn-create-order');
    if (createOrderBtn) createOrderBtn.addEventListener('click', handleCreateOrder);

    var refreshBtn = document.getElementById('btn-refresh-orders');
    if (refreshBtn) refreshBtn.addEventListener('click', loadAllOrders);

    window.getUserOrders = getUserOrders;
    window.deleteUser = deleteUser;
    window.deleteOrder = deleteOrder;
    window.loadAllOrders = loadAllOrders;

    loadUsers();
    loadAllOrders();
});
