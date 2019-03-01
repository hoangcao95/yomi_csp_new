package vn.yotel.vbilling.thread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.yotel.commons.exception.AppException;
import vn.yotel.commons.util.Util;
import vn.yotel.thread.ProcessFileThread;
import vn.yotel.thread.Constants.ManageableThreadState;
import vn.yotel.thread.Constants.HAInfo.HAMode;
import vn.yotel.vbilling.jpa.Subscriber;
import vn.yotel.vbilling.jpa.VasPackage;
import vn.yotel.vbilling.model.HandlingResult;
import vn.yotel.vbilling.service.CpGateService;
import vn.yotel.vbilling.service.SubscriberService;
import vn.yotel.vbilling.service.VasPackageService;
import vn.yotel.yomi.Constants.CommandCode;

public class ProcessSyncSubsFromFileData extends ProcessFileThread {

	private static Logger LOG = LoggerFactory.getLogger(ProcessSyncSubsFromFileData.class);

	private static final String SPLITBY = ",";

	private SubscriberService subscriberService;
	private VasPackageService vasPackageService;
	private CpGateService cpGateService;

	@Override
	protected void initializeSession() throws AppException {
		super.initializeSession();
		subscriberService = (SubscriberService) getBean("subscriberService");
		vasPackageService = (VasPackageService) getBean("vasPackageService");
		cpGateService = (CpGateService) getBean("cpGateService");
    }
	
	@Override
	protected boolean processSession() throws AppException {
		active = HAMode.MASTER.equals(getManager().getServerMode());
		if (!active) {
			LOG.warn("SyncSubs process is running on Master Server");
			setState(ManageableThreadState.IDLE);
			safeSleep(30);
			return false;
		} else {
			return super.processSession();
		}
	}

	protected void process(int paramInt) throws Exception {
		File file = this.listedFiles.get(paramInt);
		updateData(file);
	}

	private void updateData(File f) throws IOException, ParseException {
		loadContentFromFile(f);
	}

	private void loadContentFromFile(File f) throws IOException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if (!f.exists()) {
		} else {
			FileInputStream is = new FileInputStream(f);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			try {
				String line = br.readLine();

				while (line != null) {
					String[] sub = line.split(SPLITBY);
					LOG.info(line);

					Date now = new Date();
					String mpin = Util.generateMPIN();
					String transId = Util.generateTransId();
					String providerCode = "";
					String refCode = "";
					String errorCode = "0000";
					String errorDesc = "";

					boolean regNew = true;
					boolean regDone = true;
					String originalSms = sub[10];
					int amount = 0;
					LOG.info("sub[3] " + sub[3].trim());
					VasPackage vasPackage = vasPackageService.findByName(sub[3].trim());
					LOG.info("vasPackage " + vasPackage);
					String status = sub[8];
					LOG.info("sub[0] " + sub[0]);
					Subscriber subscriber = subscriberService.findByMsisdnAndPackageId(sub[0], vasPackage.getId());
					LOG.info("subscriber " + subscriber);
					int channel;
					switch (sub[9]) {
						case "WAP":
							channel = 1;
							break;
						case "APP":
							channel = 2;
							break;
						case "WEB":
							channel = 3;
							break;
						case "VASGATE":
							channel = 4;
							break;
						case "BIGTET2016":
							channel = 5;
							break;
						case "EURO2016":
							channel = 6;
							break;
						case "D10EU":
							channel = 7;
						case "BIGPRODATA2016_FREE":
							channel = 8;
							break;
						case "BIGPRODATA2016_PAID":
							channel = 9;
							break;
						case "PRO_COMBO":
							channel = 10;
							break;
						default:
							channel = 0;
							break;
					}
					LOG.info("channel " + sub[9]);
					if(subscriber == null) {
						if(status.equals("0")) {
							LOG.info("--- 0 ----");
							subscriber = new Subscriber();
							subscriber.setMsisdn(sub[0]);
							subscriber.setMpin(mpin);
							subscriber.setChannel(channel);
							subscriber.setCreatedDate(now);
							subscriber.setModifiedDate(now);
							subscriber.setRegisterDate(sdf.parse(sub[5]));
							subscriber.setExpiredDate(sdf.parse(sub[7]));
							subscriber.setLastChargedDate(null);
							subscriber.setProductId(vasPackage.getProductId());
							subscriber.setPackageId(vasPackage.getId());
							subscriber.setRegNew(1);
							subscriber.setStatus(1);
							subscriberService.create(subscriber);
							String command = CommandCode.REGISTER;
							HandlingResult postResult = cpGateService.notifyRegSubs(transId, sub[0], mpin, amount, regNew, Util.XBD_SDF.format(now),
									Util.XBD_SDF.format(subscriber.getExpiredDate()), originalSms, sub[9], sub[3], regDone, providerCode, refCode, errorCode, errorDesc, CommandCode.REGISTER_SUBS_PACKAGE);
							this.subscriberService.logSubsRequest(subscriber, command, transId, postResult.parseToHttpCode(), postResult.parseObjData(), postResult.parseResp());
						}
					} else {
//						if(status.equals("1") && (!subscriber.getRegisterDate().equals(sdf.parse(sub[5])) || !subscriber.getExpiredDate().equals(sdf.parse(sub[7])))) {
//							LOG.info("--- 1 ----");
//							subscriber.setRegNew(0);
//							subscriber.setModifiedDate(now);
//							subscriber.setRegisterDate(sdf.parse(sub[5]));
//							subscriber.setExpiredDate(sdf.parse(sub[7]));
//							subscriberService.update(subscriber);
//							String command = CommandCode.REGISTER;
//							HandlingResult postResult = cpGateService.notifyRegSubs(transId, sub[0], mpin, amount, regNew, Util.XBD_SDF.format(now),
//									Util.XBD_SDF.format(subscriber.getExpiredDate()), originalSms, sub[9], sub[3], regDone, providerCode, refCode, errorCode, errorDesc);
//							this.subscriberService.logSubsRequest(subscriber, command, transId, postResult.parseToHttpCode(), postResult.parseObjData(), postResult.parseResp());
//
//						} else if (status.equals("3") && subscriber.getStatus() != 0) {
//							LOG.info("--- 3 ----");
//							subscriber.setStatus(0);
//							subscriber.setModifiedDate(now);
//							subscriber.setUnregisterDate(sdf.parse(sub[6]));
//							this.subscriberService.update(subscriber);
//							boolean cancelDone = true;
//							String command = CommandCode.CANCEL;
//							HandlingResult postResult = cpGateService.notifyCancelSubs(transId, sub[0],
//									Util.XBD_SDF.format(now), originalSms, sub[9], vasPackage.getName(), cancelDone, "0000", "");
//							this.subscriberService.logSubsRequest(subscriber, command, transId, postResult.parseToHttpCode(), postResult.parseObjData(), postResult.parseResp());
//						}
					}
					line = br.readLine();
				}
			} finally {
				br.close();
			}
		}
	}

}
