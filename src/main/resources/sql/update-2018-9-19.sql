
ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `front_start_count`  int(10) NULL DEFAULT 0 COMMENT '身份证正面扫描开始计数' AFTER `ocr_id_card_address`;

ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `front_success_count`  int(10) NULL DEFAULT 0 COMMENT '身份证正面扫描成功计数' AFTER `front_start_count`;

ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `front_fail_count`  int(10) NULL DEFAULT 0 COMMENT '身份证正面扫描失败计数' AFTER `front_success_count`;

ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `back_start_count`  int(10) NULL DEFAULT 0 COMMENT '身份证背面扫描开始计数' AFTER `front_fail_count`;

ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `back_success_count`  int(10) NULL DEFAULT 0 COMMENT '身份证背面扫描成功计数' AFTER `back_start_count`;

ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `back_fail_count`  int(10) NULL DEFAULT 0 COMMENT '身份证背面扫描失败计数' AFTER `back_success_count`;

ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `liveness_start_count`  int(10) NULL DEFAULT 0 COMMENT '活体扫描开始计数' AFTER `back_fail_count`;

ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `liveness_success_count`  int(10) NULL DEFAULT 0 COMMENT '活体扫描成功计数' AFTER `liveness_start_count`;

ALTER TABLE `t_raptor_certify_info`
  ADD COLUMN `liveness_fail_count`  int(10) NULL DEFAULT 0 COMMENT '活体扫描失败计数' AFTER `liveness_success_count`;
