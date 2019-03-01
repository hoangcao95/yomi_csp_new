package vn.yotel.vbilling.ws;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface IHtService {

	public HtWsResponse ping(
			@WebParam(name = "username") String username,
			@WebParam(name = "password") String password);
}