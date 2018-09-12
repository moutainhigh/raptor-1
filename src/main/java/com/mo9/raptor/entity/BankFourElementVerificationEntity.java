package com.mo9.raptor.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 银行卡四要素验证表
 * @author xtgu
 *
 */
@Entity
@Table(name = "t_bank_four_element_verification")
public class BankFourElementVerificationEntity {

	/**
	 * 四要素验证结果
	 */
	public enum VerificationStatus {
		success("验证通过"),
		mobileError("手机验证失败"),
		userNameError("银行开户名验证失败"),
		cardIdError("身份证验证失败"),
		/**
		 * 链接异常等使用此状态
		 */
		padding("验证中"),
		/**
		 * 四要素存在多个异常时使用
		 */
		failed("验证失败")
		;
		private final String statusName; 

		VerificationStatus(String statusName) {
			this.statusName = statusName;
		}

		public String getStatusName() {
			return statusName;
		}

	};

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id ;
	/**
	 * 银行卡
	 */
	private String bankNo ;
	/**
	 * 身份证
	 */
	private String cardId ;
	/**
	 * 银行卡用户名
	 */
	private String userName  ;
	/**
	 * 手机号
	 */
	private String mobile ;
	/**
	 * 验证状态
	 */
	private VerificationStatus status ;
	/**
	 * 错误信息 -- 此数据是状态为failed时用来判断错误信息使用
	 */
	private String errorMsg ;
	/**
	 * 创建时间
	 */
	private Date createTime ;
	/**
	 * 修改时间
	 */
	private Date updateTime ;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	@Column(name = "bank_no")
	public String getBankNo() {
		return bankNo;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	
	@Column(name = "card_id")
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	
	@Column(name = "user_name")
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Column(name = "mobile")
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	public VerificationStatus getStatus() {
		return status;
	}
	public void setStatus(VerificationStatus status) {
		this.status = status;
	}
	
	@Column(name = "createTime")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name = "updateTime")
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "errorMsg")
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
}
