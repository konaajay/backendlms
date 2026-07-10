$sql = "ALTER TABLE lms_tenant_1770701101086.student_fee_allocations ADD COLUMN affiliate_discount DECIMAL(12,2) DEFAULT 0.00, ADD COLUMN affiliate_id BIGINT;"
& mysql.exe -u root -proot -e $sql
