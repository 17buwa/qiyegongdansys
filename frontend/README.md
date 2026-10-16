# 企业工单系统 - 前端

基于 **Vue 3 + Vite + Element Plus + Pinia + Vue Router + Axios + ECharts** 的单页应用，对接后端 RESTful API。

## 技术栈

| 技术 | 说明 |
|------|------|
| Vue 3 | 渐进式框架（Composition API） |
| Vite 5 | 开发/构建工具 |
| Element Plus | 企业级 UI 组件库 |
| Pinia | 状态管理 |
| Vue Router | 前端路由（含角色守卫） |
| Axios | HTTP 客户端（统一 Token / 错误处理） |
| ECharts | 数据看板图表 |

## 运行环境

> 需要 Node.js（>= 18）与 npm。当前机器未安装 Node，请先安装后再运行。

```bash
# 1. 安装依赖
npm install

# 2. 启动开发服务器（默认 http://localhost:5173）
npm run dev

# 3. 生产构建
npm run build
```

开发服务器已配置代理：`/api` 请求转发到 `http://localhost:8080`（后端）。

## 登录账号

后端启动时自动初始化以下账号（密码均为 `123456`）：

| 账号 | 角色 | 说明 |
|------|------|------|
| `admin` | 管理员 ADMIN | 全部功能 |
| `agent01` / `agent02` | 客服 AGENT | 处理、分配工单 |
| `user01` | 普通用户 CUSTOMER | 创建工单 |

## 目录结构

```
frontend/src
├── api/           API 请求封装（request.js 统一拦截器）
├── router/        路由与角色守卫
├── stores/        Pinia 状态（用户、权限）
├── layout/        主布局（侧边栏 + 顶栏 + 菜单权限）
├── views/         页面
│   ├── Login.vue          登录
│   ├── Dashboard.vue      工作台（统计看板，ECharts）
│   ├── TicketList.vue     工单列表（筛选/分页）
│   ├── TicketCreate.vue   创建工单（动态自定义字段）
│   ├── TicketDetail.vue   工单详情（评论/附件/分配/状态流转）
│   ├── UserManage.vue     用户管理
│   ├── RoleManage.vue     角色权限配置
│   ├── TicketTypeManage.vue 工单类型与自定义字段管理
│   ├── LogList.vue        操作日志
│   └── Profile.vue        个人中心
└── utils/        常量与展示配置
```

## 权限说明

侧边栏菜单与路由根据当前用户角色（ADMIN/AGENT/CUSTOMER）动态显示与拦截：

- 管理员：全部模块（用户、角色、工单类型、日志、统计）
- 客服：工单、统计、用户查看
- 普通用户：工单（仅本人创建）、个人中心

## 说明

- 工单状态流转按钮依据后端状态机动态展示可用目标状态
- 创建工单时，根据所选工单类型动态渲染其自定义字段表单
- 附件下载通过后端 `/api/files/download?name=xxx`（已开放白名单）
- 未携带 Token 访问受限接口时，Axios 拦截器会自动跳转登录页