package vn.yotel.vbilling.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import vn.yotel.vbilling.model.ResponseData;

@Component
@Path(value = "/pings")
@Produces(value = { MediaType.APPLICATION_JSON })
@Consumes(value = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
public class PingResource {

	private static final Logger logger = LoggerFactory.getLogger(PingResource.class);

	@Context
	private HttpServletRequest request;

	@Context
	private HttpHeaders httpHeaders;

	@GET
	public Response ping() {
		logger.info("Ready");
		ResponseData responseData = ResponseData.responseData("1", "Ready");
		return Response.ok(responseData).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

}
