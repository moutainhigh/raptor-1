ALTER TABLE `t_raptor_loan_order`
  ADD COLUMN `postpone_count` int DEFAULT 0 COMMENT '延期次数' AFTER `repayment_date`;
