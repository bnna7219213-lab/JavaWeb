import { useState, useEffect } from 'react';
import { html } from '../utils/react-html.js';
import { get, post, put } from '../utils/request.js';
import { useParams, useNavigate } from 'react-router-dom';

export default function UserForm() {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEdit = !!id;
    const [form, setForm] = useState({ name: '', age: '', email: '', phone: '' });
    const [msg, setMsg] = useState(null);

    useEffect(() => {
        if (isEdit) {
            get('api/user?id=' + id).then(r => {
                if (r.code === 200) setForm({ name: r.data.name, age: r.data.age, email: r.data.email || '', phone: r.data.phone || '' });
            });
        }
    }, [id]);

    const handleChange = e => setForm(p => ({ ...p, [e.target.name]: e.target.value }));

    const handleSubmit = async (e) => {
        e.preventDefault();
        const payload = { ...form, age: parseInt(form.age) || 0 };
        if (isEdit) payload.id = parseInt(id);
        const fn = isEdit ? put : post;
        const res = await fn('api/user', payload);
        if (res.code === 200) {
            setMsg({ type: 'ok', text: (isEdit ? '更新' : '创建') + '成功！' });
            if (!isEdit) setForm({ name: '', age: '', email: '', phone: '' });
        } else {
            setMsg({ type: 'err', text: res.msg });
        }
    };

    return html`
        <div><h2>${isEdit ? '编辑用户' : '新增用户'}</h2>
        <div class="card">
            <form onSubmit=${handleSubmit}>
                <div class="form-row"><label>姓名*</label><input name="name" value=${form.name} onChange=${handleChange} required /></div>
                <div class="form-row"><label>年龄*</label><input name="age" type="number" value=${form.age} onChange=${handleChange} required /></div>
                <div class="form-row"><label>邮箱</label><input name="email" type="email" value=${form.email} onChange=${handleChange} /></div>
                <div class="form-row"><label>电话</label><input name="phone" value=${form.phone} onChange=${handleChange} /></div>
                <div style="margin-top:12px; display:flex; gap:10px;">
                    <button type="submit">${isEdit ? '保存修改' : '创建用户'}</button>
                    <button type="button" class="danger" onclick=${() => navigate('/users')}>取消</button>
                </div>
            </form>
            ${msg ? html`<div class="msg ${msg.type}">${msg.text}</div>` : ''}
        </div></div>
    `;
}
