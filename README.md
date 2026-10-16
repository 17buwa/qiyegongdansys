# 企业工单系统 - 后端

基于 **Spring Boot 4.1.1 + MyBatis-Plus + MySQL** 的工单管理系统后端，提供 RESTful API。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 4.1.1 | 核心框架 |
| Spring Security | - | 认证与授权 |
| JWT | jjwt 0.12.6 | 无状态令牌认证 |
| MyBatis-Plus | 3.5.9 | ORM（含分页、逻辑删除） |
| MySQL | 8.x | 数据持久化 |
| AspectJ | 1.9.22 | 操作日志 AOP 切面 |

## 快速启动

1. **创建数据库**：执行 [sql/init.sql](sql/init.sql)（建库、建表、初始化角色/权限/工单类型）
2. **修改配置**：`src/main/resources/application.properties` 中数据库账号密码（默认 `root/123456`）
3. **启动应用**：运行 `QiyegongdanApplication`
   ```
   # 数据库连不上时确保 MySQL 已启动
   ./gradlew bootRun
   ```

> 默认用户由启动时自动创建（`config/DataInitializer`）：
> - 管理员：`admin / 123456`
> - 客服：`agent01 / 123456`、`agent02 / 123456`
> - 普通用户：`user01 / 123456`

## 目录结构

```
src/main/java/com/example/qiyegongdan
├── annotation/OperLog       操作日志注解
├── aspect/OperLogAspect     AOP 切面，自动记录带 @OperLog 的方法
├── common/                  统一返回结果 Result、业务异常、全局异常处理
├── config/                  Security、MyBatis-Plus、数据初始化配置
├── controller/              RESTful 控制器
├── dto/                     请求/响应 DTO
├── entity/                  数据库实体
├── mapper/                  MyBatis-Plus Mapper
├── security/                JWT 工具、过滤、当前用户上下文
└── service/                 业务逻辑层
```

## API 概览

| 模块 | 路径 | 说明 |
|------|------|------|
| 认证 | `/api/auth/login` | 登录，返回 JWT Token |
| 用户 | `/api/users/**` | 用户 CRUD、个人信息、分页 |
| 角色权限 | `/api/roles/**` | 角色列表、权限配置 |
| 工单类型 | `/api/ticket-types/**` | 类型及自定义字段管理 |
| 工单 | `/api/tickets/**` | 创建、分页、详情、分配、状态流转、评论 |
| 附件 | `/api/files/**` | 本地上传 / 下载 |
| 统计 | `/api/statistics/**` | 总览、按状态/优先级/类型、趋势 |
| 日志 | `/api/logs/**` | 操作日志分页（仅管理员） |

## 功能特性

- **RBAC 权限**：用户 → 角色 → 权限三级模型，`@PreAuthorize` 接口级校验
- **JWT 认证**：无状态，`JwtAuthenticationFilter` 解析请求头 `Authorization: Bearer <token>`
- **工单全流程**：创建 → 分配（手动/自动）→ 处理 → 评论/附件 → 解决/关闭
- **自定义字段**：按工单类型动态配置字段，前端动态渲染
- **自动分配**：最少负载算法，分配给处理中工单最少的客服
- **操作日志**：AOP + 注解记录用户关键操作
- **附件**：本地磁盘存储，文件不可执行安全下载

## 权限编码

| 编码 | 说明 |
|------|------|
| `user:manage` | 用户管理 |
| `role:manage` | 角色权限管理 |
| `ticket:create` | 创建工单 |
| `ticket:process` | 处理工单 |
| `ticket:assign` | 分配工单 |
| `ticket:view` | 查看工单 |
| `statistics:view` | 查看统计 |
| `log:view` | 查看日志 |