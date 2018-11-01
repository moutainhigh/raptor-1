CREATE TABLE  t_raptor_cash_account(
  user_id                   BIGINT UNSIGNED   NOT NULL                  COMMENT '用户ID',
  balance                   DECIMAL(10,2)     NOT NULL DEFAULT 0        COMMENT '账户余额,不能为负数',

  -- 实体通用字段
  id                        BIGINT UNSIGNED   NOT NULL AUTO_INCREMENT                     COMMENT '数据库主键',
  create_time               DATETIME(3)       NOT NULL DEFAULT NOW(3)                     COMMENT '创建时间',
  update_time               DATETIME(3)       NOT NULL DEFAULT NOW(3) ON UPDATE NOW(3)    COMMENT '更新时间',
  deleted                   TINYINT           NOT NULL DEFAULT 0                          COMMENT '是否逻辑删除',
  remark                    VARCHAR(1024)     NOT NULL DEFAULT ''                         COMMENT '备注，一般给直接操作数据库备注使用，程序一般不使用',

  PRIMARY KEY (`id`),
  UNIQUE INDEX (user_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='充值账户';


CREATE TABLE t_raptor_cash_account_log(
  user_id                   BIGINT UNSIGNED   NOT NULL                  COMMENT '用户ID',
  balance_change            DECIMAL(10,2)     NOT NULL                  COMMENT '账户余额的变更金额',
  befor_balance             DECIMAL(10,2)     NOT NULL                  COMMENT '操作后的账户余额',
  after_balance             DECIMAL(10,2)     NOT NULL                  COMMENT '操作后的账户余额',
  balance_type              VARCHAR(50)       NOT NULL                  COMMENT '金额出账入账类型',
  business_no               VARCHAR(100)      NOT NULL                  COMMENT '处理流水号',
  business_type             VARCHAR(50)       NOT NULL                   COMMENT '处理流类型',
  -- 实体通用字段
  id                        BIGINT UNSIGNED   NOT NULL AUTO_INCREMENT                     COMMENT '数据库主键',
  create_time               DATETIME(3)       NOT NULL DEFAULT NOW(3)                     COMMENT '创建时间',
  remark                    VARCHAR(1024)     NOT NULL DEFAULT ''                         COMMENT '备注，一般给直接操作数据库备注使用，程序一般不使用',
  PRIMARY KEY (`id`),
  INDEX `index_user_id` (`user_id`) USING BTREE ,
  INDEX `balance_type` (`balance_type`) USING BTREE ,
  INDEX `business_type` (`business_type`) USING BTREE ,
  INDEX `business_no` (`business_no`) USING BTREE

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='充值账户操作流水';