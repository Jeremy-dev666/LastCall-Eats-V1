# LastCall Eats — Getting Started Guide

## 1. Pull the Latest Code

```bash
git pull origin main
```

---

## 2. Start the Backend

Open the project root directory in **IntelliJ IDEA**.

Open `lastcall-api/src/main/resources/application-dev.yml` and make sure the database password matches your local setup:

```yaml
spring:
  datasource:
    password: your_local_database_password
```

Other configurations (JWT, Stripe key, etc.) are already set in the `.env` file — no changes needed.

Run the main entry class:
`lastcall-api/src/main/java/com/lastcalleats/LastCallEatsApplication.java`

Right-click → **Run**. You'll know it's up when you see `Started LastCallEatsApplication` in the console.

---

## 3. Initialize the Database

**No manual steps required.** Start PostgreSQL with `docker compose up -d postgres`; on application startup, Flyway creates the schema automatically and the dev profile seeds test data when the database is empty (re-seed with `app.seed.force=true`).

### Test Accounts (password for all: `111111`)

**Users**

| Email | Nickname |
|------|------|
| alice@example.com | Alice |
| bob@example.com | Bob |
| charlie@example.com | Charlie |

**Merchants**

| Email | Store Name |
|------|------|
| bakery@example.com | Golden Bakery |
| sushi@example.com | Sakura Sushi |
| cafe@example.com | Brew & Bite Cafe |

---

## 4. Start the Frontend

```bash
cd lastcall-frontend/lastcall-app
npm install        # first time only
npm run dev        # auto-detects your local IP and starts the server
```

Open **Expo Go** on your phone and scan the QR code shown in the terminal.

> Your phone and computer must be on the same WiFi network. If you switch networks, just re-run `npm run dev` — no code changes required.



---
# LastCall Eats — 启动指南

## 1. 拉取代码

```bash
git pull origin main
```

---

## 2. 后端启动

用 **IntelliJ IDEA** 打开项目根目录。

打开 `lastcall-api/src/main/resources/application-dev.yml`，确认数据库密码和自己电脑一致：

```yaml
spring:
  datasource:
    password: 你的本地数据库密码
```

其他配置（JWT、Stripe key 等）已在 `.env` 文件中，无需修改。

运行入口类：
`lastcall-api/src/main/java/com/lastcalleats/LastCallEatsApplication.java`

右键 → **Run**，看到 `Started LastCallEatsApplication` 即启动成功。

---

## 3. 数据库初始化

**无需手动操作。** 先 `docker compose up -d postgres` 启动 PostgreSQL；应用启动时 Flyway 自动建表，dev 环境在库为空时自动注入测试数据（强制重灌：配置 `app.seed.force=true`）。

### 测试账号（密码统一：`111111`）

**用户**

| 邮箱 | 昵称 |
|------|------|
| alice@example.com | Alice |
| bob@example.com | Bob |
| charlie@example.com | Charlie |

**商家**

| 邮箱 | 店名 |
|------|------|
| bakery@example.com | Golden Bakery |
| sushi@example.com | Sakura Sushi |
| cafe@example.com | Brew & Bite Cafe |

---

## 4. 前端启动

```bash
cd lastcall-frontend/lastcall-app
npm install        # 首次需要
npm run dev        # 自动检测本机 IP 并启动
```

用手机打开 **Expo Go**，扫描终端中的二维码即可。

> 手机和电脑必须在同一 WiFi 下。每次换网络重新运行 `npm run dev` 即可，无需改代码。