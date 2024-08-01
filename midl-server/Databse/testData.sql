COMMIT;

-- Begin Transaction for dummy data
BEGIN TRANSACTION;

-- Insert dummy data into users table
INSERT INTO users (first_name, last_name, email, password) VALUES
('John', 'Doe', 'john.doe@example.com', 'password123'),
('Jane', 'Smith', 'jane.smith@example.com', 'password456'),
('Alice', 'Johnson', 'alice.johnson@example.com', 'password789');

-- Insert dummy data into address table
INSERT INTO address (address) VALUES
('123 Main St, Anytown, USA'),
('456 Elm St, Othertown, USA'),
('789 Oak St, Anothertown, USA');

-- Insert dummy data into groups table
INSERT INTO groups (group_name, group_admin, midl_point) VALUES
('Group A', 1, 'Center A'),
('Group B', 2, 'Center B');

-- Insert dummy data into user_group_address table
INSERT INTO user_group_address (user_id, group_id, address_id) VALUES
(1, 1, 1),
(1, 1, 2),
(2, 2, 3),
(3, 2, 1);

-- Commit the transaction
COMMIT;