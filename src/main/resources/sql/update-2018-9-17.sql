ALTER TABLE `t_raptor_lend_order`
  CHANGE COLUMN `channel` `channel` VARCHAR(64) COMMENT '渠道' AFTER `bank_mobile`;

ALTER TABLE `t_raptor_user`
  ADD COLUMN `bank_auth_status` VARCHAR(64) COMMENT '银行卡认证状态' AFTER `audit_status`;
ALTER TABLE `t_raptor_user`
  ADD COLUMN `remark` VARCHAR(255) COMMENT '备注' AFTER `user_ip`;

ALTER TABLE `t_raptor_user`
  ADD COLUMN `description` VARCHAR(255) COMMENT '状态说明' AFTER `status`;