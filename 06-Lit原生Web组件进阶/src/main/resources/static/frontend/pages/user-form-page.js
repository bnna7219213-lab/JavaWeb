import { LitElement, html, css } from 'lit';
import { get, post, put } from '../utils/request.js';
import { navigate, eventBus, EVENTS } from '../components/app-router.js';
import { showToast } from '../utils/toast.js';

/**
 * 用户表单页组件
 *
 * 功能：
 * - 新增用户 (路由 /users/new)
 * - 编辑用户 (路由 /users/:id/edit)
 *
 * 特性：
 * - 表单双向绑定模拟：通过 @input 实时更新 properties
 * - 表单校验：必填字段、年龄范围、邮箱格式
 * - 组件通信：成功后通过 EventBus 通知列表组件刷新
 */
class UserFormPage extends LitElement {
    static properties = {
        // 路由参数
        id: { type: Number },
        // 表单数据
        formData: { type: Object },
        // 校验错误
        errors: { type: Object },
        // 状态
        loading: { type: Boolean, state: true },
        submitting: { type: Boolean, state: true },
        // 路由注入 (app-router 设置)
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

        .form-card {
            background: #fff;
            border-radius: 16px;
            padding: 36px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
            max-width: 640px;
        }
        .form-card h2 {
            font-size: 22px;
            color: #333;
            margin-bottom: 28px;
            padding-bottom: 16px;
            border-bottom: 2px solid #f0f0f0;
        }

        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            font-size: 14px;
            font-weight: 600;
            color: #555;
            margin-bottom: 6px;
        }
        .form-group label .required {
            color: #ea4335;
            margin-left: 2px;
        }
        .form-group input {
            width: 100%;
            padding: 10px 14px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 14px;
            outline: none;
            transition: border-color 0.3s, box-shadow 0.3s;
        }
        .form-group input:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        .form-group input.error {
            border-color: #ea4335;
        }
        .form-group .error-msg {
            font-size: 12px;
            color: #ea4335;
            margin-top: 4px;
        }
        .form-group .hint {
            font-size: 12px;
            color: #999;
            margin-top: 4px;
        }

        .form-actions {
            display: flex;
            gap: 12px;
            margin-top: 28px;
            padding-top: 20px;
            border-top: 1px solid #f0f0f0;
        }
        .btn {
            padding: 10px 24px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .btn:hover { transform: translateY(-1px); }
        .btn:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
        }
        .btn-primary {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: #fff;
            box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
        }
        .btn-secondary {
            background: #f0f2f5;
            color: #555;
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
        this.formData = this._emptyForm();
        this.errors = {};
        this.loading = false;
        this.submitting = false;
        this.id = null;
    }

    _emptyForm() {
        return { name: '', age: '', email: '', phone: '' };
    }

    connectedCallback() {
        super.connectedCallback();
        // 判断模式：编辑模式需要加载数据
        if (this.routePath && this.routePath.includes('/edit')) {
            if (this.routeParams && this.routeParams.id) {
                this.id = Number(this.routeParams.id);
                this._loadUser(this.id);
            }
        }
    }

    async _loadUser(id) {
        this.loading = true;
        try {
            const result = await get('/user', { id });
            const user = result.data;
            this.formData = {
                name: user.name || '',
                age: user.age != null ? String(user.age) : '',
                email: user.email || '',
                phone: user.phone || ''
            };
        } catch (err) {
            showToast('加载用户失败: ' + err.message, 'error');
            navigate('/users');
        } finally {
            this.loading = false;
        }
    }

    /**
     * 表单字段变更处理 - 模拟双向绑定
     * 通过 input 事件实时更新 formData，并清除对应字段的错误
     */
    _handleInput(field, e) {
        const value = e.target.value;
        this.formData = { ...this.formData, [field]: value };
        // 清除该字段的错误提示
        if (this.errors[field]) {
            this.errors = { ...this.errors, [field]: '' };
        }
    }

    /**
     * 表单校验
     */
    _validate() {
        const errors = {};
        const { name, age, email, phone } = this.formData;

        if (!name || !name.trim()) {
            errors.name = '姓名不能为空';
        } else if (name.trim().length < 2) {
            errors.name = '姓名至少2个字符';
        }

        if (!age || age === '') {
            errors.age = '年龄不能为空';
        } else {
            const ageNum = Number(age);
            if (isNaN(ageNum) || ageNum < 1 || ageNum > 150) {
                errors.age = '请输入有效年龄 (1-150)';
            }
        }

        if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            errors.email = '邮箱格式不正确';
        }

        this.errors = errors;
        return Object.keys(errors).length === 0;
    }

    /**
     * 表单提交
     */
    async _handleSubmit(e) {
        e.preventDefault();

        if (!this._validate()) {
            showToast('请修正表单中的错误', 'error');
            return;
        }

        this.submitting = true;
        try {
            const payload = {
                name: this.formData.name.trim(),
                age: Number(this.formData.age),
                email: this.formData.email.trim() || null,
                phone: this.formData.phone.trim() || null,
            };

            if (this.id) {
                // 编辑模式
                payload.id = this.id;
                await put('/user', payload);
                eventBus.emit(EVENTS.USER_UPDATED, { user: payload });
                showToast('用户更新成功', 'success');
            } else {
                // 新增模式
                const result = await post('/user', payload);
                eventBus.emit(EVENTS.USER_CREATED, { user: result.data });
                showToast('用户创建成功', 'success');
            }

            navigate('/users');
        } catch (err) {
            showToast('提交失败: ' + err.message, 'error');
        } finally {
            this.submitting = false;
        }
    }

    _handleCancel() {
        navigate('/users');
    }

    get _isEditMode() {
        return this.id != null;
    }

    render() {
        if (this.loading) {
            return html`
                <div class="loading-container">
                    <p>加载中...</p>
                </div>
            `;
        }

        return html`
            <div class="breadcrumb">
                <a @click=${() => navigate('/')}>首页</a>
                <span>></span>
                <a @click=${() => navigate('/users')}>用户列表</a>
                <span>></span>
                <span style="color:#555">${this._isEditMode ? '编辑用户' : '新增用户'}</span>
            </div>

            <div class="form-card">
                <h2>${this._isEditMode ? '编辑用户' : '新增用户'}</h2>

                <form @submit=${this._handleSubmit}>
                    <!-- 姓名 -->
                    <div class="form-group">
                        <label>姓名 <span class="required">*</span></label>
                        <input
                            type="text"
                            placeholder="请输入姓名"
                            .value=${this.formData.name}
                            class=${this.errors.name ? 'error' : ''}
                            @input=${e => this._handleInput('name', e)}
                        >
                        ${this.errors.name ? html`<div class="error-msg">${this.errors.name}</div>` : ''}
                    </div>

                    <!-- 年龄 -->
                    <div class="form-group">
                        <label>年龄 <span class="required">*</span></label>
                        <input
                            type="number"
                            placeholder="请输入年龄"
                            min="1" max="150"
                            .value=${this.formData.age}
                            class=${this.errors.age ? 'error' : ''}
                            @input=${e => this._handleInput('age', e)}
                        >
                        ${this.errors.age ? html`<div class="error-msg">${this.errors.age}</div>` : ''}
                    </div>

                    <!-- 邮箱 -->
                    <div class="form-group">
                        <label>邮箱</label>
                        <input
                            type="email"
                            placeholder="example@domain.com"
                            .value=${this.formData.email}
                            class=${this.errors.email ? 'error' : ''}
                            @input=${e => this._handleInput('email', e)}
                        >
                        ${this.errors.email ? html`<div class="error-msg">${this.errors.email}</div>` : ''}
                        <div class="hint">选填，请输入有效邮箱格式</div>
                    </div>

                    <!-- 手机号 -->
                    <div class="form-group">
                        <label>手机号</label>
                        <input
                            type="tel"
                            placeholder="13800000000"
                            .value=${this.formData.phone}
                            @input=${e => this._handleInput('phone', e)}
                        >
                        <div class="hint">选填</div>
                    </div>

                    <!-- 操作按钮 -->
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary" ?disabled=${this.submitting}>
                            ${this.submitting ? '提交中...' : (this._isEditMode ? '保存修改' : '创建用户')}
                        </button>
                        <button type="button" class="btn btn-secondary" @click=${this._handleCancel}>取消</button>
                    </div>
                </form>
            </div>
        `;
    }
}

customElements.define('user-form-page', UserFormPage);
