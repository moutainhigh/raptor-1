/**************************还款订单*************************/
DROP TABLE IF EXISTS `t_raptor_pay_order`;
CREATE TABLE `t_raptor_pay_order` (
  `id`                        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT   COMMENT '主键',
  `order_id`                  VARCHAR(64)     NOT NULL                  COMMENT '订单ID - 业务流水号',
  `owner_id`                  VARCHAR(64)     NOT NULL                  COMMENT '用户code',
  `type`                      VARCHAR(64)     NOT NULL                  COMMENT '订单类型',
  `channel`                   VARCHAR(64)     NOT NULL                  COMMENT '还款渠道',
  `status`                    VARCHAR(64)     NOT NULL                  COMMENT '订单状态',
  `pay_currency`              VARCHAR(64)     NOT NULL                  COMMENT '币种 - 用户还款币种',
  `loan_order_id`             VARCHAR(64)     NOT NULL                  COMMENT '还的借款订单',
  `apply_number`              DECIMAL(10,2)   NOT NULL                  COMMENT '用户申请还款金额',
  `pay_number`                DECIMAL(10,2)   NOT NULL DEFAULT 0        COMMENT '实际支付数量',
  `entry_number`              DECIMAL(10,2)   NOT NULL DEFAULT 0        COMMENT '成功入账金额',
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