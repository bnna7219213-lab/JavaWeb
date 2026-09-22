/**
 * 通用请求工具函数
 * 封装 fetch，统一处理响应格式
 */

/**
 * GET 请求
 * @param {string} url - 请求地址
 * @returns {Promise<{code: number, msg: string, data: any}>}
 */
export async function get(url) {
    const response = await fetch(url);
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    return response.json();
}

/**
 * POST 请求
 * @param {string} url - 请求地址
 * @param {object} body - 请求体
 * @returns {Promise<{code: number, msg: string, data: any}>}
 */
export async function post(url, body) {
    const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    return response.json();
}

/**
 * PUT 请求
 * @param {string} url - 请求地址
 * @param {object} body - 请求体
 * @returns {Promise<{code: number, msg: string, data: any}>}
 */
export async function put(url, body) {
    const response = await fetch(url, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    return response.json();
}

/**
 * DELETE 请求
 * @param {string} url - 请求地址
 * @returns {Promise<{code: number, msg: string, data: any}>}
 */
export async function del(url) {
    const response = await fetch(url, { method: 'DELETE' });
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    return response.json();
}
