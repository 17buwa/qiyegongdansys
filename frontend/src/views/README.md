# 公告管理模块 NoticeManage

> 分支：`feature/notice-page`

## 功能说明

公告管理页面，用于管理员查看和管理企业系统公告。

## 功能点

- 公告列表展示：标题、类型、状态、发布人、发布时间
- 按标题关键字筛选
- 按公告类型筛选
  - SYSTEM 系统公告
  - MAINTENANCE 维护通知
  - RULES 制度规范
  - OTHER 其他
- 分页展示
- "发布公告"入口（预留）

## 文件说明

| 文件 | 说明 |
| ---- | ---- |
| `NoticeManage.vue` | 公告管理页面视图 |
| `router/index.js` | 注册了 `/notices` 路由，图标为 `Bell`，仅 `ADMIN` 角色可见 |

## 说明

当前使用前端模拟数据展示，后续可对接后端公告接口（在 `src/api/` 下新增 `notice.js`）替换 `mockList` 数据源。