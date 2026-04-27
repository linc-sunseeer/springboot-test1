# 環境設定 & DB 移植指南

## ⚠️ 緊急：密碼洩露處理

你的 `.env` 曾被推上 GitHub，請立刻：
1. 到 Google 帳號 → 安全性 → 應用程式密碼 → **刪除舊的，產生新的**
2. 更改 MySQL root 密碼

---

## 1. .env 設定（公司電腦）

### 步驟 1：複製範例檔案
```bash
cd demo
copy .env.example .env
```

### 步驟 2：修改 .env 內容
打開 `demo/.env`，修改以下內容：

```env
# 資料庫密碼（改成你的）
SPRING_DATASOURCE_PASSWORD=你的MySQL密碼

# JWT 密鑰（改成隨機字串）
APP_JWT_SECRET=隨便打一串亂七八糟的字至少要32位元

# 郵件設定（改成你的 Gmail 應用程式密碼）
SPRING_MAIL_USERNAME=你的gmail@gmail.com
SPRING_MAIL_PASSWORD=新的gmail應用程式密碼
```

### 步驟 3：確認 .gitignore 有生效
`.gitignore` 已經加了 `demo/.env`，以後不會再被推上去。
檢查方法：
```bash
git status
```
如果看不到 `.env` 就代表成功了。

---

## 2. DB 移植到公司

### 方法一：用 SQL 檔案（推薦，最簡單）

你的專案裡已經有 SQL 檔案：
- `demo/src/main/resources/schema.sql` → 建表結構
- `demo/src/main/resources/data.sql` → 測試資料

#### 步驟：
1. 在公司電腦安裝 MySQL
2. 建立資料庫：
```sql
CREATE DATABASE bento_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 匯入 SQL：
```bash
mysql -u root -p bento_db < demo/src/main/resources/schema.sql
mysql -u root -p bento_db < demo/src/main/resources/data.sql
```

4. 修改 `.env` 的資料庫密碼
5. 啟動專案

---

### 方法二：用 mysqldump 匯出本地資料

如果你本地 DB 有重要資料想帶過去：

#### 在家裡電腦（匯出）：
```bash
mysqldump -u root -p bento_db > bento_db_backup.sql
```
把 `bento_db_backup.sql` 用 USB 或 email 帶到公司。

#### 在公司電腦（匯入）：
```bash
# 先建資料庫
mysql -u root -p -e "CREATE DATABASE bento_db CHARACTER SET utf8mb4;"

# 匯入
mysql -u root -p bento_db < bento_db_backup.sql
```

---

### 方法三：用 Docker（最快）

如果公司電腦有 Docker：

```bash
cd demo
docker-compose up -d
```

這樣會自動啟動 MySQL + 自動執行 schema.sql 和 data.sql。

---

## 3. 公司電腦完整啟動步驟

```bash
# 1. 從 GitHub 抓下來
git clone https://github.com/linc-sunseeer/springboot-test1.git
cd springboot-test1/demo

# 2. 設定 .env
copy .env.example .env
# 然後用文字編輯器改密碼

# 3. 啟動 MySQL（選一種）
# 選項 A：用 Docker
docker-compose up -d

# 選項 B：用本地 MySQL
# 確定 MySQL 有跑，然後建 bento_db 資料庫

# 4. 啟動後端
cd demo
mvn spring-boot:run

# 5. 啟動前端（另一個終端）
cd demo/frontend
npm install
npm run dev
```

---

## 4. 常見問題

### Q: 啟動後說連不上資料庫？
A: 檢查 `.env` 的 `SPRING_DATASOURCE_URL` 和密碼是否正確。

### Q: 前端說連不上後端？
A: 確認後端有在跑（看 8080 port），前端 `src/api/http.js` 的 API URL 是否正確。

### Q: git pull 說有衝突？
A: 在家裡先 `git add . && git commit -m "save" && git push`，公司再 `git pull`。
