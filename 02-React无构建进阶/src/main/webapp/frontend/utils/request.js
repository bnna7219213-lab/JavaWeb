export async function request(url, options = {}) {
    const res = await fetch(url, {
        headers: { 'Content-Type': 'application/json', ...options.headers },
        ...options
    });
    if (!res.ok) throw new Error('请求失败，状态码：' + res.status);
    return res.json();
}

export async function post(url, data) {
    return request(url, { method: 'POST', body: JSON.stringify(data) });
}

export async function remove(url) {
    return request(url, { method: 'DELETE' });
}
