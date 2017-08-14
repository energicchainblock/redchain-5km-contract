package com.utsoft.chain.km5.pojo;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
/**
 * @author hunterfox
 * @date: 2017年8月14日
 * @version 1.0.0
 */
@Entity
@Table(name = "t_chain_user")
public class PricipalUserPo implements Serializable {

	private static final long serialVersionUID = -22749623603203735L;

	  @Id
      @GeneratedValue(strategy=GenerationType.IDENTITY)
      private Long id;

      @Column(name = "username",unique=true,length=64,nullable=false)
      private String userName;

      @Column(name = "token")
      private String token;
      
      @Column(name = "privateKey",columnDefinition="text")
      @Lob
      private String  privateKey;

  	  @Temporal(TemporalType.TIMESTAMP)
  	  @Column(name = "gmt_create")
      private Date gmtCreate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}  	  
}
