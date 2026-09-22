package com.example.modulith.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 模块化架构验证测试（ArchUnit风格的手写实现）
 *
 * 本测试验证以下模块化原则：
 * 1. 模块内部包结构正确：每个模块必须有api包（对外接口）
 * 2. 模块间无非法交叉引用：内部实现类（entity, internal）不被其他模块引用
 * 3. 模块通过接口通信：跨模块调用必须通过api包中的接口
 * 4. 共享模块（shared）只包含DTO和事件，不依赖任何业务模块
 *
 * 注意：这是手动实现的模块化验证，而非使用ArchUnit库。
 * 实际项目中可以使用ArchUnit进行更精细的字节码分析。
 */
@DisplayName("模块化架构验证测试")
public class ModularityTest {

    private static final String BASE_PATH = "src/main/java/com/example/modulith/basic";
    private static final List<String> MODULES = List.of("user", "product", "order");
    private static final String SHARED_PACKAGE = "shared";

    /**
     * 验证1：所有模块都存在于modules目录下
     */
    @Test
    @DisplayName("M1: 验证所有业务模块目录存在")
    void verifyAllModuleDirectoriesExist() {
        Path modulesPath = Path.of(BASE_PATH, "modules");
        File modulesDir = modulesPath.toFile();

        assertTrue(modulesDir.exists() && modulesDir.isDirectory(),
                "modules/ 目录应该存在");

        for (String moduleName : MODULES) {
            Path modulePath = modulesPath.resolve(moduleName);
            assertTrue(modulePath.toFile().exists() && modulePath.toFile().isDirectory(),
                    "模块目录 " + moduleName + " 应该存在: " + modulePath);
        }
        System.out.println("[M1] PASS - 所有模块目录存在: " + MODULES);
    }

    /**
     * 验证2：每个模块都包含必要的子包（api, service, controller, entity）
     */
    @Test
    @DisplayName("M2: 验证每个模块包含api包（对外接口）")
    void verifyEachModuleHasApiPackage() throws IOException {
        Path modulesPath = Path.of(BASE_PATH, "modules");

        for (String moduleName : MODULES) {
            Path apiPath = modulesPath.resolve(moduleName).resolve("api");
            assertTrue(apiPath.toFile().exists() && apiPath.toFile().isDirectory(),
                    moduleName + " 模块必须包含 api/ 目录（对外接口包）");

            // api包下必须有至少一个接口文件
            try (Stream<Path> files = Files.list(apiPath)) {
                long interfaceCount = files
                        .filter(p -> p.toString().endsWith(".java"))
                        .count();
                assertTrue(interfaceCount > 0,
                        moduleName + " 模块的 api/ 包下必须有至少一个接口文件");
            }
        }
        System.out.println("[M2] PASS - 所有模块都有api包且包含接口定义");
    }

    /**
     * 验证3：验证entity是模块内部类——其他模块的Java文件不import entity包
     */
    @Test
    @DisplayName("M3: 验证entity包不被其他模块直接引用（封装内部实现）")
    void verifyEntityNotReferencedByOtherModules() throws IOException {
        Path modulesPath = Path.of(BASE_PATH, "modules");

        for (String currentModule : MODULES) {
            Path entityPackage = modulesPath.resolve(currentModule).resolve("entity");

            // 检查所有其他模块的Java文件
            for (String otherModule : MODULES) {
                if (otherModule.equals(currentModule)) {
                    continue; // 不检查自身模块
                }

                Path otherModulePath = modulesPath.resolve(otherModule);
                List<File> javaFiles = findJavaFiles(otherModulePath.toFile());

                for (File javaFile : javaFiles) {
                    String content = Files.readString(javaFile.toPath());
                    String importPattern = "import.*" + currentModule + ".entity.";

                    assertFalse(content.contains("com.example.modulith.basic.modules." + currentModule + ".entity"),
                            "模块 " + otherModule + " 不应该直接引用模块 " + currentModule + " 的entity包。" +
                                    "文件: " + javaFile.getPath() + " 包含了禁止的import。");
                }
            }
        }
        System.out.println("[M3] PASS - entity包不被其他模块直接引用");
    }

    /**
     * 验证4：验证模块间引用只通过api包中的接口
     */
    @Test
    @DisplayName("M4: 验证跨模块引用必须通过api包接口")
    void verifyCrossModuleReferenceOnlyThroughApi() throws IOException {
        Path modulesPath = Path.of(BASE_PATH, "modules");

        // order模块应该通过api接口引用user和product模块
        Path orderServicePath = modulesPath.resolve("order").resolve("service");
        List<File> orderServiceFiles = findJavaFiles(orderServicePath.toFile());

        boolean userModuleInterfaceFound = false;
        boolean productModuleInterfaceFound = false;

        for (File file : orderServiceFiles) {
            String content = Files.readString(file.toPath());
            // 通过api包引用UserModule接口
            if (content.contains("import com.example.modulith.basic.modules.user.api.UserModule")) {
                userModuleInterfaceFound = true;
            }
            // 通过api包引用ProductModule接口
            if (content.contains("import com.example.modulith.basic.modules.product.api.ProductModule")) {
                productModuleInterfaceFound = true;
            }

            // 禁止直接引用user模块的service或entity
            assertFalse(content.contains("import com.example.modulith.basic.modules.user.service"),
                    "order模块应该引用user模块的API接口，而非service实现类");
            assertFalse(content.contains("import com.example.modulith.basic.modules.user.entity"),
                    "order模块应该引用user模块的API接口，而非entity类");

            // 禁止直接引用product模块的service或entity
            assertFalse(content.contains("import com.example.modulith.basic.modules.product.service"),
                    "order模块应该引用product模块的API接口，而非service实现类");
            assertFalse(content.contains("import com.example.modulith.basic.modules.product.entity"),
                    "order模块应该引用product模块的API接口，而非entity类");
        }

        assertTrue(userModuleInterfaceFound,
                "order模块应该通过UserModule接口引用user模块");
        assertTrue(productModuleInterfaceFound,
                "order模块应该通过ProductModule接口引用product模块");

        System.out.println("[M4] PASS - 跨模块引用通过api接口，无直接实现类引用");
    }

    /**
     * 验证5：验证shared包不依赖业务模块（单向依赖）
     */
    @Test
    @DisplayName("M5: 验证shared包不反向依赖业务模块")
    void verifySharedDoesNotDependOnModules() throws IOException {
        Path sharedPath = Path.of(BASE_PATH, SHARED_PACKAGE);
        List<File> sharedFiles = findJavaFiles(sharedPath.toFile());

        for (String moduleName : MODULES) {
            for (File file : sharedFiles) {
                String content = Files.readString(file.toPath());
                assertFalse(content.contains("import com.example.modulith.basic.modules." + moduleName),
                        "shared包不应该依赖业务模块 " + moduleName +
                                "。文件: " + file.getPath());
            }
        }
        System.out.println("[M5] PASS - shared包不反向依赖业务模块");
    }

    /**
     * 验证6：验证user和product模块之间无直接依赖
     * （user模块不需要知道product模块的存在，反之亦然）
     */
    @Test
    @DisplayName("M6: 验证user和product模块无相互依赖")
    void verifyUserAndProductIndependent() throws IOException {
        Path modulesPath = Path.of(BASE_PATH, "modules");

        // user模块不应引用product
        verifyNoModuleDependency(modulesPath, "user", "product");
        // product模块不应引用user
        verifyNoModuleDependency(modulesPath, "product", "user");

        System.out.println("[M6] PASS - user和product模块相互独立，无直接依赖");
    }

    /**
     * 验证7：验证模块无循环依赖（user <-> product 不应互相引用）
     */
    @Test
    @DisplayName("M7: 验证无循环依赖")
    void verifyNoCircularDependency() throws IOException {
        Path modulesPath = Path.of(BASE_PATH, "modules");

        // 构建依赖图
        Map<String, Set<String>> dependencyGraph = new HashMap<>();

        for (String moduleName : MODULES) {
            Set<String> dependencies = new HashSet<>();
            List<File> javaFiles = findJavaFiles(modulesPath.resolve(moduleName).toFile());

            for (File file : javaFiles) {
                String content = Files.readString(file.toPath());
                for (String otherModule : MODULES) {
                    if (!otherModule.equals(moduleName) &&
                            content.contains("modules." + otherModule + ".api")) {
                        // 通过api接口的引用才是正常依赖
                        dependencies.add(otherModule);
                    }
                }
            }
            dependencyGraph.put(moduleName, dependencies);
        }

        // 检查循环（简单的三节点检查）
        for (String module : MODULES) {
            Set<String> directDeps = dependencyGraph.get(module);
            for (String dep : directDeps) {
                Set<String> transitiveDeps = dependencyGraph.get(dep);
                if (transitiveDeps != null) {
                    assertFalse(transitiveDeps.contains(module),
                            "检测到循环依赖: " + module + " -> " + dep + " -> " + module);
                }
            }
        }

        System.out.println("[M7] PASS - 无循环依赖");
        System.out.println("  依赖关系: " + dependencyGraph);
    }

    // ============ 辅助方法 ============

    /**
     * 查找目录下的所有Java文件（递归）
     */
    private List<File> findJavaFiles(File directory) throws IOException {
        if (!directory.exists() || !directory.isDirectory()) {
            return Collections.emptyList();
        }
        List<File> result = new ArrayList<>();
        findJavaFilesRecursive(directory, result);
        return result;
    }

    private void findJavaFilesRecursive(File dir, List<File> result) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                findJavaFilesRecursive(file, result);
            } else if (file.getName().endsWith(".java")) {
                result.add(file);
            }
        }
    }

    /**
     * 验证source模块不引用target模块
     */
    private void verifyNoModuleDependency(Path modulesPath, String sourceModule, String targetModule) throws IOException {
        Path sourcePath = modulesPath.resolve(sourceModule);
        List<File> sourceFiles = findJavaFiles(sourcePath.toFile());

        for (File file : sourceFiles) {
            String content = Files.readString(file.toPath());
            assertFalse(content.contains("import com.example.modulith.basic.modules." + targetModule),
                    sourceModule + " 模块不应引用 " + targetModule + " 模块。" +
                            "违规文件: " + file.getPath());
        }
    }
}
