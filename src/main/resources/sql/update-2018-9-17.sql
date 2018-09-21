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
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户通讯录表';

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


ALTER TABLE `t_raptor_user`
  ADD COLUMN `bank_auth_status` VARCHAR(64) COMMENT '银行卡认证状态' AFTER `audit_status`;
ALTER TABLE `t_raptor_user`
  ADD COLUMN `remark` VARCHAR(255) COMMENT '备注' AFTER `user_ip`;

ALTER TABLE `t_raptor_user`
  ADD COLUMN `description` VARCHAR(1024) COMMENT '状态说明' AFTER `status`;

/**************************还款明细表*************************/
DROP TABLE IF EXISTS `t_raptor_pay_order_detail`;
CREATE TABLE `t_raptor_pay_order_detail` (
  `id`                        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT   COMMENT '主键',
  `owner_id`                  VARCHAR(64)     NOT NULL                  COMMENT '用户code',
  `loan_order_id`             VARCHAR(64)     NOT NULL                  COMMENT '借款订单号',
  `pay_order_id`              VARCHAR(64)     NOT NULL                  COMMENT '还款订单号',
  `pay_currency`              VARCHAR(64)     NOT NULL                  COMMENT '还款币种',
  `item_type`                 VARCHAR(64)     NOT NULL                  COMMENT '期类型, 当期, 往期, 未出',
  `repay_day`                 VARCHAR(64)     NOT NULL                  COMMENT '还款日',
  `field`                     VARCHAR(64)     NOT NULL                  COMMENT '所还账单类型',
  `should_pay`                DECIMAL(10,2)   NOT NULL                  COMMENT '应付当期本金',
  `paid`                      DECIMAL(10,2)   NOT NULL                  COMMENT '本次还款所还本金',
  `create_time`               BIGINT          NOT NULL                  COMMENT '创建时间',
  `update_time`               BIGINT          NOT NULL                  COMMENT '修改时间',
  `remark`                    VARCHAR(1024)   NOT NULL DEFAULT ''       COMMENT '备注，一般给直接操作数据库备注使用，程序一般不使用',
  `deleted`                   INT(1)          NOT NULL                  COMMENT '是否逻辑删除',
  PRIMARY KEY (`id`),
  INDEX `index_loan_order_id` (`loan_order_id`),
  INDEX `index_pay_order_id` (`pay_order_id`),
  INDEX `index_owner_id` (`owner_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='还款明细表';


INSERT INTO `t_raptor_loan_product` VALUES (1, 1000.00000000, 7, 7.00000000, 750.00000000, 200.00000000, 30.00000000, 0, 111111, 11111);
INSERT INTO `t_raptor_loan_product` VALUES (2, 1000.00000000, 14, 14.00000000, 750.00000000, 200.00000000, 30.00000000, 0, 111111, 11111);

