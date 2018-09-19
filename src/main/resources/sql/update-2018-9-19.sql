
/***************************LINKFACE调用记录******************************/

CREATE TABLE `t_raptor_linkface_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `call_params` varchar(1000) NOT NULL COMMENT '调用参数',
  `call_result` varchar(2000) DEFAULT NULL COMMENT  '返回结果',
  `user_code` varchar(255) NOT NULL COMMENT  '用户CODE',
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP ,
  `remark` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `status` varchar(255) NOT NULL COMMENT  '当前状态' ,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
