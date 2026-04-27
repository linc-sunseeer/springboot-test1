# Mixed User/Admin UI Redesign Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Redesign the user and admin experience so the front-end keeps a warm Bento feel while the admin side becomes a cleaner operational dashboard.

**Architecture:** Keep the existing Vue route structure and backend APIs where possible, but reorganize the page information architecture. User-facing pages will emphasize clear tabbed flows and softer visual hierarchy; admin pages will shift toward denser summaries, drill-down views, and action-oriented tables. Only add backend fields/endpoints where the monthly and daily admin screens need more detail than they currently receive.

**Tech Stack:** Vue 3, Pinia, Vite, Tailwind CSS, Spring Boot, MyBatis-Plus, JUnit 5.

---

### Task 1: Fix top user navigation semantics

**Files:**
- Modify: `frontend/src/App.vue`
- Test/Verify: `frontend/src/router/index.js`

**Step 1: Define the target navigation behavior**

- Logged out: show `ログイン` instead of `ユーザー`
- Logged in as USER: hide `ログイン`, show `本日メニュー` and `マイページ`
- Logged in as ADMIN: keep admin entry separate

**Step 2: Implement the nav label and conditional rendering update**

- Replace the misleading `ユーザー` nav item with `ログイン`
- Keep the Bento pill-nav style for front-end routes

**Step 3: Verify routing behavior manually**

Run the app and confirm:
- `/login` is reachable from nav only when logged out
- `マイページ` appears only for logged-in users

**Step 4: Verify frontend build**

Run: `npm run build`
Expected: PASS

### Task 2: Redesign MyPage into user-friendly tabs

**Files:**
- Modify: `frontend/src/views/MyPageView.vue`
- Modify: `frontend/src/api/user.js` (only if tab data separation needs API helpers)
- Modify: `frontend/src/stores/auth.js` (only if user display updates need syncing)

**Step 1: Refactor page structure into three tabs**

- `我的訂單` / order management
- `個人資料` / profile editing
- `付款紀錄` / payment history

**Step 2: Reassign current content into the right tab**

- Move current reservation and order actions into `我的訂單`
- Move name/password editing into `個人資料`
- Move paid/unpaid historical rows into `付款紀錄`

**Step 3: Improve hierarchy and visual harmony**

- Reduce duplicated summary cards
- Make “confirm order” and “mark paid” feel like part of one order flow
- Use a softer Bento-style section header system

**Step 4: Verify user flows manually**

Confirm:
- order confirmation still works
- payment update still works
- profile update still works

**Step 5: Verify frontend build**

Run: `npm run build`
Expected: PASS

### Task 3: Enrich monthly aggregates with drill-down detail

**Files:**
- Modify: `src/test/java/com/example/demo/AdminControllerTest.java`
- Modify: `src/test/java/com/example/demo/AdminAggregateServiceTest.java`
- Modify: `src/main/java/com/example/demo/service/AdminAggregateService.java`
- Modify: `frontend/src/views/admin/AdminMonthlyView.vue`
- Modify: `frontend/src/api/admin.js` (if new detailed payload shape is needed)

**Step 1: Write/extend failing backend tests for richer monthly detail**

Expected monthly payload should include, per day:
- reservation count
- paid count
- unpaid count
- optional top menu breakdown or reservation details summary

**Step 2: Run focused backend tests to confirm failure**

Run: `mvn "-Dtest=AdminAggregateServiceTest,AdminControllerTest" test`
Expected: FAIL on missing richer monthly structure

**Step 3: Implement minimal backend enhancement**

- Extend monthly aggregation data without breaking current consumers

**Step 4: Redesign `AdminMonthlyView.vue`**

- Convert the current flat list into a richer monthly dashboard
- Show daily cards or rows with count + paid/unpaid + expandable detail area

**Step 5: Re-run tests and frontend build**

Run:
- `mvn "-Dtest=AdminAggregateServiceTest,AdminControllerTest" test`
- `npm run build`
Expected: PASS

### Task 4: Upgrade daily aggregates into an operational dashboard

**Files:**
- Modify: `frontend/src/views/admin/AdminDailyView.vue`
- Modify: `src/main/java/com/example/demo/service/AdminAggregateService.java` (only if more daily detail is required)
- Modify: `src/test/java/com/example/demo/AdminAggregateServiceTest.java` (if payload changes)

**Step 1: Identify missing decision-making details**

Target additions:
- clearer reservation pipeline summary
- menu share / mix visibility
- more useful user table actions or stronger visual grouping

**Step 2: Redesign the page layout**

- keep summary cards
- strengthen section hierarchy
- make reservation user table feel like an operations surface, not just a dump

**Step 3: Add any minimal backend fields only if required**

- do not add new backend logic unless the UI truly needs more structured detail

**Step 4: Verify frontend build and backend tests**

Run:
- `npm run build`
- `mvn test`
Expected: PASS

### Task 5: Redesign admin user management into a professional workflow page

**Files:**
- Modify: `frontend/src/views/admin/AdminUsersView.vue`
- Modify: `frontend/src/api/admin.js` (only if interaction changes need helper functions)
- Modify: `src/main/java/com/example/demo/service/AdminAggregateService.java` (only if present data is insufficient)

**Step 1: Reorganize page information architecture**

- left: compact user list/search/filter area
- right: selected user detail panel
- detail panel should clearly separate order history, payment record, and status actions

**Step 2: Improve action semantics**

- order status controls should look like admin workflow controls, not user-facing buttons
- payment status should be displayed clearly as record state, not visually mixed with order-state buttons

**Step 3: Improve summary density**

- show useful user summary metrics at selection time
- reduce awkward empty space and debug-like appearance

**Step 4: Verify frontend build**

Run: `npm run build`
Expected: PASS

### Task 6: End-to-end verification

**Files:**
- Verify only

**Step 1: Run backend verification**

Run: `mvn test`
Expected: PASS

**Step 2: Run frontend verification**

Run: `npm run build`
Expected: PASS

**Step 3: Manual UX verification checklist**

Check all of the following:
- front nav shows `ログイン` only when logged out
- MyPage tabs are clear and non-overlapping
- payment/order actions feel grouped under `我的訂單`
- monthly aggregates show more than a bare day/count list
- daily aggregates look more operational and informative
- admin user management feels like a real back-office workflow page
