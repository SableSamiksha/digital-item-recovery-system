-- Sample data for Digital Item Recovery System
-- Note: Passwords are bcrypt encoded
-- Default password for all users: password123

-- Insert sample users
-- Password: password123 (bcrypt: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAtZ5n8e)
INSERT INTO users (username, email, password, role) VALUES
('admin', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAtZ5n8e', 'ADMIN'),
('john_doe', 'john@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAtZ5n8e', 'USER'),
('jane_smith', 'jane@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAtZ5n8e', 'USER')
ON CONFLICT DO NOTHING;

-- Note: Actual items would be inserted through the application
-- This file is mainly for reference
