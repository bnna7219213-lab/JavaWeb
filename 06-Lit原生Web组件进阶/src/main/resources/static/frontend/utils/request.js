/**
 * 通用请求工具函数（进阶版增强）
 * 封装 fetch，统一处理响应格式、错误、Content-Type
 */

const BASE_URL = '/api';

/**
 * 统一处理响应
 */
async function handleResponse(response) {
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    const data = await response.json();
    if (data.code !== 200) throw new Error(data.msg || '请求失败');
    return data;
}

/**
 * GET 请求
 * @param {string} path - 请求路径（不含 /api）
 * @param {object} params - URL 查询参数
 */
export async function get(path, params = {}) {
    const url = new URL(BASE_URL + path, window.location.origin);
    Object.entries(params).forEach(([k, v]) => {
        if (v !== undefined && v !== null && v !== '') url.searchParams.set(k, v);
    });
    return handleResponse(await fetch(url));
}

/**
 * POST 请求
 * @param {string} path - 请求路径
 * @param {object} body - 请求体
 */
export async function post(path, body) {
    return handleResponse(await fetch(BASE_URL + path, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    }));
}

/**
 * PUT 请求
 * @param {string} path - 请求路径
 * @param {object} body - 请求体
 */
export async function put(path, body) {
    return handleResponse(await fetch(BASE_URL + path, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    }));
}

/**
 * DELETE 请求
 * @param {string} path - 请求路径
 * @param {object} params - URL 查询参数
 */
export async function del(path, params = {}) {
    const url = new URL(BASE_URL + path, window.location.origin);
    Object.entries(params).forEach(([k, v]) => {
        if (v !== undefined && v !== null) url.searchParams.set(k, v);
    });
    return handleResponse(await fetch(url, { method: 'DELETE' }));
}
