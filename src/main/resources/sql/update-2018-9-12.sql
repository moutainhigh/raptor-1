# ALTER TABLE `t_bank`
# ADD COLUMN `user_code` VARCHAR(64) NOT NULL COMMENT '用户编号' AFTER `id`;
#
# ALTER TABLE `t_raptor_loan_order`
#   ADD COLUMN `postpone_unit_charge` DECIMAL(10,2) NOT NULL COMMENT '延期单位服务费' AFTER `charge_value`;
#
#
# ALTER TABLE `t_raptor_loan_order`
#   ADD COLUMN `repayment_date` BIGINT NOT NULL COMMENT '还款日' AFTER `lend_time`;

/**************************放款订单表*************************/
DROP TABLE IF EXISTS `t_raptor_lend_order`;
CREATE TABLE `t_raptor_lend_order` (
  `id`                        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT   COMMENT '主键',
  `order_id`                  VARCHAR(64)     NOT NULL                  COMMENT '订单ID - 业务流水号',
  `apply_unique_code`         VARCHAR(64)     NOT NULL                  COMMENT '请求唯一标识',
  `owner_id`                  VARCHAR(64)     NOT NULL                  COMMENT '用户code',
  `type`                      VARCHAR(64)     NOT NULL                  COMMENT '订单类型',
  `status`                    VARCHAR(64)     NOT NULL                  COMMENT '订单状态',

  `apply_number`              DECIMAL(10,2)   NOT NULL                  COMMENT '放款金额',
  `apply_time`                BIGINT          NOT NULL DEFAULT -1       COMMENT '请求放款时间',
  `user_name`                 VARCHAR(64)     NOT NULL                  COMMENT '姓名',
  `id_card`                   VARCHAR(64)     NOT NULL                  COMMENT '身份证',
  `bank_name`                 VARCHAR(64)     NOT NULL                  COMMENT '银行名称',
  `bank_card`                 VARCHAR(64)     NOT NULL                  COMMENT '银行卡号',
  `bank_mobile`               VARCHAR(64)     NOT NULL                  COMMENT '银行预留电话',
  `channel`                   VARCHAR(64)     NOT NULL                  COMMENT '渠道',
  `channel_order_id`          VARCHAR(64)                               COMMENT '渠道订单ID',
  `channel_lend_number`       DECIMAL(10,2)                             COMMENT '渠道放款数目',
  `channel_response`          VARCHAR(1024)                             COMMENT '渠道响应',
  `channel_response_time`     BIGINT                                    COMMENT '渠道响应时间',

  `description`               VARCHAR(1024)                             COMMENT '描述',
  `create_time`               BIGINT          NOT NULL                  COMMENT '创建时间',
  `update_time`               BIGINT          NOT NULL                  COMMENT '修改时间',
  `remark`                    VARCHAR(1024)   NOT NULL DEFAULT ''       COMMENT '备注，一般给直接操作数据库备注使用，程序一般不使用',
  `deleted`                   INT(1)          NOT NULL                  COMMENT '是否逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE INDEX (apply_unique_code),
  INDEX `index_owner_id` (`owner_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='放款订单表';

ALTER TABLE `t_raptor_pay_order_log`
  ADD COLUMN `repay_amount` DECIMAL(10,2) NOT NULL COMMENT '还款申请金额' AFTER `pay_order_id`;

ALTER TABLE `t_raptor_pay_order_log`
  ADD COLUMN `channel_repay_number` DECIMAL(10,2) COMMENT '渠道返回的还款数目' AFTER `repay_amount`;

ALTER TABLE `t_raptor_pay_order_log`
  ADD COLUMN `user_code` VARCHAR(64) COMMENT '用户编号' AFTER `order_id`;

ALTER TABLE `t_raptor_pay_order_log`
  ADD COLUMN `channel_sync_response` VARCHAR(1024) COMMENT '渠道同步响应结果' AFTER `channel_response`;


/**************************渠道表*************************/
DROP TABLE IF EXISTS `t_raptor_channel`;
CREATE TABLE `t_raptor_channel` (
  `id`                        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT   COMMENT '主键',
  `channel`                   VARCHAR(64)     NOT NULL                  COMMENT '渠道名',
  `channel_name`              VARCHAR(64)     NOT NULL                  COMMENT '渠道中文名称',
  `channel_type`              VARCHAR(64)     NOT NULL                  COMMENT '渠道类型, 还款, 放款',
  `use_type`                  VARCHAR(64)     NOT NULL                  COMMENT '打开方式',

  `create_time`               BIGINT          NOT NULL                  COMMENT '创建时间',
  `update_time`               BIGINT          NOT NULL                  COMMENT '修改时间',
  `remark`                    VARCHAR(1024)   NOT NULL DEFAULT ''       COMMENT '备注，一般给直接操作数据库备注使用，程序一般不使用',
  `deleted`                   INT(1)          NOT NULL                  COMMENT '是否逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE INDEX (`channel`, `channel_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='渠道表';

INSERT INTO `t_raptor_channel` (`id`, `channel`, `channel_name`, `channel_type`, `use_type`, `create_time`, `update_time`, `remark`, `deleted`)
VALUES (                        '1','yilianh5pay', '易联H5支付', 'REPAY',             'link', 1537027200000, 1537027200000, '', 0);
INSERT INTO `t_raptor_channel` (`id`, `channel`, `channel_name`, `channel_type`, `use_type`, `create_time`, `update_time`, `remark`, `deleted`)
VALUES (                        '2','yilianh5pay', '易联H5支付', 'LOAN',             'link', 1537027200000, 1537027200000, '', 0);
