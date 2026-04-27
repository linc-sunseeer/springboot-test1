# Bento System Skeleton Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build the initial Vue + Spring Boot project skeleton for the bento reservation system and migrate the existing backend from JPA to MyBatis-Plus.

**Architecture:** Keep the existing Maven Spring Boot application in `demo/` as the backend, replace JPA repositories/entities/config with MyBatis-Plus-based persistence, and add a separate Vue 3 + Vite SPA under `demo/frontend/`. Introduce backend skeleton modules for auth, menu, reservation, and admin APIs while keeping behavior minimal and design-aligned.

**Tech Stack:** Vue 3, Vite, Vue Router, Pinia, Spring Boot 3.2.x, Spring Security, MyBatis-Plus, MySQL 8, Docker Compose, JUnit 5.

---

### Task 1: Stabilize backend test baseline

**Files:**
- Modify: `demo/src/test/java/com/example/demo/DemoApplicationTests.java`
- Create: `demo/src/test/resources/application-test.yml`

**Step 1: Write the failing test**

Add a test profile-based context load expectation that uses an in-memory datasource instead of external MySQL.

**Step 2: Run test to verify it fails**

Run: `./mvnw.cmd -Dtest=DemoApplicationTests test`

Expected: FAIL because the current test uses the default MySQL datasource.

**Step 3: Write minimal implementation**

- Add `@ActiveProfiles("test")`
- Add test datasource config using H2

**Step 4: Run test to verify it passes**

Run: `./mvnw.cmd -Dtest=DemoApplicationTests test`

Expected: PASS

---

### Task 2: Migrate dependencies from JPA to MyBatis-Plus

**Files:**
- Modify: `demo/pom.xml`
- Modify: `demo/src/main/resources/application.yml`

**Step 1: Write the failing test**

Add or update a lightweight context test that expects the app to boot without JPA repository scanning.

**Step 2: Run test to verify it fails**

Run: `./mvnw.cmd -Dtest=DemoApplicationTests test`

Expected: FAIL until dependencies/config are aligned.

**Step 3: Write minimal implementation**

- Remove `spring-boot-starter-data-jpa`
- Add MyBatis-Plus starter and H2 test dependency
- Replace `spring.jpa` config with `mybatis-plus` config

**Step 4: Run test to verify it passes**

Run: `./mvnw.cmd test`

Expected: PASS for context-related tests

---

### Task 3: Replace JPA entities/repositories with MyBatis-Plus model/mapper structure

**Files:**
- Modify: `demo/src/main/java/com/example/demo/entity/User.java`
- Modify: `demo/src/main/java/com/example/demo/entity/Menu.java`
- Create: `demo/src/main/java/com/example/demo/entity/Admin.java`
- Create: `demo/src/main/java/com/example/demo/entity/Reservation.java`
- Delete: `demo/src/main/java/com/example/demo/repository/UserRepository.java`
- Delete: `demo/src/main/java/com/example/demo/repository/MenuRepository.java`
- Create: `demo/src/main/java/com/example/demo/mapper/UserMapper.java`
- Create: `demo/src/main/java/com/example/demo/mapper/MenuMapper.java`
- Create: `demo/src/main/java/com/example/demo/mapper/AdminMapper.java`
- Create: `demo/src/main/java/com/example/demo/mapper/ReservationMapper.java`

**Step 1: Write the failing test**

Add a mapper/entity smoke test or context assertion that expects mapper beans to exist.

**Step 2: Run test to verify it fails**

Run: `./mvnw.cmd -Dtest=DemoApplicationTests test`

Expected: FAIL because mappers/entities do not exist yet.

**Step 3: Write minimal implementation**

- Convert entities to MyBatis-Plus POJOs with table mapping annotations
- Add mapper interfaces extending `BaseMapper<T>`

**Step 4: Run test to verify it passes**

Run: `./mvnw.cmd test`

Expected: PASS

---

### Task 4: Add backend DTO/VO/service/controller skeletons

**Files:**
- Modify: `demo/src/main/java/com/example/demo/service/UserService.java`
- Modify: `demo/src/main/java/com/example/demo/service/MenuService.java`
- Create: `demo/src/main/java/com/example/demo/service/AuthService.java`
- Create: `demo/src/main/java/com/example/demo/service/ReservationService.java`
- Create: `demo/src/main/java/com/example/demo/service/AdminService.java`
- Create: `demo/src/main/java/com/example/demo/controller/AuthController.java`
- Create: `demo/src/main/java/com/example/demo/controller/ReservationController.java`
- Create: `demo/src/main/java/com/example/demo/controller/AdminController.java`
- Create: `demo/src/main/java/com/example/demo/dto/...`
- Create: `demo/src/main/java/com/example/demo/vo/...`

**Step 1: Write the failing test**

Add controller tests for at least auth login, today menus, and reservation creation endpoint wiring.

**Step 2: Run test to verify it fails**

Run: `./mvnw.cmd test`

Expected: FAIL because endpoints do not exist yet.

**Step 3: Write minimal implementation**

- Expose `/api/auth/**`, `/api/user/**`, `/api/admin/**`
- Implement placeholder business logic aligned with design docs

**Step 4: Run test to verify it passes**

Run: `./mvnw.cmd test`

Expected: PASS

---

### Task 5: Add basic security skeleton

**Files:**
- Create: `demo/src/main/java/com/example/demo/config/SecurityConfig.java`
- Create: `demo/src/main/java/com/example/demo/config/MybatisPlusConfig.java`

**Step 1: Write the failing test**

Add web-layer tests asserting public auth endpoints remain accessible and protected endpoints require authentication.

**Step 2: Run test to verify it fails**

Run: `./mvnw.cmd test`

Expected: FAIL because security config is missing.

**Step 3: Write minimal implementation**

- Permit `/api/auth/**`
- Keep `/api/user/**` and `/api/admin/**` protected
- Add `@MapperScan`

**Step 4: Run test to verify it passes**

Run: `./mvnw.cmd test`

Expected: PASS

---

### Task 6: Create Vue 3 frontend scaffold

**Files:**
- Create: `demo/frontend/package.json`
- Create: `demo/frontend/vite.config.js`
- Create: `demo/frontend/index.html`
- Create: `demo/frontend/src/main.js`
- Create: `demo/frontend/src/App.vue`
- Create: `demo/frontend/src/router/index.js`
- Create: `demo/frontend/src/stores/auth.js`
- Create: `demo/frontend/src/views/...`
- Create: `demo/frontend/src/components/...`

**Step 1: Write the failing test**

For the frontend scaffold, verify buildability rather than unit behavior first.

**Step 2: Run test to verify it fails**

Run: `npm --prefix demo/frontend run build`

Expected: FAIL because frontend does not exist.

**Step 3: Write minimal implementation**

- Add Vue 3 + Vite app structure
- Add router, Pinia, user/admin views, shared layout components

**Step 4: Run test to verify it passes**

Run: `npm --prefix demo/frontend run build`

Expected: PASS

---

### Task 7: Align Docker Compose and runtime config

**Files:**
- Modify: `demo/compose.yaml`
- Modify: `demo/src/main/resources/application.yml`
- Create: `demo/frontend/Dockerfile`
- Create: `demo/Dockerfile`

**Step 1: Write the failing test**

Validate config structure by building the backend and frontend successfully.

**Step 2: Run test to verify it fails**

Run: `./mvnw.cmd test` and `npm --prefix demo/frontend run build`

Expected: FAIL until compose/config align with new structure.

**Step 3: Write minimal implementation**

- Compose services: `frontend`, `app`, `mysql`
- Backend datasource from env vars
- Frontend Dockerfile serving built app

**Step 4: Run test to verify it passes**

Run: `./mvnw.cmd test` and `npm --prefix demo/frontend run build`

Expected: PASS

---

### Task 8: Final verification

**Files:**
- Review all modified files

**Step 1: Run diagnostics**

Run LSP diagnostics on changed Java and frontend files.

**Step 2: Run verification commands**

Run: `./mvnw.cmd test`
Run: `npm --prefix demo/frontend run build`

**Step 3: Review against docs**

Confirm implementation matches the current repository docs and actual checked-in files.
