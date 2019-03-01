package vn.yotel.vbilling.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.yotel.commons.model.LinkModel;
import vn.yotel.commons.util.SaaJSoapClient;


public class ChargingCSPClient {

	private static final Logger logger = LoggerFactory.getLogger(ChargingCSPClient.class);

	private String username;
	private String password;
	private String wsUrl;
	private String wsTargetNamespace;
	private String wsNamespacePrefix;
	private String headerKey = "x-ibm-client-id";
	private String headerValue = "412c4625-5aed-459b-bf58-a10494129fc2";

	public String sendMessage(String serviceCode, String msisdn, String content) throws Exception {
        List<LinkModel> childElements = new ArrayList<LinkModel>();
        childElements.add(new LinkModel("ServiceCode", serviceCode));
        childElements.add(new LinkModel("ISDN", msisdn));
        childElements.add(new LinkModel("Content", content));
        childElements.add(new LinkModel("User", username));
        childElements.add(new LinkModel("Password", password));
        long lstart = System.currentTimeMillis();
		String _funcWsUrl = wsUrl + "/mbfn/sb/SOAPRequestServicecps/sendMessage";
		SaaJSoapClient saaJSoapClient = new SaaJSoapClient(_funcWsUrl, wsNamespacePrefix, "", wsTargetNamespace, "sendMessage", childElements);
		// Header
		List<LinkModel> headerElements = new ArrayList<LinkModel>();
		headerElements.add(new LinkModel(headerKey, headerValue));
		headerElements.add(new LinkModel("Content-Type", "application/xml"));
		saaJSoapClient.setAddedHeaders(headerElements);
		//
		long lend = System.currentTimeMillis();
		logger.info("Prepared time: " + (lend - lstart));
		lstart = System.currentTimeMillis();
		String resultCode = saaJSoapClient.processSoapCallAndResultByTagName("return");
		lend = System.currentTimeMillis();
		logger.info("Executed-1 time: " + (lend - lstart));
		logger.info("msisdn[{}] => resultCode [{}]: ", msisdn, resultCode);
		return resultCode;
	}

	/**
	 * Hệ thống dịch vụ VAS CP/SP sẽ gửi lệnh đăng ký/hủy gói cước vào hệ thống Charging CSP,
	 * mặc định hệ thống chỉ được cấp cú pháp hủy
	 * @param serviceCode
	 * @param msisdn
	 * @param commandCode
	 * @param packageCode
	 * @param sourceCode
	 * @return
	 * @throws Exception
	 */

	public String receiverServiceReq(String serviceCode, String msisdn, String commandCode, String packageCode, String sourceCode) throws Exception {
        List<LinkModel> childElements = new ArrayList<LinkModel>();
        childElements.add(new LinkModel("ServiceCode", serviceCode));
        childElements.add(new LinkModel("ISDN", msisdn));
        childElements.add(new LinkModel("CommandCode", commandCode));
        childElements.add(new LinkModel("PackageCode", packageCode));
        childElements.add(new LinkModel("SourceCode", sourceCode));
        childElements.add(new LinkModel("User", username));
        childElements.add(new LinkModel("Password", password));
        childElements.add(new LinkModel("Description", "Yomi"));
        long lstart = System.currentTimeMillis();
        String _funcWsUrl = wsUrl + "/mbfn/sb/SOAPRequestServicecps/receiverServiceReq";
		SaaJSoapClient saaJSoapClient = new SaaJSoapClient(_funcWsUrl, wsNamespacePrefix, "", wsTargetNamespace, "receiverServiceReq", childElements);
		// Header
		List<LinkModel> headerElements = new ArrayList<LinkModel>();
		headerElements.add(new LinkModel(headerKey, headerValue));
		saaJSoapClient.setAddedHeaders(headerElements);
		//
		long lend = System.currentTimeMillis();
		logger.info("Prepared time: " + (lend - lstart));
		lstart = System.currentTimeMillis();
		String resultCode = saaJSoapClient.processSoapCallAndResultByTagName("return");
		lend = System.currentTimeMillis();
		logger.info("Executed-1 time: " + (lend - lstart));
		logger.info("msisdn[{}] => resultCode [{}]: ", msisdn, resultCode);
		return resultCode;
	}

	/**
	 * Hệ thống dịch vụ VAS CP/SP sẽ gửi lệnh tải nội dung qua OTP lấy thông tin transactionID,
	 * OTP gửi về thuê bao sau đó dịch vụ VAS CP/SP sẽ gọi sang hàm 2.2.4 thực hiện trử cước cho nội dung
	 * @param serviceCode
	 * @param msisdn
	 * @param packageCode
	 * @param packageName
	 * @param spId
	 * @param cpId
	 * @param contentId
	 * @param categoryId
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	public String minusMoneyOtp(String serviceCode, String msisdn, String packageCode, String packageName, String spId, String cpId,
			String contentId, String categoryId, String amount) throws Exception {
        List<LinkModel> childElements = new ArrayList<LinkModel>();
		childElements.add(new LinkModel("ServiceCode", serviceCode));
		childElements.add(new LinkModel("ISDN", msisdn));
		childElements.add(new LinkModel("PackageCode", packageCode));
		childElements.add(new LinkModel("PackageName", packageName));
		childElements.add(new LinkModel("SP_ID", spId));
		childElements.add(new LinkModel("CP_ID", cpId));
		childElements.add(new LinkModel("Content_ID", contentId));
		childElements.add(new LinkModel("Category_ID", categoryId));
		childElements.add(new LinkModel("Amount", amount));
		childElements.add(new LinkModel("UserName", username));
		childElements.add(new LinkModel("Password", password));
		childElements.add(new LinkModel("Description", "iLoto-Taile"));
        long lstart = System.currentTimeMillis();
        String _funcWsUrl = wsUrl + "/mbfn/sb/SOAPRequestServicecps/minusMoneyOtp";
		SaaJSoapClient saaJSoapClient = new SaaJSoapClient(_funcWsUrl, wsNamespacePrefix, "", wsTargetNamespace, "minusMoneyOtp", childElements);
		// Header
		List<LinkModel> headerElements = new ArrayList<LinkModel>();
		headerElements.add(new LinkModel(headerKey, headerValue));
		saaJSoapClient.setAddedHeaders(headerElements);
		//
		long lend = System.currentTimeMillis();
		logger.info("Prepared time: " + (lend - lstart));
		lstart = System.currentTimeMillis();
		String resultCode = saaJSoapClient.processSoapCallAndResultByTagName("return");
		lend = System.currentTimeMillis();
		logger.info("Executed-1 time: " + (lend - lstart));
		logger.info("msisdn[{}] => resultCode [{}]: ", msisdn, resultCode);
		return resultCode;
	}

	/**
	 * Hệ thống dịch vụ VAS CP/SP dùng để kiêm tra OTP hợp lệ với hàm 2.2.3
	 * @param transactionId
	 * @param otp
	 * @return
	 * @throws Exception
	 */
	public String confirmMinusMoney(String transactionId, String otp) throws Exception {
        List<LinkModel> childElements = new ArrayList<LinkModel>();
		childElements.add(new LinkModel("TransactionId", transactionId));
		childElements.add(new LinkModel("OTP", otp));
		childElements.add(new LinkModel("UserName", username));
		childElements.add(new LinkModel("Password", password));
        long lstart = System.currentTimeMillis();
        String _funcWsUrl = wsUrl + "/mbfn/sb/SOAPRequestServicecps/confirmMinusMoney";
		SaaJSoapClient saaJSoapClient = new SaaJSoapClient(_funcWsUrl, wsNamespacePrefix, "", wsTargetNamespace, "confirmMinusMoney", childElements);
		// Header
		List<LinkModel> headerElements = new ArrayList<LinkModel>();
		headerElements.add(new LinkModel(headerKey, headerValue));
		saaJSoapClient.setAddedHeaders(headerElements);
		//
		long lend = System.currentTimeMillis();
		logger.info("Prepared time: " + (lend - lstart));
		lstart = System.currentTimeMillis();
		String resultCode = saaJSoapClient.processSoapCallAndResultByTagName("return");
		lend = System.currentTimeMillis();
		logger.info("Executed-1 time: " + (lend - lstart));
		logger.info("transactionId[{}] => resultCode [{}]: ", transactionId, resultCode);
		return resultCode;
	}

	public String exeReceivedCP_MT(String serviceCode, String packageCode, String contents) throws Exception {
        List<LinkModel> childElements = new ArrayList<LinkModel>();
		childElements.add(new LinkModel("ServiceCode", serviceCode));
		childElements.add(new LinkModel("PackageCode", packageCode));
		childElements.add(new LinkModel("Contents", contents));
		childElements.add(new LinkModel("User", username));
		childElements.add(new LinkModel("Password", password));
        long lstart = System.currentTimeMillis();
        String _funcWsUrl = wsUrl + "/mbfn/sb/SOAPRequestServicecps/exeReceivedCP_MT";
		SaaJSoapClient saaJSoapClient = new SaaJSoapClient(_funcWsUrl, wsNamespacePrefix, "", wsTargetNamespace, "exeReceivedCP_MT", childElements);
		// Header
		List<LinkModel> headerElements = new ArrayList<LinkModel>();
		headerElements.add(new LinkModel(headerKey, headerValue));
		saaJSoapClient.setAddedHeaders(headerElements);
		//
		long lend = System.currentTimeMillis();
		logger.info("Prepared time: " + (lend - lstart));
		lstart = System.currentTimeMillis();
		String resultCode = saaJSoapClient.processSoapCallAndResultByTagName("return");
		lend = System.currentTimeMillis();
		logger.info("Executed-1 time: " + (lend - lstart));
		logger.info("PackageCode[{}] => resultCode [{}]: ", packageCode, resultCode);
		return resultCode;
	}
	
	/**
	 * Hàm minusMoneyCheckMO ( Hàm trừ tiền không cần xác nhận OTP cần truyền các tham số trừ cước, check MO)
		Mục đích: Trừ tiền gói cước với các gói không cần phải khai báo các thông tin trên hệ thống CSP
	 * @param serviceCode
	 * @param msisdn
	 * @param requestId
	 * @param packageCode
	 * @param packageName
	 * @param spId
	 * @param cpId
	 * @param contentId
	 * @param categoryId
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	public String minusMoneyCheckMO(String serviceCode, String msisdn, String requestId, String packageCode, String packageName, String spId, String cpId,
			String contentId, String categoryId, String amount) throws Exception {
        List<LinkModel> childElements = new ArrayList<LinkModel>();
		childElements.add(new LinkModel("ServiceCode", serviceCode));
		childElements.add(new LinkModel("ISDN", msisdn));
		childElements.add(new LinkModel("RequestId", requestId));
		childElements.add(new LinkModel("PackageCode", packageCode));
		childElements.add(new LinkModel("PackageName", packageName));
		childElements.add(new LinkModel("SP_ID", spId));
		childElements.add(new LinkModel("CP_ID", cpId));
		childElements.add(new LinkModel("Content_ID", contentId));
		childElements.add(new LinkModel("Category_ID", categoryId));
		childElements.add(new LinkModel("Amount", amount));
		childElements.add(new LinkModel("UserName", username));
		childElements.add(new LinkModel("Password", password));
        long lstart = System.currentTimeMillis();
        String _funcWsUrl = wsUrl + "/mbfn/sb/SOAPRequestServicecps/receiverPackageReq";
		SaaJSoapClient saaJSoapClient = new SaaJSoapClient(_funcWsUrl, wsNamespacePrefix, "", wsTargetNamespace, "minusMoneyCheckMO", childElements);
		// Header
		List<LinkModel> headerElements = new ArrayList<LinkModel>();
		headerElements.add(new LinkModel(headerKey, headerValue));
		saaJSoapClient.setAddedHeaders(headerElements);
		//
		long lend = System.currentTimeMillis();
		logger.info("Prepared time: " + (lend - lstart));
		lstart = System.currentTimeMillis();
		String resultCode = saaJSoapClient.processSoapCallAndResultByTagName("return");
		lend = System.currentTimeMillis();
		logger.info("Executed-1 time: " + (lend - lstart));
		logger.info("msisdn[{}] => resultCode [{}]: ", msisdn, resultCode);
		return resultCode;
	}


	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setWsUrl(String wsUrl) {
		this.wsUrl = wsUrl;
	}

	public void setWsTargetNamespace(String wsTargetNamespace) {
		this.wsTargetNamespace = wsTargetNamespace;
	}

	public void setWsNamespacePrefix(String wsNamespacePrefix) {
		this.wsNamespacePrefix = wsNamespacePrefix;
	}

    public String minusMoneyCheckMORest(String serviceCode, String msisdn, String requestId, String packageCode, String packageName, String spId, String cpId,
                                    String contentId, String categoryId, String amount, String user, String pass) throws Exception {
        List<LinkModel> childElements = new ArrayList<LinkModel>();
        childElements.add(new LinkModel("ServiceCode", serviceCode));
        childElements.add(new LinkModel("ISDN", msisdn));
        childElements.add(new LinkModel("RequestId", requestId));
        childElements.add(new LinkModel("PackageCode", packageCode));
        childElements.add(new LinkModel("PackageName", packageName));
        childElements.add(new LinkModel("SP_ID", spId));
        childElements.add(new LinkModel("CP_ID", cpId));
        childElements.add(new LinkModel("Content_ID", contentId));
        childElements.add(new LinkModel("Category_ID", categoryId));
        childElements.add(new LinkModel("Amount", amount));
        childElements.add(new LinkModel("UserName", user));
        childElements.add(new LinkModel("Password", pass));
        long lstart = System.currentTimeMillis();
//        String _funcWsUrl = wsUrl + "/mbfn/sb/SOAPRequestServicecps/receiverPackageReq";
		String _funcWsUrl = "https://10.3.60.49/mbfn/sb/SOAPRequestServicecps/receiverPackageReq";
        SaaJSoapClient saaJSoapClient = new SaaJSoapClient(_funcWsUrl, wsNamespacePrefix, "", wsTargetNamespace, "minusMoneyCheckMO", childElements);
        // Header
        List<LinkModel> headerElements = new ArrayList<LinkModel>();
        headerElements.add(new LinkModel(headerKey, headerValue));
        saaJSoapClient.setAddedHeaders(headerElements);
        //
        long lend = System.currentTimeMillis();
        logger.info("Prepared time: " + (lend - lstart));
        lstart = System.currentTimeMillis();
        String resultCode = saaJSoapClient.processSoapCallAndResultByTagName("return");
        lend = System.currentTimeMillis();
        logger.info("Executed-1 time: " + (lend - lstart));
        logger.info("msisdn[{}] => resultCode [{}]: ", msisdn, resultCode);
        return resultCode;
    }
}
