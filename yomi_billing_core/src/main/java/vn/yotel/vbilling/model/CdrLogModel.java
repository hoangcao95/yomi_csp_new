package vn.yotel.vbilling.model;

public class CdrLogModel {
    private String id;

    private String msisdn;

    private String vasCode;

    private String time1;

    private String time2;

    private String time3;

    private String status;

    private String action;

    private String amount;

    private String descip;

    private String numSub;

    private String totalAmount;

    private String date1;

    public CdrLogModel() {
    }

    public CdrLogModel(String id, String msisdn, String vasCode, String time1, String time2, String time3, String status, String action, String amount, String descip, String numSub, String totalAmount, String date1) {
        this.id = id;
        this.msisdn = msisdn;
        this.vasCode = vasCode;
        this.time1 = time1;
        this.time2 = time2;
        this.time3 = time3;
        this.status = status;
        this.action = action;
        this.amount = amount;
        this.descip = descip;
        this.numSub = numSub;
        this.totalAmount = totalAmount;
        this.date1 = date1;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getVasCode() {
        return vasCode;
    }

    public void setVasCode(String vasCode) {
        this.vasCode = vasCode;
    }

    public String getTime1() {
        return time1;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public String getTime2() {
        return time2;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }

    public String getTime3() {
        return time3;
    }

    public void setTime3(String time3) {
        this.time3 = time3;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescip() {
        return descip;
    }

    public void setDescip(String descip) {
        this.descip = descip;
    }

    public String getNumSub() {
        return numSub;
    }

    public void setNumSub(String numSub) {
        this.numSub = numSub;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }
}
