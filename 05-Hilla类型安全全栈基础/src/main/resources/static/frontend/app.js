/**
 * 前端入口逻辑（编译后版本）
 * 演示 Hilla 风格类型安全客户端的实际调用
 */

import { UserEndpoint } from './endpoints.js';

/** 加载并渲染用户列表 */
async function loadUserList() {
    const container = document.getElementById('user-list');
    if (!container) return;

    container.innerHTML = '<p style="color:#999">加载中...</p>';

    try {
        const users = await UserEndpoint.listUsers();

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
async function getUserDetail(id) {
    const detailDiv = document.getElementById('user-detail');
    if (!detailDiv) return;

    detailDiv.innerHTML = '<p style="color:#999">查询中...</p>';

    try {
        const user = await UserEndpoint.getUserById(id);
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
async function handleAddUser() {
    const nameInput = document.getElementById('input-name');
    const ageInput = document.getElementById('input-age');
    const emailInput = document.getElementById('input-email');

    const name = nameInput.value.trim();
    const age = parseInt(ageInput.value, 10);
    const email = emailInput.value.trim() || null;

    if (!name) { alert('请输入姓名'); return; }
    if (isNaN(age) || age < 0 || age > 150) { alert('请输入有效年龄'); return; }

    try {
        const newUser = await UserEndpoint.addUser({ name, age, email });
        alert(`用户添加成功! ID: ${newUser.id}`);

        nameInput.value = '';
        ageInput.value = '';
        emailInput.value = '';

        await loadUserList();
    } catch (error) {
        alert(`添加失败: ${error}`);
    }
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

window.getUserDetail = getUserDetail;

document.addEventListener('DOMContentLoaded', () => {
    const addBtn = document.getElementById('btn-add');
    if (addBtn) {
        addBtn.addEventListener('click', handleAddUser);
    }
    loadUserList();
});
