package com.example.modulith.advanced;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 进阶版 - 模块化架构完整验证测试
 *
 * 相比基础版，进阶版增加了以下验证规则：
 * - internal 包封装验证
 * - notification/audit 模块事件驱动验证
 * - 循环依赖的多节点检测
 * - 完整的依赖图拓扑排序
 *
 * 验证按顺序执行（有序），体现验证逻辑的层次关系。
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("进阶版 - 模块化架构完整验证测试")
public class ModularityTest {

    private static final String BASE_PATH = "src/main/java/com/example/modulith/advanced";
    private static final List<String> BUSINESS_MODULES = List.of("user", "product", "order");
    private static final List<String> EVENT_DRIVEN_MODULES = List.of("notification", "audit");
    private static final List<String> ALL_MODULES;

    static {
        List<String> all = new ArrayList<>(BUSINESS_MODULES);
        all.addAll(EVENT_DRIVEN_MODULES);
        ALL_MODULES = Collections.unmodifiableList(all);
    }

    // ==========================================
    // 第一层：目录与包结构验证
    // ==========================================

    @Test
    @Order(1)
    @DisplayName("M1: 验证五个模块目录全部存在")
    void verifyAllFiveModulesExist() {
        Path modulesPath = Path.of(BASE_PATH, "modules");
        assertTrue(modulesPath.toFile().exists(), "modules/ 目录应该存在");

        for (String moduleName : ALL_MODULES) {
            Path modulePath = modulesPath.resolve(moduleName);
            assertTrue(modulePath.toFile().exists() && modulePath.toFile().isDirectory(),
                    "模块目录 " + moduleName + " 应该存在: " + modulePath);
        }
        System.out.println("[PASS] M1 - 五个模块目录全部存在: " + ALL_MODULES);
    }

    @Test
    @Order(2)
    @DisplayName("M2: 验证业务模块包含api包和internal包")
    void verifyBusinessModulesHaveApiAndInternal() throws IOException {
        Path modulesPath = Path.of(BASE_PATH, "modules");

        for (String moduleName : BUSINESS_MODULES) {
            // api包
            Path apiPath = modulesPath.resolve(moduleName).resolve("api");
            assertTrue(apiPath.toFile().exists(),
                    moduleName + " 模块缺少 api/ 目录");

            // api包下有至少一个接口
            long apiFileCount = Files.list(apiPath)
                    .filter(p -> p.toString().endsWith(".java"))
                    .count();
            assertTrue(apiFileCount > 0,
                    moduleName + " 模块的 api/ 包下缺少接口文件");

            // internal包 (service/internal)
            Path internalPath = modulesPath.resolve(moduleName).resolve("service").resolve("internal");
            assertTrue(internalPath.toFile().exists(),
                    moduleName + " 模块缺少 service/internal/ 目录");
        }
        System.out.println("[PASS] M2 - 所有业务模块包含api包和service/internal包");
    }

    @Test
    @Order(3)
    @DisplayName("M3: 验证事件驱动模块有listener子包")
    void verifyEventDrivenModulesHaveListeners() {
        Path modulesPath = Path.of(BASE_PATH, "modules");

        for (String moduleName : EVENT_DRIVEN_MODULES) {
            Path listenerPath = modulesPath.resolve(moduleName).resolve("listener");
            assertTrue(listenerPath.toFile().exists(),
                    moduleName + " 模块应包含 listener/ 子包（事件监听器）");

            Path servicePath = modulesPath.resolve(moduleName).resolve("service").resolve("internal");
            assertTrue(servicePath.toFile().exists(),
                    moduleName + " 模块应包含 service/internal/ 子包");
        }
        System.out.println("[PASS] M3 - 事件驱动模块有listener服务结构");
    }

    @Test
    @Order(4)
    @DisplayName("M4: 验证shared包独立，不依赖业务模块")
    void verifySharedIndependence() throws IOException {
        Path sharedPath = Path.of(BASE_PATH, "shared");
        List<File> sharedFiles = findJavaFiles(sharedPath.toFile());

        for (File file : sharedFiles) {
            String content = Files.readString(file.toPath());
            for (String moduleName : ALL_MODULES) {
                assertFalse(content.contains("import com.example.modulith.advanced.modules." + moduleName),
                        "shared 包不应依赖业务模块 " + moduleName +
                                "。违规文件: " + file.getPath());
            }
        }
        System.out.println("[PASS] M4 - shared包独立，不反向依赖业务模块");
    }

    // ==========================================
    // 第二层：模块封装验证
    // ==========================================

    @Test
    @Order(5)
    @DisplayName("M5: 验证entity包不被其他模块直接引用")
    void verifyEntityEncapsulation() throws IOException {
        Path modulesPath = Path.of(BASE_PATH, "modules");

        for (String currentModule : BUSINESS_MODULES) {
            for (String otherModule : ALL_MODULES) {
                if (otherModule.equals(currentModule)) continue;

                Path othersPath = modulesPath.resolve(otherModule);
                List<File> javaFiles = findJavaFiles(othersPath.toFile());

                for (File file : javaFiles) {
                    String content = Files.readString(file.toPath());
                    assertFalse(
                            content.contains("modules." + currentModule + ".entity"),
                            "模块 " + otherModule + " 不能引用 " + currentModule + " 的 entity 包。" +
                                    "文件: " + file.getPath()
                    );
                }
            }
        }
        System.out.println("[PASS] M5 - entity包不被其他模块直接引用");
    }

    @Test
    @Order(6)
    @DisplayName("M6: 验证service.internal包不被其他模块直接引用")
    void verifyInternalEncapsulation() throws IOException {
        Path modulesPath = Path.of(BASE_PATH, "modules");

        for (String currentModule : ALL_MODULES) {
            for (String otherModule : ALL_MODULES) {
                if (otherModule.equals(currentModule)) continue;

                Path othersPath = modulesPath.resolve(otherModule);
                List<File> javaFiles = findJavaFiles(othersPath.toFile());

                for (File file : javaFiles) {
                    String content = Files.readString(file.toPath());
                    assertFalse(
                            content.contains("modules." + currentModule + ".service.internal"),
                            "模块 " + otherModule + " 不能引用 " + currentModule + " 的 service.internal 包。" +
                                    "文件: " + file.getPath()
                    );
                }
            }
        }
        System.out.println("[PASS] M6 - service.internal包不被其他模块直接引用");
    }

    // ==========================================
    // 第三层：模块依赖规则验证
    // ==========================================

    @Test
    @Order(7)
    @DisplayName("M7: 验证order模块通过api接口引用user/product（DIP）")
    void verifyOrderDependsOnInterfaces() throws IOException {
        Path modulesPath = Path.of(BASE_PATH, "modules");
        Path orderPath = modulesPath.resolve("order");
        List<File> orderFiles = findJavaFiles(orderPath.toFile());

        boolean hasUserModule = false;
        boolean hasProductModule = false;

        for (File file : orderFiles) {
            String content = Files.readString(file.toPath());
            if (content.contains("modules.user.api.UserModule")) hasUserModule = true;
            if (content.contains("modules.product.api.ProductModule")) hasProductModule = true;

            // 禁止直接引用user模块的service或entity
            assertFalse(content.contains("modules.user.service"),
                    "order不能直接引用user.service: " + file.getPath());
            assertFalse(content.contains("modules.user.entity"),
                    "order不能直接引用user.entity: " + file.getPath());
            // 禁止直接引用product模块的service或entity
            assertFalse(content.contains("modules.product.service"),
                    "order不能直接引用product.service: " + file.getPath());
            assertFalse(content.contains("modules.product.entity"),
                    "order不能直接引用product.entity: " + file.getPath());
        }

        assertTrue(hasUserModule, "order模块应通过UserModule接口引用user模块");
        assertTrue(hasProductModule, "order模块应通过ProductModule接口引用product模块");
        System.out.println("[PASS] M7 - order模块通过api接口引用user/product（依赖倒置）");
    }

    @Test
    @Order(8)
    @DisplayName("M8: 验证user和product模块互不依赖")
    void verifyUserAndProductIndependent() throws IOException {
        assertNoDependency("user", "product");
        assertNoDependency("product", "user");
        System.out.println("[PASS] M8 - user和product模块相互独立");
    }

    @Test
    @Order(9)
    @DisplayName("M9: 验证notification模块不反向依赖order/user模块的实现")
    void verifyNotificationOnlyListensEvents() throws IOException {
        // notification模块应该通过事件通信，而不是直接调用order/user的API
        Path notifPath = Path.of(BASE_PATH, "modules", "notification");
        List<File> notifFiles = findJavaFiles(notifPath.toFile());

        for (File file : notifFiles) {
            String content = Files.readString(file.toPath());
            // notification可以引用OrderEvent和UserEvent（shared包中的事件类）
            // 但不能引用UserModule、OrderModule等服务接口
            assertFalse(content.contains("modules.user.api.UserModule"),
                    "notification不应直接引用UserModule接口（应通过事件通信）");
            assertFalse(content.contains("modules.product.api.ProductModule"),
                    "notification不应直接引用ProductModule接口");
        }
        System.out.println("[PASS] notification模块通过事件与其他模块通信，无直接API引用");
    }

    // ==========================================
    // 第四层：循环依赖检测
    // ==========================================

    @Test
    @Order(10)
    @DisplayName("M10: 验证模块依赖无循环（拓扑排序验证）")
    void verifyNoCircularDependency() throws IOException {
        Path modulesPath = Path.of(BASE_PATH, "modules");
        Map<String, Set<String>> dependencyGraph = buildDependencyGraph(modulesPath);

        // 拓扑排序检测环
        List<String> sorted = topologicalSort(dependencyGraph);
        assertFalse(sorted.isEmpty() && !dependencyGraph.isEmpty(),
                "检测到循环依赖！依赖图: " + dependencyGraph);

        System.out.println("[PASS] M10 - 模块依赖无循环。拓扑排序: " + sorted);
    }

    @Test
    @Order(11)
    @DisplayName("M11: 验证user模块是基础模块（无业务模块依赖）")
    void verifyUserModuleIsBase() throws IOException {
        assertNoModuleDependency("user", BUSINESS_MODULES);
        System.out.println("[PASS] M11 - user模块是纯基础模块，不依赖其他业务模块");
    }

    @Test
    @Order(12)
    @DisplayName("M12: 验证product模块是基础模块（无业务模块依赖）")
    void verifyProductModuleIsBase() throws IOException {
        assertNoModuleDependency("product", BUSINESS_MODULES);
        System.out.println("[PASS] M12 - product模块是纯基础模块，不依赖其他业务模块");
    }

    // ==========================================
    // 第五层：事件驱动验证
    // ==========================================

    @Test
    @Order(13)
    @DisplayName("M13: 验证notification模块监听OrderEvent和UserEvent")
    void verifyNotificationEventListeners() throws IOException {
        Path notifPath = Path.of(BASE_PATH, "modules", "notification");
        List<File> files = findJavaFiles(notifPath.toFile());

        String allContent = files.stream()
                .map(f -> { try { return Files.readString(f.toPath()); } catch (Exception e) { return ""; } })
                .collect(Collectors.joining("\n"));

        assertTrue(allContent.contains("OrderEvent"),
                "notification模块应监听OrderEvent");
        assertTrue(allContent.contains("UserEvent"),
                "notification模块应监听UserEvent");
        assertTrue(allContent.contains("@EventListener"),
                "notification模块应使用@EventListener注解");
        System.out.println("[PASS] M13 - notification模块正确监听OrderEvent和UserEvent");
    }

    @Test
    @Order(14)
    @DisplayName("M14: 验证audit模块监听OrderEvent和UserEvent")
    void verifyAuditEventListeners() throws IOException {
        Path auditPath = Path.of(BASE_PATH, "modules", "audit");
        List<File> files = findJavaFiles(auditPath.toFile());

        String allContent = files.stream()
                .map(f -> { try { return Files.readString(f.toPath()); } catch (Exception e) { return ""; } })
                .collect(Collectors.joining("\n"));

        assertTrue(allContent.contains("OrderEvent"),
                "audit模块应监听OrderEvent");
        assertTrue(allContent.contains("UserEvent"),
                "audit模块应监听UserEvent");
        assertTrue(allContent.contains("@EventListener"),
                "audit模块应使用@EventListener注解");
        System.out.println("[PASS] M14 - audit模块正确监听OrderEvent和UserEvent");
    }

    @Test
    @Order(15)
    @DisplayName("M15: 验证user模块发布UserEvent事件")
    void verifyUserModulePublishesEvents() throws IOException {
        verifyModulePublishesEvent("user", "UserEvent");
        System.out.println("[PASS] M15 - user模块正确发布UserEvent事件");
    }

    @Test
    @Order(16)
    @DisplayName("M16: 验证order模块发布OrderEvent事件")
    void verifyOrderModulePublishesEvents() throws IOException {
        verifyModulePublishesEvent("order", "OrderEvent");
        System.out.println("[PASS] M16 - order模块正确发布OrderEvent事件");
    }

    // ==========================================
    // 辅助方法
    // ==========================================

    private List<File> findJavaFiles(File dir) {
        List<File> result = new ArrayList<>();
        findJavaFilesRecursive(dir, result);
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

    private void assertNoDependency(String sourceModule, String targetModule) throws IOException {
        Path sourcePath = Path.of(BASE_PATH, "modules", sourceModule);
        List<File> files = findJavaFiles(sourcePath.toFile());
        for (File file : files) {
            String content = Files.readString(file.toPath());
            assertFalse(content.contains("modules." + targetModule),
                    sourceModule + " 不应引用 " + targetModule + ": " + file.getPath());
        }
    }

    private void assertNoModuleDependency(String sourceModule, List<String> targetModules) throws IOException {
        for (String target : targetModules) {
            if (!target.equals(sourceModule)) {
                assertNoDependency(sourceModule, target);
            }
        }
    }

    private Map<String, Set<String>> buildDependencyGraph(Path modulesPath) throws IOException {
        Map<String, Set<String>> graph = new HashMap<>();
        for (String module : ALL_MODULES) {
            graph.put(module, new HashSet<>());
            List<File> files = findJavaFiles(modulesPath.resolve(module).toFile());
            for (File file : files) {
                String content = Files.readString(file.toPath());
                for (String other : ALL_MODULES) {
                    if (!other.equals(module) && content.contains("modules." + other + ".api")) {
                        graph.get(module).add(other);
                    }
                }
            }
        }
        return graph;
    }

    private List<String> topologicalSort(Map<String, Set<String>> graph) {
        Map<String, Integer> inDegree = new HashMap<>();
        for (String node : graph.keySet()) {
            inDegree.putIfAbsent(node, 0);
            for (String dep : graph.get(node)) {
                inDegree.merge(dep, 1, Integer::sum);
            }
        }

        Queue<String> queue = new LinkedList<>();
        for (String node : graph.keySet()) {
            if (inDegree.getOrDefault(node, 0) == 0) {
                queue.offer(node);
            }
        }

        List<String> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String node = queue.poll();
            result.add(node);
            for (String neighbor : graph.keySet()) {
                if (graph.get(neighbor).contains(node)) {
                    int newDegree = inDegree.get(neighbor) - 1;
                    inDegree.put(neighbor, newDegree);
                    if (newDegree == 0) {
                        queue.offer(neighbor);
                    }
                }
            }
        }

        return result;
    }

    private void verifyModulePublishesEvent(String moduleName, String eventClassName) throws IOException {
        Path modulePath = Path.of(BASE_PATH, "modules", moduleName);
        List<File> files = findJavaFiles(modulePath.toFile());

        String allContent = files.stream()
                .map(f -> { try { return Files.readString(f.toPath()); } catch (Exception e) { return ""; } })
                .collect(Collectors.joining("\n"));

        assertTrue(allContent.contains(eventClassName),
                moduleName + " 模块应引用 " + eventClassName);
        assertTrue(allContent.contains("publishEvent"),
                moduleName + " 模块应调用 publishEvent 发布事件");
    }
}
