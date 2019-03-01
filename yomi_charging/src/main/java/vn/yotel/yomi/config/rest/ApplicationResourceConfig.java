package vn.yotel.yomi.config.rest;


import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

import vn.yotel.commons.rest.filter.RequestParameterLogFilter;


/**
 *
 */
@javax.ws.rs.ApplicationPath("/v1/")
public class ApplicationResourceConfig extends ResourceConfig {

    public ApplicationResourceConfig() {
    	register(RequestContextFilter.class);
//    	register(ObjectMapperProvider.class);
    	
    	
    	//register features
		register(JacksonFeature.class);	
		register(MultiPartFeature.class);
		
    	// register  filter
//		register(CORSResponseFilter.class);
		register(RequestParameterLogFilter.class);
        // point to packages containing your resources
//        packages("vn.yotel.iloto.resource");
		packages("vn.yotel.vbilling.resource");
    }
    
}
