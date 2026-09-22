import { useState, useEffect } from 'react';
import { html } from '../utils/react-html.js';
import { get, del } from '../utils/request.js';
import { useParams, useNavigate } from 'react-router-dom';

export default function UserDetail() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [user, setUser] = useState(null);

    useEffect(() => {
        get('api/user?id=' + id).then(r => { if (r.code === 200) setUser(r.data); });
    }, [id]);

    const handleDelete = async () => {
        if (!confirm('确认删除？')) return;
        const r = await del('api/user?id=' + id);
        if (r.code === 200) navigate('/users');
        else alert(r.msg);
    };

    return html`
        <div><h2>用户详情</h2>
        <div class="card">
            ${user ? html`
                <div style="line-height:2">
                    <p><strong>ID：</strong>${user.id}</p>
                    <p><strong>姓名：</strong>${user.name}</p>
                    <p><strong>年龄：</strong>${user.age}</p>
                    <p><strong>邮箱：</strong>${user.email || '未填'}</p>
                    <p><strong>电话：</strong>${user.phone || '未填'}</p>
                </div>
                <div style="margin-top:16px; display:flex; gap:10px;">
                    <button onclick=${() => navigate('/users')}>返回</button>
                    <button class="edit" onclick=${() => navigate('/users/' + id + '/edit')}>编辑</button>
                    <button class="danger" onclick=${handleDelete}>删除</button>
                </div>
            ` : html`<p>加载中...</p>`}
        </div></div>
    `;
}
