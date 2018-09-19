 /**********************银行卡字段添加**********************/
ALTER TABLE `t_raptor_bank`
  ADD COLUMN `card_start_count` int(8) NOT NULL COMMENT '银行卡扫描开始计数' AFTER `update_time`;
ALTER TABLE `t_raptor_bank`
  ADD COLUMN `card_success_count` int(8) NOT NULL COMMENT '银行卡扫描成功计数' AFTER `card_start_count`;
ALTER TABLE `t_raptor_bank`
  ADD COLUMN `card_fail_count` int(8) NOT NULL COMMENT '银行卡扫描失败计数' AFTER `card_success_count`;