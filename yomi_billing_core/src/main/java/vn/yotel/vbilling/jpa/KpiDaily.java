package vn.yotel.vbilling.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="core_kpi_daily")
@NamedQuery(name="KpiDaily.findAll", query="SELECT a FROM KpiDaily a")
public class KpiDaily implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1677721350450714110L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Temporal(TemporalType.DATE)
	@Column(name="`datetime`")
	private Date datetime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="summary_at")
	private Date summaryAt;

	@Column(name="total")
	private Long total;

	@Column(name="success")
	private Long success;

	@Column(name="kpi_type")
	private int kpiType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public Date getSummaryAt() {
		return summaryAt;
	}

	public void setSummaryAt(Date summaryAt) {
		this.summaryAt = summaryAt;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getSuccess() {
		return success;
	}

	public void setSuccess(Long success) {
		this.success = success;
	}

	public int getKpiType() {
		return kpiType;
	}

	public void setKpiType(int kpiType) {
		this.kpiType = kpiType;
	}

}
