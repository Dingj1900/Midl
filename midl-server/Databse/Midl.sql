BEGIN TRANSACTION;

DROP TABLE IF EXISTS user_group_address, address, groups, users CASCADE;

CREATE TABLE users (
	user_id serial NOT NULL,
	first_name varchar (100) NOT NULL,
	last_name varchar (100) NOT NULL,
	email varchar(100) UNIQUE NOT NULL,
	password varchar(50) NOT NULL,
	CONSTRAINT PK_users PRIMARY KEY (user_id)
	);

CREATE TABLE groups (
	group_id serial NOT NULL,
	group_name varchar (200) NOT NULL,
	group_admin int NOT NULL,
	midl_point varchar (100) NOT NULL,
	CONSTRAINT PK_groups PRIMARY KEY (group_id),
	CONSTRAINT FK_groups_group_admin FOREIGN KEY (group_admin) REFERENCES users (user_id)
	);

CREATE TABLE address (
	address_id serial NOT NULL,
	address varchar (100) NOT NULL,
	CONSTRAINT PK_address PRIMARY KEY (address_id)
	);

CREATE TABLE user_group_address (
	user_id int NOT NULL,
	group_id int NOT NULL,
	address_id int NOT NULL,
	CONSTRAINT PK_user_group_address PRIMARY KEY (user_id, group_id, address_id),
	CONSTRAINT FK_user_id FOREIGN KEY (user_id) REFERENCES users (user_id),
	CONSTRAINT FK_group_id FOREIGN KEY (group_id) REFERENCES groups (group_id),
	CONSTRAINT FK_address_id FOREIGN KEY (address_id) REFERENCES address (address_id)
	);


COMMIT;