/**
 * ============================================================
 * Hilla 自动生成的 TypeScript 客户端文件（模拟）
 * ============================================================
 *
 * 文件路径: frontend/generated/endpoints.ts
 * 来源: 扫描所有 @Endpoint 类自动生成
 *
 * 每个 @Endpoint Java 类会生成一个对应的 TypeScript 导出对象，
 * 封装所有 API 调用，参数和返回值类型与后端严格一致。
 * ============================================================
 */

import { User, Order, OrderStatus } from './models.js';

// ============================================================
// 通用错误类型（来自全局异常处理器的响应格式）
// ============================================================

export interface ApiError {
    status: number;
    message: string;
    timestamp: string;
}

/**
 * API 错误类
 * 后端 GlobalExceptionHandler 返回的 JSON 被前端 TypeGuard 解析为此类型
 */
export class ApiException extends Error {
    public readonly status: number;
    public readonly timestamp: string;

    constructor(error: ApiError) {
        super(error.message);
        this.name = 'ApiException';
        this.status = error.status;
        this.timestamp = error.timestamp;
    }
}

/** 通用的 HTTP 响应处理 */
async function handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
        const errorData: ApiError = await response.json();
        throw new ApiException(errorData);
    }
    return response.json();
}

// ============================================================
// UserEndpoint - 用户端点客户端
// 来源: com.example.hilla.endpoint.UserEndpoint
// ============================================================

export const UserEndpoint = {
    /**
     * GET /api/user/list
     */
    async listUsers(): Promise<User[]> {
        const response = await fetch('/api/user/list');
        return handleResponse<User[]>(response);
    },

    /**
     * GET /api/user/:id
     */
    async getUserById(id: number): Promise<User> {
        const response = await fetch(`/api/user/${id}`);
        if (!response.ok) {
            const error: ApiError = await response.json();
            throw new ApiException(error);
        }
        return response.json();
    },

    /**
     * POST /api/user
     */
    async createUser(user: Omit<User, 'id'>): Promise<User> {
        const response = await fetch('/api/user', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user),
        });
        return handleResponse<User>(response);
    },

    /**
     * PUT /api/user/:id
     */
    async updateUser(id: number, user: Omit<User, 'id'>): Promise<User> {
        const response = await fetch(`/api/user/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user),
        });
        return handleResponse<User>(response);
    },

    /**
     * DELETE /api/user/:id
     */
    async deleteUser(id: number): Promise<string> {
        const response = await fetch(`/api/user/${id}`, {
            method: 'DELETE',
        });
        return handleResponse<string>(response);
    },

    /**
     * GET /api/user/count
     */
    async countUsers(): Promise<number> {
        const response = await fetch('/api/user/count');
        return handleResponse<number>(response);
    },
};

// ============================================================
// OrderEndpoint - 订单端点客户端
// 来源: com.example.hilla.endpoint.OrderEndpoint
// ============================================================

export const OrderEndpoint = {
    /**
     * GET /api/order/list
     */
    async listOrders(): Promise<Order[]> {
        const response = await fetch('/api/order/list');
        return handleResponse<Order[]>(response);
    },

    /**
     * GET /api/order/:id
     */
    async getOrderById(id: number): Promise<Order> {
        const response = await fetch(`/api/order/${id}`);
        return handleResponse<Order>(response);
    },

    /**
     * GET /api/order/user/:userId
     */
    async listOrdersByUser(userId: number): Promise<Order[]> {
        const response = await fetch(`/api/order/user/${userId}`);
        return handleResponse<Order[]>(response);
    },

    /**
     * POST /api/order
     */
    async createOrder(order: Omit<Order, 'id' | 'createdAt'>): Promise<Order> {
        const response = await fetch('/api/order', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(order),
        });
        return handleResponse<Order>(response);
    },

    /**
     * PUT /api/order/:id
     */
    async updateOrder(id: number, order: Omit<Order, 'id' | 'createdAt'>): Promise<Order> {
        const response = await fetch(`/api/order/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(order),
        });
        return handleResponse<Order>(response);
    },

    /**
     * PATCH /api/order/:id/status
     */
    async updateOrderStatus(id: number, status: OrderStatus): Promise<Order> {
        const response = await fetch(`/api/order/${id}/status`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status }),
        });
        return handleResponse<Order>(response);
    },

    /**
     * DELETE /api/order/:id
     */
    async deleteOrder(id: number): Promise<string> {
        const response = await fetch(`/api/order/${id}`, {
            method: 'DELETE',
        });
        return handleResponse<string>(response);
    },
};
