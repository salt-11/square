package cn.hawy.quick.partner.core.common.exception;


/**
 * 封装guns的异常
 *
 * @author fengshuonan
 * @Date 2017/12/28 下午10:32
 */
public class RestException extends RuntimeException {

    private Integer code;

    private String message;
    
    private Object data;

    public RestException(BizExceptionEnum serviceExceptionEnum) {
        this.code = serviceExceptionEnum.getCode();
        this.message = serviceExceptionEnum.getMessage();
    }
    
    public RestException(Integer code,String message) {
    	this.code = code;
    	this.message = message;
    }
    
    public RestException(Integer code,String message,Object data) {
    	this.code = code;
    	this.message = message;
    	this.data = data;
    }
    

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
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
