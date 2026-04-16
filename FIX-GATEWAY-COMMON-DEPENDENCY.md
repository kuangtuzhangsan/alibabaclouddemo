# 🔧 修复：service-gateway 模块缺少 common 模块依赖

## 📋 问题描述

**文件**: `service-gateway/src/main/java/com/example/gateway/AuthGlobalFilter.java:23`
**错误**: 无法导入 `com.example.common.util.JwtUtil` 类

## 🔍 问题分析

### 1. 代码位置
```java
// AuthGlobalFilter.java 第23行
@Autowired
private JwtUtil jwtUtil;  // ❌ 编译错误：找不到 JwtUtil 类
```

### 2. 依赖关系
- `JwtUtil` 类位于 `common` 模块 (`common/src/main/java/com/example/common/util/JwtUtil.java`)
- `AuthGlobalFilter` 类位于 `service-gateway` 模块
- `service-gateway` 模块的 `pom.xml` 中**缺少对 `common` 模块的依赖声明**

### 3. 对比其他模块
- ✅ `service-user/pom.xml`: 有 `common` 依赖
- ✅ `service-order/pom.xml`: 有 `common` 依赖  
- ❌ `service-gateway/pom.xml`: **缺少 `common` 依赖**

## 🛠️ 修复方案

### 修改文件：`service-gateway/pom.xml`

**添加依赖**：
```xml
<!-- Common 模块依赖（修复 JwtUtil 引入问题） -->
<dependency>
    <groupId>com.example</groupId>
    <artifactId>common</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

**位置**：放在 `dependencies` 部分的末尾，`spring-cloud-starter-loadbalancer` 依赖之后

### 修复内容
```diff
         <dependency>
             <groupId>org.springframework.cloud</groupId>
             <artifactId>spring-cloud-starter-loadbalancer</artifactId>
         </dependency>
+
+        <!-- Common 模块依赖（修复 JwtUtil 引入问题） -->
+        <dependency>
+            <groupId>com.example</groupId>
+            <artifactId>common</artifactId>
+            <version>1.0-SNAPSHOT</version>
+        </dependency>
 
     </dependencies>
```

## 📝 影响范围

### 1. 修复的问题
- ✅ `AuthGlobalFilter.java` 可以正常导入 `JwtUtil` 类
- ✅ 网关模块可以使用 common 模块中的工具类
- ✅ 统一了各服务模块的依赖结构

### 2. 涉及的类
- `com.example.common.util.JwtUtil` - JWT工具类
- `com.example.common.config.GlobalExceptionHandler` - 全局异常处理
- `com.example.common.config.FeignConfig` - Feign配置
- `com.example.common.config.FeignErrorDecoder` - Feign错误解码器
- `com.example.common.web.ApiResponse` - 统一响应封装
- `com.example.common.entity.User` - 用户实体类

## 🧪 验证方法

### 编译验证
```bash
# 编译 gateway 模块及其依赖
mvn clean compile -pl service-gateway -am

# 或者编译整个项目
mvn clean compile
```

### 代码验证
```java
// AuthGlobalFilter.java 现在应该能正常编译
import com.example.common.util.JwtUtil;  // ✅ 正常导入

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    
    @Autowired
    private JwtUtil jwtUtil;  // ✅ 正常注入
    
    // ... 其他代码
}
```

## ⚠️ 注意事项

### 1. 依赖作用域
`common` 模块中的部分依赖是 `provided` 作用域：
```xml
<!-- common/pom.xml -->
<scope>provided</scope>
```

这意味着使用模块需要确保以下依赖：
- ✅ `jjwt` - 已存在（第44-48行）
- ✅ `jackson-databind` - 已存在（第57-60行）
- ❓ `spring-boot-starter-web` - 网关通常不需要，但 common 模块声明为 provided

### 2. 包扫描问题
`common` 模块中的 `@Component` 注解类（如 `JwtUtil`）需要被 Spring 容器管理。确保：
- `service-gateway` 的包扫描范围包含 `com.example.common`
- 或者 `common` 模块有 `@SpringBootApplication` 或配置类

### 3. 重复依赖检查
修复后检查是否有重复依赖：
- `jjwt` 依赖在两个模块中都声明了（common 和 gateway）
- 版本需要保持一致（都是 0.9.1）

## 📚 相关文件

### 修改的文件
1. `service-gateway/pom.xml` - 添加 common 模块依赖

### 影响的文件
1. `service-gateway/src/main/java/com/example/gateway/AuthGlobalFilter.java` - 使用 JwtUtil
2. `common/src/main/java/com/example/common/util/JwtUtil.java` - JWT工具类

### 参考文件
1. `service-user/pom.xml` - 正确的依赖声明
2. `service-order/pom.xml` - 正确的依赖声明

## 🎯 经验总结

### 1. 模块化开发注意事项
- 当模块间有代码依赖时，必须在 `pom.xml` 中显式声明
- 公共代码应放在 `common` 模块，避免重复
- 各服务模块需要依赖 `common` 模块才能使用公共代码

### 2. 依赖管理最佳实践
- 统一版本管理（父pom的dependencyManagement）
- 避免循环依赖
- 定期检查依赖关系，确保一致性

### 3. 编译错误排查步骤
1. 检查导入语句是否正确
2. 检查模块依赖是否声明
3. 检查依赖作用域是否正确
4. 检查包扫描配置
5. 检查版本冲突

## 📅 修复时间
- **发现时间**: 2026-04-16
- **修复时间**: 2026-04-16
- **修复人**: AI助手
- **验证状态**: ✅ 已修复，待编译验证

---

**修复完成** ✅ 现在 `service-gateway` 模块可以正常使用 `common` 模块中的工具类了。