import { html } from '../utils/react-html.js';

export default function Home() {
    return html`
        <div>
            <h2>Spring Boot + React 无构建进阶版</h2>
            <div class="card">
                <h3>架构特点</h3>
                <ul style="padding-left:20px; line-height:2;">
                    <li><strong>Spring Boot 3</strong> + <strong>嵌入式Tomcat</strong> — 一键启动，无需web.xml</li>
                    <li><strong>@RestController</strong> — 注解驱动RESTful API</li>
                    <li><strong>Service层分离</strong> — @Service组件，依赖注入</li>
                    <li><strong>CORS统一配置</strong> — WebMvcConfigurer</li>
                    <li><strong>React Router</strong> — HashRouter多页面</li>
                    <li><strong>完整CRUD</strong> — GET/POST/PUT/DELETE全覆盖</li>
                </ul>
            </div>
            <div class="card">
                <h3>API接口列表</h3>
                <table>
                    <thead><tr><th>方法</th><th>路径</th><th>说明</th></tr></thead>
                    <tbody>
                        <tr><td>GET</td><td>/api/user?id=1</td><td>查询单个用户</td></tr>
                        <tr><td>POST</td><td>/api/user</td><td>新增用户</td></tr>
                        <tr><td>PUT</td><td>/api/user</td><td>更新用户</td></tr>
                        <tr><td>DELETE</td><td>/api/user?id=1</td><td>删除用户</td></tr>
                        <tr><td>GET</td><td>/api/user/list</td><td>查询全部</td></tr>
                        <tr><td>GET</td><td>/api/user/list?search=张</td><td>搜索用户</td></tr>
                    </tbody>
                </table>
            </div>
        </div>
    `;
}
