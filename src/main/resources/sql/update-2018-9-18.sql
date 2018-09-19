 /**********************添加是否收到通讯录数据字段**********************/
ALTER TABLE `t_raptor_user`
  ADD COLUMN `receive_call_history` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否收到通话记录数据' AFTER `call_history`;
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