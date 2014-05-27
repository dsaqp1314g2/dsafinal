drop database if exists videosharedb;
create database videosharedb;
 
use videosharedb;

create table users (
    	username	varchar(20) not null primary key,
	contraseña	varchar(20) not null,
	name		varchar(50) not null,
	email		varchar(50) not null

);

create table videos(
	videoid    	int not null auto_increment unique,
	nombre_video 	varchar(50),
	username 	varchar(50) not null,
	fecha		date,
foreign key(username) references users (username) on delete cascade

);

create table review (

	reviewid	 int not null auto_increment unique,
	videoid		int not null,
	username 	varchar(50) not null,
	reviewtext	varchar(500),
	fecha_hora	timestamp,
foreign key(username) references users (username) on delete cascade,
foreign key(videoid) references videos (videoid) on delete cascade
);

create table puntuaciones (
	puntuacionid  int not null auto_increment unique,
	videoid		int not null,
	username	varchar(50) not null,
	puntuacion	int not null,
foreign key(username) references users (username) on delete cascade,
foreign key(videoid) references videos (videoid) on delete cascade

);


create table categorias (
	tagid    	int not null auto_increment unique,
	descripcion 	varchar(500),
	videoid 	int not null,
	categoria 	varchar(30),
foreign key(videoid) references videos (videoid) on delete cascade






);

