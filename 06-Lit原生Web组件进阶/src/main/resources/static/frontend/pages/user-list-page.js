import { LitElement, html, css } from 'lit';
import { get, del } from '../utils/request.js';
import { navigate, eventBus, EVENTS } from '../components/app-router.js';
import { showToast } from '../utils/toast.js';

/**
 * 用户列表页组件
 * 功能：展示用户列表、搜索、删除
 * 组件通信：通过 dispatchEvent 向父级传递操作事件
 */
class UserListPage extends LitElement {
    static properties = {
        users: { type: Array },
        searchKeyword: { type: String },
        loading: { type: Boolean, state: true }
    };

    static styles = css`
        .page-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 24px;
            flex-wrap: wrap;
            gap: 12px;
        }
        .page-header h2 {
            font-size: 22px;
            color: #333;
        }
        .search-bar {
            display: flex;
            gap: 8px;
            align-items: center;
        }
        .search-bar input {
            padding: 10px 14px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 14px;
            outline: none;
            width: 240px;
            transition: border-color 0.3s;
        }
        .search-bar input:focus { border-color: #667eea; }
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
            box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
        }
        .btn-danger {
            background: #fce8e6;
            color: #c5221f;
            padding: 6px 14px;
            font-size: 13px;
        }
        .btn-danger:hover { background: #f8d7da; }
        .btn-detail {
            background: #e8f0fe;
            color: #1a73e8;
            padding: 6px 14px;
            font-size: 13px;
        }
        .btn-detail:hover { background: #d2e3fc; }
        .btn-edit {
            background: #fef7e0;
            color: #b06000;
            padding: 6px 14px;
            font-size: 13px;
        }
        .btn-edit:hover { background: #feefc3; }

        .table-container {
            background: #fff;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
            overflow: hidden;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th {
            background: #f8f9fa;
            padding: 14px 16px;
            text-align: left;
            font-size: 13px;
            font-weight: 600;
            color: #5f6368;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        td {
            padding: 14px 16px;
            border-bottom: 1px solid #f0f0f0;
            font-size: 14px;
            color: #333;
        }
        tr:hover td { background: #fafbfc; }
        .actions-cell {
            display: flex;
            gap: 6px;
        }
        .loading-row td {
            text-align: center;
            padding: 40px;
            color: #999;
        }
        .empty-row td {
            text-align: center;
            padding: 40px;
            color: #999;
            font-size: 15px;
        }
        .user-name {
            font-weight: 600;
            color: #333;
        }
        .user-email {
            color: #888;
            font-size: 13px;
        }
    `;

    constructor() {
        super();
        this.users = [];
        this.searchKeyword = '';
        this.loading = false;
    }

    connectedCallback() {
        super.connectedCallback();
        this._loadUsers();

        // 监听子组件触发的操作事件（通过 EventBus）
        this._unsubCreate = eventBus.on(EVENTS.USER_CREATED, () => {
            showToast('用户创建成功', 'success');
            this._loadUsers();
        });
        this._unsubUpdate = eventBus.on(EVENTS.USER_UPDATED, () => {
            showToast('用户更新成功', 'success');
            this._loadUsers();
        });
        this._unsubDelete = eventBus.on(EVENTS.USER_DELETED, () => {
            showToast('用户删除成功', 'success');
            this._loadUsers();
        });
    }

    disconnectedCallback() {
        super.disconnectedCallback();
        this._unsubCreate?.();
        this._unsubUpdate?.();
        this._unsubDelete?.();
    }

    async _loadUsers() {
        this.loading = true;
        try {
            const params = this.searchKeyword ? { search: this.searchKeyword } : {};
            const result = await get('/user/list', params);
            this.users = result.data || [];
        } catch (err) {
            showToast('加载失败: ' + err.message, 'error');
        } finally {
            this.loading = false;
        }
    }

    _handleSearch() {
        this._loadUsers();
    }

    _handleSearchInput(e) {
        this.searchKeyword = e.target.value;
        // 防抖搜索
        clearTimeout(this._searchTimer);
        this._searchTimer = setTimeout(() => this._loadUsers(), 400);
    }

    _handleDelete(id, name) {
        if (!confirm(`确定要删除用户 "${name}" 吗？`)) return;

        del('/user', { id })
            .then(() => {
                eventBus.emit(EVENTS.USER_DELETED, { id });
            })
            .catch(err => {
                showToast('删除失败: ' + err.message, 'error');
            });
    }

    _handleView(id) {
        navigate(`/users/${id}`);
    }

    _handleEdit(id) {
        navigate(`/users/${id}/edit`);
    }

    render() {
        return html`
            <div class="page-header">
                <h2>用户列表</h2>
                <div class="search-bar">
                    <input
                        type="text"
                        placeholder="搜索用户名或邮箱..."
                        .value=${this.searchKeyword}
                        @input=${this._handleSearchInput}
                        @keydown=${e => e.key === 'Enter' && this._handleSearch()}
                    >
                    <button class="btn btn-primary" @click=${() => navigate('/users/new')}>
                        + 新增用户
                    </button>
                </div>
            </div>

            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>用户名</th>
                            <th>年龄</th>
                            <th>邮箱</th>
                            <th>手机号</th>
                            <th style="width:220px">操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${this.loading
                            ? html`<tr class="loading-row"><td colspan="6">加载中...</td></tr>`
                            : this.users.length === 0
                                ? html`<tr class="empty-row"><td colspan="6">暂无数据</td></tr>`
                                : this.users.map(user => html`
                                    <tr>
                                        <td>${user.id}</td>
                                        <td>
                                            <div class="user-name">${user.name}</div>
                                        </td>
                                        <td>${user.age}</td>
                                        <td class="user-email">${user.email || '-'}</td>
                                        <td>${user.phone || '-'}</td>
                                        <td>
                                            <div class="actions-cell">
                                                <button class="btn btn-detail" @click=${() => this._handleView(user.id)}>详情</button>
                                                <button class="btn btn-edit" @click=${() => this._handleEdit(user.id)}>编辑</button>
                                                <button class="btn btn-danger" @click=${() => this._handleDelete(user.id, user.name)}>删除</button>
                                            </div>
                                        </td>
                                    </tr>
                                `)
                        }
                    </tbody>
                </table>
            </div>
        `;
    }
}

customElements.define('user-list-page', UserListPage);
