ALTER TABLE `t_raptor_lend_order`
  CHANGE COLUMN `channel` `channel` VARCHAR(64) COMMENT '渠道' AFTER `bank_mobile`;

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
