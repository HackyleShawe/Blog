

DROP TABLE IF EXISTS website_feedback;
CREATE TABLE website_feedback
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(64)     COMMENT '留言者称呼',
    email       VARCHAR(128)    COMMENT '留言者邮箱',
    phone       VARCHAR(64)     COMMENT '留言者电话',
    link        VARCHAR(1024)   COMMENT '留言者的链接（个人主页、博客页）',

    ip          VARCHAR(64)  DEFAULT ''  COMMENT '访问IP',
    ip_location VARCHAR(128) DEFAULT ''  COMMENT 'IP解析的地理位置（国省市区）',
    user_agent  VARCHAR(512) DEFAULT ''  COMMENT 'User-Agent',

    content     VARCHAR(6000)  DEFAULT '' COMMENT '留言内容',

    deleted     BIT      DEFAULT b'0'  COMMENT '0-False-未删除, 1-True-已删除',
    create_by   BIGINT   DEFAULT 0,
    create_time DATETIME DEFAULT now(),
    update_by   BIGINT   DEFAULT 0,
    update_time DATETIME DEFAULT  now() ON UPDATE now()
) COMMENT '反馈留言';


