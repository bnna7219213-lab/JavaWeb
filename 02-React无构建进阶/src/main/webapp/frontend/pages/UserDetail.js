import { useState, useEffect } from 'react';
import { html } from '../utils/react-html.js';
import { request, remove } from '../utils/request.js';
import { useParams, useNavigate } from 'react-router-dom';

export default function UserDetail() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const load = async () => {
            try {
                const res = await request('api/user?id=' + id);
                if (res.code === 200) setUser(res.data);
            } catch (err) {
                console.error(err);
            } finally { setLoading(false); }
        };
        load();
    }, [id]);

    const handleDelete = async () => {
        if (!confirm('确认删除？')) return;
        const res = await remove('api/user?id=' + id);
        if (res.code === 200) navigate('/users');
        else alert(res.msg);
    };

    return html`
        <h2>用户详情</h2>
        <div class="card">
            ${loading
                ? html`<p>加载中...</p>`
                : user
                    ? html`
                        <div style="line-height:2;">
                            <p><strong>ID：</strong>${user.id}</p>
                            <p><strong>姓名：</strong>${user.name}</p>
                            <p><strong>年龄：</strong>${user.age}</p>
                            <p><strong>邮箱：</strong>${user.email || '未填写'}</p>
                        </div>
                        <div style="margin-top:16px; display:flex; gap:10px;">
                            <button onclick=${() => navigate('/users')}>返回列表</button>
                            <button class="danger" onclick=${handleDelete}>删除用户</button>
                        </div>
                    `
                    : html`<p>用户不存在</p>`
            }
        </div>
    `;
}
