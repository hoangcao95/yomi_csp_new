package vn.yotel.vbilling.jpa;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(
        name = "subscriber_log"
)
@NamedQuery(
        name = "SubscriberLog.findAll",
        query = "SELECT s FROM SubscriberLog s"
)
@NamedStoredProcedureQueries(
        @NamedStoredProcedureQuery(
                name = "sp_charge_history",
                procedureName = "sp_charge_history",
                parameters = {
                        @StoredProcedureParameter( name = "fromDate", type = Date.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter( name = "toDate", type = Date.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "phone", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "status", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "action", type = String.class, mode = ParameterMode.IN)
                }
        )
)
public class SubscriberLog implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private int id;
    private String action;
    @Column(
            name = "actor_code"
    )
    private String actorCode;
    private String channel;
    @Column(
            name = "charge_amount"
    )
    private float chargeAmount;
    @Column(
            name = "charge_time"
    )
    private Timestamp chargeTime;
    @Column(
            name = "created_time"
    )
    private Timestamp createdTime;
    @Column(
            name = "customer_id"
    )
    private int customerId;
    @Column(
            name = "error_code"
    )
    private String errorCode;
    @Column(
            name = "error_desc"
    )
    private String errorDesc;
    @Column(
            name = "expire_time"
    )
    private Timestamp expireTime;
    @Column(
            name = "extra_params_1"
    )
    private String extraParams1;
    @Column(
            name = "extra_params_2"
    )
    private String extraParams2;
    @Column(
            name = "from_status"
    )
    private int fromStatus;
    private String message;
    private String msisdn;
    @Column(
            name = "provider_code"
    )
    private String providerCode;
    @Column(
            name = "reference_code"
    )
    private String referenceCode;
    private byte status;
    @Column(
            name = "subs_package_code"
    )
    private String subsPackageCode;
    @Column(
            name = "subscriber_id"
    )
    private int subscriberId;
    @Column(
            name = "target_type"
    )
    private String targetType;
    @Column(
            name = "tartge_id"
    )
    private String tartgeId;
    @Column(
            name = "to_status"
    )
    private int toStatus;
    @Column(
            name = "trans_id"
    )
    private String transId;
    @Column(
            name = "registered_time"
    )
    private Timestamp registeredTime;
    @Column(
            name = "extend_time"
    )
    private Timestamp extendTime;
    @Column(
            name = "partner_id"
    )
    private int partnerId;

    public SubscriberLog() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActorCode() {
        return this.actorCode;
    }

    public void setActorCode(String actorCode) {
        this.actorCode = actorCode;
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public float getChargeAmount() {
        return this.chargeAmount;
    }

    public void setChargeAmount(float chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public Timestamp getChargeTime() {
        return this.chargeTime;
    }

    public void setChargeTime(Timestamp chargeTime) {
        this.chargeTime = chargeTime;
    }

    public Timestamp getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public int getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDesc() {
        return this.errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public Timestamp getExpireTime() {
        return this.expireTime;
    }

    public void setExpireTime(Timestamp expireTime) {
        this.expireTime = expireTime;
    }

    public String getExtraParams1() {
        return this.extraParams1;
    }

    public void setExtraParams1(String extraParams1) {
        this.extraParams1 = extraParams1;
    }

    public String getExtraParams2() {
        return this.extraParams2;
    }

    public void setExtraParams2(String extraParams2) {
        this.extraParams2 = extraParams2;
    }

    public int getFromStatus() {
        return this.fromStatus;
    }

    public void setFromStatus(int fromStatus) {
        this.fromStatus = fromStatus;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsisdn() {
        return this.msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getProviderCode() {
        return this.providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getReferenceCode() {
        return this.referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public byte getStatus() {
        return this.status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getSubsPackageCode() {
        return this.subsPackageCode;
    }

    public void setSubsPackageCode(String subsPackageCode) {
        this.subsPackageCode = subsPackageCode;
    }

    public int getSubscriberId() {
        return this.subscriberId;
    }

    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getTargetType() {
        return this.targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTartgeId() {
        return this.tartgeId;
    }

    public void setTartgeId(String tartgeId) {
        this.tartgeId = tartgeId;
    }

    public int getToStatus() {
        return this.toStatus;
    }

    public void setToStatus(int toStatus) {
        this.toStatus = toStatus;
    }

    public String getTransId() {
        return this.transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public int getPartnerId() {
        return this.partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }

    public Timestamp getRegisteredTime() {
        return this.registeredTime;
    }

    public void setRegisteredTime(Timestamp registeredTime) {
        this.registeredTime = registeredTime;
    }

    public Timestamp getExtendTime() {
        return this.extendTime;
    }

    public void setExtendTime(Timestamp extendTime) {
        this.extendTime = extendTime;
    }
}

