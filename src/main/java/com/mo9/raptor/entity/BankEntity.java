package com.mo9.raptor.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 银行卡四要素验证表
 * @author xtgu
 *
 */
@Entity
@Table(name = "t_bank")
public class BankEntity {

	/**
	 * 四要素验证结果
	 */
	public enum Type {
		/**
		 * 放款
		 */
		LOAN ,
		/**
		 * 还款
		 */
		PAYOFF ;

	};

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id ;

	/**
	 * 银行卡
	 */
	@Column(name = "bank_no")
	private String bankNo ;

	/**
	 * 身份证
	 */
	@Column(name = "card_id")
	private String cardId ;

	/**
	 * 银行卡用户名
	 */
	@Column(name = "user_name")
	private String userName  ;

	/**
	 * 手机号
	 */
	@Column(name = "mobile")
	private String mobile ;

	/**
	 * 类型
	 */
	@Column(name = "type")
	private Type type ;

	/**
	 * 创建时间
	 */
	@Column(name = "create_time")
	private Long createTime ;

	/**
	 * 修改时间
	 */
	@Column(name = "update_time")
	private Long updateTime ;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
}
