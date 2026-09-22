import { LitElement, html, css } from 'lit';

/**
 * user-card 组件
 *
 * 最简单的 Lit 原生 Web 组件示例：
 * - 通过 properties 声明响应式属性
 * - 使用 static styles 定义 Shadow DOM 样式
 * - render() 函数返回模板
 *
 * 用法: <user-card .user={...}></user-card>
 */
class UserCard extends LitElement {
    // 声明响应式属性
    static properties = {
        user: { type: Object },
        loading: { type: Boolean, state: true }
    };

    static styles = css`
        :host {
            display: block;
        }
        .card {
            background: #fff;
            border: 1px solid #e8eaed;
            border-radius: 12px;
            padding: 20px;
            transition: box-shadow 0.3s ease, transform 0.2s ease;
            position: relative;
            overflow: hidden;
        }
        .card:hover {
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.15);
            transform: translateY(-2px);
        }
        .card-header {
            display: flex;
            align-items: center;
            gap: 14px;
            margin-bottom: 14px;
        }
        .avatar {
            width: 52px;
            height: 52px;
            border-radius: 50%;
            object-fit: cover;
            border: 3px solid #f0f0f0;
        }
        .name {
            font-size: 18px;
            font-weight: 700;
            color: #333;
        }
        .user-id {
            font-size: 12px;
            color: #999;
            margin-top: 2px;
        }
        .info-row {
            display: flex;
            align-items: center;
            gap: 8px;
            margin: 6px 0;
            font-size: 14px;
            color: #555;
        }
        .info-label {
            display: inline-block;
            width: 50px;
            color: #888;
            font-size: 13px;
        }
        .badge {
            display: inline-block;
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: #fff;
            font-size: 12px;
            padding: 2px 10px;
            border-radius: 20px;
            margin-left: auto;
        }
        .empty {
            text-align: center;
            padding: 30px;
            color: #aaa;
            font-size: 14px;
        }
    `;

    constructor() {
        super();
        this.user = null;
        this.loading = false;
    }

    render() {
        if (this.loading) {
            return html`<div class="card"><div class="empty">加载中...</div></div>`;
        }
        if (!this.user) {
            return html`<div class="card"><div class="empty">暂无用户数据</div></div>`;
        }
        return html`
            <div class="card">
                <div class="card-header">
                    <img class="avatar" src="${this.user.avatar || ''}" alt="头像">
                    <div>
                        <div class="name">${this.user.name}</div>
                        <div class="user-id">ID: ${this.user.id}</div>
                    </div>
                    <span class="badge">用户</span>
                </div>
                <div class="info-row">
                    <span class="info-label">年龄</span>
                    <span>${this.user.age} 岁</span>
                </div>
                <div class="info-row">
                    <span class="info-label">邮箱</span>
                    <span>${this.user.email || '未设置'}</span>
                </div>
            </div>
        `;
    }
}

// 注册自定义元素
customElements.define('user-card', UserCard);
