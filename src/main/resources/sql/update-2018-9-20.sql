ALTER TABLE `t_raptor_lend_order`
  ADD COLUMN `channel_sync_response` text COMMENT '渠道同步响应结果' AFTER `channel_response`;

ALTER TABLE `t_raptor_lend_order`
  CHANGE COLUMN `channel_response` `channel_response` text COMMENT '渠道异步响应' AFTER `channel_lend_number`;


ALTER TABLE `t_raptor_pay_order_log`
  CHANGE COLUMN `channel_response` `channel_response` text COMMENT '渠道同步响应结果' AFTER `third_channel_no`;

ALTER TABLE `t_raptor_pay_order_log`
  CHANGE COLUMN `channel_sync_response` `channel_sync_response` text COMMENT '渠道异步响应' AFTER `channel_response`;



/*
 **************************mq信息表*************************
 */
DROP TABLE IF EXISTS `t_raptor_rabbit_producer_mq`;
CREATE TABLE `t_raptor_rabbit_producer_mq` (
  `id`  bigint(20) NOT NULL AUTO_INCREMENT ,
  `tag`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发送mq的tag' ,
  `message_key`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'mq发送key' ,
  `message`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'mq信息' ,
  `status`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'mq发送状态' ,
  `create_time`  bigint(20) NULL DEFAULT NULL ,
  `update_time`  bigint(20) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`),
  INDEX `tag` (`tag`) USING BTREE ,
  INDEX `message_key` (`message_key`) USING BTREE ,
  INDEX `status` (`status`) USING BTREE ,
  INDEX `create_time` (`create_time`) USING BTREE
)
  ENGINE=InnoDB
  DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
  AUTO_INCREMENT=1
  ROW_FORMAT=COMPACT
;