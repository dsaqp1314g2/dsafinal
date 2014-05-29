package edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api;

import java.awt.print.Book;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import javax.sql.DataSource;
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
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api.model.Categoria;
import edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api.model.Puntuaciones;
import edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api.model.Review;
import edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api.model.ReviewCollection;
import edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api.model.User;
import edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api.model.Videos;
import edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api.model.VideosCollection;

@Path("/videoshare")
public class VideoshareResource {

	// variables globales:
	@Context
	SecurityContext security;
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

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
			throw new ServerErrorException(
					"Could not connect to the databaseeeeeeeeeeeeee",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			String sql = buildQueryGetVideosCollection();
			stmt = conn.prepareStatement(sql);
			// obtenemos la respuesta
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Videos video = new Videos();
				video.setVideoid(rs.getString("videoid"));
				video.setNombre_video(rs.getString("nombre_video"));
				video.setUsername(rs.getString("username"));
				video.setFecha(rs.getDate("fecha"));

				String videoid = video.getVideoid();
				try {
					System.out.println("ahora coge las reviews");
					String sqlr = "select*from review where videoid = ?";

					stmt = conn.prepareStatement(sqlr);
					stmt.setString(1, videoid);
					ResultSet rs2 = stmt.executeQuery();

					rs2 = stmt.executeQuery();
					while (rs2.next()) {
						Review review = new Review();
						review.setVideoid(rs2.getInt("videoid"));
						review.setReviewtext(rs2.getString("reviewtext"));
						review.setFecha_hora(rs2.getDate("fecha_hora"));
						review.setReviewid(rs2.getInt("reviewid"));
						review.setUsername(rs2.getString("username"));

						video.addReview(review);
					}

					System.out.println("ahora coge las categorias");

					String sqlc = "select * from categorias where videoid=?";
					stmt.close();
					stmt = conn.prepareStatement(sqlc);
					stmt.setString(1, videoid);
					rs2 = stmt.executeQuery();
					if (rs2.next()) {
						Categoria cat = new Categoria();
						cat.setTagid(rs2.getString("tagid"));
						cat.setCategoria(rs2.getString("categoria"));

						video.addCategoria(cat);
					} else {
						System.out.println("Aqui es que no hay categorias");
					}
					System.out.println("ahora coge las puntuaciones");
					String sqlp = "select * from puntuaciones where videoid=?";
					stmt.close();
					stmt = conn.prepareStatement(sqlp);
					stmt.setString(1, videoid);
					rs2 = stmt.executeQuery();
					if (rs2.next()) {
						Puntuaciones punt = new Puntuaciones();
						punt.setPuntuacionid(rs2.getInt("puntuacionid"));
						punt.setPuntuacion(rs2.getInt("puntuacion"));

						video.addPuntuacion(punt);
					} else {
						System.out.println("AAAAAHHHH");
					}
				} catch (SQLException e) {
					throw new ServerErrorException(e.getMessage(),
							Response.Status.INTERNAL_SERVER_ERROR);
				} finally {
					System.out.println("ahora añade el video");
					videos.addVideos(video);
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
				throw new NotFoundException();
			}
		}

		return videos;
	}

	// (2) Obtener un video a partir de su identificador videoid
	@GET
	@Path("/{videoid}")
	@Produces(MediaType.VIDEOSHARE_API_VIDEOS)
	public Videos getVideoid(@PathParam("videoid") String videoid) {

		// llamaremos a la método que nos permite obtener un video a partir de
		// su
		// video id además de la categoría que está asociado, comentarios, y
		// puntuación

		Videos video = getVideoFromDatabase(videoid);
		return video;

	}
	public static String md5(String clear) throws Exception {
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    byte[] b = md.digest(clear.getBytes());
	    int size = b.length;
	    StringBuffer h = new StringBuffer(size);
	    //algoritmo y arreglo md5
	        for (int i = 0; i < size; i++) {
	            int u = b[i] & 255;
	                if (u < 16) {
	                    h.append("0" + Integer.toHexString(u));
	                }
	               else {
	                    h.append(Integer.toHexString(u));
	               }
	           }
	      //clave encriptada
	      return h.toString();
	    }
	//crear un nuevo usuario. 
		@POST
		@Consumes(MediaType.VIDEOSHARE_API_USERS)
		@Produces(MediaType.VIDEOSHARE_API_USERS)
		public User creatUser(User usuario) throws Exception {
			
			Connection conn = null;
			try {
				conn = ds.getConnection();
			} catch (SQLException e) {
				throw new ServerErrorException("Could not connect to the database",
						Response.Status.SERVICE_UNAVAILABLE);
			}
			
			PreparedStatement stmt = null;
			try {
				String sql = buildCreateUser();
				stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setString(1,usuario.getUsername());
				stmt.setString(2, md5(usuario.getUserpass()));
				stmt.setString(3, usuario.getName());
				stmt.setString(4, usuario.getEmail());
				stmt.executeUpdate();
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					throw new ForbiddenException("You have got registered");
				} else {
					// Something has failed...
				}
			}
			catch (SQLException e) {
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
			return usuario;
		}
		
		private String buildCreateUser(){
			return "insert into users (username, contrasena, name,email) values (?,?,   ? ,?)";
		}
	// (6)PUT de un video. Sólo lo puede modificar el usuario que ha subido el
	// video
	@PUT
	@Path("/{videoid}")
	@Consumes(MediaType.VIDEOSHARE_API_VIDEOS)
	@Produces(MediaType.VIDEOSHARE_API_VIDEOS)
	public Videos updateBook(@PathParam("videoid") String videoid, Videos video) {

		// ¡¡¡¡¡¡¡ falta añadir que compruebe que el usuario que edita sea el
		// que lo haya creado
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
			stmt.setString(2, videoid);
		
			stmt.executeUpdate(); // para añadir la ficha del libro con los
									// datos a la BBDD
			// si ha ido bien la inserción
			video = getVideoFromDatabase(videoid);
			
			
			
			//ResultSet rs = stmt.getGeneratedKeys();
//			if (rs.next()) {
//				// devuelve el video editado
//				
//			} else {
//				throw new NotFoundException("Could not update the video info");
//			}

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

	// (7)DELETE, eliminar un video a partir de su libroid. Eliminar solo el
	// usuario que lo ha creado. Exige estar registrado.

	// cuando se elimine el video se eliminará todo lo que esté asociado a él,
	// es decir, se eliminarán las categorías, los comentarios, las
	// puntuaciones.
	@POST
	@Consumes(MediaType.VIDEOSHARE_API_VIDEOS)
	@Produces(MediaType.VIDEOSHARE_API_VIDEOS)
	public VideosCollection creatvideo(Videos video) {
		// Comprobamos que el usuario que vaya a crear la ficha de libro sea
		// ADMIN
		VideosCollection videos = new VideosCollection();

		/*if (!security.isUserInRole("registered")) {
			throw new ForbiddenException("You have not get registered");
		} */

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			String sql = buildQueryInsertVideo();
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			stmt.setString(1, video.getNombre_video());
			stmt.setString(2, video.getUsername());
			//stmt.setDate(3, video.getFecha());
			
			stmt.executeUpdate();

			// si ha ido bien la inserciÃ³n
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				videos = getVideos();
			} else {
				// Something has failed...
				//throw new NotFoundException();
				System.out.println("no encuentra el video");
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

		return videos;
	}
	private String buildQueryInsertVideo() {
		return "insert into videos (videoid, nombre_video, username,  fecha) value (null, ?, ?, now())";
	}
	@DELETE
	@Path("/{videoid}")
	public void deleteVideo(@PathParam("videoid") String videoid) {
		// Comprobamos que el usuario que vaya a crear la ficha de libro sea
		// ADMIN: llamamos al método validateUser del usuario user
	/*	if (!security.isUserInRole("registered")) {
			throw new ForbiddenException("You are not an admin.");
		} */

		// ¡¡¡¡ falta añadir que el usuario que vaya a eliminar sea el que ha
		// subido el video !!!!

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
			stmt.setString(1, videoid);

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
	@Consumes(MediaType.VIDEOSHARE_API_REVIEWS)
	@Produces(MediaType.VIDEOSHARE_API_REVIEWS)
	public Videos creatReview(@PathParam("videoid") String videoid,
			Review review) {
		// Comprobamos que el usuario que vaya a crear la ficha de libro sea
		// ADMIN
		Videos video = null;
/*
		if (!security.isUserInRole("registered")) {
			throw new ForbiddenException("You have not registered");
		} */

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
			stmt.setString(1, videoid);
			stmt.setString(2, review.getUsername());
			stmt.setString(4, review.getReviewtext());
			stmt.setDate(3, review.getFecha_hora());
			stmt.executeUpdate();

			// si ha ido bien la inserción
			ResultSet rs = stmt.getGeneratedKeys();
			video = getVideoFromDatabase(videoid);
			if (rs.next()) {
				
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

		// ahora el usuario, que el usuario que vaya a eliminar el comentario
		// del video
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

			stmt.setString(1, reviewid);

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
		return "select * from videos where videoid=?";
	}

	

	// 5.3. Método para obtener libro con bookid
	private Videos getVideoFromDatabase(String videoid) {
		Connection conn = null;
		//VideosCollection videos = new VideosCollection();
		Videos video = new Videos();
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
			

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {

				video.setVideoid(rs.getString("videoid"));
				video.setNombre_video(rs.getString("nombre_video"));
				video.setUsername(rs.getString("username"));
				video.setFecha(rs.getDate("fecha"));
			} else {
				
			}

			String sqlr = "select*from review where videoid = ?";
			stmt.close();
			stmt = conn.prepareStatement(sqlr);
			stmt.setString(1, videoid);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Review review = new Review();
				review.setVideoid(rs.getInt("videoid"));
				review.setReviewtext(rs.getString("reviewtext"));
				review.setFecha_hora(rs.getDate("fecha_hora"));
				review.setReviewid(rs.getInt("reviewid"));
				review.setUsername(rs.getString("username"));

				video.addReview(review);
			}

			String sqlc = "select*from categorias where videoid=?";
			stmt.close();
			stmt = conn.prepareStatement(sqlc);
			stmt.setString(1, videoid);
			rs = stmt.executeQuery();
			if (rs.next()) {
				Categoria cat = new Categoria();
				cat.setTagid(rs.getString("tagid"));
				cat.setCategoria(rs.getString("categoria"));

				video.addCategoria(cat);
			} else {
				throw new NotFoundException();
			}

			String sqlp = "select*from puntuaciones where videoid=?";
			stmt.close();
			stmt = conn.prepareStatement(sqlp);
			stmt.setString(1, videoid);
			rs = stmt.executeQuery();
			if (rs.next()) {
				Puntuaciones punt = new Puntuaciones();
				punt.setPuntuacionid(rs.getInt("puntuacionid"));
				punt.setPuntuacion(rs.getInt("puntuacion"));

				video.addPuntuacion(punt);
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
		return video;
	}

	// (6)PUT hacer una actualización de la ficha del libro:
	private String buildUpdateVideo() {
		return "update videos set nombre_video = ? where videoid = ?";
	}

	// (7) DELETE - eliminar un video a partir de videoid
	private String buildDeleteVideo() {
		return "delete from videos where videoid=?";
	}

	// (8) POST Crear una reseña de un libro con bookid
	private String buildCreateReview() {
		return "insert into review (videoid, username, fecha_hora, reviewtext) value (?, ?, ?, ?)";
	}

	// 8.1. Obtener review a partir del reviewid
	private Review getReviewFromDatbase(String reviewid) {
		Connection conn = null;

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
			stmt.setString(1, reviewid);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				review.setVideoid(rs.getInt("videoid"));
				review.setUsername(rs.getString("username"));
				review.setReviewid(rs.getInt("reviewid"));
				review.setFecha_hora(rs.getDate("fecha_hora"));
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
	private void validateUser(String reviewid) {
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
