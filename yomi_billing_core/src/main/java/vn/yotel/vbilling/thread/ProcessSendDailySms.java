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
import vn.yotel.vbilling.jpa.SubscriberDailySms;
import vn.yotel.vbilling.model.MTRequest;
import vn.yotel.vbilling.service.SubscriberDailySmsService;
import vn.yotel.vbilling.util.CDRUtil;
import vn.yotel.vbilling.util.MessageBuilder;
import vn.yotel.yomi.AppParams;
import vn.yotel.yomi.Constants;

public class ProcessSendDailySms extends ManageableThread {

    private static Logger LOG = LoggerFactory.getLogger(ProcessSendDailySms.class);

    private SubscriberDailySmsService subscriberDailySmsService;
    private ConcurrentLinkedQueue<MTRequest> mtQueueToCSP;
    private Object mtQueueToCSPNotifier;

    private Date today = new Date();
    private String suffixMtDailySms;
    private String startTimeStr = "08:00:00";
    protected String lastDate = "";
    protected boolean isDone = true;
    protected String configFile = "";
    private String listPackageid = "1,5,2,7";
    protected String templateSms = "";
    protected boolean isDev = true;
    protected String msisdnsTest = "";
    protected String LAST_DATE_KEY = "_LAST_NEWS_SMS_3DAY_KEY_";
    protected String FIRST_DATE_KEY = "_FIRST_NEWS_SMS_3DAY_KEY_";
    protected String IS_DONE_KEY = "_IS_DONE_KEY_";
    protected boolean firstTime = false;
    protected String startDate = "";
    protected boolean isGreater = true;
    protected String hourStart = "0";
    private int tps = 10;
    List<Integer> packageIds = new ArrayList<Integer>();

    @Override
    protected void loadParameters() throws AppException {
        if (this.params != null) {
            this.suffixMtDailySms = this.getParamAsString("mt-suffix-dailysms");
            this.startTimeStr = this.getParamAsString("start-time");
            this.isDev = this.getParamAsBoolean("is-dev");
            this.templateSms = this.getParamAsString("template-sms");
            this.msisdnsTest = this.getParamAsString("msisdns-test");
            this.configFile = this.params.optString("config-file", "news_3day_sms_config.txt");
            this.tps = this.params.optInt("tps", 10);
            this.isGreater = this.getParamAsBoolean("is-greater");
            this.startDate = this.getParamAsString("start-date");
            this.listPackageid = this.getParamAsString("list-packageid");
            this.hourStart = this.getParamAsString("hour-start");
            String[] splLstPackage = this.listPackageid.split(",");
            for (String id : splLstPackage) {
                if (!id.isEmpty())
                    packageIds.add(Integer.parseInt(id));
            }
        } else {
            LOG.warn("Could not get parameters from the configuration file");
        }
        subscriberDailySmsService = (SubscriberDailySmsService) AppContext.getBean("subscriberDailySmsService");
        mtQueueToCSP = (ConcurrentLinkedQueue<MTRequest>) getBean("mtQueueToCSP");
        mtQueueToCSPNotifier = getBean("mtQueueToCSPNotifier");
    }

    @Override
    protected void initializeSession() throws AppException {
        active = vn.yotel.thread.Constants.HAInfo.HAMode.MASTER.equals(AppParams.SERVER_MODE);
        File _configFile = new File(configFile);
        if (!_configFile.exists()) {
            LOG.info("Create empty confige file: {}", configFile);
            try {
                _configFile.createNewFile();
                FileUtil.storeContentToFile("{}", configFile);
            } catch (Exception e) {
                LOG.error("", e);
            }
        }
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
                    for (int count = 0; (count < 60) && !active && !requireStop; count++) {
                        activeMonitorObj.wait(1000L);
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
            LOG.info("Not serving time. Start serving at: {}", startTimeStr);
        } else {
            try {
                this.loadConfig(false);
                long _lastDate = Long.parseLong(this.lastDate);
                long _getToday = Long.parseLong(getToday());
                if ((this.lastDate.isEmpty() && !this.isDone)
                        || (!this.lastDate.isEmpty() && _lastDate <= _getToday && !this.isDone)) {
                    boolean hasMore = true;
                    this.isDone = true;
                    while (!requireStop && hasMore && active) {
                        today = new Date();
                        List<String> allSubs = new ArrayList();
                        if (isDev) {
                            if (!msisdnsTest.isEmpty()) {
                                String[] lsMsisdn = msisdnsTest.split(";");
                                for (String item : lsMsisdn) {
                                    allSubs.add(item);
                                }
                            }
                        } else {
                            // allSubs =
                            // subscriberDailySmsBo.getAllSubscriberToSendDailySms(today);
                            allSubs = subscriberDailySmsService.getAllSubscriberToSendDailySmsWithCondition(today,
                                    CDRUtil.YYYYMMDD_sdf.parse(startDate));
                        }
                        LOG.info("Process send daily-sms: {} subscriber(s)", allSubs.size());
                        if (allSubs.size() == 0) {
                            hasMore = false;
                        } else {
                            LOG.info("Process send daily-sms for: {} subscriber(s)", allSubs.size());
                        }
                        while (allSubs.size() > 0) {
                            String oneSubs = allSubs.remove(0);
                            String mtSms = this.suffixMtDailySms;
                            mtSms = mtSms.replaceAll("<MSISDN>", oneSubs);
                            // LOG.info("3 day sms <MSISDN>: {} --
                            // suffixMtDailySms: {}", oneSubs,
                            // this.suffixMtDailySms);

                            MTRequest mtReq = MessageBuilder.buildMTRequest(AppParams.SHORT_CODE, oneSubs, mtSms, null,
                                    Constants.CommandCode.SMS_DAILY);
                            mtReq.setBrandName(true);
                            this.sendSMS(mtReq);
                            SubscriberDailySms subsDailySms = new SubscriberDailySms();
                            subsDailySms.setMessage(mtSms);
                            subsDailySms.setMsisdn(oneSubs);
                            subsDailySms.setCreatedDate(new Date());
                            subsDailySms.seType(1); // 1 - Daily SMS
                            subscriberDailySmsService.create(subsDailySms);
                            safeSleep(sleepTime);
                        }
                        if (isDev)
                            hasMore = false;
                    }
                    this.lastDate = getTodayByInt(3);
                } else {
                    LOG.info("Process send daily-sms: {} already run", this.lastDate);
                }
            } catch (Exception e) {
                LOG.error("", e);
            } finally {
                this.storeConfig();
            }
        }
        return true;
    }

    /**
     *
     * @param mtReq
     */
    private void sendSMS(MTRequest mtReq) {
        mtQueueToCSP.offer(mtReq);
        synchronized (mtQueueToCSPNotifier) {
            mtQueueToCSPNotifier.notifyAll();
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

    @Override
    protected void storeConfig() {
        try {
            this.isDone = false;
            //this.lastDate = getTodayByInt(3);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put(LAST_DATE_KEY, this.lastDate);
            jsonObj.put(IS_DONE_KEY, this.isDone);
            FileUtil.storeContentToFile(jsonObj.toString(), configFile);
        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    protected void loadConfig(boolean changeHAMode) {
        try {
            String jsonConfig = FileUtil.loadContentFromFile(configFile);
            JSONObject jsonObj = new JSONObject(jsonConfig);
            this.lastDate = jsonObj.optString(LAST_DATE_KEY, getToday());
            this.isDone = jsonObj.optBoolean(IS_DONE_KEY, false);
        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    private boolean isServingTime() throws AppException {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Time currentTime = Time.valueOf(dateFormat.format(new Date()));
        Time startTime = Time.valueOf(startTimeStr);
        return currentTime.after(startTime);
    }

    protected String getTodayByInt(int backDays) {
        DateTime todayDt = new DateTime().plusDays(backDays);
        long today = Long.parseLong(CDRUtil.YYYYMMDD_sdf.format(todayDt.toDate()));
        return String.valueOf(today);
    }

    protected String getToday() {
        return CDRUtil.YYYYMMDD_sdf.format(new Date());
    }
}
