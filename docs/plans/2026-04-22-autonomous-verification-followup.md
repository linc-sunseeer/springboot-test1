# 自主驗證後續處理計畫

> **給 opencode：** 必須使用 superpowers:executing-plans，依照任務逐步執行本計畫。

**目標：** 修正自主驗證中找到的落差：對齊郵件 / 執行環境設定、移除殘留的樣板值與憑證，並讓 CSV 與管理者畫面更符合要件。

**架構：** 維持既有的 Spring Boot + Vue 3 分層，只做最小必要修正。優先清理設定，不額外新增抽象層，並以現有 controller / service / view 作為主要驗證面。每次修改都要用指定測試與最後建置確認。

**技術棧：** Spring Boot 3.2、Vue 3、Vite、MyBatis-Plus、JUnit5/MockMvc、既有文件。

---

### 任務 1：對齊郵件執行環境網址

**檔案：**
- Modify: `demo/compose.yaml`
- Review: `demo/src/main/resources/application.yml`
- Review: `demo/src/main/java/com/example/demo/service/EmailVerificationService.java`
- Review: `demo/src/main/java/com/example/demo/service/PasswordResetService.java`

**步驟 1：確認目前網址不一致**
- 確認前端實際對外是 `5174`，但郵件連結仍指向 `5173`。

**步驟 2：做最小設定修正**
- 讓郵件連結的 base URL 與 compose 實際前端埠號一致。

**步驟 3：確認連結一致**
- 重新檢查設定與 service 程式，確認所有產生的連結都使用同一個 base URL。

### 任務 2：移除樣板 / 示範殘留

**檔案：**
- Modify: `demo/frontend/src/views/ForgotPasswordView.vue`
- Modify: `demo/frontend/src/views/MyPageView.vue`
- Review: `demo/compose.yaml`
- Review: `demo/src/test/resources/application-test.yml`

**步驟 1：找出可見的樣板值**
- 找出任何預設示範信箱或寫死的 fallback 字串。

**步驟 2：改成中性預設值**
- 移除使用者看得到的預填示範值。

**步驟 3：重新檢查 UI 文案**
- 確認正常流程裡不再殘留 demo 專用文字。

### 任務 3：加強 CSV 與管理者畫面呈現

**檔案：**
- Modify: `demo/src/main/java/com/example/demo/controller/AdminController.java`
- Modify: `demo/src/main/java/com/example/demo/service/AdminAggregateService.java`
- Modify: `demo/frontend/src/views/admin/AdminDailyView.vue`
- Modify: `demo/src/test/java/com/example/demo/AdminAggregateServiceTest.java`
- Modify: `demo/src/test/java/com/example/demo/AdminControllerTest.java`

**步驟 1：補 / 調整匯出行為測試**
- 覆蓋 CSV 的 content type / 編碼，以及預期的輸出資料形狀。

**步驟 2：必要時做最小後端修正**
- 讓匯出行為符合已驗證的執行環境結果。

**步驟 3：重新確認日次統計畫面**
- 確認管理者日次頁清楚呈現要件要求的數量與姓名清單。

### 任務 4：最後驗證與文件同步

**檔案：**
- Review changed files only
- Modify: `docs/README.md`

**步驟 1：執行針對性檢查**
- 跑受影響檔案的測試，以及必要的輕量建置檢查。

**步驟 2：必要時更新文件索引**
- 確認這份 4/22 計畫已列入 `docs/README.md`。

**步驟 3：重新讀一次文件**
- 確認 README 仍只有一個入口，且計畫標題保持一致。
