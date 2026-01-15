
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    -- group_name  VARCHAR(100) Not NULL  COMMENT '参数分组名称：例如按照模块分组：sys-系统模块',

    config_name  VARCHAR(100) DEFAULT ''  COMMENT '参数名称',
    config_desc VARCHAR(1000) DEFAULT '' COMMENT '参数说明',
    config_key   VARCHAR(100) UNIQUE DEFAULT ''  COMMENT '参数键名',
    config_value VARCHAR(500) DEFAULT ''  COMMENT '参数键值',

    status       BIT          DEFAULT 0 COMMENT '状态：0-无效 1-有效',
    deleted      BIT          DEFAULT 0 COMMENT '0-False-未删除, 1-True-已删除',
    create_by    BIGINT       DEFAULT 0,
    create_time  DATETIME     DEFAULT NOW(),
    update_by    BIGINT       DEFAULT 0,
    update_time  DATETIME     DEFAULT NOW() ON UPDATE NOW()
) COMMENT '参数配置';


DROP TABLE IF EXISTS sys_dict;
CREATE TABLE sys_dict
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    code        VARCHAR(128) UNIQUE DEFAULT '' COMMENT '字典编码',
    `name`      VARCHAR(256) DEFAULT '' COMMENT '字典名称',
    `description` VARCHAR(2048) DEFAULT '' COMMENT '描述',

    status      BIT          DEFAULT 0 COMMENT '状态：0-无效 1-有效',
    deleted     BIT          DEFAULT 0 COMMENT '0-False-未删除, 1-True-已删除',
    create_by   BIGINT       DEFAULT 0,
    create_time DATETIME     DEFAULT NOW(),
    update_by   BIGINT       DEFAULT 0,
    update_time DATETIME     DEFAULT NOW() ON UPDATE NOW()
) COMMENT ='数据字典';

DROP TABLE IF EXISTS sys_dict_detail;
CREATE TABLE sys_dict_detail
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `dict_id`   BIGINT       DEFAULT 0 COMMENT '字典id',
    `value`     VARCHAR(256) DEFAULT '' COMMENT '字典值',
    `label`     VARCHAR(256) DEFAULT '' COMMENT '字典说明',
    `sort`      INT          DEFAULT 0 COMMENT '排序',

    status      BIT          DEFAULT 0 COMMENT '状态：0-无效 1-有效',
    deleted     BIT          DEFAULT 0 COMMENT '0-False-未删除, 1-True-已删除',
    create_by   BIGINT       DEFAULT 0,
    create_time DATETIME     DEFAULT NOW(),
    update_by   BIGINT       DEFAULT 0,
    update_time DATETIME     DEFAULT NOW() ON UPDATE NOW(),
    INDEX idx_dict_id (dict_id)
) COMMENT ='数据字典详情';


