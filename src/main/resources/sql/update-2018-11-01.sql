CREATE TABLE `t_raptor_cash_account` (
`id`  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '数据库主键' ,
`user_code`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户ID' ,
`balance`  decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '账户余额,不能为负数' ,
`create_time`  datetime(3) NOT NULL DEFAULT 'CURRENT_TIMESTAMP(3)' COMMENT '创建时间' ,
`update_time`  datetime(3) NOT NULL DEFAULT 'CURRENT_TIMESTAMP(3)' COMMENT '更新时间' ,
`deleted`  tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否逻辑删除' ,
`remark`  varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '备注，一般给直接操作数据库备注使用，程序一般不使用' ,
PRIMARY KEY (`id`),
UNIQUE INDEX `user_id` (`user_code`) USING BTREE
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='充值账户'
AUTO_INCREMENT=4
ROW_FORMAT=DYNAMIC
;

CREATE TABLE `t_raptor_cash_account_log` (
`id`  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '数据库主键' ,
`user_code`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户ID' ,
`balance_change`  decimal(10,2) NOT NULL COMMENT '账户余额的变更金额' ,
`before_balance`  decimal(10,2) NOT NULL COMMENT '操作后的账户余额' ,
`after_balance`  decimal(10,2) NOT NULL COMMENT '操作后的账户余额' ,
`balance_type`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '金额出账入账类型' ,
`business_no`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '处理流水号' ,
`business_type`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '处理流类型' ,
`create_time`  datetime(3) NOT NULL DEFAULT 'CURRENT_TIMESTAMP(3)' COMMENT '创建时间' ,
`remark`  varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '备注，一般给直接操作数据库备注使用，程序一般不使用' ,
PRIMARY KEY (`id`),
INDEX `index_user_id` (`user_code`) USING BTREE ,
INDEX `balance_type` (`balance_type`) USING BTREE ,
INDEX `business_type` (`business_type`) USING BTREE ,
INDEX `business_no` (`business_no`) USING BTREE
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='充值账户操作流水'
AUTO_INCREMENT=6
ROW_FORMAT=DYNAMIC
;

