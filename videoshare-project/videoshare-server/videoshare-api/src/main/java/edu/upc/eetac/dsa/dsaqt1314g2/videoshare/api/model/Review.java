package edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api.model;


public class Review {
	int reviewid;
	int videoid;
	String username;
	String reviewtext;
	long fecha_hora;
	public int getReviewid() {
		return reviewid;
	}
	public void setReviewid(int reviewid) {
		this.reviewid = reviewid;
	}
	public int getVideoid() {
		return videoid;
	}
	public void setVideoid(int videoid) {
		this.videoid = videoid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getReviewtext() {
		return reviewtext;
	}
	public void setReviewtext(String reviewtext) {
		this.reviewtext = reviewtext;
	}
	public long getFecha_hora() {
		return fecha_hora;
	}
	public void setFecha_hora(long fecha_hora) {
		this.fecha_hora = fecha_hora;
	}
	
}
