ALTER TABLE `t_raptor_coupon`
ADD COLUMN `pay_order_id` VARCHAR(64) COMMENT '还款订单号' AFTER `bound_order_id`;