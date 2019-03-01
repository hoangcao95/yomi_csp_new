package vn.yotel.vbilling.jpa;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name="cdr_log")
@NamedQuery(name = "CdrLog.findAll", query = "SELECT a FROM CdrLog a")
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(
                name = "sp_report_control_monthly",
                procedureName = "sp_report_control_monthly",
                parameters = {
                        @StoredProcedureParameter( name = "fromDate", type = Date.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter( name = "toDate", type = Date.class, mode = ParameterMode.IN)
                }
        ),
        @NamedStoredProcedureQuery(
                name = "sp_report_revenue_monthly",
                procedureName = "sp_report_revenue_monthly",
                parameters = {
                        @StoredProcedureParameter( name = "fromDate", type = Date.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter( name = "toDate", type = Date.class, mode = ParameterMode.IN)
                }
        )
})
public class CdrLog {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id",unique=true, nullable=false)
    private Long id;

    @Column(nullable=false, length=15)
    private String msisdn;

    private String vasCode;

    private Date time1;

    private Date time2;

    private Date time3;

    private int status;

    private String action;

    private int amount;

    private String descip;

    public CdrLog() {
    }

    public CdrLog(Long id, String msisdn, String vasCode, Date time1, Date time2, Date time3, int status, String action, int amount, String descip) {
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
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Date getTime1() {
        return time1;
    }

    public void setTime1(Date time1) {
        this.time1 = time1;
    }

    public Date getTime2() {
        return time2;
    }

    public void setTime2(Date time2) {
        this.time2 = time2;
    }

    public Date getTime3() {
        return time3;
    }

    public void setTime3(Date time3) {
        this.time3 = time3;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescip() {
        return descip;
    }

    public void setDescip(String descip) {
        this.descip = descip;
    }
}
