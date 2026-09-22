import { useState, useEffect } from 'react';
import { html } from '../utils/react-html.js';
import { get, del } from '../utils/request.js';
import { useNavigate } from 'react-router-dom';

export default function UserList() {
    const [users, setUsers] = useState([]);
    const [search, setSearch] = useState('');
    const navigate = useNavigate();

    const load = async (key) => {
        const url = key ? 'api/user/list?search=' + encodeURIComponent(key) : 'api/user/list';
        const res = await get(url);
        if (res.code === 200) setUsers(res.data);
    };

    useEffect(() => { load(); }, []);

    const handleDelete = async (id) => {
        if (!confirm('确认删除？')) return;
        const res = await del('api/user?id=' + id);
        if (res.code === 200) load(search);
        else alert(res.msg);
    };

    return html`
        <div>
            <h2>用户列表</h2>
            <div class="card">
                <form onSubmit=${e => { e.preventDefault(); load(search); }} class="form-row">
                    <input placeholder="搜索姓名或邮箱..." value=${search} onChange=${e => setSearch(e.target.value)} />
                    <button type="submit">搜索</button>
                    <button type="button" onclick=${() => { setSearch(''); load(''); }}>重置</button>
                </form>
            </div>
            <div class="card">
                <table>
                    <thead><tr><th>ID</th><th>姓名</th><th>年龄</th><th>邮箱</th><th>电话</th><th>操作</th></tr></thead>
                    <tbody>
                        ${users.length === 0
                            ? html`<tr><td colspan="6">暂无数据</td></tr>`
                            : users.map(u => html`<tr>
                                <td>${u.id}</td><td>${u.name}</td><td>${u.age}</td>
                                <td>${u.email || '-'}</td><td>${u.phone || '-'}</td>
                                <td>
                                    <button onclick=${() => navigate('/users/' + u.id)}>查看</button>
                                    <button class="edit" onclick=${() => navigate('/users/' + u.id + '/edit')}>编辑</button>
                                    <button class="danger" onclick=${() => handleDelete(u.id)}>删除</button>
                                </td>
                            </tr>`)
                        }
                    </tbody>
                </table>
            </div>
        </div>
    `;
}
