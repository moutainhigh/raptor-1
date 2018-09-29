ALTER TABLE `t_raptor_user`
ADD COLUMN `source`  varchar(255) NOT NULL DEFAULT 'WHITE' COMMENT '用户来源，默认来自白名单' AFTER `update_time`,
ADD COLUMN `mobile_contacts_time`  bigint(20) NULL DEFAULT -1 COMMENT '通讯录完成时间' AFTER `source`,
ADD COLUMN `certify_info_time`  bigint(20) NULL DEFAULT -1 COMMENT '身份认证完成时间' AFTER `mobile_contacts_time`,
ADD COLUMN `bank_card_set_time`  bigint(20) NULL DEFAULT -1 COMMENT '银行卡设置时间' AFTER `certify_info_time`,
ADD COLUMN `call_history_time`  bigint(20) NULL DEFAULT -1 COMMENT '用户通话记录授权时间' AFTER `bank_card_set_time`,
ADD COLUMN `receive_call_history_time`  bigint(20) NULL DEFAULT -1 COMMENT '接收爬虫数据通话记录时间' AFTER `call_history_time`;

ALTER TABLE `t_raptor_user`
ADD INDEX `source` (`source`) USING BTREE ;



/***************************优惠表, 优惠券 *************************/
DROP TABLE IF EXISTS t_raptor_coupon;
CREATE TABLE t_raptor_coupon (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `coupon_id` varchar(64) NOT NULL COMMENT '优惠券号, 唯一编号',
  `status` varchar(64) NOT NULL COMMENT '优惠券状态',
  `description` text COMMENT '状态变更描述',
  `bound_order_id` varchar(64) COMMENT '绑定订单号, 绑定订单后有值',
  `apply_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '可优惠金额, 绑定订单后有值',
  `entry_amount` DECIMAL(10,2) DEFAULT 0  COMMENT '已入账金额, 绑定订单后有值',
  `effective_date` BIGINT DEFAULT -1 COMMENT '生效起始日期',
  `expire_date` BIGINT DEFAULT -1 COMMENT '失效日期',
  `end_time` bigint(20) DEFAULT -1 COMMENT '入账结束时间',
  `creator` varchar(64) NOT NULL COMMENT '优惠券创建者',
  `reason` varchar(64) NOT NULL COMMENT '优惠券创建原因',

  `remark` text COMMENT '备注',
  `create_time` bigint(20) NOT NULL,
  `update_time` bigint(20) NOT NULL,
  `deleted` int NOT NULL DEFAULT '0' COMMENT '是否删除（0：否；1:是）',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `index_coupon_id` (`coupon_id`) USING BTREE,
  INDEX `index_update_time` (`update_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='优惠表';


ALTER TABLE `t_raptor_pay_order_detail`
  ADD COLUMN `dest_type` VARCHAR(64) not null COMMENT '目标' AFTER `owner_id`;

ALTER TABLE `t_raptor_pay_order_detail`
  ADD COLUMN `source_type` VARCHAR(64) not null COMMENT '入账源类型' AFTER `owner_id`;

ALTER TABLE `t_raptor_pay_order_detail`
  CHANGE COLUMN `loan_order_id` `destination_id` VARCHAR(64) COMMENT '目标id' AFTER `dest_type`;

ALTER TABLE `t_raptor_pay_order_detail`
  CHANGE COLUMN `pay_order_id` `source_id` VARCHAR(64) COMMENT '入账源id' AFTER `source_type`;

ALTER TABLE `t_raptor_pay_order_log`
  ADD COLUMN `former_repayment_date` bigint(20) COMMENT '延期前的还款日' AFTER `fail_reason`;

ALTER TABLE `t_raptor_pay_order_log`
  ADD COLUMN `postpone_begin_date` bigint(20) COMMENT '本次延期起始时间' AFTER `former_repayment_date`;

ALTER TABLE `t_raptor_pay_order_log` DROP COLUMN `postpone_count`;

ALTER TABLE `t_raptor_loan_order`
  ADD COLUMN `payoff_time` bigint(20) COMMENT '还清时间' AFTER `postpone_count`;