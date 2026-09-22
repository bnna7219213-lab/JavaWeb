/**
 * Hilla 自动生成的 TypeScript 客户端文件（编译后版本）
 * 来源: frontend/endpoints.ts 编译输出
 */

export const UserEndpoint = {
    async listUsers() {
        const response = await fetch('/api/user/list');
        if (!response.ok) {
            throw new Error(`请求失败: ${response.status} ${response.statusText}`);
        }
        return response.json();
    },

    async getUserById(id) {
        const response = await fetch(`/api/user/${id}`);
        if (!response.ok) {
            throw new Error(`用户不存在: id=${id}`);
        }
        return response.json();
    },

    async addUser(user) {
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
