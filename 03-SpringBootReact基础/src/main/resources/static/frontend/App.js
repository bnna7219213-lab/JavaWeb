import { useState, useEffect } from 'react';
import { html } from './utils/react-html.js';
import UserCard from './components/UserCard.js';
import { request, post } from './utils/request.js';

export default function App() {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [formData, setFormData] = useState({ name: '', age: '' });
    const [message, setMessage] = useState('');

    useEffect(() => {
        request('api/user').then(res => {
            if (res.code === 200) setUser(res.data);
        }).finally(() => setLoading(false));
    }, []);

    const handleChange = (e) => {
        setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('提交中...');
        const res = await post('api/user/add', {
            name: formData.name, age: parseInt(formData.age) || 0
        });
        setMessage(res.code === 200 ? '提交成功！' : '失败');
        if (res.code === 200) setFormData({ name: '', age: '' });
    };

    return html`
        <div>
            <h2>Spring Boot + React 无构建</h2>
            <div class="card">
                <h3>用户信息</h3>
                ${loading ? html`<p>加载中...</p>` : user ? html`<${UserCard} user=${user} />` : html`<p>暂无数据</p>`}
            </div>
            <div class="card">
                <h3>新增用户</h3>
                <form onSubmit=${handleSubmit}>
                    <input name="name" value=${formData.name} onChange=${handleChange} placeholder="姓名" required />
                    <input name="age" type="number" value=${formData.age} onChange=${handleChange} placeholder="年龄" required />
                    <button type="submit">提交</button>
                </form>
                ${message ? html`<div class="msg">${message}</div>` : ''}
            </div>
        </div>
    `;
}
