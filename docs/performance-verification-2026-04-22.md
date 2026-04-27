# Performance Verification Record (2026-04-22)

## Scope

Basic response-time verification for key user/admin pages after final cleanup.

## Environment

- Frontend: `http://127.0.0.1:5174`
- Backend: `http://127.0.0.1:8080`
- Browser target from requirement: Chrome latest

## Measured Results

### User menu page

Command:

```powershell
Invoke-WebRequest -Uri "http://127.0.0.1:5174/menus/today"
```

Observed elapsed time: **88 ms**

### Admin daily aggregate page

Command:

```powershell
Invoke-WebRequest -Uri "http://127.0.0.1:5174/admin/aggregates/daily"
```

Observed elapsed time: **87 ms**

## CSV Availability Check

Command:

```powershell
Invoke-WebRequest -Uri "http://127.0.0.1:8080/api/admin/aggregates/daily/export?date=2026-04-15" -Headers @{ Authorization = "Bearer admin-token" }
```

Result: **available**

## Notes

- This is a lightweight verification record, not a load/performance benchmark.
- Current measurements are comfortably within the requirement target of 2 seconds per page display.
- Registration / payment approval latency was also improved by making mail sending asynchronous.
