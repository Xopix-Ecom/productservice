-- V1__Create_products_table.sql
CREATE TABLE products (
    id VARCHAR(36) PRIMARY KEY NOT NULL, -- UUIDs for primary keys
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    sku VARCHAR(100) UNIQUE NOT NULL, -- Stock Keeping Unit (unique identifier)
    category VARCHAR(100),
    image_url VARCHAR(255),
    stock_quantity INT NOT NULL, -- Initial stock (will be moved/synced with Inventory Service later)
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);