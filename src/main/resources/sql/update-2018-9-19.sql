 /**********************银行卡字段添加**********************/
ALTER TABLE `t_raptor_bank`
  ADD COLUMN `card_start_count` int(8) NOT NULL COMMENT '银行卡扫描开始计数' AFTER `update_time`;
ALTER TABLE `t_raptor_bank`
  ADD COLUMN `card_success_count` int(8) NOT NULL COMMENT '银行卡扫描成功计数' AFTER `card_start_count`;
ALTER TABLE `t_raptor_bank`
  ADD COLUMN `card_fail_count` int(8) NOT NULL COMMENT '银行卡扫描失败计数' AFTER `card_success_count`;

/***************************LINKFACE调用记录******************************/

CREATE TABLE `t_raptor_linkface_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `call_params` varchar(1000) NOT NULL COMMENT '调用参数',
  `call_result` varchar(2000) DEFAULT NULL COMMENT  '返回结果',
  `user_code` varchar(255) NOT NULL COMMENT  '用户CODE',
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP ,
  `remark` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `status` varchar(255) NOT NULL COMMENT  '当前状态' ,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE `t_raptor_user`
  CHANGE COLUMN `description` `description` text COMMENT '状态说明' AFTER `status`;