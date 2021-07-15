package cn.hawy.quick.agent.core.common;

public class RestResponse {
	
	private int code;
	
	private String message;
	
	private Object data;

	public int getCode() {
		return code;
	}
	
	
	public RestResponse(Object data) {
		this.code = 200;
		this.message = "成功";
		this.data = data;
	}
	
	public RestResponse(int code,String message) {
		this.code = code;
		this.message = message;
	}
	
	
	public static RestResponse success(Object data) {
		return new RestResponse(data);
	}
	
	public static RestResponse error(int code,String message) {
		return new RestResponse(code,message);
	}

	public void setCode(int code) {
		this.code = code;
	}

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
	
	

}
