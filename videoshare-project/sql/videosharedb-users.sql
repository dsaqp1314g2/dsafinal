drop user 'videoshare'@'localhost';
create user 'videoshare'@'localhost' identified by 'videoshare';
grant all privileges on reinodb.* to 'videoshare'@'localhost';
flush privileges;
