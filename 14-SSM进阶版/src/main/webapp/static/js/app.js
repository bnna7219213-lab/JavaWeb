// ========== 用户操作 ==========

async function addUser(event) {
    event.preventDefault();
    const payload = {
        name: document.getElementById('name').value,
        age: parseInt(document.getElementById('age').value) || 0,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value
    };
    const msgBox = document.getElementById('msg');
    try {
        const res = await fetch('/api/user', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json();
        msgBox.className = data.code === 200 ? 'ok' : 'err';
        msgBox.textContent = data.msg;
        if (data.code === 200) setTimeout(() => location.reload(), 600);
    } catch (err) {
        msgBox.className = 'err'; msgBox.textContent = '请求失败: ' + err;
    }
    return false;
}

async function deleteUser(id) {
    if (!confirm('确认删除该用户？')) return;
    try {
        const res = await fetch('/api/user?id=' + id, { method: 'DELETE' });
        const data = await res.json();
        if (data.code === 200) { alert('删除成功'); location.reload(); }
        else alert(data.msg);
    } catch (err) { alert('删除失败: ' + err); }
}

// ========== 订单操作 ==========

async function addOrder(event) {
    event.preventDefault();
    const payload = {
        userId: parseInt(document.getElementById('userId').value),
        product: document.getElementById('product').value,
        amount: parseFloat(document.getElementById('amount').value) || 0,
        status: 'PENDING'
    };
    const msgBox = document.getElementById('msg');
    try {
        const res = await fetch('/api/order', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json();
        msgBox.className = data.code === 200 ? 'ok' : 'err';
        msgBox.textContent = data.msg;
        if (data.code === 200) setTimeout(() => location.reload(), 600);
    } catch (err) {
        msgBox.className = 'err'; msgBox.textContent = '请求失败: ' + err;
    }
    return false;
}

async function deleteOrder(id) {
    if (!confirm('确认删除该订单？')) return;
    try {
        const res = await fetch('/api/order?id=' + id, { method: 'DELETE' });
        const data = await res.json();
        if (data.code === 200) { alert('删除成功'); location.reload(); }
        else alert(data.msg);
    } catch (err) { alert('删除失败: ' + err); }
}
