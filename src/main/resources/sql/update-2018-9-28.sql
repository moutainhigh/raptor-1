ALTER TABLE `t_raptor_user`
ADD COLUMN `source`  varchar(255) NOT NULL DEFAULT 'WHITE' COMMENT '用户来源，默认来自白名单' AFTER `update_time`,
ADD COLUMN `mobile_contacts_time`  bigint(20) NULL DEFAULT -1 COMMENT '通讯录完成时间' AFTER `source`,
ADD COLUMN `certify_info_time`  bigint(20) NULL DEFAULT -1 COMMENT '身份认证完成时间' AFTER `mobile_contacts_time`,
ADD COLUMN `bank_card_set_time`  bigint(20) NULL DEFAULT -1 COMMENT '银行卡设置时间' AFTER `certify_info_time`,
ADD COLUMN `call_history_time`  bigint(20) NULL DEFAULT -1 COMMENT '用户通话记录授权时间' AFTER `bank_card_set_time`,
ADD COLUMN `receive_call_history_time`  bigint(20) NULL DEFAULT -1 COMMENT '接收爬虫数据通话记录时间' AFTER `call_history_time`;

ALTER TABLE `t_raptor_user`
ADD INDEX `source` (`source`) USING BTREE ;


