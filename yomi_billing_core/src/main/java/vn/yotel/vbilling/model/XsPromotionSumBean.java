package vn.yotel.vbilling.model;

import java.io.Serializable;

public class XsPromotionSumBean implements Serializable {
    private String msisdn;
    private String number_pick;
    private String status;
    private String created_date;
    private String created_time;
    private String id;
    private String promo_id;
    private String award_type;
    private String sum_note;
    private String vpoint;

    public XsPromotionSumBean() {
    }

    public XsPromotionSumBean(String msisdn, String number_pick, String status, String created_date, String created_time, String id, String promo_id, String award_type, String sum_note, String vpoint) {
        this.msisdn = msisdn;
        this.number_pick = number_pick;
        this.status = status;
        this.created_date = created_date;
        this.created_time = created_time;
        this.id = id;
        this.promo_id = promo_id;
        this.award_type = award_type;
        this.sum_note = sum_note;
        this.vpoint = vpoint;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getNumber_pick() {
        return number_pick;
    }

    public void setNumber_pick(String number_pick) {
        this.number_pick = number_pick;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPromo_id() {
        return promo_id;
    }

    public void setPromo_id(String promo_id) {
        this.promo_id = promo_id;
    }

    public String getAward_type() {
        return award_type;
    }

    public void setAward_type(String award_type) {
        this.award_type = award_type;
    }

    public String getSum_note() {
        return sum_note;
    }

    public void setSum_note(String sum_note) {
        this.sum_note = sum_note;
    }

    public String getVpoint() {
        return vpoint;
    }

    public void setVpoint(String vpoint) {
        this.vpoint = vpoint;
    }
}
