//package vn.yotel.vbilling.jpa;
//
//import java.io.Serializable;
//import java.sql.Timestamp;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.NamedQuery;
//import javax.persistence.Table;
//
//@Entity
//@Table(
//        name = "app_param"
//)
//@NamedQuery(
//        name = "AppParam.findAll",
//        query = "SELECT a FROM AppParam a"
//)
//public class AppParam implements Serializable {
//    private static final long serialVersionUID = 1L;
//    @Id
//    private int id;
//    private String code;
//    @Column(
//            name = "created_time"
//    )
//    private Timestamp createdTime;
//    private String desc;
//    private byte status;
//    private String type;
//    private String value;
//
//    public AppParam() {
//    }
//
//    public int getId() {
//        return this.id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getCode() {
//        return this.code;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public Timestamp getCreatedTime() {
//        return this.createdTime;
//    }
//
//    public void setCreatedTime(Timestamp createdTime) {
//        this.createdTime = createdTime;
//    }
//
//    public String getDesc() {
//        return this.desc;
//    }
//
//    public void setDesc(String desc) {
//        this.desc = desc;
//    }
//
//    public byte getStatus() {
//        return this.status;
//    }
//
//    public void setStatus(byte status) {
//        this.status = status;
//    }
//
//    public String getType() {
//        return this.type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public String getValue() {
//        return this.value;
//    }
//
//    public void setValue(String value) {
//        this.value = value;
//    }
//}
//
