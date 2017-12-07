package com.chelsea.thriftSpring.domain;

import java.io.Serializable;

/**
 * 返回对象
 * 
 * @author baojun
 *
 */
public class RspMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer code;
	private String desc;
	private Object obj;

	public RspMessage() {
	}
	
	public RspMessage(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}
	
	public RspMessage(Integer code, String desc, Object obj) {
		this.code = code;
		this.desc = desc;
		this.obj = obj;
	}
	
	public static RspMessage getSuccessMsg(){
		RspMessage rsp = new RspMessage(Constants.SUCCESS_CODE, Constants.SUCCESS_DESC);
		return rsp;
	}
	
	public static RspMessage getSuccessMsg(Object obj){
		RspMessage rsp = new RspMessage(Constants.SUCCESS_CODE, Constants.SUCCESS_DESC, obj);
		return rsp;
	}
	
	public static RspMessage getFailMsg(String desc){
		RspMessage rsp = new RspMessage(Constants.FAIL_CODE, desc);
		return rsp;
	}
	
	public static RspMessage getFailMsg(String desc, Object obj){
		RspMessage rsp = new RspMessage(Constants.FAIL_CODE, desc, obj);
		return rsp;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

}
