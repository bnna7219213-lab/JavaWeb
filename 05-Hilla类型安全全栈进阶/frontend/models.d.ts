/**
 * ============================================================
 * Hilla 自动生成的类型定义文件（模拟）
 * ============================================================
 *
 * 文件路径: frontend/generated/models.d.ts
 * 来源: 扫描 Java 实体类自动生成
 *
 * 类型映射规则：
 *   Java Integer/Long        → number
 *   Java String              → string
 *   Java BigDecimal/Double   → number
 *   Java Boolean             → boolean
 *   Java LocalDateTime       → string
 *   Java Optional<T>         → T | null
 * ============================================================
 */

/**
 * 用户实体类型
 * 来源: com.example.hilla.entity.User
 */
export interface User {
    id: number;
    name: string;
    age: number;
    email: string | null;
}

/**
 * 订单实体类型
 * 来源: com.example.hilla.entity.Order
 */
export interface Order {
    id: number;
    userId: number;
    productName: string;
    quantity: number;
    price: number;
    status: OrderStatus;
    createdAt: string;
}

/**
 * 订单状态枚举类型
 * 来源: Java 中 order.getStatus() 的字符串常量
 */
export type OrderStatus = 'PENDING' | 'PAID' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
