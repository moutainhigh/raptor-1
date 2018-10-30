DROP TABLE IF EXISTS `t_raptor_user_log`;
CREATE TABLE `t_raptor_user_log` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT   COMMENT '主键',
  `user_code` VARCHAR(64) NOT NULL COMMENT '用户标识',
  `pre_status` VARCHAR(64) DEFAULT NULL COMMENT '用户修改前状态',
  `post_status` VARCHAR(64) DEFAULT NULL COMMENT '用户修改后状态',
  `describe` text DEFAULT NULL COMMENT '描述信息',
  `create_time` BIGINT NOT NULL COMMENT '创建时间'
  INDEX `user_code` (`user_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户状态修改日志记录表';