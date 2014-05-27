source videosharedb-schema.sql;


insert into users (username, contraseña, name,email)values('moha', MD5('moha'), 'Mohito', 'Moha@acme.com');
insert into users (username, contraseña, name,email)values('carles', MD5('carles'), 'Carlitos', 'carles@acme.com');
insert into users (username, contraseña, name,email)values('natalia', MD5('natalia'), 'Naty', 'natalia@acme.com');


insert into videos ( nombre_video, username , fecha) values ('El ataque de los pedos', 'natalia' , '2014-07-15');
insert into videos ( nombre_video, username , fecha) values ('En busca de la fecundidad', 'moha' , '2014-08-15');
insert into videos ( nombre_video, username , fecha) values ('Nabocop', 'carles' , '2014-08-15');




insert into review ( videoid, username ,reviewtext ) values (2, 'natalia' , 'Es una pelicula muy interesante, el estruendo que producen los pedos  me sobresalto en mas de una ocasion, no me lo esperaba...');

insert into review ( videoid, username ,reviewtext ) values (2, 'moha' , 'Nunca habia entendido el porque la gente hace largos caminos , cruza el desierto para encontrar la fecundidad');

insert into review ( videoid, username ,reviewtext ) values (3, 'natalia' , 'Sin palabras....Nunca habia visto tal cantidad de nabos en una pelicula... sobretodo el de acero, es increible');


insert into puntuaciones (videoid, username , puntuacion) values (2, 'moha', 7);

insert into puntuaciones (videoid, username , puntuacion) values (3, 'natalia',10);


insert into categorias ( videoid , descripcion ,  categoria) values(1, 'Esta categoria nos hace volar por nuestra imaginacion y resolver las incognitas del universo','ciencia_ficcion' );

insert into categorias (videoid , descripcion ,  categoria) values( 2,'Categoria para cuando estas solo por la noche', 'porn' );


