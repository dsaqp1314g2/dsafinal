package edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api;


import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
 

 
@Provider
public class WebApplicationExceptionsMapper implements
		ExceptionMapper<WebApplicationException> {
	@Override
	public Response toResponse(WebApplicationException exception) {
		VideoshareError error  = new VideoshareError(
				exception.getResponse().getStatus(), exception.getMessage());
		return Response.status(error.getStatus()).entity(error)
				.type(MediaType.VIDEOSHARE_API_ERROR).build();
	}
 
}