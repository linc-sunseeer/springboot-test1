INSERT INTO admins (id, name, email, password, role, created_at, updated_at)
SELECT 1, 'Admin', 'admin@example.com', '$2b$12$Q/.PlYLF0o8neaUOpgbMjunQzelZrSE.raBjVgLJUIf4A2TMIavfW', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM admins WHERE id = 1);

UPDATE admins
SET password = '$2b$12$Q/.PlYLF0o8neaUOpgbMjunQzelZrSE.raBjVgLJUIf4A2TMIavfW'
WHERE email = 'admin@example.com' AND password = 'password';

INSERT INTO system_settings (setting_key, setting_value, updated_at)
SELECT 'MIN_RESERVATION_THRESHOLD', '6', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM system_settings WHERE setting_key = 'MIN_RESERVATION_THRESHOLD');
