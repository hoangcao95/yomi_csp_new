package vn.yotel.vbilling.thread;

import java.io.File;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.yotel.commons.context.AppContext;
import vn.yotel.commons.exception.AppException;
import vn.yotel.commons.util.FileUtil;
import vn.yotel.thread.ManageableThread;
import vn.yotel.vbilling.jpa.Subscriber;
import vn.yotel.vbilling.jpa.SubscriberDailySms;
import vn.yotel.vbilling.jpa.VasPackage;
import vn.yotel.vbilling.model.MTRequest;
import vn.yotel.vbilling.service.SubscriberDailySmsService;
import vn.yotel.vbilling.service.SubscriberService;
import vn.yotel.vbilling.service.VasPackageService;
import vn.yotel.vbilling.util.CDRUtil;
import vn.yotel.vbilling.util.MessageBuilder;
import vn.yotel.yomi.AppParams;
import vn.yotel.yomi.Constants;

public class ProcessSendDailySmsForDayPackage extends ManageableThread {

    private static Logger LOG = LoggerFactory.getLogger(ProcessSendDailySmsForDayPackage.class);


    @SuppressWarnings("unchecked")
    private ConcurrentLinkedQueue<MTRequest> mtQueue = (ConcurrentLinkedQueue<MTRequest>) AppContext.getBean("mtQueueToCSP");
    private Object mtQueueNotifier = AppContext.getBean("mtQueueToCSPNotifier");

    private SubscriberDailySmsService subscriberDailySmsService;
    private SubscriberService subscriberService;
    private VasPackageService vasPackageService;
    private Date today = new Date();
    private String suffixMtDailySms;
    private String startTimeStr = "09:00:00";
    private String listPackageid = "1,5,2,7";
    protected String startDate = "";
    protected boolean isGreater = true;
    private boolean isDev = true;
    private boolean firstTime = true;
    private String msisdnsTest = "";
    protected String lastDate = "";
    // protected String firstDate = "";
    protected boolean isDone = true;
    protected String configFile = "";
    protected String hourStart = "0";
    protected String templateSms = "";

    protected String LAST_DATE_KEY = "_LAST_SMS_7DAY_KEY_";
    protected String FIRST_DATE_KEY = "_FIRST_SMS_7DAY_KEY_";
    protected String IS_DONE_KEY = "_IS_DONE_KEY_";

    private int tps = 10;

    protected List<Integer> packageIds = new ArrayList<Integer>();

    // Gói ngày: Nhắn 01 tin nhắn, tin nhắn đầu tiền sau 15 ngày từ khi đăng ký
    // dịch vụ thành công, tần suất 7 ngày/lần.

    @Override
    protected void loadParameters() throws AppException {
        // yomiBo = (YomiBo) AppContext.getBean("yomiBo");
        if (this.params != null) {
            this.suffixMtDailySms = this.getParamAsString("mt-suffix-dailysms");
            this.startTimeStr = this.getParamAsString("start-time");
            this.listPackageid = this.getParamAsString("list-packageid");
            this.isDev = this.getParamAsBoolean("is-dev");
            this.msisdnsTest = this.getParamAsString("msisdns-test");
            this.isGreater = this.getParamAsBoolean("is-greater");
            this.startDate = this.getParamAsString("start-date");
            this.tps = this.params.optInt("tps", 10);
            this.hourStart = this.getParamAsString("hour-start");
            this.firstTime = this.getParamAsBoolean("first-time");
            this.configFile = this.params.optString("config-file", "7day_sms_config.txt");
            String[] splLstPackage = this.listPackageid.split(",");
            for (String id : splLstPackage) {
                if (!id.isEmpty())
                    packageIds.add(Integer.parseInt(id));
            }
        } else {
            LOG.warn("Could not get parameters from the configuration file");
        }
        subscriberDailySmsService = (SubscriberDailySmsService) AppContext.getBean("subscriberDailySmsService");
        subscriberService = (SubscriberService) AppContext.getBean("subscriberService");
        vasPackageService = (VasPackageService) AppContext.getBean("vasPackageService");
    }

    @Override
    protected void initializeSession() throws AppException {
        active = vn.yotel.thread.Constants.HAInfo.HAMode.MASTER.equals(AppParams.SERVER_MODE);
    }

    @Override
    protected boolean processSession() throws AppException {
        if (tps <= 0) {
            tps = 10; // Default TPS
        }
        int sleepTime = (int) (1000.0 / tps);
        while (!active && !requireStop) {
            this.setState(vn.yotel.thread.Constants.ManageableThreadState.IDLE);
            synchronized (activeMonitorObj) {
                try {
                    for (int count = 0; (count < 600) && !active && !requireStop; count++) {
                        activeMonitorObj.wait(100L);
                        // activeMonitorObj.wait(10L);
                    }
                } catch (InterruptedException e) {
                }
            }
            active = vn.yotel.thread.Constants.HAInfo.HAMode.MASTER.equals(AppParams.SERVER_MODE);
        }
        LOG.info("Process started and in MASTER mode: {}", AppParams.SERVER_MODE);
        this.setState(vn.yotel.thread.Constants.ManageableThreadState.NORMAL);
        String currentHour = new SimpleDateFormat("HH").format(new Date());
        if (!this.hourStart.equals(currentHour)) {
            LOG.info("Not serving time. Start serving at: {}", hourStart);
        } else {
            try {
                boolean hasMore = true;
                while (!requireStop && hasMore && active) {
                    today = new Date();
                    List<Object[]> allSubs = new ArrayList();
                    if (isDev) {
                        if (!msisdnsTest.isEmpty()) {
                            String[] lsMsisdn = msisdnsTest.split(";");
                            for (String item : lsMsisdn) {
                                Object[] obj = new Object[3];
                                obj[0] = item;
                                obj[1] = 77;
                                obj[2] = 1;
                                allSubs.add(obj);
                            }
                        }
                    } else {
                        allSubs = subscriberDailySmsService.getAllSubscriberToSendDailySmsForDayPackage(today, packageIds,
                                CDRUtil.YYYYMMDD_sdf.parse(startDate));
                    }
                    LOG.info("Process SendDaily Sms For Day Package for: {} subscriber(s)", allSubs.size());
                    if (allSubs.size() == 0) {
                        hasMore = false;
                    } else {
                        LOG.info("Process send daily-sms for day package : {} subscriber(s)", allSubs.size());
                    }
                    while (allSubs.size() > 0) {
                        Object[] oneSubs = allSubs.remove(0);
                        String msisdn = (String) oneSubs[0];
                        int days = (int) oneSubs[1];
                        int packageId = (int) oneSubs[2];
                        String mtSms = this.suffixMtDailySms;
                        Subscriber subscriber = subscriberService.findByMsisdnAndPackageId(msisdn, packageId);
                        if (subscriber != null) {
                            VasPackage vasPackage = vasPackageService.findOne(packageId);
                            mtSms = mtSms.replaceAll("<TENGOI>", vasPackage.getDesc());

                            SimpleDateFormat sdfHHmmss = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                            String stringDate = sdfHHmmss.format(subscriber.getExpiredDate());
                            mtSms = mtSms.replaceAll("<HANSUDUNG>", stringDate);

                            String giaGoi = String.valueOf(vasPackage.getPriceFormattedDot()) + "đ/"
                                    + getDurationString(vasPackage.getDuration());
                            mtSms = mtSms.replaceAll("<GIAGOI>", giaGoi);

                            // LOG.info("7 day <MSISDN>: {} -- sms: {}",
                            // (String) oneSubs[0], mtSms);

                            MTRequest mtReq = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, (String) oneSubs[0],
                                    mtSms, null, Constants.CommandCode.SMS_DAILY_PRG);
                            mtReq.setBrandName(true);
                            this.sendSMS(mtReq);
                            SubscriberDailySms subsDailySms = new SubscriberDailySms();
                            subsDailySms.setMessage(mtSms);
                            subsDailySms.setMsisdn((String) oneSubs[0]);
                            subsDailySms.setCreatedDate(new Date());
                            subsDailySms.seType(2);
                            subscriberDailySmsService.create(subsDailySms);
                            safeSleep(sleepTime);
                        }
                    }
                    if (isDev && firstTime) {
                        hasMore = false;
                        firstTime = false;
                    }
                }
            } catch (Exception e) {
                LOG.error("", e);
            }
        }
        return true;
    }

    /**
     *
     * @param mtReq
     */
    private void sendSMS(MTRequest mtReq) {
        mtQueue.offer(mtReq);
        synchronized (mtQueueNotifier) {
            mtQueueNotifier.notifyAll();
        }
    }

    @Override
    protected void completeSession() throws AppException {
    }

    @Override
    public void notifyEnteringNewMode(String serverOldMode, String serverNewMode) {
        if ((serverNewMode != null) && serverNewMode.equals(serverOldMode)) {
            LOG.warn("Shoudl not call notify when there is nothing changed");
            return;
        }
        if (vn.yotel.thread.Constants.HAInfo.HAMode.MASTER.equals(serverNewMode)) {
            active = true;
            synchronized (activeMonitorObj) {
                activeMonitorObj.notifyAll();
            }
        } else {
            active = false;
            LOG.warn("Going to stop thread charging thread while entering BACKUP mode");
        }
    }

    private String getDurationString(int duration) {

        String durationString = "";
        if (duration == 1)
            durationString = "ngày";
        if (duration == 7)
            durationString = "tuần";
        if (duration == 30)
            durationString = "tháng";
        return durationString;

    }

    private boolean isServingTime() throws AppException {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Time currentTime = Time.valueOf(dateFormat.format(new Date()));
        Time startTime = Time.valueOf(startTimeStr);
        return currentTime.after(startTime);
    }
}

