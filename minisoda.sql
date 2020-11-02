CREATE DATABASE minisoda default character set utf8mb4 collate utf8mb4_general_ci;

CREATE TABLE member ( 
		member_id INT NOT NULL AUTO_INCREMENT,  
		first_name VARCHAR(20) NOT NULL, 
		last_name VARCHAR(20) NOT NULL, 
		country VARCHAR(20) NOT NULL, 
		province VARCHAR(20) NOT NULL, 
		city VARCHAR(20) NOT NULL, 
		street VARCHAR(20) NOT NULL, 
		house_number INT, 
		phone_number VARCHAR(11) NOT NULL, 
		email VARCHAR(200) NOT NULL, 
		PRIMARY KEY(member_id));

ALTER TABLE member
		ADD INDEX ix_email(email);

CREATE TABLE bankcode (
		bankcode_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
		code VARCHAR(20));

ALTER TABLE bankcode
		ADD CONSTRAINT unique_code UNIQUE(code);

CREATE TABLE openapi ( 
		openapi_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
		bankcode_id INT NOT NULL, 
		account_number VARCHAR(50) NOT NULL, 
		owner VARCHAR(50) NOT NULL, 
		balance BIGINT UNSIGNED NOT NULL, 
		CONSTRAINT fk_code_bankcode 
			FOREIGN KEY (bankcode_id) REFERENCES bankcode(bankcode_id));
ALTER TABLE openapi
		ADD INDEX ix_bankcode_acnt_num (bankcode_id, account_number);

CREATE TABLE account (
    	account_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    	member_id INT NOT NULL,
    	openapi_id INT NOT NULL UNIQUE,
    	CONSTRAINT fk_mid_member 
			FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE,
    	CONSTRAINT fk_oid_openapi 
			FOREIGN KEY (openapi_id) REFERENCES openapi(openapi_id) ON DELETE CASCADE);


CREATE TABLE transaction ( 
		transaction_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
		member_id INT NOT NULL, 
		send_account INT NOT NULL, 
		recv_account INT NOT NULL,
		amount BIGINT UNSIGNED NOT NULL,
		after_balance BIGINT UNSIGNED NOT NULL,
		transaction_status VARCHAR(10) NOT NULL, 
		process_at TIMESTAMP NOT NULL,  
		CONSTRAINT fk_member_id_member 
			FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE, 
		CONSTRAINT fk_send_openapi 
			FOREIGN KEY (send_account) REFERENCES openapi(openapi_id), 
		CONSTRAINT fk_recv_openapi 
		FOREIGN KEY (recv_account) REFERENCES openapi(openapi_id));

ALTER TABLE transaction
	ADD INDEX ix_process_at(process_at);

INSERT INTO bankcode (code)
	VALUES
	('A BANK'),
	('B BANK'),
	('C BANK');

INSERT INTO openapi (bankcode_id, account_number, owner, balance)
	VALUES
	(1, '123-45-6789', "양태환", 50000),
	(2, '111-22-3333', "양태환", 20000),
	(3, '222-33-4321', "양태환", 10000),
	(1, '333-44-1234', "이순신", 30000),
	(2, '222-33-4321', "장영실", 500);

INSERT INTO member (first_name, last_name, country, province, city, street, house_number, phone_number, email) 
	VALUES 
	("태환", "양", "대한민국", "경기도", "군포시", "산본천로", "214", "01066496270", "ythwork@naver.com"),  
	("순신", "이", "대한민국", "경기도", "한양시", "육전거리", "111", "01011111234", "sunsin@gmail.com");
