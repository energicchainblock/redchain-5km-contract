package com.utsoft.chain.km5.pojo;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
/**
 * 交易结果
 * @author hunterfox
 * @date: 2017年8月14日
 * @version 1.0.0
 */
@Entity
@Table(name = "t_chain_transaction")
public class TransactionResultPo  implements Serializable {
	
	public String getSubmitId() {
		return submitId;
	}

	public void setSubmitId(String submitId) {
		this.submitId = submitId;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	private static final long serialVersionUID = 7877754150521126742L;

	 @Id
	 @Column(name = "submitId",unique=true,length=64,nullable=false)
	 private String submitId;
	 
	 @Column(name = "txId",unique=true,length=64,nullable=false)
	 private String txId;
	 
	 @Column(name = "reqId",unique=true,length=64,nullable=false)
	 private String reqId;
	
	  private int status;
	
	 @Temporal(TemporalType.TIMESTAMP)
 	 @Column(name = "gmt_create")
     private Date gmtCreate;
 
}
