# Shopping Cart Flow Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add a real shopping cart flow with multi-item quantity management and quantity-aware reservation submission.

**Architecture:** Keep cart state on the frontend in a dedicated Pinia store, expose a new cart page and nav badge, and replace direct menu reservation with add-to-cart actions. On the backend, extend reservations with a `quantity` field and add a batch-style submission endpoint that creates one reservation row per menu type for the same day.

**Tech Stack:** Vue 3, Pinia, Vite, Tailwind CSS, Spring Boot, MyBatis-Plus, JUnit 5, Mockito.

---

### Task 1: Add reservation quantity to the backend model

**Files:**
- Modify: `src/test/java/com/example/demo/ReservationServiceTest.java`
- Modify: `src/test/java/com/example/demo/ReservationControllerTest.java`
- Modify: `src/main/java/com/example/demo/entity/Reservation.java`
- Modify: `src/main/java/com/example/demo/dto/ReservationRequest.java`
- Modify: `src/main/resources/schema.sql`

**Step 1: Write the failing tests**

- Add a failing service test asserting created reservations store `quantity`.
- Add a failing controller test asserting request payloads can include `quantity`.

**Step 2: Run focused tests to verify RED**

Run: `mvn "-Dtest=ReservationServiceTest,ReservationControllerTest" test`
Expected: FAIL because `quantity` is missing from the model and request handling.

**Step 3: Write minimal implementation**

- Add `quantity` to `Reservation`.
- Add `quantity` to `ReservationRequest`.
- Update schema creation/migration for a MySQL-compatible `quantity` column with default/backfill.

**Step 4: Re-run focused tests**

Run: `mvn "-Dtest=ReservationServiceTest,ReservationControllerTest" test`
Expected: PASS

### Task 2: Add cart-style reservation submission API

**Files:**
- Create: `src/main/java/com/example/demo/dto/CartCheckoutRequest.java`
- Modify: `src/test/java/com/example/demo/ReservationServiceTest.java`
- Modify: `src/test/java/com/example/demo/ReservationControllerTest.java`
- Modify: `src/main/java/com/example/demo/service/ReservationService.java`
- Modify: `src/main/java/com/example/demo/controller/ReservationController.java`

**Step 1: Write the failing tests**

- Add a failing service test for submitting multiple cart items for the same date.
- Add a failing controller test for `POST /api/user/reservations/cart-checkout`.
- Verify the rules: positive quantity only, same-day only, cutoff still enforced.

**Step 2: Run focused tests to verify RED**

Run: `mvn "-Dtest=ReservationServiceTest,ReservationControllerTest" test`
Expected: FAIL because no checkout DTO/endpoint/service exists.

**Step 3: Write minimal implementation**

- Add checkout DTO with a list of reservation items.
- Add service method that creates one reservation row per menu type with quantity.
- Keep validation minimal and consistent with existing rules.

**Step 4: Re-run focused tests**

Run: `mvn "-Dtest=ReservationServiceTest,ReservationControllerTest" test`
Expected: PASS

### Task 3: Add cart store and top-right cart badge

**Files:**
- Create: `frontend/src/stores/cart.js`
- Modify: `frontend/src/App.vue`
- Modify: `frontend/src/router/index.js`

**Step 1: Write the failing test or define verification target**

- Since frontend unit tests are not present, define manual verification targets before implementation:
  - badge appears in top-right for user layout
  - badge shows total quantity across cart items
  - cart icon is hidden for admin layout

**Step 2: Write minimal implementation**

- Create a dedicated cart Pinia store.
- Add total quantity getter.
- Add a cart icon and badge to `App.vue` user header.
- Add a `/cart` route placeholder.

**Step 3: Verify build**

Run: `npm run build`
Expected: PASS

### Task 4: Replace direct reserve with add-to-cart on menu page

**Files:**
- Modify: `frontend/src/views/TodayMenusView.vue`
- Modify: `frontend/src/stores/cart.js`

**Step 1: Define RED behavior**

- Manual target:
  - `予約する` is replaced by `カートに追加`
  - clicking adds/increments cart item quantity
  - cutoff message still blocks cart addition if required by chosen UX

**Step 2: Write minimal implementation**

- Replace direct reservation API call with cart-store updates.
- Show success toast for cart additions.

**Step 3: Verify build**

Run: `npm run build`
Expected: PASS

### Task 5: Build the cart page with quantity controls and checkout

**Files:**
- Create: `frontend/src/views/CartView.vue`
- Modify: `frontend/src/api/user.js`
- Modify: `frontend/src/stores/cart.js`

**Step 1: Define RED behavior**

- Manual target:
  - cart page lists multiple menu types
  - each item can increment/decrement quantity
  - items can be removed
  - checkout submits all items at once

**Step 2: Write minimal implementation**

- Add checkout API helper.
- Add cart page UI.
- Clear cart after successful checkout.

**Step 3: Verify build**

Run: `npm run build`
Expected: PASS

### Task 6: Update MyPage for quantity-aware orders

**Files:**
- Modify: `frontend/src/views/MyPageView.vue`
- Modify: `src/main/java/com/example/demo/service/AdminAggregateService.java` (only if aggregate displays need quantity totals)

**Step 1: Define RED behavior**

- Manual target:
  - order/payment history shows quantities cleanly
  - active orders no longer look inconsistent with the new cart flow

**Step 2: Write minimal implementation**

- Show quantity on current order and history rows.
- Keep tabbed MyPage structure.

**Step 3: Verify build**

Run: `npm run build`
Expected: PASS

### Task 7: Final verification

**Files:**
- Verify only

**Step 1: Run backend verification**

Run: `mvn test`
Expected: PASS

**Step 2: Run frontend verification**

Run: `npm run build`
Expected: PASS

**Step 3: Manual verification checklist**

- top-right user header shows cart icon + total quantity
- menu page adds items to cart instead of submitting directly
- cart page supports multi-item quantity adjustment and removal
- checkout creates one reservation row per menu type with quantity
- MyPage shows quantity-aware orders and payment flow
