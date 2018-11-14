ALTER TABLE `t_raptor_pay_order`
  ADD COLUMN `coupon_id` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '优惠券码' AFTER `postpone_days`;

ALTER TABLE `t_raptor_pay_order`
  ADD COLUMN `balance_number` decimal(10,2)  DEFAULT NULL COMMENT '使用现金钱包余额' AFTER `entry_number`;

ALTER TABLE `t_raptor_coupon`
  ADD COLUMN `user_code` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户标识' AFTER `coupon_id`;

ALTER TABLE `t_raptor_coupon`
  ADD COLUMN `use_type` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '优惠券使用类型' AFTER `user_code`;

ALTER TABLE `t_raptor_coupon`
  ADD COLUMN `limit_amount` decimal(10,2)  DEFAULT 0 COMMENT '优惠券使用限定金额 大于' AFTER `pay_order_id`;


