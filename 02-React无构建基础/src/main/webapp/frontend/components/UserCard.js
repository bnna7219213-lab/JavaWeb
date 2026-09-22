import { html } from '../utils/react-html.js';

export default function UserCard({ user }) {
    return html`
        <div style="border: 1px solid #e5e7eb; padding: 16px; border-radius: 8px;">
            <h3 style="color:#61dafb">用户信息</h3>
            <p>用户ID：${user.id}</p>
            <p>姓名：${user.name}</p>
            <p>年龄：${user.age}</p>
        </div>
    `;
}
