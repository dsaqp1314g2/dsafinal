package edu.upc.eetac.dsa.nmendo.books.api;

import java.security.SecurityPermission;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;


@Path("/videoshare")
public class VideoshareResource {

	// variables globales:
	@Context
	SecurityContext security;
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	// para obtener la coleción de videos GET (1)

	@GET
	@Produces(MediaType.VIDEOSHARE_API_VIDEOS_COLLECTION)
	public VideosCollection getVideos() {
		VideosCollection videos = new VideosCollection();

		// hacemos la conexión a la base de datos
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			String sql = buildQueryGetVideosCollection();
			stmt = conn.prepareStatement(sql);
			// obtenemos la respuesta
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Video video = new Video();
                video.setVideoid(rs.getString("videoid"));
                video.setNombre_video(rs.getString("nombre_video"));
                video.setUsername(rs.getString("username"));
                video.setFecha(rs.getString("fecha"));
                
                try
                {
                    String sqlr="select*from review where videoid = ?";
                    stmt.close();
                    stmt = conn.prepareStatement(sqlr);
                    stmt.setInt(1, videoid);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        Review review = new Review();
                        review.setVideoid(rs.getInt("videoid"));
                        review.setReviewtext(rs.getString("reviewtext"));
                        review.setFecha(rs.getTimestamp("fecha_hora"));
                        review.setReviewid(rs.getInt("reviewid"));
                        review.setUsername(rs.getString("username"));
                        
                        book.addReview(review);
                    }
                    
                    String sqlc = "select*from categorias where videoid=?";
                    stmt.close();
                    stmt = conn.prepareStatement(sqlc);
                    stmt.setInt(1, videoid);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        Categroia cat = new Categroia();
                        cat.setTagid(rs.getString("tagid"));
                        review.setCategoria.getInt("categoria"));
                        
                        book.addCategoria(cat);
                    }
                    else
                    {
                        throw new NotFoundException();
                    }
                    
                    String sqlp = "select*from puntuaciones where videoid=?";
                    stmt.close();
                    stmt = conn.prepareStatement(sqlp);
                    stmt.setInt(1, videoid);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        Puntuaciones punt = new Puntuaciones();
                        punt.setPuntuacionid(rs.getInt("puntuacionid"));
                        punt.setPuntuacion(rs.getInt("puntuacion"));
                        
                        book.addPuntuacion(punt);
                    }
                }
                catch()
                {
                    throw new ServerErrorException(e.getMessage(),
                                                   Response.Status.INTERNAL_SERVER_ERROR);
                }
                finally{
                    video.addVideo(video);
                }
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return videos;
	}

	// (2) Obtener un video a partir de su identificador videoid
	@GET
	@Path("/{idvideo}")
	@Produces(MediaType.VIDEOSHARE_API_VIDEOS)
	public Video getVideoid(@PathParam("videoid") String videoid) {
        
        //llamaremos a la método que nos permite obtener un video a partir de su
        //video id además de la categoría que está asociado, comentarios, y puntuación
        
        Video video = getVideoFromDatabase(videoid);
        return video;
    
	}

	// (6)PUT de un video. Sólo lo puede modificar el usuario que ha subido el video
	@PUT
	@Path("/{videoid}")
	@Consumes(MediaType.VIDEOSHARE_API_VIDEOS)
	@Produces(MediaType.VIDEOSHARE_API_VIDEOS)
	public Video updateBook(@PathParam("videoid") String videoid, Video video) {
		
        //¡¡¡¡¡¡¡ falta añadir que compruebe que el usuario que edita sea el que lo haya creado
        // Alicia solo edita lo de Alicia
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			// llamamos a la función para la query y la hacemos la database
			String sql = buildUpdateVideo();
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, video.getNombre_video());
			stmt.setString(2, video.getUsername());
			stmt.setString(3, video.getFecha());
            stmt.setString(4, video.getUsername());
			stmt.setInt(5, videoid);
			stmt.executeUpdate(); // para añadir la ficha del libro con los
									// datos a la BBDD
			// si ha ido bien la inserción
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				// devuelve el video editado
				video = getBookFromDatabase(videoid);
			} else {
				throw new NotFoundException("Could not update the video info");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new ServerErrorException(e.getMessage(),
						Response.Status.INTERNAL_SERVER_ERROR);
			}
		}

		return video;

	}

	// (7)DELETE, eliminar un video a partir de su libroid. Eliminar solo el usuario que lo ha creado. Exige estar registrado.

        //cuando se elimine el video se eliminará todo lo que esté asociado a él, es decir, se eliminarán las categorías, los comentarios, las puntuaciones.
	@DELETE
	@Path("/{videoid}")
	public void deleteBook(@PathParam("videoid") String videoid) {
		// Comprobamos que el usuario que vaya a crear la ficha de libro sea
		// ADMIN: llamamos al método validateUser del usuario user
		if (!security.isUserInRole("registered")) {
			throw new ForbiddenException("You are not an admin.");
		}
        
        // ¡¡¡¡ falta añadir que el usuario que vaya a eliminar sea el que ha subido el video !!!!

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			// llamamos a la función para la query y la hacemos la database
			String sql = buildDeleteVideo();
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, videoid);

			int rows = stmt.executeUpdate();

			if (rows == 0) {
				throw new NotFoundException("There's no video with videod="
						+ videoid);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new ServerErrorException(e.getMessage(),
						Response.Status.INTERNAL_SERVER_ERROR);
			}
		}
	}

	// (8) Hacer publicación de un comentario de un video
	@POST
	@Path("/{videoid}/reviews")
	@Consumes(MediaType.VIDEOSHARE_API_VIDEOS)
	@Produces(MediaType.VIDEOSHARE_API_VIDEOS)
	public Video creatReview(@PathParam("videoid") String videoid, Review review) {
		// Comprobamos que el usuario que vaya a crear la ficha de libro sea
		// ADMIN
		Video video = null;

		if (!security.isUserInRole("registered")) {
			throw new ForbiddenException("You have not registered");
		}

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			String sql = buildCreateReview();
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(2, review.getVideoid());
			stmt.setString(3, review.getUsername());
            stmt.setString(3, review.getReviewtext());
            stmt.setString(4, review.getFehca());
			stmt.executeUpdate();

			// si ha ido bien la inserción
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				video = getVideoFromDatabase(videoid);
			} else {
				// Something has failed...
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new ServerErrorException(e.getMessage(),
						Response.Status.INTERNAL_SERVER_ERROR);
			}
		}

		return video;
	}

	// (10) DELETE - eliminar un comentario con reviewid de un video que videoid
	@DELETE
	@Path("/{videoid}/reviews/{reviewid}")
	public void deleteReview(@PathParam("videoid") String videoid,
			@PathParam("reviewid") String reviewid) {

		// nos aseguramos que el usuario esté registrado
		if (!security.isUserInRole("registered")) {
			throw new ForbiddenException("You have not registered");
		}

		// ahora el usuario, que el usuario que vaya a eliminar el comentario del video
        // sea el que ha creado dicho comentario
		validateUser(reviewid);

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			// llamamos a la función para la query y la hacemos la database
			String sql = buildDeleteReview();
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, reviewid);

			int rows = stmt.executeUpdate();

			if (rows == 0) {
				throw new NotFoundException("There's no review with review="
						+ reviewid + "with the video with videoid=" + videoid);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new ServerErrorException(e.getMessage(),
						Response.Status.INTERNAL_SERVER_ERROR);
			}
		}
	}


// ******************* Métodos adicionales / QUERIES *********************
    
	// (1)GET colección de libros
	private String buildQueryGetVideosCollection() {
		return "select * from videos";
	}

	// (2)GET de un libro con identificador bookid
	private String buildQueryGetVideoByVideoid() {
		return "select * from video v, review r, categorias c, puntuaciones p where v.videoid = ?, r.videoid=?, c.videoid=? and p.videoid=?";
	}

	// 5.2. Método para query de Insert la ficha del libro en la BBDD
	private String buildQueryInsertVideo() {
		return "insert into books (bookid, titulo, autor, lengua, edicion, editorial, fechae, fechai) value (null, ?, ?, ?, ?, ?, ?, ?)";
	}

	// 5.3. Método para obtener libro con bookid
	private Video getVideoFromDatabase(String videoid) {
		Connection conn = null;
		Video video = new Video();
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			String sql = buildQueryGetVideoByVideoid();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, videoid);
            stmt.setString(2, videoid);
            stmt.setString(3, videoid);
            stmt.setString(4, videoid);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
                Video video = new Video();
                video.setVideoid(rs.getString("videoid"));
                video.setNombre_video(rs.getString("nombre_video"));
                video.setUsername(rs.getString("username"));
                video.setFecha(rs.getString("fecha"));
            }
            else
            {
                throw new NotFoundException();
            }
            
            String sqlr="select*from review where videoid = ?";
            stmt.close();
			stmt = conn.prepareStatement(sqlr);
			stmt.setInt(1, videoid);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Review review = new Review();
                review.setVideoid(rs.getInt("videoid"));
				review.setReviewtext(rs.getString("reviewtext"));
				review.setFecha(rs.getTimestamp("fecha_hora"));
				review.setReviewid(rs.getInt("reviewid"));
				review.setUsername(rs.getString("username"));
                
				book.addReview(review);
			}
            
            String sqlc = "select*from categorias where videoid=?";
            stmt.close();
			stmt = conn.prepareStatement(sqlc);
			stmt.setInt(1, videoid);
			rs = stmt.executeQuery();
			if (rs.next()) {
				Categroia cat = new Categroia();
				cat.setTagid(rs.getString("tagid"));
				review.setCategoria.getInt("categoria"));
                
				book.addCategoria(cat);
			}
            else
            {
                throw new NotFoundException();
            }
            
            String sqlp = "select*from puntuaciones where videoid=?";
            stmt.close();
			stmt = conn.prepareStatement(sqlp);
			stmt.setInt(1, videoid);
			rs = stmt.executeQuery();
			if (rs.next()) {
				Puntuaciones punt = new Puntuaciones();
				punt.setPuntuacionid(rs.getInt("puntuacionid"));
                punt.setPuntuacion(rs.getInt("puntuacion"));
                
				book.addPuntuacion(punt);
			}
            
            video.addVideo(video);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				// Haya ido bien o haya ido mal cierra las conexiones
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new ServerErrorException(e.getMessage(),
						Response.Status.INTERNAL_SERVER_ERROR);
			}
		}
		return book;
	}

	// (6)PUT hacer una actualización de la ficha del libro:
	private String buildUpdateVideo() {
		return "u";
	}

	// (7) DELETE - eliminar un video a partir de videoid
	private String buildDeleteVideo() {
		return "delete from videos where videoid=?";
	}

	// (8) POST Crear una reseña de un libro con bookid
	private String buildCreateReview() {
		return "insert into review (videoid, username, fecha_hora, reviewtext) value (?, ?, ?, ?, ?)";
	}

	// 8.1. Obtener review a partir del reviewid
	private Review getReviewFromDatbase(int reviewid) {
		Connection conn = null;
		Book book = new Book();
		Review review = new Review();
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			String sql = buildQueryGetReviewByReviewid();
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, reviewid);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				review.setvideoid(rs.getInt("videoid"));
				review.setUsername(rs.getString("username"));
				review.setReviewid(rs.getInt("reviewid"));
				review.setTimestamp(rs.get("fecha_hora"));
				// FALTA AÑADIR PARA EL TEXTO DE LA RESEÑA
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				// Haya ido bien o haya ido mal cierra las conexiones
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new ServerErrorException(e.getMessage(),
						Response.Status.INTERNAL_SERVER_ERROR);
			}
		}
		return review;

	}

	// 8.2. Hacer query
	private String buildQueryGetReviewByReviewid() {
		return "select*from reviews where reviewid=?";
	}

    private String getReviewsFromDatabaseByVideoid(String videoid) {
		return "select username from review where review.videoid = " + videoid;
	}

	// (9) Actulizar reseña
	// 9.1. Validación del usuario (alicia sólo editar/eliminar reseña de
	// Alicia)
	private void validateUser(int reviewid) {
		// si el usuario que consulta la reseña no es el que la ha creado,
		// ForbiddenException
		Review currentReview = getReviewFromDatbase(reviewid);
		if (!security.getUserPrincipal().getName()
				.equals(currentReview.getUsername()))
			throw new ForbiddenException(
					"You are not allowed to modify/delete this review.");
	}

	// (10) Eliminar una reseña de un libro
	// 10.1. Query a la base de datos:
	private String buildDeleteReview() {
		return "delete from review where reviewid=?";
	}
}
