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
        const loadUser = async () => {
            try {
                const res = await request('api/user');
                if (res.code === 200) setUser(res.data);
            } catch (err) {
                console.error('查询失败:', err);
            } finally {
                setLoading(false);
            }
        };
        loadUser();
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('提交中...');
        try {
            const res = await post('api/user/add', {
                name: formData.name,
                age: parseInt(formData.age) || 0
            });
            if (res.code === 200) {
                setMessage('提交成功！');
                setFormData({ name: '', age: '' });
            }
        } catch (err) {
            setMessage('请求异常：' + err.message);
        }
    };

    return html`
        <div>
            <h2>React 无构建前端示例</h2>
            <div class="card">
                <h3>用户信息（GET查询）</h3>
                ${loading
                    ? html`<p>加载中...</p>`
                    : user
                        ? html`<${UserCard} user=${user} />`
                        : html`<p>暂无用户数据</p>`
                }
            </div>
            <div class="card">
                <h3>新增用户（POST提交）</h3>
                <form onSubmit=${handleSubmit}>
                    <div>
                        <label>姓名：</label>
                        <input type="text" name="name" value=${formData.name} onChange=${handleChange} required />
                    </div>
                    <div style="margin-top:8px">
                        <label>年龄：</label>
                        <input type="number" name="age" value=${formData.age} onChange=${handleChange} required />
                    </div>
                    <button type="submit" style="margin-top:12px">提交</button>
                </form>
                ${message ? html`<div class="msg">${message}</div>` : ''}
            </div>
        </div>
    `;
}
