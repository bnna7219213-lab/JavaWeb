import { useState } from 'react';
import { html } from '../utils/react-html.js';
import { post } from '../utils/request.js';
import { useNavigate } from 'react-router-dom';

export default function UserCreate() {
    const navigate = useNavigate();
    const [form, setForm] = useState({ name: '', age: '', email: '' });
    const [msg, setMsg] = useState(null);

    const handleChange = (e) => {
        setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMsg(null);
        try {
            const res = await post('api/user', {
                name: form.name,
                age: parseInt(form.age) || 0,
                email: form.email
            });
            if (res.code === 200) {
                setMsg({ type: 'ok', text: '创建成功！用户ID: ' + res.data.id });
                setForm({ name: '', age: '', email: '' });
            } else {
                setMsg({ type: 'err', text: res.msg });
            }
        } catch (err) {
            setMsg({ type: 'err', text: '请求异常: ' + err.message });
        }
    };

    return html`
        <h2>新增用户</h2>
        <div class="card">
            <form onSubmit=${handleSubmit}>
                <div class="form-row">
                    <label>姓名：</label>
                    <input name="name" value=${form.name} onChange=${handleChange} required />
                </div>
                <div class="form-row">
                    <label>年龄：</label>
                    <input name="age" type="number" value=${form.age} onChange=${handleChange} required />
                </div>
                <div class="form-row">
                    <label>邮箱：</label>
                    <input name="email" type="email" value=${form.email} onChange=${handleChange} />
                </div>
                <div style="margin-top:12px; display:flex; gap:10px;">
                    <button type="submit">创建</button>
                    <button type="button" class="danger" onclick=${() => navigate('/users')}>取消</button>
                </div>
            </form>
            ${msg ? html`<div class="msg ${msg.type}">${msg.text}</div>` : ''}
        </div>
    `;
}
