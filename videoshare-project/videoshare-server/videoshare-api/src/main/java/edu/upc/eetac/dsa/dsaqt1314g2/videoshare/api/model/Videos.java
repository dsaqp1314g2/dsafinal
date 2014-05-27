package edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

public class Videos {


	private List<Link> links;
	private String videoid;
	private String nombre_video;
	private String username;
	private Date fecha;
    
    
    //Para añadir a Reviews, Tags, Puntuaciones...
    
    private List<Review> reviews = new ArrayList<Review>();
    private List<Categoria> categorias = new ArrayList<Categoria>();
    private List<Puntuaciones> puntuaciones = new ArrayList<Puntuaciones>();
    
    //añadimos métodos para añadir y 'coger'
    //Reviews
    public List<Review> getReviews() {
		return reviews;
	}
    
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	public void addReview(Review review) {
		reviews.add(review);
	}
    
    //Categorias
	public void addCategoria(Categoria categoria) {
		categorias.add(categoria);
	}
    public List<Categoria> getCategorias() {
		return categorias;
	}
	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}
    
    //Puntuaciones
    public void addPuntuacion(Puntuaciones puntuacion) {
		puntuaciones.add(puntuacion);
	}
    public List<Puntuaciones> getPuntuaciones() {
		return puntuaciones;
	}
	public void setPuntuaciones(List<Puntuaciones> puntuaciones) {
		this.puntuaciones = puntuaciones;
	}

    //Métodos para las variables
    
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	public String getVideoid() {
		return videoid;
	}
	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}
	public String getNombre_video() {
		return nombre_video;
	}
	public void setNombre_video(String nombre_video) {
		this.nombre_video = nombre_video;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	//faltan que añada Reviews/Tags/Puntuaciones
    
	
}
