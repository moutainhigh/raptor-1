DROP TABLE IF EXISTS `t_raptor_card_bin_info`;
CREATE TABLE `t_raptor_card_bin_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT ,
  `card_prefix` varchar(6) NOT NULL,
  `card_type` varchar(20) DEFAULT NULL,
  `card_bank` varchar(100) DEFAULT NULL,
  `bank_code` varchar(20) DEFAULT NULL,
  `issupport` tinyint(1) DEFAULT '1',
  `create_time` bigint(20) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `CARD_PREFIX` (`CARD_PREFIX`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='银行卡bin表';

ALTER TABLE `t_raptor_lend_order`
  ADD COLUMN `fail_reason`  VARCHAR(1024) COMMENT '失败原因' AFTER `channel`;

ALTER TABLE `t_raptor_lend_order`
  ADD COLUMN `deal_code`  VARCHAR(64) COMMENT '第三方放款流水号' AFTER `bank_mobile`;