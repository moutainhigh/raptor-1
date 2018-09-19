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

ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `front_start_count`  int(10) NULL DEFAULT 0 COMMENT '身份证正面扫描开始计数' AFTER `ocr_id_card_address`;

ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `front_success_count`  int(10) NULL DEFAULT 0 COMMENT '身份证正面扫描成功计数' AFTER `front_start_count`;

ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `front_fail_count`  int(10) NULL DEFAULT 0 COMMENT '身份证正面扫描失败计数' AFTER `front_success_count`;

ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `back_start_count`  int(10) NULL DEFAULT 0 COMMENT '身份证背面扫描开始计数' AFTER `front_fail_count`;

ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `back_success_count`  int(10) NULL DEFAULT 0 COMMENT '身份证背面扫描成功计数' AFTER `back_start_count`;

ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `back_fail_count`  int(10) NULL DEFAULT 0 COMMENT '身份证背面扫描失败计数' AFTER `back_success_count`;

ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `liveness_start_count`  int(10) NULL DEFAULT 0 COMMENT '活体扫描开始计数' AFTER `back_fail_count`;

ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `liveness_success_count`  int(10) NULL DEFAULT 0 COMMENT '活体扫描成功计数' AFTER `liveness_start_count`;

ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `liveness_fail_count`  int(10) NULL DEFAULT 0 COMMENT '活体扫描失败计数' AFTER `liveness_success_count`;
