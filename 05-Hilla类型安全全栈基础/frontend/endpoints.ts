/**
 * ============================================================
 * Hilla 自动生成的 TypeScript 客户端文件（模拟）
 * ============================================================
 *
 * 在真正的 Hilla 框架中，此文件由 Hilla Maven 插件在编译期自动生成。
 * 它会为每个 @Endpoint 类生成一个对应的 TypeScript 对象，
 * 前端可以直接 import 并获得完全类型安全的 API 调用。
 *
 * 文件路径对应于 Hilla 自动生成的：
 *   frontend/generated/endpoints.ts
 * ============================================================
 */

import { User } from './models.js';

/**
 * 用户端点客户端
 * 来源: com.example.hilla.endpoint.UserEndpoint
 *
 * 使用示例:
 *   const users = await UserEndpoint.listUsers();   // users: User[]
 *   const user = await UserEndpoint.getUserById(1); // user: User
 *   const newUser = await UserEndpoint.addUser({ name: '赵六', age: 25, email: null });
 */
export const UserEndpoint = {
    /**
     * GET /api/user/list
     * 获取所有用户列表
     */
    async listUsers(): Promise<User[]> {
        const response = await fetch('/api/user/list');
        if (!response.ok) {
            throw new Error(`请求失败: ${response.status} ${response.statusText}`);
        }
        return response.json();
    },

    /**
     * GET /api/user/:id
     * 根据 ID 获取单个用户
     */
    async getUserById(id: number): Promise<User> {
        const response = await fetch(`/api/user/${id}`);
        if (!response.ok) {
            // 【注意】这里 TypeScript 知道返回值是 User，IDE 会有完整提示
            // 对比传统 fetch().then(r => r.json()) 没有类型信息
            throw new Error(`用户不存在: id=${id}`);
        }
        return response.json();
    },

    /**
     * POST /api/user/add
     * 新增用户
     */
    async addUser(user: Omit<User, 'id'>): Promise<User> {
        const response = await fetch('/api/user/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user),
        });
        if (!response.ok) {
            throw new Error(`添加失败: ${response.status} ${response.statusText}`);
        }
        return response.json();
    },
};
