import re

with open('src/main/resources/db/tenant_template.sql', 'r', encoding='utf-8') as f:
    content = f.read()

old_block = '''CREATE TABLE ee_penalty_slabs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,

  ee_structure_id BIGINT NOT NULL,

  rom_day INT NOT NULL,
  	o_day INT NOT NULL,

  penalty_amount DECIMAL(12,2) NOT NULL,

  ctive BOOLEAN NOT NULL,
  is_active BOOLEAN NOT NULL,

  days_overdue INT NOT NULL,

  CONSTRAINT k_fee_penalty_structure
    FOREIGN KEY (ee_structure_id)
    REFERENCES ee_structures (id)
    ON DELETE CASCADE
);'''

new_block = '''CREATE TABLE ee_penalty_slabs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  ee_structure_id BIGINT NOT NULL,
  rom_day INT NOT NULL,
  	o_day INT NOT NULL,
  penalty_value DECIMAL(12,2) NOT NULL,
  penalty_type VARCHAR(20) NOT NULL,
  payment_schedule VARCHAR(20),
  period_count INT,
  effective_from DATE,
  effective_to DATE,
  ctive BOOLEAN NOT NULL,
  is_active BOOLEAN NOT NULL,
  CONSTRAINT k_fee_penalty_structure
    FOREIGN KEY (ee_structure_id)
    REFERENCES ee_structures (id)
    ON DELETE CASCADE
);'''

content = content.replace(old_block, new_block)

with open('src/main/resources/db/tenant_template.sql', 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated successfully")
