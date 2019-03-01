package vn.yotel.vbilling.util;

import vn.yotel.yomi.AppParams;
import vn.yotel.yomi.Constants;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CDRUtil {

    public static SimpleDateFormat YYYYMMDD_sdf = new SimpleDateFormat("yyyyMMdd");
    public static SimpleDateFormat _YYYYMMDD_sdf = new SimpleDateFormat("yyyy_MM_dd");
    public static SimpleDateFormat _YYYYMMDDHH_sdf = new SimpleDateFormat("yyyy_MM_dd_HH");
    public static SimpleDateFormat PRC_EXEC_SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    static List<String> RETRY_CHARGE_CPS_STATUS = Arrays.asList(
            "CPS-2003", "2003",
            "CPS-2011", "2011",
            "CPS-2014", "2014",
            "CPS-2015", "2015",
            "CPS-2019", "2019",
            "CPS-2020", "2020",
            "CPS-3000", "3000", "9999");


    public static String generateFileName(String index) {
        String val1 = YYYYMMDD_sdf.format(new Date());
        return String.format("%s_%s_%s.cdr", AppParams.CDR_FILE_NAME, val1, index);
    }

    public static String formatIsdn(String normaliedIsdn) {
        return Constants.COUNTRY_MOBILE_PREFIX + normaliedIsdn;
    }

    public static String convertPRCTime2CDRTime(String prcDateTime) {
        try {
            return Constants.CDR.SDF.format(PRC_EXEC_SDF.parse(prcDateTime));
        } catch (Exception e) {
            return prcDateTime;
        }
    }

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date dateOld, Date dateNew, TimeUnit timeUnit) {
        long diffInMillies = dateNew.getTime() - dateOld.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    /**
     *
     * @param chargeStatus
     * @return
     */
    public static boolean shouldGenerateCDR(String chargeStatus) {
        List<String> SUITABLE_STATUS = Arrays.asList("1001");
        if(SUITABLE_STATUS.contains(chargeStatus)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param chargeStatus
     * @return
     */
    public static boolean shouldUpdateLastSuccessTransaction(String chargeStatus) {
        List<String> SUITABLE_STATUS = Arrays.asList("2011");
        if(SUITABLE_STATUS.contains(chargeStatus)) {
            return false;
        } else {
            return false;
        }
    }

    public static boolean shouldRetryCharge(String chargeStatus) {
        if (RETRY_CHARGE_CPS_STATUS.contains(chargeStatus)) {
            return true;
        } else {
            return false;
        }
    }

    public static int todayInt() {
        Date now = new Date();
        return Integer.parseInt(YYYYMMDD_sdf.format(now));
    }

    public static int updateChargeCountInDay(int oldChargeCountInDay) {
        int newChargeCountInDay = CDRUtil.todayInt() * 10;
        if (oldChargeCountInDay >= newChargeCountInDay) {
            newChargeCountInDay = oldChargeCountInDay + 1;
        }
        return newChargeCountInDay;
    }
}
