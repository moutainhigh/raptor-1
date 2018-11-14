ALTER TABLE `t_raptor_rule_log`
  ADD COLUMN `version` VARCHAR(255) DEFAULT NULL COMMENT '版本号' AFTER `hit`;
ALTER TABLE `t_raptor_rule_log`
  ADD COLUMN `sub_rule` text DEFAULT NULL COMMENT '子规则' AFTER `version`;

ALTER TABLE `t_raptor_risk_score`
  ADD COLUMN `version` VARCHAR(255) DEFAULT "V0_1" COMMENT '版本号' AFTER `score`;
