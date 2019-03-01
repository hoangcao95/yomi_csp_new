package vn.yotel.vbilling.model;

import org.json.JSONObject;

public class HandlingResult {

	private String status = "0";
    private String message = "";
    private Object data = null;
    private Object option = null;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public boolean isSuccess() {
		return "0".equals(status);
	}
	
	/**
	 * 200 - call & process successfully
	 * 9999 - Exception in process
	 * 9998 - Status code is not an interger number
	 * @return
	 */
	public int parseToHttpCode() {
		int result = 200;
		if (!this.isSuccess()) {
			try {
				result = Integer.parseInt(status);				
			} catch (Exception e) {
				return 9998;
			}
		}
		return result;		
	}
	
	public String parseObjData() {
		if (this.data != null && this.data instanceof JSONObject) {
			return ((JSONObject) this.data).toString();
		} else {
			return "";
		}
	}
	
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HandlingResult [status=");
        builder.append(status);
        builder.append(", message=");
        builder.append(message);
        builder.append(", data=");
        builder.append(data);
        builder.append("]");
        return builder.toString();
    }
    
    public Object getOption() {
		return option;
	}

	public void setOption(Object option) {
		this.option = option;
	}

	public static class HandlingResultBuilder {

        public static HandlingResult SUCCESS() {
            return SUCCESS("Success");
        }

        public static HandlingResult SUCCESS(String message) {
        	HandlingResult msg = new HandlingResult();
            msg.setStatus("0");
            msg.setMessage(message);
            msg.setData(null);
            return msg;
        }

        public static HandlingResult FAIL(String code, String message) {
        	HandlingResult msg = new HandlingResult();
            msg.setStatus(code);
            msg.setMessage(message);
            msg.setData(null);
            return msg;
        }
    }

	public String parseResp() {
		String result = "";
		if (this.option != null) {
			if (option instanceof JSONObject) {
				result = ((JSONObject) option).toString();
			} else {
				result = option.toString();
			}
		} else {
			result = "";
		}
		return result;
	}
}
