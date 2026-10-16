# 公告类型常量 NOTICE_TYPE_CONFIG

> 分支：`feature/ticket-constants`

## 功能说明

在 `frontend/src/utils/constants.js` 中新增了公告类型的展示配置与辅助函数，供公告管理页面等需要展示公告类型标签的地方统一使用。

## 新增内容

| 名称 | 说明 |
| ---- | ---- |
| `NOTICE_TYPE_CONFIG` | 公告类型配置对象（label 显示名 + type 标签颜色） |
| `noticeLabel(type)` | 根据类型值返回中文标签 |
| `noticeType(type)` | 根据类型值返回 Element Plus 标签颜色类型 |

## 类型说明

- `SYSTEM` 系统公告（danger）
- `MAINTENANCE` 维护通知（warning）
- `RULES` 制度规范（primary）
- `OTHER` 其他（info）

## 说明

与 `feature/notice-page` 分支的公告管理页面配合使用，将公告类型展示抽离为统一常量，便于维护复用。