# Cutoff / Password Verification / Payment Approval Hardening Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Enforce JST cutoff reliably, require verified email before profile password changes, and move CASH payment to user request + admin final approval with approval email.

**Architecture:** Keep current controller/service split, add minimal status transition guards in `ReservationService`, and reuse existing `MailService`/`UserService` for payment approval notifications. Preserve existing Vue API/data flow by extending current endpoints instead of replacing them.

**Tech Stack:** Spring Boot 3.2, MyBatis-Plus, JUnit5/MockMvc/Mockito, Vue 3 + Vite.

---

### Task 1: Lock current behavior with tests (RED)

**Files:**
- Modify: `demo/src/test/java/com/example/demo/UserServiceHashingTest.java`
- Modify: `demo/src/test/java/com/example/demo/ReservationServiceTest.java`
- Modify: `demo/src/test/java/com/example/demo/ReservationControllerTest.java`
- Modify: `demo/src/test/java/com/example/demo/AdminControllerTest.java`

**Step 1: Add failing test for password guard**
- Add a test proving `updatePassword` rejects users whose `emailVerified=false`.

**Step 2: Add failing tests for payment request/approval states**
- Add a test proving CASH user action becomes `PAYMENT_REQUESTED` (not `PAID`).
- Add a test proving user cannot mark CASH as `PAID` directly.
- Add a test proving admin can approve a requested CASH payment to `PAID` and mail is sent.

**Step 3: Add failing controller tests for new request/approval API behavior**
- User endpoint accepts `PAYMENT_REQUESTED`.
- Admin endpoint accepts payment approval PATCH.

**Step 4: Run targeted tests and confirm RED**
- Run only the touched test classes and verify expected failures.

### Task 2: Backend implementation (GREEN)

**Files:**
- Modify: `demo/src/main/java/com/example/demo/service/ReservationService.java`
- Modify: `demo/src/main/java/com/example/demo/controller/ReservationController.java`
- Modify: `demo/src/main/java/com/example/demo/controller/AdminController.java`
- Modify: `demo/src/main/java/com/example/demo/service/UserService.java`
- Modify: `demo/src/main/java/com/example/demo/service/AdminAggregateService.java`

**Step 1: Cutoff enforcement hardening**
- Force service default clock to Asia/Tokyo.
- Centralize cutoff check in one helper and reuse it for create/cart-checkout paths.

**Step 2: Password verification guard**
- In `UserService.updatePassword`, reject unverified users with clear message.

**Step 3: Payment request/approval transitions**
- Add `markPaymentRequested(userId, reservationId)` for CASH.
- Keep direct user `PAID` only for non-CASH.
- Add `approvePaymentAsAdmin(reservationId)` requiring `PAYMENT_REQUESTED` → `PAID` for CASH.

**Step 4: Approval email**
- Send payment-approved email in admin approval flow.

**Step 5: Admin daily data support**
- Include `reservationId` and `paymentMethod` in daily users payload for approval actions.

### Task 3: Frontend implementation

**Files:**
- Modify: `demo/frontend/src/api/user.js`
- Modify: `demo/frontend/src/api/admin.js`
- Modify: `demo/frontend/src/views/MyPageView.vue`
- Modify: `demo/frontend/src/views/admin/AdminDailyView.vue`

**Step 1: User payment request UX**
- Preserve existing buttons and flow, but for CASH send `PAYMENT_REQUESTED` and show pending label.

**Step 2: Admin approval UX**
- Add admin action button for `PAYMENT_REQUESTED` CASH rows.

**Step 3: Status labels/tone update**
- Add `PAYMENT_REQUESTED` label and visual tone consistently.

### Task 4: Verification

**Files:**
- Review changed files only

**Step 1: LSP diagnostics**
- Run diagnostics on changed backend/frontend files.

**Step 2: Targeted tests**
- Run touched test classes first.

**Step 3: Full backend tests**
- Run `mvn test`.

**Step 4: Frontend build + container/API checks**
- Run frontend build.
- Run lightweight API checks for payment request and admin approval endpoints.
