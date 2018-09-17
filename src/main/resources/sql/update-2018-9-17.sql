ALTER TABLE `t_raptor_lend_order`
  CHANGE COLUMN `channel` `channel` VARCHAR(64) COMMENT '渠道' AFTER `bank_mobile`;

ALTER TABLE `t_raptor_loan_order`
  ADD COLUMN `client_id` VARCHAR(64) COMMENT '客户端Id' AFTER `remark`;
ALTER TABLE `t_raptor_loan_order`
  ADD COLUMN `client_version` VARCHAR(64) COMMENT '客户端版本号' AFTER `client_id`;

ALTER TABLE `t_raptor_pay_order_log`
  CHANGE COLUMN `client_id` `client_id` VARCHAR(64) COMMENT '客户端Id' AFTER `channel`;