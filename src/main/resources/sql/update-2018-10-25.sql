DROP TABLE IF EXISTS `t_raptor_thrid_black`;
CREATE TABLE `t_raptor_thrid_black` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT   COMMENT '主键',
  `user_code` VARCHAR(64) NOT NULL COMMENT '用户标识',
  `channel` VARCHAR(255) NOT NULL COMMENT '调用渠道',
  `result` VARCHAR(255) NOT NULL COMMENT '调用结果，SUCCESS or FAILED',
  `thrid_res` text DEFAULT NULL COMMENT '第三方反回结果',
  `remark` VARCHAR(255)   DEFAULT NULL COMMENT '备注',
  `create_time` BIGINT NOT NULL COMMENT '创建时间'
  INDEX `user_code` (`user_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='调用第三方渠道返回记录';