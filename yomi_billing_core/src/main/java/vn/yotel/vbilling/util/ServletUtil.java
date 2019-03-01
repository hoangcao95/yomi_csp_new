package vn.yotel.vbilling.util;

import javax.servlet.http.HttpServletRequest;

public class ServletUtil {
	public static String baseUrl(HttpServletRequest request) {
		String fullUrl = request.getRequestURL().toString();
		String baseUrl = fullUrl.replace(request.getRequestURI(), request.getContextPath());
		return baseUrl;
	}
}
