/**
 * ============================================================
 * 类型安全的表单验证模块
 * ============================================================
 *
 * 在 Hilla 框架中，验证器可以与 Bean Validation (JSR-303) 联动。
 * 后端实体上的 @NotNull、@Size、@Min、@Max 等注解会被同步到前端，
 * 实现"后端验证规则自动同步到前端表单校验"。
 *
 * 本项目模拟这一机制，提供类型安全的验证函数。
 * ============================================================
 */

import { User, Order, OrderStatus } from './models.js';

// ============================================================
// 类型定义
// ============================================================

/** 验证结果：空对象表示无错误 */
export type ValidationErrors<T> = {
    [K in keyof T]?: string;
};

export function hasErrors<T>(errors: ValidationErrors<T>): boolean {
    return Object.keys(errors).length > 0;
}

/** 用户表单数据（创建时不含 id） */
export type UserFormData = Omit<User, 'id'>;

/** 订单表单数据（创建时不含 id 和 createdAt） */
export type OrderFormData = Omit<Order, 'id' | 'createdAt'>;

// ============================================================
// 用户表单验证
// ============================================================

/**
 * 验证用户表单数据
 * 对应后端 Java 实体的 Bean Validation 约束:
 *   - name: @NotBlank, @Size(min=1, max=50)
 *   - age:  @NotNull, @Min(0), @Max(150)
 *   - email: @Email (optional)
 */
export function validateUserForm(data: Partial<UserFormData>): ValidationErrors<UserFormData> {
    const errors: ValidationErrors<UserFormData> = {};

    // 验证姓名
    if (!data.name || data.name.trim().length === 0) {
        errors.name = '姓名不能为空';
    } else if (data.name.trim().length > 50) {
        errors.name = '姓名长度不能超过50个字符';
    }

    // 验证年龄
    if (data.age === null || data.age === undefined) {
        errors.age = '年龄不能为空';
    } else if (data.age < 0 || data.age > 150) {
        errors.age = '年龄必须在 0-150 之间';
    }

    // 验证邮箱（选填）
    if (data.email && data.email.trim().length > 0) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(data.email.trim())) {
            errors.email = '邮箱格式不正确';
        }
    }

    return errors;
}

// ============================================================
// 订单表单验证
// ============================================================

/**
 * 验证订单表单数据
 * 对应后端 Bean Validation:
 *   - productName: @NotBlank, @Size(min=1, max=100)
 *   - quantity:    @NotNull, @Min(1)
 *   - price:       @NotNull, @DecimalMin("0.01")
 *   - userId:      @NotNull
 */
export function validateOrderForm(data: Partial<OrderFormData>): ValidationErrors<OrderFormData> {
    const errors: ValidationErrors<OrderFormData> = {};

    // 验证产品名称
    if (!data.productName || data.productName.trim().length === 0) {
        errors.productName = '产品名称不能为空';
    } else if (data.productName.trim().length > 100) {
        errors.productName = '产品名称不能超过100个字符';
    }

    // 验证数量
    if (data.quantity === null || data.quantity === undefined) {
        errors.quantity = '数量不能为空';
    } else if (data.quantity < 1) {
        errors.quantity = '数量至少为1';
    }

    // 验证价格
    if (data.price === null || data.price === undefined) {
        errors.price = '价格不能为空';
    } else if (data.price <= 0) {
        errors.price = '价格必须大于0';
    }

    // 验证用户 ID
    if (data.userId === null || data.userId === undefined) {
        errors.userId = '用户ID不能为空';
    }

    return errors;
}

// ============================================================
// 类型安全的验证工具函数
// ============================================================

/** 通用非空校验 */
export function required(value: string | null | undefined, fieldName: string): string | undefined {
    if (value === null || value === undefined || value.trim().length === 0) {
        return `${fieldName}不能为空`;
    }
    return undefined;
}

/** 数字范围校验 */
export function range(value: number, min: number, max: number, fieldName: string): string | undefined {
    if (value < min || value > max) {
        return `${fieldName}必须在 ${min}-${max} 之间`;
    }
    return undefined;
}

/** 列出所有订单状态选项（用于下拉框） */
export const ORDER_STATUS_OPTIONS: { value: OrderStatus; label: string }[] = [
    { value: 'PENDING', label: '待支付' },
    { value: 'PAID', label: '已支付' },
    { value: 'SHIPPED', label: '已发货' },
    { value: 'DELIVERED', label: '已送达' },
    { value: 'CANCELLED', label: '已取消' },
];
