package com.example.modulith.advanced.shared.modularity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ApplicationModule 注解 - 描述模块的元信息
 *
 * 类似 Spring Modulith 中的 @ApplicationModule，
 * 但通过纯注解实现，不引入额外依赖。
 *
 * 每个模块的 api 接口类上标注此注解，描述：
 * - moduleName: 模块名称
 * - allowedDependencies: 允许依赖的其他模块名
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ApplicationModule {
    /**
     * 模块名称
     */
    String name();

    /**
     * 模块描述
     */
    String description() default "";

    /**
     * 允许依赖的其他模块名称
     * 如果为空，表示不允许依赖任何其他模块
     */
    String[] allowedDependencies() default {};
}
