/**
 * ============================================================
 * 前端应用入口 (TypeScript)
 * 演示 Hilla 类型安全全栈的完整 CRUD 应用
 * ============================================================
 */

import { UserEndpoint, OrderEndpoint, ApiException } from './endpoints.js';
import { User, Order, OrderStatus } from './models.js';
import { validateUserForm, validateOrderForm, hasErrors, ORDER_STATUS_OPTIONS } from './validation.js';
import type { ValidationErrors, UserFormData, OrderFormData } from './validation.js';

// ============================================================
// 通用工具
// ============================================================

function escapeHtml(text: string): string {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function showToast(message: string, type: 'success' | 'error' | 'info' = 'info'): void {
    const container = document.getElementById('toast-container');
    if (!container) return;
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    container.appendChild(toast);
    setTimeout(() => { toast.classList.add('toast-fade-out'); }, 2500);
    setTimeout(() => { toast.remove(); }, 3000);
}

// ============================================================
// 用户管理
// ============================================================

async function loadUsers(): Promise<void> {
    const container = document.getElementById('user-list');
    if (!container) return;

    container.innerHTML = '<tr colspan="6"><td style="text-align:center;color:#999;padding:24px">加载中...</td></tr>';

    try {
        const users = await UserEndpoint.listUsers();
        renderUserList(users);
    } catch (error) {
        container.innerHTML = `<tr><td colspan="6" style="text-align:center;color:#e74c3c;padding:24px">加载失败: ${error}</td></tr>`;
    }
}

function renderUserList(users: User[]): void {
    const container = document.getElementById('user-list');
    if (!container) return;

    if (users.length === 0) {
        container.innerHTML = '<tr><td colspan="6" style="text-align:center;color:#999">暂无用户数据</td></tr>';
        return;
    }

    container.innerHTML = users.map(user => `
        <tr>
            <td><span class="id-badge">${user.id}</span></td>
            <td>${escapeHtml(user.name)}</td>
            <td>${user.age}</td>
            <td>${escapeHtml(user.email ?? '<span style="color:#bbb">未填写</span>')}</td>
            <td>
                <button class="btn-xs btn-info" onclick="window.getUserOrders(${user.id})">查看订单</button>
            </td>
            <td>
                <button class="btn-xs btn-danger" onclick="window.deleteUser(${user.id})">删除</button>
            </td>
        </tr>
    `).join('');

    // 更新用户总数
    const countEl = document.getElementById('user-count');
    if (countEl) countEl.textContent = String(users.length);
}

async function handleCreateUser(): Promise<void> {
    const nameInput = document.getElementById('user-name') as HTMLInputElement;
    const ageInput = document.getElementById('user-age') as HTMLInputElement;
    const emailInput = document.getElementById('user-email') as HTMLInputElement;

    const formData: Partial<UserFormData> = {
        name: nameInput.value.trim(),
        age: parseInt(ageInput.value, 10),
        email: emailInput.value.trim() || null,
    };

    // 【类型安全验证】TypeScript 确保字段名与 UserFormData 一致
    const errors = validateUserForm(formData);
    if (hasErrors(errors)) {
        showToast(Object.values(errors).join('; '), 'error');
        return;
    }

    try {
        await UserEndpoint.createUser(formData as UserFormData);
        showToast(`用户 "${formData.name}" 创建成功`, 'success');
        nameInput.value = '';
        ageInput.value = '';
        emailInput.value = '';
        await loadUsers();
    } catch (error) {
        if (error instanceof ApiException) {
            showToast(`创建失败: ${error.message}`, 'error');
        } else {
            showToast(`创建失败: ${error}`, 'error');
        }
    }
}

async function deleteUser(id: number): Promise<void> {
    if (!confirm(`确认删除用户 #${id}?`)) return;

    try {
        await UserEndpoint.deleteUser(id);
        showToast(`用户 #${id} 已删除`, 'success');
        await loadUsers();
    } catch (error) {
        if (error instanceof ApiException) {
            showToast(`删除失败: ${error.message}`, 'error');
        } else {
            showToast(`删除失败: ${error}`, 'error');
        }
    }
}

// ============================================================
// 订单管理
// ============================================================

async function getUserOrders(userId: number): Promise<void> {
    const container = document.getElementById('order-list');
    if (!container) return;

    container.innerHTML = '<tr><td colspan="7" style="text-align:center;color:#999;padding:24px">加载中...</td></tr>';

    try {
        const orders = await OrderEndpoint.listOrdersByUser(userId);
        renderOrderList(orders, userId);
        // 滚动到订单区域
        document.getElementById('order-section')?.scrollIntoView({ behavior: 'smooth' });
    } catch (error) {
        container.innerHTML = `<tr><td colspan="7" style="text-align:center;color:#e74c3c;padding:24px">加载失败: ${error}</td></tr>`;
    }
}

async function loadAllOrders(): Promise<void> {
    const container = document.getElementById('order-list');
    if (!container) return;

    container.innerHTML = '<tr><td colspan="7" style="text-align:center;color:#999;padding:24px">加载中...</td></tr>';

    try {
        const orders = await OrderEndpoint.listOrders();
        renderOrderList(orders, null);
    } catch (error) {
        container.innerHTML = `<tr><td colspan="7" style="text-align:center;color:#e74c3c;padding:24px">加载失败: ${error}</td></tr>`;
    }
}

function renderOrderList(orders: Order[], userId: number | null): void {
    const container = document.getElementById('order-list');
    if (!container) return;

    const subtitle = document.getElementById('order-subtitle');
    if (subtitle) {
        subtitle.textContent = userId !== null ? `用户 #${userId} 的订单 (${orders.length}条)` : `全部订单 (${orders.length}条)`;
    }

    if (orders.length === 0) {
        container.innerHTML = '<tr><td colspan="7" style="text-align:center;color:#999">暂无订单数据</td></tr>';
        return;
    }

    container.innerHTML = orders.map(order => `
        <tr>
            <td>${order.id}</td>
            <td>User #${order.userId}</td>
            <td>${escapeHtml(order.productName)}</td>
            <td>${order.quantity}</td>
            <td>¥${order.price.toFixed(2)}</td>
            <td><span class="status-badge status-${order.status.toLowerCase()}">${getStatusLabel(order.status)}</span></td>
            <td>
                <button class="btn-xs btn-danger" onclick="window.deleteOrder(${order.id})">删除</button>
            </td>
        </tr>
    `).join('');
}

function getStatusLabel(status: OrderStatus): string {
    const map: Record<OrderStatus, string> = {
        PENDING: '待支付', PAID: '已支付', SHIPPED: '已发货',
        DELIVERED: '已送达', CANCELLED: '已取消'
    };
    return map[status] || status;
}

async function handleCreateOrder(): Promise<void> {
    const userIdInput = document.getElementById('order-userId') as HTMLInputElement;
    const productInput = document.getElementById('order-product') as HTMLInputElement;
    const qtyInput = document.getElementById('order-quantity') as HTMLInputElement;
    const priceInput = document.getElementById('order-price') as HTMLInputElement;
    const statusSelect = document.getElementById('order-status') as HTMLSelectElement;

    const formData: Partial<OrderFormData> = {
        userId: parseInt(userIdInput.value, 10),
        productName: productInput.value.trim(),
        quantity: parseInt(qtyInput.value, 10),
        price: parseFloat(priceInput.value),
        status: statusSelect.value as OrderStatus,
    };

    const errors = validateOrderForm(formData);
    if (hasErrors(errors)) {
        showToast(Object.values(errors).join('; '), 'error');
        return;
    }

    try {
        await OrderEndpoint.createOrder(formData as OrderFormData);
        showToast('订单创建成功', 'success');
        productInput.value = '';
        qtyInput.value = '';
        priceInput.value = '';
        await loadAllOrders();
    } catch (error) {
        if (error instanceof ApiException) {
            showToast(`创建失败: ${error.message}`, 'error');
        } else {
            showToast(`创建失败: ${error}`, 'error');
        }
    }
}

async function deleteOrder(id: number): Promise<void> {
    if (!confirm(`确认删除订单 #${id}?`)) return;

    try {
        await OrderEndpoint.deleteOrder(id);
        showToast(`订单 #${id} 已删除`, 'success');
        await loadAllOrders();
    } catch (error) {
        if (error instanceof ApiException) {
            showToast(`删除失败: ${error.message}`, 'error');
        } else {
            showToast(`删除失败: ${error}`, 'error');
        }
    }
}

// ============================================================
// 注册全局函数（供 HTML onclick 调用）
// ============================================================

declare global {
    interface Window {
        getUserOrders: (userId: number) => Promise<void>;
        deleteUser: (id: number) => Promise<void>;
        deleteOrder: (id: number) => Promise<void>;
        loadAllOrders: () => Promise<void>;
    }
}

// ============================================================
// 页面初始化
// ============================================================

/** 生成订单状态下拉选项 */
function renderStatusOptions(): void {
    const select = document.getElementById('order-status');
    if (!select) return;
    select.innerHTML = ORDER_STATUS_OPTIONS
        .map(opt => `<option value="${opt.value}">${opt.label}</option>`)
        .join('');
}

document.addEventListener('DOMContentLoaded', () => {
    renderStatusOptions();

    // 绑定事件
    document.getElementById('btn-create-user')?.addEventListener('click', handleCreateUser);
    document.getElementById('btn-create-order')?.addEventListener('click', handleCreateOrder);
    document.getElementById('btn-refresh-orders')?.addEventListener('click', loadAllOrders);

    // 暴露到全局
    window.getUserOrders = getUserOrders;
    window.deleteUser = deleteUser;
    window.deleteOrder = deleteOrder;
    window.loadAllOrders = loadAllOrders;

    // 加载初始数据
    loadUsers();
    loadAllOrders();
});
