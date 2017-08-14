package com.utsoft.chain.km5.pojo;

import java.io.Serializable;

/**
 * @author hunterfox
 * @date: 2017年8月14日
 * @version 1.0.0
 */
public class ConventionChaincode implements Serializable {

	private static final long serialVersionUID = 8001039004128078362L;
	
	private String reqId;
	private String to;
    private String cmd;
     
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	private String from;
	private String submitJson;
	
	public String getReqId() {
		return reqId;
	}
	public void setReqId(String reqId) {
		this.reqId = reqId;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getSubmitJson() {
		return submitJson;
	}
	public void setSubmitJson(String submitJson) {
		this.submitJson = submitJson;
	}
}
