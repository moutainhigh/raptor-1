ALTER TABLE `t_raptor_loan_order`
  ADD COLUMN `paid_postpone_charge` DECIMAL(10,2) DEFAULT 0 COMMENT '已还延期服务费' AFTER `paid_charge`;
ALTER TABLE `t_raptor_loan_order`
  ADD COLUMN `paid_postpone_interest` DECIMAL(10,2) DEFAULT 0 COMMENT '已还延期利息' AFTER `paid_postpone_charge`;