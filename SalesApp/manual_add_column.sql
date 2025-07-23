-- Manual SQL commands to add CartItemsSnapshot column
-- Run these commands in your MySQL database client

USE SalesAppDB;

-- Check if column already exists
SELECT COUNT(*) 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'SalesAppDB' 
  AND TABLE_NAME = 'Orders' 
  AND COLUMN_NAME = 'CartItemsSnapshot';

-- Add column if not exists (MySQL 8.0+)
ALTER TABLE Orders ADD COLUMN IF NOT EXISTS CartItemsSnapshot TEXT;

-- For older MySQL versions, use this instead:
-- ALTER TABLE Orders ADD COLUMN CartItemsSnapshot TEXT;

-- Verify the column was added
DESCRIBE Orders;
