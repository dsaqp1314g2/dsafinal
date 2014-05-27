package edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api;

import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;







public class VideoshareApplication extends ResourceConfig {
	
	public VideoshareApplication ( ) {
		super ( ) ;
		register(DeclarativeLinkingFeature.class) ;
		}

}
