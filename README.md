# 图书管理系统

一个前后端分离的图书管理系统原型，包含以下能力：

- Spring Boot + Spring Security + JWT + MyBatis-Plus 后端
- Vue 3 + Vite + Pinia + Element Plus 前端
- 角色分为系统管理员、图书管理员、读者
- 覆盖图书分类、图书主档、实体副本、读者档案、借阅归还、逾期管理、统计报表

## 目录结构

- `backend`: Java 后端工程
- `frontend`: Vue 前端工程

## 后端启动

1. 准备 `MySQL 8`，执行 [schema.sql](/D:/develop/aicode/codex5/backend/src/main/resources/db/schema.sql) 和 [data.sql](/D:/develop/aicode/codex5/backend/src/main/resources/db/data.sql)。
2. 修改 [application.yml](/D:/develop/aicode/codex5/backend/src/main/resources/application.yml) 中的数据库连接，或通过环境变量 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD` 覆盖。
3. 使用 `JDK 17` 运行：

```powershell
$env:JAVA_HOME='D:\sofeInstall\jdk\openjdk-17.0.7.0.7-1.win.x86_64'
$env:Path='D:\sofeInstall\jdk\openjdk-17.0.7.0.7-1.win.x86_64\bin;' + $env:Path
mvn.cmd -s .mvn\local-settings.xml spring-boot:run
```

默认端口为 `8080`。

## 前端启动

```powershell
cd frontend
npm.cmd install
npm.cmd run dev -- --host 0.0.0.0
```

默认访问地址为 `http://127.0.0.1:5173`。

## 演示账号

- 管理员：`admin / Admin@123`
- 图书管理员：`librarian / Admin@123`
- 读者：`reader01 / Admin@123`

## 说明

- 初始化演示账号为了便于首次导库使用了 `{noop}` 前缀密码；系统内新建账号会按默认加密策略存储。
- 借阅逻辑限制为最多同时借 `5` 本，默认借期 `30` 天。
- 逾期状态会在借阅相关接口和报表查询时自动刷新。
# bookManag
