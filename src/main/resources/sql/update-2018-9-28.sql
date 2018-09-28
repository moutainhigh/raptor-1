/***************************优惠表, 优惠券 *************************/
DROP TABLE IF EXISTS t_raptor_coupon;
CREATE TABLE t_raptor_coupon (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `coupon_id` varchar(64) NOT NULL COMMENT '优惠券号, 唯一编号',
  `coupon_type` varchar(64) NOT NULL COMMENT '优惠券类型',
  `state` varchar(64) NOT NULL COMMENT '优惠券状态',
  `owner_id` varchar(64) COMMENT '绑定用户唯一标识',
  `bound_order_id` varchar(64) COMMENT '绑定订单号, 绑定订单后有值',
  `condition` JSON COMMENT '使用特别限制',
  `apply_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '可优惠金额, 绑定订单后有值',
  `entry_amount` DECIMAL(10,2) DEFAULT 0  COMMENT '已入账金额, 绑定订单后有值',
  `effective_date` BIGINT DEFAULT -1 COMMENT '生效起始日期',
  `expire_date` BIGINT DEFAULT -1 COMMENT '失效日期',
  `end_time` bigint(20) DEFAULT -1 COMMENT '入账结束时间',
  `remark` text COMMENT '备注',
  `create_time` bigint(20) NOT NULL,
  `update_time` bigint(20) NOT NULL,
  `is_deleted` int NOT NULL DEFAULT '0' COMMENT '是否删除（0：否；1:是）',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `index_coupon_id` (`coupon_id`) USING BTREE,
  INDEX `index_owner_id` (`owner_id`) USING BTREE,
  INDEX `index_update_time` (`update_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='优惠表';