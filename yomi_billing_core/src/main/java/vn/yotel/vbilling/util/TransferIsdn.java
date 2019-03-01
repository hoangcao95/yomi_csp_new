package vn.yotel.vbilling.util;

import java.util.Arrays;
import java.util.List;

public class TransferIsdn {
	private static final String PREFIX_0 = "0";
    private static final String PREFIX_00 = "00";
    private static final String PREFIX_84 = "84";

    private static final String[] ARR_PREFIX_OLD = {"120", "121", "122", "126", "128"};
    private static final String[] ARR_PREFIX_NEW = {"70", "79", "77", "76", "78"};

    private static final String regexValidNativeParam = "[0-9]+\\|[0-9]+";
    private static final String regexValidNumberIsdn = "[^0-9]+";

    public static String transferToParam(String msisdn) {
        if (msisdn == null || "".equals(msisdn)) {
            return "^$";
        }
        if (msisdn.startsWith(PREFIX_00)) {
            msisdn = msisdn.substring(2, msisdn.length());
        } else if (msisdn.startsWith(PREFIX_0)) {
            msisdn = msisdn.substring(1, msisdn.length());
        }
        if (msisdn.startsWith(PREFIX_84)) {
            msisdn = msisdn.substring(2, msisdn.length());
        }

        for (String prefixOld : Arrays.asList(ARR_PREFIX_OLD)) {
            if (msisdn.startsWith(prefixOld)) {
                return prepareOldValue(prefixOld, msisdn);
            }
        }

        for (String prefixNew : Arrays.asList(ARR_PREFIX_NEW)) {
            if (msisdn.startsWith(prefixNew)) {
                return prepareNewValue(prefixNew, msisdn);
            }
        }
        return prepareDefaultValue(msisdn);
    }

    public static String transferTo10(String msisdn) {
        if (msisdn == null || "".equals(msisdn)) {
            return "";
        }
        if (msisdn.startsWith(PREFIX_00)) {
            msisdn = msisdn.substring(2, msisdn.length());
        } else if (msisdn.startsWith(PREFIX_0)) {
            msisdn = msisdn.substring(1, msisdn.length());
        }
        if (msisdn.startsWith(PREFIX_84)) {
            msisdn = msisdn.substring(2, msisdn.length());
        }

        for (String prefixOld : Arrays.asList(ARR_PREFIX_OLD)) {
            if (msisdn.startsWith(prefixOld)) {
                return prepare10Digit(prefixOld, msisdn);
            }
        }
        return msisdn;
    }

    public static String transferToNativeSQL(String msisdn) {
        if (msisdn == null || "".equals(msisdn)) {
            return "000000000|000000000";
        }
        msisdn = msisdn.replaceAll(regexValidNumberIsdn, "");
        if (msisdn.startsWith(PREFIX_00)) {
            msisdn = msisdn.substring(2, msisdn.length());
        } else if (msisdn.startsWith(PREFIX_0)) {
            msisdn = msisdn.substring(1, msisdn.length());
        }
        if (msisdn.startsWith(PREFIX_84)) {
            msisdn = msisdn.substring(2, msisdn.length());
        }

        String isdn2 = "";

        for (String prefixNew : Arrays.asList(ARR_PREFIX_NEW)) {
            if (msisdn.startsWith(prefixNew)) {
                isdn2 = prepareNewValueNative(prefixNew, msisdn);
            }
        }

        for (String prefixOld : Arrays.asList(ARR_PREFIX_OLD)) {
            if (msisdn.startsWith(prefixOld)) {
                isdn2 = prepareOldValueNative(prefixOld, msisdn);
            }
        }

        if ((isdn2).matches(regexValidNativeParam)) {
            return isdn2;
        } else {
            return msisdn + "|" + msisdn;
        }
    }

    public static String prepareOldValue(String prefixOld, String msisdn) {
        StringBuilder isdnBuilder = new StringBuilder();
        String number = msisdn.replaceFirst(prefixOld, "");
        String prefixNew = Arrays.asList(ARR_PREFIX_NEW).get(getPrefixIndex(Arrays.asList(ARR_PREFIX_OLD), prefixOld));
        isdnBuilder.append("^").append(msisdn).append("$").append("|").append("^").append(prefixNew).append(number).append("$");
        return isdnBuilder.toString();
    }

    public static String prepareNewValue(String prefixOld, String msisdn) {
        StringBuilder isdnBuilder = new StringBuilder();
        String number = msisdn.replaceFirst(prefixOld, "");
        String prefixNew = Arrays.asList(ARR_PREFIX_OLD).get(getPrefixIndex(Arrays.asList(ARR_PREFIX_NEW), prefixOld));
        isdnBuilder.append("^").append(msisdn).append("$").append("|").append("^").append(prefixNew).append(number).append("$");
        return isdnBuilder.toString();
    }

    public static String prepareOldValueNative(String prefixOld, String msisdn) {
        StringBuilder isdnBuilder = new StringBuilder();
        String number = msisdn.replaceFirst(prefixOld, "");
        String prefixNew = Arrays.asList(ARR_PREFIX_NEW).get(getPrefixIndex(Arrays.asList(ARR_PREFIX_OLD), prefixOld));
        isdnBuilder.append(msisdn).append("|").append(prefixNew).append(number);
        return isdnBuilder.toString();
    }

    public static String prepareNewValueNative(String prefixOld, String msisdn) {
        StringBuilder isdnBuilder = new StringBuilder();
        String number = msisdn.replaceFirst(prefixOld, "");
        String prefixNew = Arrays.asList(ARR_PREFIX_OLD).get(getPrefixIndex(Arrays.asList(ARR_PREFIX_NEW), prefixOld));
        isdnBuilder.append(msisdn).append("|").append(prefixNew).append(number);
        return isdnBuilder.toString();
    }

    public static String prepareDefaultValue(String msisdn) {
        StringBuilder isdnBuilder = new StringBuilder();
        isdnBuilder.append("^").append(msisdn).append("$");
        return isdnBuilder.toString();
    }

    private static String prepare10Digit(String prefixOld, String msisdn) {
        StringBuilder isdnBuilder = new StringBuilder();
        String number = msisdn.replaceFirst(prefixOld, "");
        String prefixNew = Arrays.asList(ARR_PREFIX_NEW).get(getPrefixIndex(Arrays.asList(ARR_PREFIX_OLD), prefixOld));
        isdnBuilder.append(prefixNew).append(number);
        return isdnBuilder.toString();
    }

    public static int getPrefixIndex(List lst, String prefix) {
        for (int i = 0; i < lst.size(); i++) {
            if (lst.get(i).equals(prefix)) {
                return i;
            }
        }
        return -1;
    }
}
