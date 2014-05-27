package edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api;


public class VideoshareError {
	public VideoshareError (int status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
 
	
	private int status;
	private String message;
 
	public VideoshareError() {
		super();
	}
 
	
	public int getStatus() {
		return status;
	}
 
	public void setStatus(int status) {
		this.status = status;
	}
 
	public String getMessage() {
		return message;
	}
 
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
