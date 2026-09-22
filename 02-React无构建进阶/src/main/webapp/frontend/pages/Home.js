import { html } from '../utils/react-html.js';

export default function Home() {
    return html`
        <h2>React 无构建进阶版</h2>
        <div class="card">
            <h3>架构特点</h3>
            <ul style="padding-left: 20px; line-height: 1.8;">
                <li><strong>React Router HashRouter</strong> — 多页面路由，页面切换无需后端配合</li>
                <li><strong>Service 分层</strong> — Servlet → Service → Entity 三层解耦</li>
                <li><strong>统一响应封装</strong> — code + msg + data 标准格式</li>
                <li><strong>CORS 跨域</strong> — 独立过滤器支持跨域请求</li>
                <li><strong>RESTful API</strong> — GET/POST/DELETE 标准接口</li>
                <li><strong>零构建</strong> — 只依赖浏览器原生 ESM + CDN，无需 Node.js</li>
            </ul>
        </div>
        <div class="card" style="background: #e8f5e9; border-left: 4px solid #4caf50;">
            <p>本文档端可独立部署在任意静态资源服务器（Nginx/Apache），后端接口可部署在不同地址，只需修改 CORS 配置即可。</p>
        </div>
    `;
}
