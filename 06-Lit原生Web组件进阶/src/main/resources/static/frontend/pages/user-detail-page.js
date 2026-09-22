import { LitElement, html, css } from 'lit';
import { get } from '../utils/request.js';
import { navigate } from '../components/app-router.js';
import { showToast } from '../utils/toast.js';

/**
 * 用户详情页组件
 * 路由参数通过 properties 接收（由 app-router 注入）
 *
 * 组件通信示例：
 * - 通过 dispatchEvent 向父容器派发删除事件
 * - 父组件可通过 addEventListener 监听
 */
class UserDetailPage extends LitElement {
    static properties = {
        id: { type: Number },
        user: { type: Object },
        loading: { type: Boolean, state: true },
        routePath: { type: String, attribute: false },
        routeParams: { type: Object, attribute: false }
    };

    static styles = css`
        .breadcrumb {
            font-size: 14px;
            color: #888;
            margin-bottom: 20px;
        }
        .breadcrumb a {
            color: #667eea;
            text-decoration: none;
            cursor: pointer;
        }
        .breadcrumb a:hover { text-decoration: underline; }
        .breadcrumb span { margin: 0 6px; }

        .detail-card {
            background: #fff;
            border-radius: 16px;
            padding: 36px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        }
        .detail-header {
            display: flex;
            align-items: center;
            gap: 24px;
            margin-bottom: 28px;
            padding-bottom: 24px;
            border-bottom: 1px solid #f0f0f0;
        }
        .avatar {
            width: 80px;
            height: 80px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea, #764ba2);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 32px;
            font-weight: 700;
            flex-shrink: 0;
        }
        .detail-header-info h2 {
            font-size: 24px;
            color: #333;
            margin-bottom: 4px;
        }
        .detail-header-info .id-tag {
            font-size: 13px;
            color: #999;
        }

        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
            gap: 20px;
            margin-bottom: 28px;
        }
        .info-item {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 16px 20px;
        }
        .info-item .label {
            font-size: 12px;
            color: #999;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 6px;
        }
        .info-item .value {
            font-size: 16px;
            color: #333;
            font-weight: 500;
        }

        .actions {
            display: flex;
            gap: 12px;
            padding-top: 20px;
            border-top: 1px solid #f0f0f0;
        }
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .btn:hover { transform: translateY(-1px); }
        .btn-primary {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: #fff;
        }
        .btn-secondary {
            background: #f0f2f5;
            color: #555;
        }
        .btn-danger {
            background: #fce8e6;
            color: #c5221f;
        }

        .loading-container {
            text-align: center;
            padding: 60px;
            background: #fff;
            border-radius: 16px;
            color: #999;
        }
    `;

    constructor() {
        super();
        this.id = null;
        this.user = null;
        this.loading = false;
    }

    connectedCallback() {
        super.connectedCallback();
        // 从路由参数中获取 id
        if (this.routeParams && this.routeParams.id) {
            this.id = Number(this.routeParams.id);
        }
        if (this.id) {
            this._loadUser(this.id);
        }
    }

    async _loadUser(id) {
        this.loading = true;
        try {
            const result = await get('/user', { id });
            this.user = result.data;
        } catch (err) {
            showToast('加载失败: ' + err.message, 'error');
            navigate('/users');
        } finally {
            this.loading = false;
        }
    }

    _handleEdit() {
        navigate(`/users/${this.id}/edit`);
    }

    _handleDelete() {
        if (!confirm(`确定要删除用户 "${this.user.name}" 吗？`)) return;

        // 通过 dispatchEvent 向父级传递删除事件
        // 父组件可以 addEventListener('user-delete', ...) 来监听
        this.dispatchEvent(new CustomEvent('user-delete', {
            detail: { id: this.id, name: this.user.name },
            bubbles: true,
            composed: true
        }));

        // 同时也通过 EventBus 通知其他组件
        import('../utils/request.js').then(({ del }) => {
            del('/user', { id: this.id })
                .then(() => {
                    showToast('删除成功', 'success');
                    // 派发全局事件
                    const { eventBus, EVENTS } = window.__LIT_IMPORTS__ || {};
                    navigate('/users');
                })
                .catch(err => showToast('删除失败: ' + err.message, 'error'));
        });
    }

    /**
     * 获取用户姓名首字母（用于头像占位）
     */
    _getInitial(name) {
        return name ? name.charAt(0) : '?';
    }

    render() {
        if (this.loading) {
            return html`
                <div class="loading-container">
                    <p>加载中...</p>
                </div>
            `;
        }

        if (!this.user) {
            return html`
                <div class="loading-container">
                    <p>用户不存在</p>
                    <button class="btn btn-secondary" @click=${() => navigate('/users')} style="margin-top:16px">
                        返回列表
                    </button>
                </div>
            `;
        }

        return html`
            <div class="breadcrumb">
                <a @click=${() => navigate('/')}>首页</a>
                <span>></span>
                <a @click=${() => navigate('/users')}>用户列表</a>
                <span>></span>
                <span style="color:#555">${this.user.name}</span>
            </div>

            <div class="detail-card">
                <div class="detail-header">
                    <div class="avatar">${this._getInitial(this.user.name)}</div>
                    <div class="detail-header-info">
                        <h2>${this.user.name}</h2>
                        <span class="id-tag">用户 ID: ${this.user.id}</span>
                    </div>
                </div>

                <div class="info-grid">
                    <div class="info-item">
                        <div class="label">姓名</div>
                        <div class="value">${this.user.name}</div>
                    </div>
                    <div class="info-item">
                        <div class="label">年龄</div>
                        <div class="value">${this.user.age} 岁</div>
                    </div>
                    <div class="info-item">
                        <div class="label">邮箱</div>
                        <div class="value">${this.user.email || '未填写'}</div>
                    </div>
                    <div class="info-item">
                        <div class="label">手机号</div>
                        <div class="value">${this.user.phone || '未填写'}</div>
                    </div>
                </div>

                <div class="actions">
                    <button class="btn btn-primary" @click=${this._handleEdit}>编辑</button>
                    <button class="btn btn-danger" @click=${this._handleDelete}>删除</button>
                    <button class="btn btn-secondary" @click=${() => navigate('/users')}>返回列表</button>
                </div>
            </div>
        `;
    }
}

customElements.define('user-detail-page', UserDetailPage);
