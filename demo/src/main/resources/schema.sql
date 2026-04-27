CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    role VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS email_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    type VARCHAR(30) NOT NULL,
    expires_at DATETIME NOT NULL,
    used_at DATETIME,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_email_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS menus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description VARCHAR(1000),
    image_url VARCHAR(500),
    available_date DATE NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    reservation_date DATE NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    payment_status VARCHAR(20) NOT NULL,
    order_status VARCHAR(20) NOT NULL DEFAULT 'RESERVED',
    payment_method VARCHAR(20) NOT NULL DEFAULT 'CASH',
    reserved_at DATETIME NOT NULL,
    payment_checked_at DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_reservations_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reservations_menu FOREIGN KEY (menu_id) REFERENCES menus(id),
    CONSTRAINT uq_reservations_user_date_menu UNIQUE (user_id, reservation_date, menu_id)
);

CREATE TABLE IF NOT EXISTS system_settings (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(255) NOT NULL,
    updated_at DATETIME NOT NULL
);

SET @order_status_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'reservations'
      AND column_name = 'order_status'
);

SET @order_status_ddl := IF(
    @order_status_exists = 0,
    'ALTER TABLE reservations ADD COLUMN order_status VARCHAR(20) NOT NULL DEFAULT ''RESERVED'' AFTER payment_status',
    'SELECT 1'
);

PREPARE add_order_status_stmt FROM @order_status_ddl;
EXECUTE add_order_status_stmt;
DEALLOCATE PREPARE add_order_status_stmt;

SET @payment_method_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'reservations'
      AND column_name = 'payment_method'
);

SET @payment_method_ddl := IF(
    @payment_method_exists = 0,
    'ALTER TABLE reservations ADD COLUMN payment_method VARCHAR(20) NOT NULL DEFAULT ''CASH'' AFTER order_status',
    'SELECT 1'
);

PREPARE add_payment_method_stmt FROM @payment_method_ddl;
EXECUTE add_payment_method_stmt;
DEALLOCATE PREPARE add_payment_method_stmt;

UPDATE reservations SET payment_method = 'CASH' WHERE payment_method IS NULL;

UPDATE reservations SET order_status = 'RESERVED' WHERE order_status IS NULL;

SET @quantity_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'reservations'
      AND column_name = 'quantity'
);

SET @quantity_ddl := IF(
    @quantity_exists = 0,
    'ALTER TABLE reservations ADD COLUMN quantity INT NOT NULL DEFAULT 1 AFTER reservation_date',
    'SELECT 1'
);

PREPARE add_quantity_stmt FROM @quantity_ddl;
EXECUTE add_quantity_stmt;
DEALLOCATE PREPARE add_quantity_stmt;

UPDATE reservations SET quantity = 1 WHERE quantity IS NULL;

SET @user_idx_exists := (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'reservations'
      AND index_name = 'idx_reservations_user_id'
);

SET @add_user_idx_sql := IF(
    @user_idx_exists = 0,
    'ALTER TABLE reservations ADD INDEX idx_reservations_user_id (user_id)',
    'SELECT 1'
);

PREPARE add_user_idx_stmt FROM @add_user_idx_sql;
EXECUTE add_user_idx_stmt;
DEALLOCATE PREPARE add_user_idx_stmt;

SET @old_unique_exists := (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'reservations'
      AND index_name = 'uq_reservations_user_date'
);

SET @drop_old_unique_sql := IF(
    @old_unique_exists > 0,
    'ALTER TABLE reservations DROP INDEX uq_reservations_user_date',
    'SELECT 1'
);

PREPARE drop_old_unique_stmt FROM @drop_old_unique_sql;
EXECUTE drop_old_unique_stmt;
DEALLOCATE PREPARE drop_old_unique_stmt;

SET @new_unique_exists := (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'reservations'
      AND index_name = 'uq_reservations_user_date_menu'
);

SET @add_new_unique_sql := IF(
    @new_unique_exists = 0,
    'ALTER TABLE reservations ADD CONSTRAINT uq_reservations_user_date_menu UNIQUE (user_id, reservation_date, menu_id)',
    'SELECT 1'
);

PREPARE add_new_unique_stmt FROM @add_new_unique_sql;
EXECUTE add_new_unique_stmt;
DEALLOCATE PREPARE add_new_unique_stmt;

SET @email_verified_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'users'
      AND column_name = 'email_verified'
);

SET @email_verified_ddl := IF(
    @email_verified_exists = 0,
    'ALTER TABLE users ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE AFTER password',
    'SELECT 1'
);

PREPARE add_email_verified_stmt FROM @email_verified_ddl;
EXECUTE add_email_verified_stmt;
DEALLOCATE PREPARE add_email_verified_stmt;

UPDATE users SET email_verified = FALSE WHERE email_verified IS NULL;

-- menus 表新增 calorie 和 allergens 字段
SET @menu_calorie_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'menus'
      AND column_name = 'calorie'
);

SET @menu_calorie_ddl := IF(
    @menu_calorie_exists = 0,
    'ALTER TABLE menus ADD COLUMN calorie INT AFTER price',
    'SELECT 1'
);

PREPARE add_menu_calorie_stmt FROM @menu_calorie_ddl;
EXECUTE add_menu_calorie_stmt;
DEALLOCATE PREPARE add_menu_calorie_stmt;

SET @menu_allergens_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'menus'
      AND column_name = 'allergens'
);

SET @menu_allergens_ddl := IF(
    @menu_allergens_exists = 0,
    'ALTER TABLE menus ADD COLUMN allergens VARCHAR(255) AFTER calorie',
    'SELECT 1'
);

PREPARE add_menu_allergens_stmt FROM @menu_allergens_ddl;
EXECUTE add_menu_allergens_stmt;
DEALLOCATE PREPARE add_menu_allergens_stmt;

-- users 表新增 target_calorie 和 allergen_settings 字段
SET @user_target_calorie_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'users'
      AND column_name = 'target_calorie'
);

SET @user_target_calorie_ddl := IF(
    @user_target_calorie_exists = 0,
    'ALTER TABLE users ADD COLUMN target_calorie INT AFTER role',
    'SELECT 1'
);

PREPARE add_user_target_calorie_stmt FROM @user_target_calorie_ddl;
EXECUTE add_user_target_calorie_stmt;
DEALLOCATE PREPARE add_user_target_calorie_stmt;

SET @user_allergen_settings_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'users'
      AND column_name = 'allergen_settings'
);

SET @user_allergen_settings_ddl := IF(
    @user_allergen_settings_exists = 0,
    'ALTER TABLE users ADD COLUMN allergen_settings VARCHAR(255) AFTER target_calorie',
    'SELECT 1'
);

PREPARE add_user_allergen_settings_stmt FROM @user_allergen_settings_ddl;
EXECUTE add_user_allergen_settings_stmt;
DEALLOCATE PREPARE add_user_allergen_settings_stmt;

-- reservations 表新增 refund_reason 字段（退款原因备注）
SET @refund_reason_exists := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'reservations'
      AND column_name = 'refund_reason'
);

SET @refund_reason_ddl := IF(
    @refund_reason_exists = 0,
    'ALTER TABLE reservations ADD COLUMN refund_reason VARCHAR(500) AFTER payment_checked_at',
    'SELECT 1'
);

PREPARE add_refund_reason_stmt FROM @refund_reason_ddl;
EXECUTE add_refund_reason_stmt;
DEALLOCATE PREPARE add_refund_reason_stmt;
