// 新增用户（POST JSON）
async function addUser(event) {
    event.preventDefault();
    const name = document.getElementById('name').value;
    const age = parseInt(document.getElementById('age').value) || 0;
    const email = document.getElementById('email').value;
    const msgBox = document.getElementById('msg');

    try {
        const res = await fetch('/api/user', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, age, email })
        });
        const data = await res.json();
        msgBox.className = data.code === 200 ? 'ok' : 'err';
        msgBox.textContent = data.msg;
        if (data.code === 200) {
            setTimeout(() => location.reload(), 600);
        }
    } catch (err) {
        msgBox.className = 'err';
        msgBox.textContent = '请求失败: ' + err;
    }
    return false;
}

// 删除用户
async function deleteUser(id) {
    if (!confirm('确认删除该用户？')) return;
    try {
        const res = await fetch('/api/user?id=' + id, { method: 'DELETE' });
        const data = await res.json();
        if (data.code === 200) {
            alert('删除成功');
            location.href = '/';
        } else {
            alert(data.msg);
        }
    } catch (err) {
        alert('删除失败: ' + err);
    }
}
