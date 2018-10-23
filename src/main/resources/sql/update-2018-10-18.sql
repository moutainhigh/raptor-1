ALTER TABLE `t_risk_contract_info`
ADD COLUMN `is_matching` tinyint(4) NOT NULL DEFAULT 0 COMMENT '通话记录手机号是否匹配' AFTER `contact_type`;