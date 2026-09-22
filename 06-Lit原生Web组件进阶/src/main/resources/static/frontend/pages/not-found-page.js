import { LitElement, html, css } from 'lit';
import { navigate } from '../components/app-router.js';

/**
 * 404 页面组件
 */
class NotFoundPage extends LitElement {
    static styles = css`
        .not-found {
            text-align: center;
            padding: 80px 20px;
        }
        .error-code {
            font-size: 80px;
            font-weight: 800;
            color: #667eea;
            line-height: 1;
        }
        .message {
            font-size: 20px;
            color: #333;
            margin: 16px 0 8px;
        }
        .desc {
            color: #888;
            font-size: 15px;
            margin-bottom: 28px;
        }
        .back-btn {
            display: inline-block;
            padding: 10px 24px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: #fff;
            border: none;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
        }
    `;

    render() {
        return html`
            <div class="not-found">
                <div class="error-code">404</div>
                <div class="message">页面未找到</div>
                <div class="desc">您访问的路径不存在</div>
                <button class="back-btn" @click=${() => navigate('/')}>返回首页</button>
            </div>
        `;
    }
}

customElements.define('not-found-page', NotFoundPage);
