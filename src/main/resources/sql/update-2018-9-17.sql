ALTER TABLE `t_raptor_lend_order`
  CHANGE COLUMN `channel` `channel` VARCHAR(64) COMMENT '渠道' AFTER `bank_mobile`;

ALTER TABLE `t_raptor_loan_order`
  ADD COLUMN `client_id` VARCHAR(64) COMMENT '客户端Id' AFTER `remark`;
ALTER TABLE `t_raptor_loan_order`
  ADD COLUMN `client_version` VARCHAR(64) COMMENT '客户端版本号' AFTER `client_id`;

ALTER TABLE `t_raptor_pay_order_log`
  CHANGE COLUMN `client_id` `client_id` VARCHAR(64) COMMENT '客户端Id' AFTER `channel`;

/**************************用户通讯录表*************************/
DROP TABLE IF EXISTS `t_raptor_user_contacts`;
CREATE TABLE `t_raptor_user_contacts` (
`id`  int NOT NULL AUTO_INCREMENT ,
`user_code`  varchar(255) NOT NULL ,
`contacts_list`  text DEFAULT NULL COMMENT '通讯录文本' ,
`client_id`  int DEFAULT NULL COMMENT '客户端id' ,
`client_version`  varchar(255) DEFAULT NULL COMMENT '版本号' ,
`sockpuppet`  varchar(255) DEFAULT NULL COMMENT '马甲名称' ,
`deleted`  tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否可用' ,
`remark`  varchar(255) DEFAULT NULL COMMENT '备注' ,
`create_time`  bigint NOT NULL ,
`update_time`  bigint NOT NULL ,
PRIMARY KEY (`id`),
INDEX `user_code` (`user_code`) USING BTREE
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户通讯录表';

/**************************数据字典配置表（父表）*************************/
DROP TABLE IF EXISTS `t_raptor_dict_type`;
CREATE TABLE `t_raptor_dict_type` (
`id`  int NOT NULL AUTO_INCREMENT ,
`dict_type_no`  varchar(255) NOT NULL COMMENT '编号' ,
`name`  varchar(255) NOT NULL COMMENT '名称' ,
`remark`  varchar(255) DEFAULT NULL COMMENT '备注' ,
`create_time`  bigint NOT NULL ,
`update_time`  bigint NOT NULL ,
PRIMARY KEY (`id`),
INDEX `dict_type_no` (`dict_type_no`) USING BTREE
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据字典配置表（父表）';

/**************************数据字典配置表（子表）*************************/
DROP TABLE IF EXISTS `t_raptor_dict_data`;
CREATE TABLE `t_raptor_dict_data` (
`id`  int NOT NULL AUTO_INCREMENT ,
`dict_type_no`  varchar(255) NOT NULL COMMENT '父表编号' ,
`dict_data_no`  varchar(255) NOT NULL COMMENT '编号' ,
`name`  varchar(255) DEFAULT NULL COMMENT 'data值' ,
`remark`  varchar(255) DEFAULT NULL COMMENT '备注' ,
`deleted`  tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否可用' ,
`create_time`  bigint NOT NULL ,
`update_time`  bigint NOT NULL ,
PRIMARY KEY (`id`),
INDEX `dict_type_no` (`dict_type_no`) USING BTREE,
INDEX `dict_data_no` (`dict_data_no`) USING BTREE
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据字典配置表（子表）';
