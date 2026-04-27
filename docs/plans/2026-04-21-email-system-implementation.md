# Email System Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add a minimal Spring Boot email system using built-in mail and scheduling support with Gmail SMTP, covering welcome, verification, order confirmation, reset password, and daily reminder emails.

**Architecture:** Keep mail logic centralized in a small `MailService`/`MailTemplateService` pair, reuse Spring Boot auto-configured `JavaMailSender`, and store only token data needed for verification and password reset. Avoid Maildog-specific code and avoid introducing extra frameworks.

**Tech Stack:** Spring Boot 3.2, MyBatis-Plus, JavaMailSender, Spring Scheduling, Vue 3 frontend routes already present for verify/reset flows.

---

### Task 1: Add schema and config foundations

**Files:**
- Modify: `src/main/resources/schema.sql`
- Modify: `src/main/resources/application.yml`
- Modify: `src/test/resources/application-test.yml`
- Modify: `pom.xml`

**Step 1: Write failing tests**
- Add tests that require mail beans/config to exist and token persistence to support verification/reset flows.

**Step 2: Run tests to verify they fail**
- Run targeted Maven tests for the new mail/token tests.

**Step 3: Write minimal implementation**
- Add `spring-boot-starter-mail`.
- Add Gmail-ready `spring.mail` properties using env vars.
- Add app URLs and reminder cron config.
- Add schema changes for `users.email_verified` and a single `email_tokens` table.
- Add safe test-profile overrides that disable real SMTP.

**Step 4: Run tests to verify they pass**
- Run targeted tests again.

### Task 2: Add minimal token and mail infrastructure

**Files:**
- Create: `src/main/java/com/example/demo/entity/EmailToken.java`
- Create: `src/main/java/com/example/demo/mapper/EmailTokenMapper.java`
- Create: `src/main/java/com/example/demo/service/EmailTokenService.java`
- Create: `src/main/java/com/example/demo/service/MailService.java`
- Create: `src/main/java/com/example/demo/service/MailTemplateService.java`

**Step 1: Write failing tests**
- Token generation/consumption tests.
- Mail composition tests using mocked `JavaMailSender`.

**Step 2: Run tests to verify they fail**
- Run the new unit tests only.

**Step 3: Write minimal implementation**
- Use one token table for `VERIFY_EMAIL` and `RESET_PASSWORD`.
- Generate secure random tokens, enforce expiry, and single-use semantics.
- Centralize plain-text email composition and sending.

**Step 4: Run tests to verify they pass**
- Run the new unit tests again.

### Task 3: Wire registration and password reset flows

**Files:**
- Modify: `src/main/java/com/example/demo/controller/AuthController.java`
- Modify: `src/main/java/com/example/demo/service/UserService.java`
- Modify: `src/main/java/com/example/demo/service/PasswordResetService.java`
- Modify: `src/main/java/com/example/demo/entity/User.java`
- Modify: `src/test/java/com/example/demo/PasswordResetControllerTest.java`
- Create: `src/test/java/com/example/demo/AuthControllerMailFlowTest.java`

**Step 1: Write failing tests**
- Register flow sends welcome + verification trigger.
- Verify-email endpoint marks user verified.
- Reset-request hides account existence and sends reset mail only when user exists.

**Step 2: Run tests to verify they fail**
- Run controller/service mail-flow tests.

**Step 3: Write minimal implementation**
- Add `GET /api/auth/verify-email` endpoint.
- Mark new users as unverified by default.
- Replace in-memory reset token map with DB-backed tokens.
- Keep reset-request response constant for safety.

**Step 4: Run tests to verify they pass**
- Run the targeted tests again.

### Task 4: Wire reservation confirmation and reminder flows

**Files:**
- Modify: `src/main/java/com/example/demo/service/ReservationService.java`
- Modify: `src/main/java/com/example/demo/mapper/UserMapper.java`
- Create: `src/main/java/com/example/demo/service/ReservationReminderService.java`
- Create: `src/test/java/com/example/demo/ReservationMailFlowTest.java`

**Step 1: Write failing tests**
- Reservation creation sends order confirmation.
- Reminder job only emails verified users with no active reservation for today.

**Step 2: Run tests to verify they fail**
- Run reservation/reminder tests.

**Step 3: Write minimal implementation**
- Send order confirmation after successful reservation creation.
- Add one scheduled reminder job using Spring scheduling.
- Keep reminder query logic minimal and skip duplicates per run.

**Step 4: Run tests to verify they pass**
- Run reservation/reminder tests.

### Task 5: Frontend reset polish and cleanup verification

**Files:**
- Modify: `frontend/src/views/ResetPasswordView.vue`
- Optionally Modify: `frontend/src/views/ForgotPasswordView.vue`
- Review: `compose.yaml`, docs/config files for mail-test leftovers

**Step 1: Write failing test or identify minimal UI gap**
- Confirm reset page can read `token` from query string.

**Step 2: Write minimal implementation**
- Prefill token from query parameter so email links work.
- Keep UI change minimal.

**Step 3: Verify cleanup**
- Confirm no Maildog service/config/dependency remains.

### Task 6: Verify end-to-end evidence

**Files:**
- Review changed files only

**Step 1: Run diagnostics**
- Run LSP diagnostics on changed Java/Vue files.

**Step 2: Run tests**
- Run targeted tests, then full Maven test suite if targeted tests pass.

**Step 3: Run build**
- Run Maven package/build if practical.

**Step 4: Report actual status**
- Summarize verified passing evidence and any pre-existing failures separately.
