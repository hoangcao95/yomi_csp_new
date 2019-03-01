package vn.yotel.vbilling.ws;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService
public class HtServiceImpl implements IHtService {
	
	private static	Logger LOG = LoggerFactory.getLogger(HtServiceImpl.class);
	
	public HtWsResponse ping(
			@WebParam(name = "username") String username,
			@WebParam(name = "password") String password){
		LOG.info(String.format("username=%s", username));
		return new HtWsResponse();
	}
}