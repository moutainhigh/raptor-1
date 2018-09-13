/**************************还款订单*************************/
DROP TABLE IF EXISTS `t_raptor_pay_order`;
CREATE TABLE `t_raptor_pay_order` (
  `id`                        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT   COMMENT '主键',
  `order_id`                  VARCHAR(64)     NOT NULL                  COMMENT '订单ID - 业务流水号',
  `owner_id`                  VARCHAR(64)     NOT NULL                  COMMENT '用户code - 用户中心uesrCode',
  `type`                      VARCHAR(64)     NOT NULL                  COMMENT '订单类型',
  `channel`                   VARCHAR(64)     NOT NULL                  COMMENT '还款渠道',
  `status`                    VARCHAR(64)     NOT NULL                  COMMENT '订单状态',
  `pay_currency`              VARCHAR(64)     NOT NULL                  COMMENT '币种 - 用户还款币种',
  `loan_order_id`             VARCHAR(64)     NOT NULL                  COMMENT '还的借款订单',
  `apply_number`              DECIMAL(18,2)   NOT NULL                  COMMENT '用户申请还款金额',
  `pay_number`                DECIMAL(18,2)   NOT NULL DEFAULT 0        COMMENT '实际支付数量',
  `entry_number`              DECIMAL(18,2)   NOT NULL DEFAULT 0        COMMENT '成功入账金额',
  `pay_time`                  BIGINT          NOT NULL DEFAULT -1       COMMENT '成功扣款时间',
  `entry_over_time`           BIGINT          NOT NULL DEFAULT -1       COMMENT '入账时间',
  `postpone_days`             INT             NOT NULL DEFAULT 0        COMMENT '还除本金之外的所有金额时, 可推迟还款时间, 一般为一个账期',
  `description`               VARCHAR(1024)   NOT NULL DEFAULT ''       COMMENT '订单描述，记录订单状态变化过程',
  `create_time`               BIGINT          NOT NULL                  COMMENT '创建时间',
  `update_time`               BIGINT          NOT NULL                  COMMENT '修改时间',
  `remark`                    VARCHAR(1024)   NOT NULL DEFAULT ''       COMMENT '备注，一般给直接操作数据库备注使用，程序一般不使用',
  `deleted`                   INT(1)          NOT NULL                  COMMENT '是否逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE INDEX (order_id),
  INDEX `index_owner_id` (`owner_id`) USING BTREE,
  INDEX `index_loan_order_id` (`loan_order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='还款订单表';

/**************************用户基本信息表*************************/
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_raptor_user
-- ----------------------------
DROP TABLE IF EXISTS `t_raptor_user`;
CREATE TABLE `t_raptor_user` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_code` varchar(50) NOT NULL COMMENT '用户唯一标识',
  `mobile` varchar(15) NOT NULL COMMENT '手机号',
  `real_name` varchar(15) DEFAULT NULL COMMENT '真实姓名',
  `id_card` varchar(255) DEFAULT NULL COMMENT '证件号',
  `credit_status` varchar(50) DEFAULT NULL COMMENT '账户信用状态',
  `audit_status` varchar(50) DEFAULT NULL COMMENT '审核状态 -1 重新认证 0未认证 1 认证中 2已认证',
  `mobile_contacts` tinyint(4) NOT NULL DEFAULT '0' COMMENT '手机通讯录是否上传',
  `certify_info` tinyint(4) NOT NULL DEFAULT '0' COMMENT '认证信息是否上传并通过',
  `bank_card_set` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否设置银行卡 信息',
  `call_history` tinyint(4) NOT NULL DEFAULT '0' COMMENT '通话记录是否授权',
  `status` varchar(50) DEFAULT NULL COMMENT '当前认证状态',
  `living_body_certify` tinyint(4) NOT NULL DEFAULT '0' COMMENT '活体认证是否通过',
  `mobile_contacts_text` text COMMENT '用户联系人信息文本',
  `call_history_text` text COMMENT '用户通话记录文本',
  `user_ip` varchar(50) DEFAULT NULL COMMENT '用户ip',
  `login_enable` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否可登录',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `last_login_time` bigint(20) DEFAULT NULL COMMENT '最后一次登录时间',
  `auth_time` bigint(20) DEFAULT NULL COMMENT '四要素提交认证时间',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `mobile` (`mobile`),
  UNIQUE KEY `user_code` (`user_code`) USING BTREE,
  KEY `deleted` (`deleted`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8; COMMENT='用户基本信息表';
/**************************消息发送记录表*************************/
DROP TABLE IF EXISTS `t_raptor_message_notify`;
CREATE TABLE `t_raptor_message_notify` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型 - 短信,邮箱,app',
  `status` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '成功失败 -- http不异常就是成功',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送内容',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL COMMENT '修改时间',
  `business_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `language` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `message_id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息id',
  `receivers` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接收人',
  `send_time` bigint(20) DEFAULT NULL,
  `template_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息模版号',
  PRIMARY KEY (`id`),
  KEY `message_id` (`message_id`) USING BTREE,
  KEY `status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13874 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息发送记录表';

/**************************用户认证信息表*************************/
DROP TABLE IF EXISTS `t_raptor_certify_info`;
CREATE TABLE `t_raptor_certify_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '真实姓名',
  `user_code` varchar(50) NOT NULL,
  `real_name` varchar(20) DEFAULT NULL,
  `id_card` varchar(50) DEFAULT NULL COMMENT '身份证',
  `issuing_organ` varchar(50) DEFAULT NULL COMMENT '识别地址',
  `validity_start_period` varchar(50) DEFAULT NULL COMMENT '有效期开始时间(yyyy-mm-dd)',
  `validity_end_period` varchar(50) DEFAULT NULL COMMENT '有效期结束时间(yyyy-mm-dd)',
  `type` varchar(20) DEFAULT NULL COMMENT '"0: 其他，1: 长期"',
  `account_front_img` varchar(255) DEFAULT NULL COMMENT '身份证正面照片地址',
  `account_back_img` varchar(255) DEFAULT NULL COMMENT '身份证反面照片地址',
  `account_ocr` varchar(255) DEFAULT NULL COMMENT '账户ocr照片',
  `ocr_real_name` varchar(50) DEFAULT NULL COMMENT '识别真实姓名',
  `ocr_id_card` varchar(255) DEFAULT NULL COMMENT '识别身份证',
  `ocr_issue_at` varchar(50) DEFAULT NULL COMMENT '识别签发地',
  `ocr_duration_start_time` varchar(20) DEFAULT NULL COMMENT '识别身份有效期起始日期(yyyy-mm-dd)',
  `ocr_duration_end_time` varchar(20) DEFAULT NULL COMMENT '识别身份有效期结束日期(yyyy-mm-dd)',
  `ocr_gender` varchar(10) DEFAULT NULL COMMENT '识别性别',
  `ocr_nationality` varchar(10) DEFAULT NULL COMMENT '识别民族',
  `ocr_birthday` varchar(20) DEFAULT NULL COMMENT '识别生日(1992-05-01)',
  `ocr_id_card_address` varchar(255) DEFAULT NULL,
  `create_time` bigint(20) NOT NULL,
  `update_time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户认证信息表';;