# Reservation, User, and Admin Enhancements Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Fix reservation error handling and complete the missing user/admin order-management flows.

**Architecture:** Keep the current Spring Boot + MyBatis-Plus + Vue structure. Add focused backend endpoints/service methods for reservation status management and admin user/order views, then wire them into MyPage and new admin screens. Reuse existing entities where possible and return consistent JSON error payloads for business-rule failures.

**Tech Stack:** Spring Boot, MyBatis-Plus, JUnit 5, Mockito, Vue 3, Pinia, Vite, Tailwind CSS.

---

### Task 1: Reservation validation errors return concrete messages

**Files:**
- Modify: `src/test/java/com/example/demo/ReservationServiceTest.java`
- Create/Modify: `src/test/java/com/example/demo/ReservationControllerTest.java`
- Modify: `src/main/java/com/example/demo/controller/ReservationController.java`
- Modify: `src/main/java/com/example/demo/controller/GlobalExceptionHandler.java`

**Steps:**
1. Add a failing controller-level test proving an over-cutoff reservation returns HTTP 400 with `message="予約可能時間を過ぎています。"`.
2. Run the focused test and confirm it fails for the expected reason.
3. Implement/adjust exception mapping so `IllegalArgumentException` becomes a structured 400 response.
4. Re-run the focused test and existing reservation tests.

### Task 2: User reservation confirmation + payment flow on My Page

**Files:**
- Modify: `frontend/src/views/MyPageView.vue`
- Modify: `frontend/src/api/user.js`
- Modify: `src/test/java/com/example/demo/ReservationServiceTest.java`
- Modify: `src/main/java/com/example/demo/service/ReservationService.java`
- Modify: `src/main/java/com/example/demo/controller/ReservationController.java`

**Steps:**
1. Add a failing backend test for fetching the signed-in user’s reservations in a stable order.
2. Add a failing backend test for updating order status/payment state only on that user’s reservation.
3. Implement the minimal backend support for user-scoped reservation listing and status/payment update.
4. Update `MyPageView.vue` to show reservation cards/history with clear confirmation and payment actions.
5. Verify focused backend tests plus frontend build.

### Task 3: Profile editing UX polish and completion

**Files:**
- Modify: `frontend/src/views/MyPageView.vue`
- Modify: `frontend/src/stores/auth.js`
- Modify: `src/test/java/com/example/demo/UserProfileControllerTest.java` (create if missing)
- Modify: `src/main/java/com/example/demo/controller/UserProfileController.java`

**Steps:**
1. Add/extend controller tests for fetching profile and updating display name/password.
2. Verify failing cases for blank input and successful update payloads.
3. Keep backend minimal if already present; fill any response gaps.
4. Improve My Page profile section so editing fields feel complete and update visible user state.
5. Run focused tests and frontend build.

### Task 4: Admin daily view must show placed reservations

**Files:**
- Modify: `src/test/java/com/example/demo/AdminControllerTest.java`
- Modify: `src/main/java/com/example/demo/service/AdminAggregateService.java`
- Modify: `src/main/java/com/example/demo/controller/AdminController.java`
- Modify: `frontend/src/views/admin/AdminDailyView.vue`

**Steps:**
1. Add a failing backend test proving daily aggregate endpoints return actual reservation rows for the target date.
2. Add/adjust service logic so reservation users are resolved with real user names and menu names.
3. Make sure date handling matches frontend-selected date.
4. Update admin daily UI if needed to render returned rows robustly.
5. Re-run focused tests.

### Task 5: Admin user management with order history/payment records/order status management

**Files:**
- Create: `src/test/java/com/example/demo/AdminUserManagementControllerTest.java`
- Modify: `src/main/java/com/example/demo/controller/AdminController.java`
- Modify: `src/main/java/com/example/demo/service/AdminAggregateService.java`
- Modify: `src/main/java/com/example/demo/service/ReservationService.java`
- Modify: `src/main/java/com/example/demo/service/UserService.java`
- Create: `frontend/src/views/admin/AdminUsersView.vue`
- Modify: `frontend/src/api/admin.js`
- Modify: `frontend/src/router/index.js`
- Modify: `frontend/src/App.vue`

**Steps:**
1. Add failing backend tests for admin user list, per-user reservation history, payment records, and order-status update endpoint.
2. Implement the minimal read/update backend APIs needed by those tests.
3. Build a new admin users screen showing users, order history, payment records, and order status controls.
4. Wire the route/API/sidebar entry.
5. Re-run focused tests and frontend build.

### Task 6: Final verification

**Files:**
- Verify only

**Steps:**
1. Run `mvn test` from `demo`.
2. Run `npm run build` from `demo/frontend`.
3. Manually verify: over-cutoff reservation shows concrete message, My Page shows reservation/payment flow, admin daily view shows placed reservation, admin users view shows history and can update order status.
