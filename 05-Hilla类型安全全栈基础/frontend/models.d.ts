/**
 * ============================================================
 * Hilla 自动生成的类型定义文件（模拟）
 * ============================================================
 *
 * 在真正的 Hilla 框架中，此文件由 Hilla Maven 插件自动生成。
 * 它会扫描所有 @Endpoint 类及其引用的实体类，生成对应的 TypeScript 接口定义。
 *
 * 生成规则：
 * - Java Integer/Long → number
 * - Java String → string
 * - Java Boolean → boolean
 * - Java Date/LocalDateTime → Date
 * - Java 实体类 → TypeScript interface
 *
 * 文件路径对应于 Hilla 自动生成的：
 *   frontend/generated/models.d.ts
 * ============================================================
 */

/**
 * 用户实体类型定义
 * 来源: com.example.hilla.entity.User
 */
export interface User {
    id: number;
    name: string;
    age: number;
    email: string | null;
}
