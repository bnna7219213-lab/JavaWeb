import { useState, useEffect } from 'react';
import { html } from '../utils/react-html.js';
import { request, remove } from '../utils/request.js';
import { useNavigate } from 'react-router-dom';

export default function UserList() {
    const [users, setUsers] = useState([]);
    const [search, setSearch] = useState('');
    const navigate = useNavigate();

    const loadUsers = async (keyword) => {
        const url = keyword ? 'api/user/list?search=' + encodeURIComponent(keyword) : 'api/user/list';
        try {
            const res = await request(url);
            if (res.code === 200) setUsers(res.data);
        } catch (err) {
            console.error('加载失败:', err);
        }
    };

    useEffect(() => { loadUsers(); }, []);

    const handleDelete = async (id) => {
        if (!confirm('确认删除该用户？')) return;
        try {
            const res = await remove('api/user?id=' + id);
            if (res.code === 200) loadUsers(search);
            else alert(res.msg);
        } catch (err) {
            alert('删除失败: ' + err);
        }
    };

    const handleSearch = (e) => {
        e.preventDefault();
        loadUsers(search);
    };

    return html`
        <h2>用户列表</h2>
        <div class="card">
            <form onSubmit=${handleSearch} class="form-row">
                <input type="text" placeholder="搜索姓名..." value=${search} onChange=${e => setSearch(e.target.value)} />
                <button type="submit">搜索</button>
                <button type="button" style="background:#4caf50" onclick=${() => { setSearch(''); loadUsers(''); }}>重置</button>
            </form>
        </div>
        <div class="card">
            <table>
                <thead><tr><th>ID</th><th>姓名</th><th>年龄</th><th>邮箱</th><th>操作</th></tr></thead>
                <tbody>
                    ${users.length === 0
                        ? html`<tr><td colspan="5">暂无数据</td></tr>`
                        : users.map(u => html`
                            <tr>
                                <td>${u.id}</td><td>${u.name}</td><td>${u.age}</td><td>${u.email || '-'}</td>
                                <td>
                                    <button onclick=${() => navigate('/users/' + u.id)}>查看</button>
                                    <button class="danger" onclick=${() => handleDelete(u.id)}>删除</button>
                                </td>
                            </tr>
                        `)
                    }
                </tbody>
            </table>
        </div>
    `;
}
