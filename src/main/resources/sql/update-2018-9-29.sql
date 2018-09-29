ALTER TABLE `t_raptor_user`
ADD COLUMN `sub_source`  VARCHAR(255) DEFAULT NULL COMMENT '子来源' AFTER `source`;

