ALTER TABLE `t_raptor_loan_order`
  ADD COLUMN `paid_principal` DECIMAL(10,2) DEFAULT 0 COMMENT '已还本金' AFTER `postpone_unit_charge`;
ALTER TABLE `t_raptor_loan_order`
  ADD COLUMN `last_paid_principal_date` BIGINT DEFAULT -1 COMMENT '上一次还到本金的日期' AFTER `paid_principal`;
ALTER TABLE `t_raptor_loan_order`
  ADD COLUMN `paid_interest` DECIMAL(10,2) DEFAULT 0 COMMENT '已还利息' AFTER `last_paid_principal_date`;
ALTER TABLE `t_raptor_loan_order`
  ADD COLUMN `paid_penalty` DECIMAL(10,2) DEFAULT 0 COMMENT '已还罚息' AFTER `paid_interest`;
ALTER TABLE `t_raptor_loan_order`
  ADD COLUMN `paid_charge` DECIMAL(10,2) DEFAULT 0 COMMENT '已还服务费' AFTER `paid_penalty`;