/**
 * 基础版入口文件
 * 演示最简单的 Lit 自定义组件使用：
 * - 后端获取数据，设置到组件属性
 * - 通过 fetch + Lit 响应式属性更新视图
 */

import './components/user-card.js';
import { get, post } from './utils/request.js';

// ==================== 查询单个用户 ====================

const userCard = document.getElementById('userCard');
const userIdInput = document.getElementById('userIdInput');
const btnQuery = document.getElementById('btnQuery');

async function queryUser() {
    const id = userIdInput.value;
    if (!id) {
        alert('请输入用户 ID');
        return;
    }

    userCard.loading = true;

    try {
        const result = await get(`/api/user?id=${id}`);
        if (result.code === 200) {
            userCard.user = result.data;
        } else {
            userCard.user = null;
            alert(result.msg);
        }
    } catch (err) {
        alert('请求失败：' + err.message);
    } finally {
        userCard.loading = false;
    }
}

btnQuery.addEventListener('click', queryUser);
userIdInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') queryUser();
});

// 页面加载时自动查询默认用户
queryUser();

// ==================== 加载全部用户 ====================

const btnLoadAll = document.getElementById('btnLoadAll');
const cardGrid = document.getElementById('cardGrid');

btnLoadAll.addEventListener('click', async () => {
    cardGrid.innerHTML = '<div class="loading" style="grid-column:1/-1">加载中...</div>';

    try {
        const result = await get('/api/user/list');
        if (result.code === 200 && Array.isArray(result.data)) {
            renderUserList(result.data);
        }
    } catch (err) {
        cardGrid.innerHTML = `<div class="error" style="grid-column:1/-1">加载失败：${err.message}</div>`;
    }
});

/**
 * 将用户列表渲染为多个 user-card 组件
 */
function renderUserList(users) {
    cardGrid.innerHTML = '';
    users.forEach(user => {
        const card = document.createElement('user-card');
        card.user = user;  // 直接设置属性，Lit 会自动触发重渲染
        cardGrid.appendChild(card);
    });
}
