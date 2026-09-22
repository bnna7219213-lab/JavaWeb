/**
 * 类型安全的表单验证模块（编译后版本）
 * 来源: frontend/validation.ts
 */

import { ORDER_STATUS_OPTIONS } from './models.js';

// 验证结果工具
function hasErrors(errors) {
    return Object.keys(errors).length > 0;
}

/**
 * 验证用户表单数据
 */
function validateUserForm(data) {
    const errors = {};

    if (!data.name || data.name.trim().length === 0) {
        errors.name = '姓名不能为空';
    } else if (data.name.trim().length > 50) {
        errors.name = '姓名长度不能超过50个字符';
    }

    if (data.age === null || data.age === undefined) {
        errors.age = '年龄不能为空';
    } else if (data.age < 0 || data.age > 150) {
        errors.age = '年龄必须在 0-150 之间';
    }

    if (data.email && data.email.trim().length > 0) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(data.email.trim())) {
            errors.email = '邮箱格式不正确';
        }
    }

    return errors;
}

/**
 * 验证订单表单数据
 */
function validateOrderForm(data) {
    const errors = {};

    if (!data.productName || data.productName.trim().length === 0) {
        errors.productName = '产品名称不能为空';
    } else if (data.productName.trim().length > 100) {
        errors.productName = '产品名称不能超过100个字符';
    }

    if (data.quantity === null || data.quantity === undefined) {
        errors.quantity = '数量不能为空';
    } else if (data.quantity < 1) {
        errors.quantity = '数量至少为1';
    }

    if (data.price === null || data.price === undefined) {
        errors.price = '价格不能为空';
    } else if (data.price <= 0) {
        errors.price = '价格必须大于0';
    }

    if (data.userId === null || data.userId === undefined) {
        errors.userId = '用户ID不能为空';
    }

    return errors;
}

export { hasErrors, validateUserForm, validateOrderForm, ORDER_STATUS_OPTIONS };
export const ORDER_STATUS_OPTIONS = [
    { value: 'PENDING', label: '待支付' },
    { value: 'PAID', label: '已支付' },
    { value: 'SHIPPED', label: '已发货' },
    { value: 'DELIVERED', label: '已送达' },
    { value: 'CANCELLED', label: '已取消' },
];
