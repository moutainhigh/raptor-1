-- 信审用户表
DROP TABLE IF EXISTS `t_audit_user`;
CREATE TABLE `t_audit_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `login_name` varchar(100) DEFAULT NULL COMMENT '用户名',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `name` varchar(100) DEFAULT NULL COMMENT '姓名',
  `email` varchar(100) DEFAULT NULL,
  `mobile` varchar(20) DEFAULT NULL,
  `level` varchar(20) DEFAULT 'NORMAL' COMMENT '帐号级别，MANAGE 主管，NORMAL 操作员',
  `remark` varchar(1024) DEFAULT NULL,
  `deleted` int(1) NOT NULL DEFAULT '0' COMMENT '是否逻辑删除',
  `create_time` bigint(20) DEFAULT '0',
  `update_time` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `login_name` (`login_name`) USING BTREE,
  KEY `password` (`password`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
-- 信审操作记录表
DROP TABLE IF EXISTS `t_audit_operation_record`;
CREATE TABLE `t_audit_operation_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_code` varchar(100) DEFAULT NULL COMMENT '唯一用户标识',
  `operate_id` varchar(20) DEFAULT NULL COMMENT '操作人id',
  `distribute_id` varchar(20) DEFAULT NULL COMMENT '主管id',
  `status` varchar(20) DEFAULT NULL COMMENT '用户状态',
  `audit_time` bigint(20) DEFAULT NULL COMMENT '人工审核时间',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
  `deleted` int(1) NOT NULL DEFAULT '0' COMMENT '是否逻辑删除',
  `create_time` bigint(20) NOT NULL DEFAULT '0',
  `update_time` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_code` (`user_code`) USING BTREE,
  KEY `operate_id` (`operate_id`),
  KEY `status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=492 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;