export async function request(url, options = {}) {
    const res = await fetch(url, {
        headers: { 'Content-Type': 'application/json', ...options.headers },
        ...options
    });
    if (!res.ok) throw new Error('请求失败：' + res.status);
    return res.json();
}

export const get = (url) => request(url);
export const post = (url, data) => request(url, { method: 'POST', body: JSON.stringify(data) });
export const put = (url, data) => request(url, { method: 'PUT', body: JSON.stringify(data) });
export const del = (url) => request(url, { method: 'DELETE' });
