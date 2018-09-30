ALTER TABLE `t_raptor_user`
ADD COLUMN `sub_source`  VARCHAR(255) DEFAULT NULL COMMENT '子来源' AFTER `source`;

/**************************渠道商推广*************************/
DROP TABLE IF EXISTS `t_raptor_spread_channel`;
CREATE TABLE `t_raptor_spread_channel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `channel_code` varchar(64) NOT NULL COMMENT '渠道编号',
  `login_name` varchar(100) DEFAULT NULL COMMENT '用户名',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `email` varchar(100) DEFAULT NULL,
  `source` varchar(100) DEFAULT NULL COMMENT '渠道',
  `mobile` varchar(20) DEFAULT NULL,
  `sub_source` varchar(100) DEFAULT NULL COMMENT '子渠道',
  `create_time` bigint(20) DEFAULT '0',
  `update_time` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `channel_code` (`channel_code`),
  KEY `login_name` (`login_name`) USING BTREE,
  KEY `password` (`password`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
INSERT INTO `t_raptor_spread_channel` (`id`, `channel_code`, `login_name`, `password`, `email`, `source`, `mobile`, `sub_source`, `create_time`, `update_time`) VALUES ('1', 'testCode', 'admin', 'mo9@2018', NULL, 'NEW', NULL, NULL, '1514736000000', '1514736000000');


