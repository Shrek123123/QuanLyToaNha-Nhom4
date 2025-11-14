-- Database Schema for QuanLyToaNha System
-- Version: 1.0
-- Description: Tạo database và các bảng cơ bản

-- Tạo database nếu chưa tồn tại
CREATE DATABASE IF NOT EXISTS quanlytoanha;
USE quanlytoanha;

-- Bảng user: Lưu thông tin người dùng
CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    role VARCHAR(30),
    password VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Bảng apartment: Lưu thông tin căn hộ
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
);

-- Bảng complaint: Lưu thông tin khiếu nại
CREATE TABLE IF NOT EXISTS complaint (
    id INT AUTO_INCREMENT PRIMARY KEY,
    residentId INT,
    content TEXT,
    status VARCHAR(30) DEFAULT 'pending',
    createdDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (residentId) REFERENCES user(id) ON DELETE CASCADE
);

-- Bảng vehicle: Lưu thông tin phương tiện
CREATE TABLE IF NOT EXISTS vehicle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    vehicleNumber VARCHAR(50),
    residentId INT,
    vehicle_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (residentId) REFERENCES user(id) ON DELETE CASCADE
);

-- Bảng bms_system: Lưu thông tin hệ thống BMS
CREATE TABLE IF NOT EXISTS bms_system (
    id INT AUTO_INCREMENT PRIMARY KEY,
    system_name VARCHAR(100),
    status VARCHAR(50),
    location VARCHAR(100),
    last_maintenance DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Bảng maintenance: Lưu thông tin bảo trì
CREATE TABLE IF NOT EXISTS maintenance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    equipment_name VARCHAR(100),
    maintenance_type VARCHAR(50),
    scheduled_date DATE,
    completed_date DATE,
    status VARCHAR(50) DEFAULT 'pending',
    technician_id INT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (technician_id) REFERENCES user(id) ON DELETE SET NULL
);

-- Bảng security: Lưu thông tin an ninh
CREATE TABLE IF NOT EXISTS security (
    id INT AUTO_INCREMENT PRIMARY KEY,
    incident_type VARCHAR(100),
    location VARCHAR(100),
    description TEXT,
    priority VARCHAR(50),
    status VARCHAR(50) DEFAULT 'open',
    reported_by INT,
    resolved_at DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (reported_by) REFERENCES user(id) ON DELETE SET NULL
);

-- Bảng cleaning: Lưu thông tin vệ sinh
CREATE TABLE IF NOT EXISTS cleaning (
    id INT AUTO_INCREMENT PRIMARY KEY,
    area VARCHAR(100),
    cleaning_type VARCHAR(50),
    scheduled_date DATE,
    completed_date DATE,
    status VARCHAR(50) DEFAULT 'pending',
    notes TEXT,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES user(id) ON DELETE SET NULL
);

-- Bảng customer_request: Lưu thông tin yêu cầu khách hàng
CREATE TABLE IF NOT EXISTS customer_request (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    request_type VARCHAR(100),
    description TEXT,
    status VARCHAR(50) DEFAULT 'pending',
    priority VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Bảng admin_task: Lưu thông tin nhiệm vụ hành chính
CREATE TABLE IF NOT EXISTS admin_task (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200),
    description TEXT,
    due_date DATE,
    priority VARCHAR(50),
    status VARCHAR(50) DEFAULT 'pending',
    notes TEXT,
    created_by INT,
    assigned_to INT,
    completed_at DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES user(id) ON DELETE SET NULL,
    FOREIGN KEY (assigned_to) REFERENCES user(id) ON DELETE SET NULL
);

-- Insert dữ liệu mẫu (tùy chọn)
-- Tạo user admin mặc định (password: admin123 - đã hash)
INSERT INTO user (username, role, password, email) 
VALUES ('admin', 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@example.com')
ON DUPLICATE KEY UPDATE username=username;



