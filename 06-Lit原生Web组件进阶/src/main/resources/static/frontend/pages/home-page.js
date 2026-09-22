import { LitElement, html, css } from 'lit';
import { navigate } from '../components/app-router.js';

/**
 * 首页组件 — 欢迎页与功能介绍
 */
class HomePage extends LitElement {
    static styles = css`
        .hero {
            text-align: center;
            padding: 60px 20px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 16px;
            color: #fff;
            margin-bottom: 32px;
        }
        .hero h1 {
            font-size: 32px;
            font-weight: 800;
            margin-bottom: 12px;
        }
        .hero p {
            font-size: 16px;
            opacity: 0.9;
            line-height: 1.6;
        }
        .actions {
            margin-top: 28px;
            display: flex;
            gap: 14px;
            justify-content: center;
        }
        .btn {
            display: inline-block;
            padding: 12px 28px;
            border-radius: 8px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s, box-shadow 0.2s;
            border: none;
            text-decoration: none;
        }
        .btn-primary {
            background: #fff;
            color: #667eea;
        }
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
        }
        .btn-outline {
            background: transparent;
            color: #fff;
            border: 2px solid rgba(255, 255, 255, 0.6);
        }
        .btn-outline:hover {
            background: rgba(255, 255, 255, 0.1);
        }

        .features {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
            gap: 20px;
        }
        .feature-card {
            background: #fff;
            border-radius: 12px;
            padding: 28px;
            text-align: center;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .feature-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
        }
        .feature-card h3 {
            font-size: 17px;
            color: #333;
            margin: 8px 0;
        }
        .feature-card p {
            font-size: 14px;
            color: #777;
            line-height: 1.6;
        }

        .tech-stack {
            margin-top: 32px;
            background: #fff;
            border-radius: 12px;
            padding: 28px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        }
        .tech-stack h3 {
            font-size: 17px;
            color: #333;
            margin-bottom: 16px;
        }
        .tech-items {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
        }
        .tech-tag {
            display: inline-block;
            background: #f0f2f5;
            color: #555;
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 13px;
            font-weight: 500;
        }
    `;

    render() {
        return html`
            <div class="hero">
                <h1>Lit 原生 Web 组件 - 进阶版</h1>
                <p>
                    基于 Web Components 标准的完整 CRUD 系统<br>
                    零构建、路由、多组件通信、表单双向绑定
                </p>
                <div class="actions">
                    <button class="btn btn-primary" @click=${() => navigate('/users')}>
                        进入用户管理
                    </button>
                    <button class="btn btn-outline" @click=${() => navigate('/users/new')}>
                        新增用户
                    </button>
                </div>
            </div>

            <div class="features">
                <div class="feature-card">
                    <h3>自定义元素</h3>
                    <p>使用 LitElement 创建可复用自定义组件，Shadow DOM 样式隔离</p>
                </div>
                <div class="feature-card">
                    <h3>零构建方案</h3>
                    <p>ESM CDN + Import Maps 管理依赖，无需 webpack/vite</p>
                </div>
                <div class="feature-card">
                    <h3>前端路由</h3>
                    <p>Hash 路由 + hashchange 监听，组件动态切换</p>
                </div>
                <div class="feature-card">
                    <h3>组件通信</h3>
                    <p>dispatchEvent + EventBus 实现组件间解耦通信</p>
                </div>
            </div>

            <div class="tech-stack">
                <h3>技术栈</h3>
                <div class="tech-items">
                    <span class="tech-tag">JDK 21</span>
                    <span class="tech-tag">Spring Boot 3.3.4</span>
                    <span class="tech-tag">Lit 3.1.0</span>
                    <span class="tech-tag">Web Components</span>
                    <span class="tech-tag">Import Maps</span>
                    <span class="tech-tag">ESM CDN</span>
                    <span class="tech-tag">Shadow DOM</span>
                    <span class="tech-tag">Hash Router</span>
                </div>
            </div>
        `;
    }
}

customElements.define('home-page', HomePage);
