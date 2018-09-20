package com.mo9.raptor.entity;



import javax.persistence.*;

/**
 * Created by xtgu on 2018/7/5.
 * @author xtgu
 * mq消息表
 */
@Entity
@Table(name = "t_libra_rabbit_producer_mq")
public class RabbitProducerMqEntity {

	public enum ProducerMqEntityStatus {
		/**
		 * 成功
		 */
		SUCCESS(),
		/**
		 * 失败
		 */
		FAILED(),
		/**
		 * 初始化
		 */
		START()
		;
	}

	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id ;

	/**
	 * 发送mq的tag
	 */
	@Column(name = "tag")
	private String tag ;

	/**
	 * mq发送key
	 */
	@Column(name = "message_key")
	private String messageKey ;

	/**
	 * mq信息
	 */
	@Column(name = "message")
	private String message ;

	/**
	 * mq发送状态
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private ProducerMqEntityStatus status ;

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

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ProducerMqEntityStatus getStatus() {
		return status;
	}

	public void setStatus(ProducerMqEntityStatus status) {
		this.status = status;
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

	public RabbitProducerMqEntity() {
	}

	public RabbitProducerMqEntity(String tag, String messageKey, String message , ProducerMqEntityStatus status){
		this.tag = tag ;
		this.messageKey = messageKey ;
		this.message = message ;
		this.status = status ;
		Long time = System.currentTimeMillis() ;
		this.setCreateTime(time);
		this.setUpdateTime(time);
	}

}
