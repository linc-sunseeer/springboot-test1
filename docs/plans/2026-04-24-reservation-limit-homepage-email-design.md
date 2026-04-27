# Reservation Limit, Homepage, and Email Recovery Design

**Date:** 2026-04-24
**Status:** Draft design / grounded record

---

## 1. Purpose

This document records the current grounded state of three issues and proposes a minimal design direction:

1. Reservation quantity should be configurable from the admin side for testing, instead of being hardcoded.
2. The user homepage should show a single reservable lunch for today, plus a weekly preview list that is view-only.
3. Email delivery is currently failing and needs an operational recovery design.

This document is intentionally based on the current codebase and existing plan/history documents, not on speculative redesign.

---

## 2. Grounded Current State

### 2.1 Reservation quantity rule

Current code does **not** support a configurable upper limit.

Observed implementation:

- `demo/src/main/java/com/example/demo/service/ReservationService.java`
  - `validateReservationRequest()` currently rejects any quantity other than `1`.
  - Existing error message is `1日1件まで予約できます。`
- `demo/frontend/src/stores/cart.js`
  - `quantity` is initialized to `1`
  - `increment()` / `decrement()` are effectively disabled
- `demo/frontend/src/views/CartView.vue`
  - UI text explicitly says `1日1件・数量1固定`

Conclusion:

- The current system is not enforcing a `> 6` rule.
- It is enforcing a stricter hardcoded `quantity must equal 1` rule.
- Therefore, the new requirement is not a small threshold tweak only; it also requires the current single-quantity UX to be loosened.

### 2.2 Admin-configurable settings

Current admin settings only support reservation cutoff time.

Observed implementation:

- `demo/frontend/src/views/admin/AdminSettingsView.vue`
  - Only exposes `予約締切時間`
- `demo/src/main/java/com/example/demo/service/SystemSettingService.java`
  - Only reads/writes `RESERVATION_CUTOFF_TIME`
- `demo/src/main/java/com/example/demo/controller/AdminController.java`
  - Existing admin settings endpoints are only for reservation cutoff time

Conclusion:

- The existing `SystemSetting` pattern is a good extension point.
- A configurable quantity cap should reuse this exact pattern instead of introducing a separate config system.

### 2.3 Homepage / menu listing

Current user homepage is effectively the “today menus list” page.

Observed implementation:

- `demo/frontend/src/router/index.js`
  - `/` redirects to `/menus/today`
- `demo/frontend/src/views/TodayMenusView.vue`
  - Renders all today menus as a grid of reservable cards
- `demo/src/main/java/com/example/demo/controller/UserMenuController.java`
  - `/api/user/menus/today` returns a list for today plus `cutoffTime`

Conclusion:

- The current page is a “today menu collection” screen.
- It is not yet shaped as:
  - one primary reservable lunch for today
  - one secondary weekly preview section that is non-reservable

### 2.4 Email sending failure

Email flow exists in code, but current deployment/config is incomplete.

Observed implementation:

- `demo/src/main/java/com/example/demo/service/MailService.java`
- `demo/src/main/java/com/example/demo/service/EmailVerificationService.java`
- `demo/src/main/java/com/example/demo/service/PasswordResetService.java`
- `demo/src/main/java/com/example/demo/controller/AuthController.java`
- `demo/src/main/resources/application.yml`
- `demo/compose.yaml`

Current operational findings:

- `APP_MAIL_ENABLED` is expected to be `true` in compose.
- `SPRING_MAIL_USERNAME` and `SPRING_MAIL_PASSWORD` are currently unresolved / effectively empty.
- Mail dependency exists (`spring-boot-starter-mail` is present).
- Gmail SMTP is configured by default, so valid SMTP credentials are required.

Conclusion:

- Email is not failing because the feature is missing.
- Email is failing because deployment credentials/settings are incomplete.
- This is partly a configuration issue and partly an operational setup/documentation issue.

---

## 3. Design Goals

### 3.1 Reservation quantity

- Admin can change the quantity cap without code changes.
- The active business rule becomes: **reject only when quantity exceeds the configured cap**.
- Default testing value can be set to `6`.
- Validation must still happen on the backend even if frontend controls are bypassed.

### 3.2 Homepage

- Show only one “today” lunch as the main reservable card.
- Show weekly lunch cards below as preview-only.
- Weekly preview cards must not allow reservation.
- Users should still be able to open detail pages for preview items if desired.

### 3.3 Email recovery

- Make the missing operational dependency explicit.
- Define exactly what must be configured for email to work.
- Keep the design aligned with current Gmail SMTP approach unless requirements change.

---

## 4. Proposed Design

### 4.1 Reservation quantity cap becomes a system setting

Add a new system setting key:

- `MAX_RESERVATION_QUANTITY`

Proposed behavior:

- Backend reads `MAX_RESERVATION_QUANTITY` from `system_settings`
- If no value exists, default to `6`
- Reservation request is valid when:
  - quantity is present
  - quantity is positive
  - quantity is less than or equal to configured max

Proposed responsibility split:

- **Backend is source of truth**
  - `ReservationService` validates final quantity
- **Frontend mirrors the rule for usability**
  - cart quantity controls stop increasing at configured max
  - admin settings screen shows and edits the max

Why this design fits current code:

- Reuses `SystemSettingService`
- Reuses existing `SystemSetting` table/pattern
- Avoids magic numbers in frontend and backend

### 4.2 Homepage becomes “today primary + weekly preview”

Recommended structure:

1. **Primary section: 今日の便当**
   - Show exactly one card
   - Reservable
   - Includes image, description, price, reservation count, cutoff info

2. **Secondary section: 今週の便当プレビュー**
   - Show this week’s menu cards
   - View-only label such as `今週プレビュー` / `予約不可`
   - No reserve/add-to-cart button in this section

Recommended assumption for “this week”:

- Use **Monday through Friday of the current week**
- This is the cleanest fit for office lunch operations

Selection rules:

- `todayMenu`: the single menu whose available date is today
- `weeklyMenus`: menus in the current week, excluding `todayMenu`

If there are multiple menus for today in data:

- Do not silently show all of them in the primary slot
- Prefer one of these approaches during implementation:
  1. sort by creation/display order and take the first
  2. introduce an admin-selected featured menu flag later if needed

For now, the minimal design should document this as a data assumption:

- **Operational assumption:** only one lunch is intended per day

### 4.3 Email recovery design

Email recovery should be treated as an operational checklist plus light code hardening.

Minimum required operational inputs:

- `APP_MAIL_ENABLED=true`
- `SPRING_MAIL_USERNAME=<gmail address>`
- `SPRING_MAIL_PASSWORD=<gmail app password>`
- `APP_FRONTEND_BASE_URL=http://localhost:5174` (or the active frontend URL)

Recommended recovery steps:

1. Confirm compose env values are actually populated
2. Use Gmail App Password, not normal account password
3. Verify generated verification/reset links point to the current frontend URL
4. Add explicit operator documentation for SMTP setup

Recommended optional hardening:

- Add clearer startup/runtime logging when mail credentials are missing
- Add a small admin/operator checklist document for mail setup
- Consider enabling async properly if async delivery is required later

---

## 5. Impacted Areas

### Reservation quantity cap

Likely impacted files:

- `demo/src/main/java/com/example/demo/service/SystemSettingService.java`
- `demo/src/main/java/com/example/demo/controller/AdminController.java`
- `demo/src/main/java/com/example/demo/service/ReservationService.java`
- `demo/frontend/src/views/admin/AdminSettingsView.vue`
- `demo/frontend/src/api/admin.js`
- `demo/frontend/src/stores/cart.js`
- `demo/frontend/src/views/CartView.vue`
- `demo/src/test/java/com/example/demo/ReservationServiceTest.java`
- `demo/src/test/java/com/example/demo/AdminControllerTest.java`
- `demo/src/test/java/com/example/demo/ReservationControllerTest.java`

### Homepage redesign

Likely impacted files:

- `demo/src/main/java/com/example/demo/controller/UserMenuController.java`
- `demo/src/main/java/com/example/demo/service/MenuService.java`
- `demo/frontend/src/views/TodayMenusView.vue`
- `demo/frontend/src/api/user.js`
- `demo/src/test/java/com/example/demo/UserMenuControllerTest.java`

### Email recovery

Likely impacted files:

- `demo/compose.yaml`
- `demo/src/main/resources/application.yml`
- `demo/src/main/java/com/example/demo/service/MailService.java`
- `docs/plans/2026-04-21-email-system-implementation.md`
- optional new operator/setup doc under `docs/`

---

## 6. Risks and Constraints

### Reservation rule risk

- Current copy and tests are written around `1日1件`
- If quantity becomes configurable up to 6, both error messages and UX wording must be updated
- Existing single-item cart language becomes misleading unless updated together

### Homepage risk

- Current backend endpoint returns only today menus
- Weekly preview likely needs either:
  - a new API shape, or
  - an expanded existing endpoint
- If data contains more than one menu for today, the “single today lunch” design needs deterministic selection behavior

### Email risk

- Even with correct code, email still fails until SMTP credentials are supplied
- This cannot be solved by frontend work alone

---

## 7. Recommended Implementation Order

1. **Email operational recovery first**
   - restore ability to send verification/reset emails
   - lowest design ambiguity

2. **Admin-configurable quantity cap second**
   - adds a clear business-rule control for testing

3. **Homepage redesign third**
   - depends on confirming how weekly preview data should be fetched

---

## 8. Decision Log

### Confirmed from user input

- The desired reservation rule is **not** “always quantity 1”
- The desired business rule is: **only block when quantity exceeds 6**
- For testing, this quantity should be configurable from the admin side
- User wants homepage to show:
  - one lunch for today
  - weekly lunches below
  - weekly section must not support reservation

### Working assumption used in this draft

- “This week” means **Monday through Friday of the current week**

This assumption should be confirmed during implementation planning, but it is the most natural default for the current lunch-reservation domain.
