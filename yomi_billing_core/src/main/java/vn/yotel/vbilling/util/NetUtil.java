package vn.yotel.vbilling.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Strings;

public class NetUtil {

	public static String contentFromLink(String link, final CloseableHttpClient httpClient, final String localAddr)
			throws ClientProtocolException, IOException {
		boolean closeClient = false;
		CloseableHttpClient localHttpClient = httpClient;
		if (localHttpClient == null) {
			localHttpClient = HttpClients.createDefault();
			closeClient = true;
		}
		String respStr = null;
		try {
			RequestConfig config = null;
			if (!Strings.isNullOrEmpty(localAddr)) {
				InetAddress inetLocalAddr = InetAddress.getByName(localAddr);
				config = RequestConfig.custom().setLocalAddress(inetLocalAddr).build();
			} else {
				config = RequestConfig.custom().build();
			}
			HttpGet request = new HttpGet(link);
			request.setConfig(config);
			CloseableHttpResponse response = localHttpClient.execute(request);
			try {
				respStr = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			} finally {
				response.close();
			}
		} finally {
			if (closeClient) {
				localHttpClient.close();
			}
		}
		return respStr;
	}
	
	public static void storeImageFromLink(String link, String toFile, final CloseableHttpClient httpClient, final String localAddr)
			throws ClientProtocolException, IOException {
		boolean closeClient = false;
		CloseableHttpClient localHttpClient = httpClient;
		if (localHttpClient == null) {
			localHttpClient = HttpClients.createDefault();
			closeClient = true;
		}
		try {
			RequestConfig config = null;
			if (!Strings.isNullOrEmpty(localAddr)) {
				InetAddress inetLocalAddr = InetAddress.getByName(localAddr);
				config = RequestConfig.custom().setLocalAddress(inetLocalAddr).build();
			} else {
				config = RequestConfig.custom().build();
			}
			HttpGet request = new HttpGet(link);
			request.setConfig(config);
			CloseableHttpResponse response = localHttpClient.execute(request);
			try {
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				FileOutputStream os = new FileOutputStream(toFile);
				copy(is, os);
				os.close();
				is.close();
			} finally {
				response.close();
			}
		} finally {
			if (closeClient) {
				localHttpClient.close();
			}
		}
	}
	
	/**
	 * Copy the content of the input stream into the output stream, using a
	 * temporary byte array buffer whose size is defined by
	 * {@link #IO_BUFFER_SIZE}.
	 * 
	 * @param in    The input stream to copy from.
	 * @param out   The output stream to copy to.
	 * 
	 * @throws IOException   If any error occurs during the copy.
	 */
	private static final int IO_BUFFER_SIZE = 4 * 1024;

	private static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] b = new byte[IO_BUFFER_SIZE];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}
	
	public static String encodeUrl(String url) throws MalformedURLException {
		String encodedUrl = url.replaceAll(" ", "%20");
		return encodedUrl;
	}
	
	public static String fileNameFromUrl(String url) throws UnsupportedEncodingException {
		String result = "";
		String urlDecoded = java.net.URLDecoder.decode(url, "UTF-8");
		Pattern pattern = Pattern.compile("/[a-zA-Z\\d\\-_\\.\\s\\(\\)]{1,}\\.(?i)(jpeg|jpg|png|gif|bmp)$");
		//\\.(?i)(bmp|gif|jpg|jpeg|png|ico|wmv|3gp|avi|mpg|mpeg|mp4|flv|mp3|mid|js|css)$
		Matcher matcher = pattern.matcher(urlDecoded);
		if (matcher.find()) {
			result = matcher.group(0).trim();
			result = result.replaceAll("/", "");
			result = result.replaceAll("\\s", "");
		} else {
			result = url;
			int pos = result.lastIndexOf("/");
			result = result.substring(pos + 1);
		}
		return result;
	}
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		CloseableHttpClient localHttpClient = HttpClients.createDefault();
		List<String> listLinks = Arrays.asList("http://songkhoe.vn/chuyen-muc-gioi-tinh-s2959-0.html", "http://songkhoe.vn/chuyen-muc-vui-khoe-s2953-0.html");
		for (String eachLink : listLinks) {
			String link = eachLink;
			String content = NetUtil.contentFromLink(link, localHttpClient, null);
			System.out.println(content);
		}
		localHttpClient.close();
	}
}
