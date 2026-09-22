/**
 * 前端入口逻辑（TypeScript）
 * 演示如何使用 Hilla 风格自动生成的类型安全客户端
 */

import { UserEndpoint } from './endpoints.js';
import { User } from './models.js';

// ============================================================
// 页面渲染逻辑
// ============================================================

/** 加载并渲染用户列表 */
async function loadUserList(): Promise<void> {
    const container = document.getElementById('user-list');
    if (!container) return;

    container.innerHTML = '<p style="color:#999">加载中...</p>';

    try {
        // 【类型安全】TypeScript 知道 users 是 User[]
        const users: User[] = await UserEndpoint.listUsers();

        if (users.length === 0) {
            container.innerHTML = '<p style="color:#999">暂无用户数据</p>';
            return;
        }

        const html = users.map(user => `
            <tr>
                <td>${user.id}</td>
                <td>${escapeHtml(user.name)}</td>
                <td>${user.age}</td>
                <td>${escapeHtml(user.email ?? '-')}</td>
                <td><button onclick="window.getUserDetail(${user.id})">查看</button></td>
            </tr>
        `).join('');

        container.innerHTML = html;
    } catch (error) {
        container.innerHTML = `<p style="color:red">加载失败: ${error}</p>`;
    }
}

/** 查看用户详情 */
async function getUserDetail(id: number): Promise<void> {
    const detailDiv = document.getElementById('user-detail');
    if (!detailDiv) return;

    detailDiv.innerHTML = '<p style="color:#999">查询中...</p>';

    try {
        // 【类型安全】TypeScript 知道 user 是 User，IDE 会提示 id/name/age/email
        const user: User = await UserEndpoint.getUserById(id);
        detailDiv.innerHTML = `
            <div style="padding:10px;background:#f0f8ff;border-radius:6px">
                <h4>用户 #${user.id} 详情</h4>
                <p><b>姓名:</b> ${escapeHtml(user.name)}</p>
                <p><b>年龄:</b> ${user.age}</p>
                <p><b>邮箱:</b> ${escapeHtml(user.email ?? '未填写')}</p>
            </div>
        `;
    } catch (error) {
        detailDiv.innerHTML = `<p style="color:red">查询失败: ${error}</p>`;
    }
}

/** 新增用户表单提交 */
async function handleAddUser(): Promise<void> {
    const nameInput = document.getElementById('input-name') as HTMLInputElement;
    const ageInput = document.getElementById('input-age') as HTMLInputElement;
    const emailInput = document.getElementById('input-email') as HTMLInputElement;

    const name = nameInput.value.trim();
    const age = parseInt(ageInput.value, 10);
    const email = emailInput.value.trim() || null;

    if (!name) { alert('请输入姓名'); return; }
    if (isNaN(age) || age < 0 || age > 150) { alert('请输入有效年龄'); return; }

    try {
        // 【类型安全】参数类型由 Omit<User, 'id'> 约束
        const newUser = await UserEndpoint.addUser({ name, age, email });
        alert(`用户添加成功! ID: ${newUser.id}`);

        // 清空表单
        nameInput.value = '';
        ageInput.value = '';
        emailInput.value = '';

        // 刷新列表
        await loadUserList();
    } catch (error) {
        alert(`添加失败: ${error}`);
    }
}

// ============================================================
// 工具函数
// ============================================================

function escapeHtml(text: string): string {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// ============================================================
// 注册全局函数（供 HTML onclick 调用）
// ============================================================

declare global {
    interface Window {
        getUserDetail: (id: number) => Promise<void>;
    }
}
window.getUserDetail = getUserDetail;

// ============================================================
// 页面加载完成后初始化
// ============================================================

document.addEventListener('DOMContentLoaded', () => {
    // 绑定表单提交
    const addBtn = document.getElementById('btn-add');
    if (addBtn) {
        addBtn.addEventListener('click', handleAddUser);
    }

    // 加载用户列表
    loadUserList();
});
