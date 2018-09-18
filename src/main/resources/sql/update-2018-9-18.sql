 /**********************添加是否收到通讯录数据字段**********************/
ALTER TABLE `t_raptor_user`
  ADD COLUMN `receive_call_history` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否收到通讯录数据' AFTER `call_history`;