INSERT INTO admin_users (company_email, password, secret_key, company_name)
SELECT 'manager@securevaultcorp.com', 'Admin@123', 'SECUREVAULT-CORP-2024', 'SecureVault Corporation'
WHERE NOT EXISTS (
    SELECT 1 FROM admin_users WHERE company_email = 'manager@securevaultcorp.com'
);

INSERT INTO customers (name, aadhaar, bank_account, username, email, password, role, company_key)
SELECT 'Aarav Sharma', '123456781001', '55667788990011', 'aarav', 'aarav@example.com', 'User@123', 'USER', NULL
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE username = 'aarav');

INSERT INTO customers (name, aadhaar, bank_account, username, email, password, role, company_key)
SELECT 'Diya Mehta', '123456781002', '55667788990012', 'diya', 'diya@example.com', 'User@123', 'USER', NULL
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE username = 'diya');

INSERT INTO customers (name, aadhaar, bank_account, username, email, password, role, company_key)
SELECT 'Rohan Patel', '123456781003', '55667788990013', 'rohan', 'rohan@example.com', 'User@123', 'USER', NULL
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE username = 'rohan');

INSERT INTO customers (name, aadhaar, bank_account, username, email, password, role, company_key)
SELECT 'Isha Nair', '123456781004', '55667788990014', 'isha', 'isha@example.com', 'User@123', 'USER', NULL
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE username = 'isha');

INSERT INTO customers (name, aadhaar, bank_account, username, email, password, role, company_key)
SELECT 'Karan Singh', '123456781005', '55667788990015', 'karan', 'karan@example.com', 'User@123', 'USER', NULL
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE username = 'karan');

INSERT INTO customers (name, aadhaar, bank_account, username, email, password, role, company_key)
SELECT 'Neha Kapoor', '123456781006', '55667788990016', 'neha', 'neha@example.com', 'User@123', 'USER', NULL
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE username = 'neha');
