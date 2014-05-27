package edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api;



import javax.ws.rs.GET;
import javax.ws.rs.Path;

import edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api.model.VideoshareRootAPI;





@Path("/")
public class VideoshareRootAPIResource {

	
	@GET
	public VideoshareRootAPI getRootAPI() {
		VideoshareRootAPI api = new VideoshareRootAPI();
		return api;
	}
	
	private boolean admin;

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}	
	
	
}
