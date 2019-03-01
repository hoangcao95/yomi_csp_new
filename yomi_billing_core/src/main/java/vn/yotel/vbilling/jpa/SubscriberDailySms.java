package vn.yotel.vbilling.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="yomi_subscriber_dailysms")
@NamedQueries({ @NamedQuery(name = "SubscriberDailySms.findAll", query = "SELECT a FROM SubscriberDailySms a") })
public class SubscriberDailySms implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1677721350450714110L;

    public SubscriberDailySms() {
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name="msisdn")
    private String msisdn;

    @Column(name="created_date")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name="message", columnDefinition = "TEXT")
    private String message;

    @Column(name="type")
    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public int getType() {
        return type;
    }

    public void seType(int type) {
        this.type = type;
    }
}
