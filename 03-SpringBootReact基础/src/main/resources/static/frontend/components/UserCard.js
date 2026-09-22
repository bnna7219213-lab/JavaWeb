import { html } from '../utils/react-html.js';

export default function UserCard({ user }) {
    return html`
        <div style="padding:12px; background:#fff; border-radius:6px; border:1px solid #eee;">
            <p>ID: ${user.id}　姓名: ${user.name}　年龄: ${user.age}</p>
        </div>
    `;
}
