ALTER TABLE `t_raptor_rule_log`
  ADD COLUMN `version` VARCHAR(255) DEFAULT NULL COMMENT '版本号' AFTER `hit`;
ALTER TABLE `t_raptor_rule_log`
  ADD COLUMN `sub_rule` text DEFAULT NULL COMMENT '子规则' AFTER `version`;