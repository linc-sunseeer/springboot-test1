# Worktree → Main Project Sync Checklist

## Source / Target

- **Source worktree**: `F:\日本電子\sunseer_java練習\0406\.worktrees\bento-system-skeleton\demo`
- **Target main project**: `F:\日本電子\sunseer_java練習\0406\demo`
- **Current branch in worktree**: `feature/bento-system-skeleton`

---

## 1. Sync Goal

This checklist is for copying the completed implementation from the isolated worktree back into the main project folder after development is accepted.

The work includes:
- Spring Boot backend migration from JPA to MyBatis-Plus
- Vue 3 + Vite frontend scaffold and page integration
- Reservation / payment business rules
- Admin menu CRUD, daily aggregate, monthly aggregate, CSV export
- Docker / SQL / configuration updates
- Requirements and design documents

---

## 2. Copy These New Paths Into Main Project

### 2.1 New frontend
- `frontend/`

### 2.2 New backend packages
- `src/main/java/com/example/demo/config/`
- `src/main/java/com/example/demo/dto/`
- `src/main/java/com/example/demo/mapper/`
- `src/main/java/com/example/demo/security/`

### 2.3 New backend files
- `src/main/java/com/example/demo/controller/AdminController.java`
- `src/main/java/com/example/demo/controller/AuthController.java`
- `src/main/java/com/example/demo/controller/ReservationController.java`
- `src/main/java/com/example/demo/controller/UserMenuController.java`
- `src/main/java/com/example/demo/entity/Admin.java`
- `src/main/java/com/example/demo/entity/Reservation.java`
- `src/main/java/com/example/demo/service/AdminAggregateService.java`
- `src/main/java/com/example/demo/service/AdminService.java`
- `src/main/java/com/example/demo/service/AuthService.java`
- `src/main/java/com/example/demo/service/ReservationService.java`

### 2.4 New resources
- `src/main/resources/schema.sql`
- `src/main/resources/data.sql`

### 2.5 New tests
- `src/test/java/com/example/demo/AdminControllerTest.java`
- `src/test/java/com/example/demo/ApiSkeletonSecurityTest.java`
- `src/test/java/com/example/demo/ReservationServiceTest.java`
- `src/test/resources/`

### 2.6 New docs and infra
- `docs/`
- `Dockerfile`

---

## 3. Replace These Existing Files In Main Project

- `compose.yaml`
- `pom.xml`
- `src/main/java/com/example/demo/DemoApplication.java`
- `src/main/java/com/example/demo/entity/Menu.java`
- `src/main/java/com/example/demo/entity/User.java`
- `src/main/java/com/example/demo/service/MenuService.java`
- `src/main/java/com/example/demo/service/UserService.java`
- `src/main/resources/application.yml`
- `src/test/java/com/example/demo/DemoApplicationTests.java`

---

## 4. Delete These Old Main-Project Files After Sync

These old files are replaced by the new architecture and should not remain in the main project.

- `src/main/java/com/example/demo/controller/MenuController.java`
- `src/main/java/com/example/demo/controller/UserController.java`
- `src/main/java/com/example/demo/repository/MenuRepository.java`
- `src/main/java/com/example/demo/repository/UserRepository.java`

---

## 5. Functional Areas Included In Sync

### Backend
- MyBatis-Plus configuration and mapper structure
- Auth skeleton endpoints
- User menu endpoint
- Reservation create / view / payment update
- Admin menu CRUD
- Admin daily aggregate
- Admin monthly aggregate
- Admin daily CSV export

### Frontend
- User login page
- Admin login page
- Today menus page
- My page / payment update page
- Admin menus page
- Admin daily aggregate page
- Admin monthly aggregate page
- Shared API modules
- Route guards
- Header auth status / logout
- Basic loading / success / error UX

---

## 6. Verification After Sync Back

Run in main project `demo/` after copying:

### Backend
```powershell
.\mvnw.cmd test
```

Expected:
- All tests pass

### Frontend
```powershell
npm install --prefix frontend
npm run build --prefix frontend
```

Expected:
- Vue build succeeds

---

## 7. Important Cautions

### 7.1 Do not keep old JPA repository files
The backend now assumes MyBatis-Plus mapper-based access. Old JPA repository files should stay deleted.

### 7.2 Keep source and target paths aligned exactly
Sync must happen from `...\.worktrees\bento-system-skeleton\demo` into `...\0406\demo`, not from the worktree root into the main project root.

### 7.3 Frontend is now a real subproject
The main project must contain:
- `demo/frontend/package.json`
- `demo/frontend/src/...`
- `demo/frontend/Dockerfile`

### 7.4 Auth is still placeholder-level
Current login/logout is development-stage auth wiring, not final JWT production auth.

---

## 8. Recommended Sync Order

1. Copy `docs/`, `frontend/`, new backend packages, new tests, SQL, Dockerfile
2. Replace existing modified core files (`pom.xml`, `application.yml`, entities, services, compose.yaml`)
3. Delete obsolete JPA controllers/repositories
4. Run backend tests
5. Run frontend build
6. Review `git status` in main project before any commit

---

## 9. Ready-To-Sync Status

At the time this checklist was written in the worktree:

- Frontend build: passed
- Frontend diagnostics: clean
- Backend Maven tests: passed
