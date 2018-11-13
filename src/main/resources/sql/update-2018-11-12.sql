ALTER TABLE `t_raptor_pay_order`
  ADD COLUMN `coupon_id` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '优惠券码' AFTER `postpone_days`;

ALTER TABLE `t_raptor_coupon`
  ADD COLUMN `user_code` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户标识' AFTER `coupon_id`;