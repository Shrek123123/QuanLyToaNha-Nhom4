-- ============================================================
--  QuanLyToaNha - Nhóm 4
--  MySQL setup script để đồng bộ các nút Thêm/Cập nhật/Xóa/Làm mới
--  Cách chạy: mysql -u root -p < database_setup.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS quanlytoanha
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE quanlytoanha;

-- ======================
--  Người dùng
-- ======================
CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    role VARCHAR(30),
    password VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
--  Căn hộ
-- ======================
CREATE TABLE resident (
    resident_id INT(11) NOT NULL AUTO_INCREMENT,
    user_id INT(11) NOT NULL,
    name VARCHAR(100) COLLATE utf8mb4_general_ci NOT NULL,
    full_name VARCHAR(150) COLLATE utf8mb4_general_ci DEFAULT NULL,
    apartment VARCHAR(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
    is_owner TINYINT(1) DEFAULT 0,
    phone VARCHAR(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
    email VARCHAR(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
    identity_card VARCHAR(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
    date_of_birth DATE DEFAULT NULL,
    gender TINYINT(1) DEFAULT 0 COMMENT '0 = Nữ, 1 = Nam, 2 = Khác',
    address VARCHAR(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
    emergency_contact VARCHAR(150) COLLATE utf8mb4_general_ci DEFAULT NULL,
    emergency_phone VARCHAR(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
    status TINYINT(1) DEFAULT 1 COMMENT '1 = Đang sống, 0 = Rời đi',
    notes TEXT COLLATE utf8mb4_general_ci DEFAULT NULL,
    balance DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (resident_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
ALTER TABLE resident
ADD CONSTRAINT fk_resident_user
FOREIGN KEY (user_id) REFERENCES user(id);


CREATE TABLE IF NOT EXISTS apartment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resident_owner_id INT,
    apartment_no VARCHAR(20),
    number_of_rooms INT,
    number_of_people INT,
    area DOUBLE,
    price DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (resident_owner_id) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
--  Khiếu nại
-- ======================
CREATE TABLE `fees_and_payments` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    assigned_to_resident_id INT NOT NULL,
    resident_name VARCHAR(100) NOT NULL,
    electric_type BOOLEAN DEFAULT FALSE,
    water_type BOOLEAN DEFAULT FALSE,
    parking_type BOOLEAN DEFAULT FALSE,
    services_type BOOLEAN DEFAULT FALSE,
    rent_type BOOLEAN DEFAULT FALSE,
    payment_amount DECIMAL(12,2) NOT NULL,
    deadline DATE NOT NULL,
    payment_status ENUM('payed','not_payed','past_deadline') DEFAULT 'not_payed',
    payment_date DATE,
    payment_creation_date DATE NOT NULL,
    FOREIGN KEY (assigned_to_resident_id) REFERENCES user(id)
);


CREATE TABLE IF NOT EXISTS complaint (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resident_id INT,
    content TEXT,
    status VARCHAR(30) DEFAULT 'pending',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (resident_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
--  Phương tiện
-- ======================
CREATE TABLE IF NOT EXISTS vehicle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_number VARCHAR(50),
    resident_id INT,
    vehicle_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resident_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE resident_card (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resident_id INT NOT NULL,
    apartment_id INT NOT NULL,
    card_number VARCHAR(50) NOT NULL,
    resident_name VARCHAR(100) NOT NULL,
    card_type ENUM('guest', 'resident') NOT NULL,
    date_issued DATE NOT NULL,
    request_for_renewal BOOLEAN DEFAULT 0,

    -- FK tới user(user_id)
    CONSTRAINT fk_resident_card_user
        FOREIGN KEY (resident_id)
        REFERENCES user(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    -- FK tới apartment(id)
    CONSTRAINT fk_resident_card_apartment
        FOREIGN KEY (apartment_id)
        REFERENCES apartment(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);


-- ======================
--  Hệ thống BMS
-- ======================
CREATE TABLE IF NOT EXISTS bms_system (
    id INT AUTO_INCREMENT PRIMARY KEY,
    system_type VARCHAR(50) NOT NULL,
    system_name VARCHAR(100) NOT NULL,
    location VARCHAR(150),
    status VARCHAR(30) DEFAULT 'NORMAL',
    current_value DOUBLE,
    unit VARCHAR(20),
    description TEXT,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
--  Bảo trì
-- ======================
CREATE TABLE IF NOT EXISTS maintenance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    system_id INT,
    system_type VARCHAR(50) NOT NULL,
    maintenance_type VARCHAR(50) NOT NULL,
    description TEXT,
    scheduled_date DATE,
    completed_date DATE,
    status VARCHAR(30) DEFAULT 'PENDING',
    assigned_to INT,
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    created_by INT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (system_id) REFERENCES bms_system(id) ON DELETE SET NULL,
    FOREIGN KEY (assigned_to) REFERENCES user(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
--  Sự cố an ninh
-- ======================
CREATE TABLE IF NOT EXISTS security (
    id INT AUTO_INCREMENT PRIMARY KEY,
    incident_type VARCHAR(100) NOT NULL,
    location VARCHAR(150) NOT NULL,
    description TEXT,
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    status VARCHAR(30) DEFAULT 'OPEN',
    reported_by INT,
    reported_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_to INT,
    resolved_date TIMESTAMP NULL,
    resolution TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (reported_by) REFERENCES user(id) ON DELETE SET NULL,
    FOREIGN KEY (assigned_to) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
--  Công việc vệ sinh
-- ======================
CREATE TABLE IF NOT EXISTS cleaning (
    id INT AUTO_INCREMENT PRIMARY KEY,
    area VARCHAR(150) NOT NULL,
    cleaning_type VARCHAR(50) NOT NULL,
    scheduled_date DATE,
    completed_date DATE,
    status VARCHAR(30) DEFAULT 'PENDING',
    assigned_to INT,
    created_by INT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    quality_rating INT,
    FOREIGN KEY (assigned_to) REFERENCES user(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
--  Yêu cầu khách hàng
-- ======================
CREATE TABLE IF NOT EXISTS customer_request (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resident_id INT NOT NULL,
    request_type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    status VARCHAR(30) DEFAULT 'PENDING',
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_date TIMESTAMP NULL,
    assigned_to INT,
    resolution TEXT,
    satisfaction_rating INT,
    FOREIGN KEY (resident_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_to) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
--  Nhiệm vụ hành chính
-- ======================
CREATE TABLE IF NOT EXISTS admin_task (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    assigned_to INT,
    created_by INT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date DATE,
    completed_date DATE,
    status VARCHAR(30) DEFAULT 'PENDING',
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    notes TEXT,
    FOREIGN KEY (assigned_to) REFERENCES user(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
--  Nhân sự
-- ======================
CREATE TABLE IF NOT EXISTS staff (
    id INT AUTO_INCREMENT PRIMARY KEY,
    staff_code VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    position VARCHAR(100),
    department VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(120),
    start_date DATE,
    status VARCHAR(30) DEFAULT 'ACTIVE',
    base_salary DOUBLE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    staff_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    shift VARCHAR(30),
    check_in TIME,
    check_out TIME,
    status VARCHAR(30) DEFAULT 'PRESENT',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS shift_schedule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    staff_id INT NOT NULL,
    shift_name VARCHAR(50),
    start_date DATE,
    end_date DATE,
    assigned_by VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS contract (
    id INT AUTO_INCREMENT PRIMARY KEY,
    staff_id INT NOT NULL,
    contract_type VARCHAR(100),
    start_date DATE,
    end_date DATE,
    salary DOUBLE,
    status VARCHAR(30) DEFAULT 'ACTIVE',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================
--  Tài khoản mặc định
-- ======================
INSERT INTO user (username, role, password, email)
VALUES ('admin', 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@example.com')
ON DUPLICATE KEY UPDATE username = VALUES(username);

INSERT INTO apartment (
    resident_owner_id,
    apartment_no,
    number_of_rooms,
    number_of_people,
    area,
    price
) VALUES
(2, 'A101', 3, 4, 75.5, 12000000);

INSERT INTO resident_card (
    resident_id,
    apartment_id,
    card_number,
    resident_name,
    card_type,
    date_issued,
    request_for_renewal
) VALUES
(2, 1, 'RC-2024-001', 'Nguyen Van A', 'resident', '2024-03-12', 0),
(2, 1, 'RC-2024-002', 'Tran Thi B', 'resident', '2024-04-05', 0),
(2, 1, 'GC-2024-003', 'Pham Hoang C', 'guest',    '2024-06-18', 1),
(2, 1, 'RC-2024-004', 'Le Minh D',     'resident','2024-07-01', 0),
(2, 1, 'GC-2024-005', 'Visitor E',     'guest',    '2024-07-20', 1);
