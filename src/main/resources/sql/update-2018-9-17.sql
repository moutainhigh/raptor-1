ALTER TABLE `t_raptor_lend_order`
  CHANGE COLUMN `channel` `channel` VARCHAR(64) COMMENT '渠道' AFTER `bank_mobile`;

ALTER TABLE `t_raptor_loan_order`
  ADD COLUMN `client_id` VARCHAR(64) COMMENT '客户端Id' AFTER `remark`;
ALTER TABLE `t_raptor_loan_order`
  ADD COLUMN `client_version` VARCHAR(64) COMMENT '客户端版本号' AFTER `client_id`;

ALTER TABLE `t_raptor_pay_order_log`
  CHANGE COLUMN `client_id` `client_id` VARCHAR(64) COMMENT '客户端Id' AFTER `channel`;

/**************************还款明细表*************************/
DROP TABLE IF EXISTS `t_raptor_pay_order_detail`;
CREATE TABLE `t_raptor_pay_order_detail` (
  `id`                        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT   COMMENT '主键',
  `owner_id`                  VARCHAR(64)     NOT NULL                  COMMENT '用户code',
  `loan_order_id`             VARCHAR(64)     NOT NULL                  COMMENT '借款订单号',
  `pay_order_id`              VARCHAR(64)     NOT NULL                  COMMENT '还款订单号',
  `pay_currency`              VARCHAR(64)     NOT NULL                  COMMENT '还款币种',
  `item_type`                 VARCHAR(64)     NOT NULL                  COMMENT '期类型, 当期, 往期, 未出',
  `repay_day`                 VARCHAR(64)     NOT NULL                  COMMENT '还款日',
  `field`                     VARCHAR(64)     NOT NULL                  COMMENT '所还账单类型',
  `should_pay`                DECIMAL(10,2)   NOT NULL                  COMMENT '应付当期本金',
  `paid`                      DECIMAL(10,2)   NOT NULL                  COMMENT '本次还款所还本金',
  `create_time`               BIGINT          NOT NULL                  COMMENT '创建时间',
  `update_time`               BIGINT          NOT NULL                  COMMENT '修改时间',
  `remark`                    VARCHAR(1024)   NOT NULL DEFAULT ''       COMMENT '备注，一般给直接操作数据库备注使用，程序一般不使用',
  `deleted`                   INT(1)          NOT NULL                  COMMENT '是否逻辑删除',
  PRIMARY KEY (`id`),
  INDEX `index_loan_order_id` (`loan_order_id`),
  INDEX `index_pay_order_id` (`pay_order_id`),
  INDEX `index_owner_id` (`owner_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='还款明细表';

