ALTER TABLE `t_raptor_lend_order`
  ADD COLUMN `channel_sync_response` text COMMENT '渠道同步响应结果' AFTER `channel_response`;

ALTER TABLE `t_raptor_lend_order`
  CHANGE COLUMN `channel_response` `channel_response` text COMMENT '渠道异步响应' AFTER `channel_lend_number`;


ALTER TABLE `t_raptor_pay_order_log`
  CHANGE COLUMN `channel_response` `channel_response` text COMMENT '渠道同步响应结果' AFTER `third_channel_no`;

ALTER TABLE `t_raptor_pay_order_log`
  CHANGE COLUMN `channel_sync_response` `channel_sync_response` text COMMENT '渠道异步响应' AFTER `channel_response`;
/***************************删除用户表冗余字段，审核状态，手机通讯录，手机通话记录******************************/
ALTER TABLE t_raptor_user  DROP COLUMN audit_status;
ALTER TABLE t_raptor_user  DROP COLUMN mobile_contacts_text;
ALTER TABLE t_raptor_user  DROP COLUMN call_history_text;