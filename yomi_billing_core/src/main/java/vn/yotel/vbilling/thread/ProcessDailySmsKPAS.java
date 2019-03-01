package vn.yotel.vbilling.thread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.yotel.admin.jpa.SysParam;
import vn.yotel.admin.service.SysParamService;
import vn.yotel.commons.context.AppContext;
import vn.yotel.commons.exception.AppException;
import vn.yotel.thread.ManageableThread;
import vn.yotel.vbilling.jpa.XsPromotion;
import vn.yotel.vbilling.model.MTRequest;
import vn.yotel.vbilling.model.SmsModel;
import vn.yotel.vbilling.service.XsPromotionService;
import vn.yotel.vbilling.util.MessageBuilder;
import vn.yotel.yomi.AppParams;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProcessDailySmsKPAS extends ManageableThread {

	private static Logger LOG = LoggerFactory.getLogger(ProcessMoRequest.class);
	private static final Gson GSON_ALL = new GsonBuilder().serializeNulls().create();
	private static final SimpleDateFormat sdf_DDMMYYYY = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat sdf_DDMMYYYYHHmmss = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private ConcurrentLinkedQueue<MTRequest> mtQueueToCSP;
	private Object mtQueueToCSPNotifier;

	private SysParamService sysParamService;
	private XsPromotionService xsPromotionService;

	private final String AS_TIME_SEND_NOTIFICATION_KEY = "AS_TIME_SEND_NOTIFICATION";
	private String AS_TIME_SEND_NOTIFICATION_VALUE = "";
	private final String AS_TIME_SEND_NOTIFICATION_END_KEY = "AS_TIME_SEND_NOTIFICATION_END";
	private String AS_TIME_SEND_NOTIFICATION_END_VALUE = "";
	private final String AS_NOTIFICATION_MESSAGE_KEY = "AS_NOTIFICATION_MESSAGE";
    private final String AS_TIME_END_KEY = "AS_TIME_END";
    private String AS_TIME_END_VALUE = "";

	@Override
	protected void loadParameters() throws AppException {
		if (this.params != null) {
		} else {
			LOG.warn("Could not get parameters from the configuration file");
		}

		mtQueueToCSP = (ConcurrentLinkedQueue<MTRequest>) AppContext.getBean("mtQueueToCSP");
		mtQueueToCSPNotifier = AppContext.getBean("mtQueueToCSPNotifier");
	}

	@Override
	protected void initializeSession() throws AppException {
		sysParamService = (SysParamService) AppContext.getBean("sysParamService");
		xsPromotionService = (XsPromotionService) AppContext.getBean("xsPromotionService");

        SysParam sysParam = sysParamService.findByKey(AS_TIME_END_KEY);
        if (sysParam != null) {
            AS_TIME_END_VALUE = sysParam.getValue();
        } else {
            AS_TIME_END_VALUE = "09:00:00";
        }
	}

	@Override
	protected boolean processSession() throws AppException {
		try {
			Date dtCurrentTime = Calendar.getInstance().getTime();
			Date dtSendNotification = null;
			Date dtSendNotificationEnd = null;
			StringBuilder sb = new StringBuilder();
			SmsModel smsModel = new SmsModel();
			MTRequest mtReq = null;

			/*
			List<String> lstMsisdnMaxNumber = new ArrayList<>();
			HashMap<String, Integer> hashMap = new HashMap<>();

			List<XsPromotion> lstPromotion = xsPromotionService.findAllByStatus("ACTIVE");
			SysParam sysParamSend = sysParamService.findByKey(AS_TIME_SEND_NOTIFICATION_KEY);
			SysParam sysParamSend1 = sysParamService.findByKey(AS_TIME_SEND_NOTIFICATION_END_KEY);
			String strCurrentTime = sdf_DDMMYYYY.format(dtCurrentTime);

			if (sysParamSend != null) {
				AS_TIME_SEND_NOTIFICATION_VALUE = sysParamSend.getValue();
				dtSendNotification = sdf_DDMMYYYYHHmmss.parse(strCurrentTime + " " + AS_TIME_SEND_NOTIFICATION_VALUE);
				AS_TIME_SEND_NOTIFICATION_END_VALUE = sysParamSend1.getValue();
				dtSendNotificationEnd = sdf_DDMMYYYYHHmmss.parse(strCurrentTime + " " + AS_TIME_SEND_NOTIFICATION_END_VALUE);
				//Neu thoi gian hien tai lon hon hoac bang thoi gian gui tin thong bao thi gui
				if (dtCurrentTime.getTime() >= dtSendNotification.getTime() && dtCurrentTime.getTime() <= dtSendNotificationEnd.getTime()) {


					SysParam sysParamSMS = sysParamService.findByKey(AS_NOTIFICATION_MESSAGE_KEY);
					if (sysParamSMS != null) {
						smsModel = GSON_ALL.fromJson(sysParamSMS.getValue(), SmsModel.class);
						for (XsPromotion xs: lstPromotion) {
							if (xs.getNumber() <= 0) {
								sb.append(smsModel.getMtContent1().replaceAll("<NGAY>", strCurrentTime));
							} else {
//								sb.append(smsModel.getMtContent2().replace("<DIEM>", String.valueOf(xs.getNumber())));
								sb.append(smsModel.getMtContent2().replace("<DIEM>", String.valueOf(xs.getNumber())).replaceAll("<NGAY>", strCurrentTime));
							}
							mtReq = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, xs.getMsisdn(), sb.toString(), null, "AS_DAILY_SMS");

							if (mtReq != null) {
								mtQueueToCSP.offer(mtReq);
								synchronized (mtQueueToCSPNotifier) {
									mtQueueToCSPNotifier.notifyAll();
								}

								//Update trang thai nhan tin cua thue bao trong XsPromotion
								xs.setSendNotification(1);
								xsPromotionService.update(xs);
							}
						}
					} else {
						LOG.error("System not config content SMS Notification at time schedule");
					}
				}
			}

            Date dtEndTime = sdf_DDMMYYYYHHmmss.parse(strCurrentTime + " " + AS_TIME_END_VALUE);
			//Neu ngoai gio ket thuc giai, clear het so da pick cua thue bao va cap nhat lai trang thai gui tin
            if (dtCurrentTime.getTime() > dtEndTime.getTime()) {
                for (XsPromotion xs: lstPromotion) {
                    xs.setArrNumberPick("");
                    xs.setSendNotification(0);
                    xsPromotionService.update(xs);
                }
            }
            */




		} catch (Exception ex) {
			LOG.info("", ex);
		}
		return  false;
	}

}
