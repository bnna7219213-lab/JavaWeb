/**
 * Hilla 自动生成的 TypeScript 客户端文件（编译后版本）
 * 来源: frontend/endpoints.ts
 */

/**
 * API 错误类
 */
class ApiException extends Error {
    constructor(error) {
        super(error.message);
        this.name = 'ApiException';
        this.status = error.status;
        this.timestamp = error.timestamp;
    }
}

/** 通用的 HTTP 响应处理 */
async function handleResponse(response) {
    if (!response.ok) {
        const errorData = await response.json();
        throw new ApiException(errorData);
    }
    return response.json();
}

// ============================================================
// UserEndpoint
// ============================================================

const UserEndpoint = {
    async listUsers() {
        const response = await fetch('/api/user/list');
        return handleResponse(response);
    },

    async getUserById(id) {
        const response = await fetch(`/api/user/${id}`);
        if (!response.ok) {
            const error = await response.json();
            throw new ApiException(error);
        }
        return response.json();
    },

    async createUser(user) {
        const response = await fetch('/api/user', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user),
        });
        return handleResponse(response);
    },

    async updateUser(id, user) {
        const response = await fetch(`/api/user/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user),
        });
        return handleResponse(response);
    },

    async deleteUser(id) {
        const response = await fetch(`/api/user/${id}`, {
            method: 'DELETE',
        });
        return handleResponse(response);
    },

    async countUsers() {
        const response = await fetch('/api/user/count');
        return handleResponse(response);
    },
};

// ============================================================
// OrderEndpoint
// ============================================================

const OrderEndpoint = {
    async listOrders() {
        const response = await fetch('/api/order/list');
        return handleResponse(response);
    },

    async getOrderById(id) {
        const response = await fetch(`/api/order/${id}`);
        return handleResponse(response);
    },

    async listOrdersByUser(userId) {
        const response = await fetch(`/api/order/user/${userId}`);
        return handleResponse(response);
    },

    async createOrder(order) {
        const response = await fetch('/api/order', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(order),
        });
        return handleResponse(response);
    },

    async updateOrder(id, order) {
        const response = await fetch(`/api/order/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(order),
        });
        return handleResponse(response);
    },

    async updateOrderStatus(id, status) {
        const response = await fetch(`/api/order/${id}/status`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status }),
        });
        return handleResponse(response);
    },

    async deleteOrder(id) {
        const response = await fetch(`/api/order/${id}`, {
            method: 'DELETE',
        });
        return handleResponse(response);
    },
};

export { UserEndpoint, OrderEndpoint, ApiException };
