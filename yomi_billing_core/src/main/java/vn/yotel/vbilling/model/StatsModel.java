package vn.yotel.vbilling.model;

import java.io.Serializable;

public class StatsModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5027295859892231113L;
	private String key;
	private String value;
	private String desc;
	
	public StatsModel() {
	}

	public StatsModel(String _key, String _value) {
		this.key = _key;
		this.value = _value;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}
