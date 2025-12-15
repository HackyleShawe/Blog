

DROP TABLE IF EXISTS article;
CREATE TABLE article
(
    id          BIGINT       NOT NULL PRIMARY KEY,
    title       VARCHAR(256) NOT NULL COMMENT '标题',
    summary     VARCHAR(5000) DEFAULT '' COMMENT '总结概要',
    keywords    VARCHAR(1000) DEFAULT '' COMMENT '文章关键字，直接用于meta标签，SEO',
    path        VARCHAR(128) NOT NULL COMMENT '文章的链接路径，path组成为：/category-code/id混淆后的唯一串',
    content     TEXT          DEFAULT NULL COMMENT '文章内容',
    cover_img   VARCHAR(512)  DEFAULT '' COMMENT '封面图URL',

    released    BIT           DEFAULT 0 COMMENT '是否发布：0-草稿 1-发布',
    commented   BIT           DEFAULT 0 COMMENT '是否可以评论：0-不可评论 1-可以评论',
    deleted     BIT           DEFAULT 0 COMMENT '0-False-未删除, 1-True-已删除',

    create_by   BIGINT        DEFAULT 0,
    create_time DATETIME      DEFAULT NOW(),
    update_by   BIGINT        DEFAULT 0,
    update_time DATETIME      DEFAULT NULL ON UPDATE NOW()
) COMMENT '文章主体';

DROP TABLE IF EXISTS article_category;
CREATE TABLE article_category
(
    id          BIGINT      NOT NULL PRIMARY KEY,

    name        VARCHAR(32) NOT NULL COMMENT '分类名称',
    code        VARCHAR(16) NOT NULL UNIQUE COMMENT '分类编码',
    description VARCHAR(1024) DEFAULT '' COMMENT '分类描述',
    color       VARCHAR(16)   DEFAULT '' COMMENT '分类颜色',
    icon_url    VARCHAR(512)  DEFAULT '' COMMENT '分类的图标URL',
    pid         BIGINT        DEFAULT 0 COMMENT '上一级分类',

    status      BIT           DEFAULT 0 COMMENT '状态：0-无效 1-有效',
    deleted     BIT           DEFAULT 0 COMMENT '0-False-未删除, 1-True-已删除',

    create_by   BIGINT        DEFAULT 0,
    create_time DATETIME      DEFAULT NOW(),
    update_by   BIGINT        DEFAULT 0,
    update_time DATETIME      DEFAULT NULL ON UPDATE NOW(),
    INDEX       idx_pid (pid)
) COMMENT '文章分类';

DROP TABLE IF EXISTS article_category_relation;
CREATE TABLE article_category_relation
(
    article_id  BIGINT NOT NULL COMMENT '文章ID',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    PRIMARY KEY (article_id, category_id)
) COMMENT '文章与分类的关联，一篇文章可以有多个分类';

DROP TABLE IF EXISTS article_author;
CREATE TABLE article_author
(
    id          BIGINT      NOT NULL PRIMARY KEY,

    nick_name   VARCHAR(64) NOT NULL COMMENT '笔名',
    real_name   VARCHAR(32)   DEFAULT '' COMMENT '真实姓名',
    description VARCHAR(2048) DEFAULT '' COMMENT '描述',

    email       VARCHAR(128)  DEFAULT '' COMMENT '用户邮箱',
    phone       VARCHAR(128)  DEFAULT '' COMMENT '用户手机号',
    address     VARCHAR(128)  DEFAULT '' COMMENT '地址',
    birthday    DATETIME      DEFAULT NULL COMMENT '生日',
    gender      INT           DEFAULT 1 COMMENT '1-男; 0-女；2-未知',

    status      BIT           DEFAULT 0 COMMENT '状态：0-无效 1-有效',
    deleted     BIT           DEFAULT 0 COMMENT '0-False-未删除, 1-True-已删除',
    create_by   BIGINT        DEFAULT 0,
    create_time DATETIME      DEFAULT NOW(),
    update_by   BIGINT        DEFAULT 0,
    update_time DATETIME      DEFAULT NULL ON UPDATE NOW()
) COMMENT '文章作者';

DROP TABLE IF EXISTS article_author_relation;
CREATE TABLE article_author_relation
(
    article_id BIGINT NOT NULL COMMENT '文章ID',
    author_id  BIGINT NOT NULL COMMENT '作者ID',
    PRIMARY KEY (article_id, author_id)
) ENGINE = InnoDB COMMENT '文章与作者的关联，一篇文章可以有多个作者';

DROP TABLE IF EXISTS article_comment;
CREATE TABLE article_comment
(
    id                BIGINT        NOT NULL PRIMARY KEY,
    `article_id`      BIGINT        NOT NULL COMMENT '被评论文章ID',

    -- 评论者（注意，整合到业务上时，一般只需要记录评论者ID）
    commentator_id    BIGINT       DEFAULT 0 COMMENT '评论者ID',
    commentator_name  VARCHAR(50)  DEFAULT '' COMMENT '评论者名称',
    commentator_email VARCHAR(100) DEFAULT '' COMMENT '评论人的邮箱',
    commentator_link  VARCHAR(50)  DEFAULT '' COMMENT '评论者的拓展链接，如个人网站地址',
    commentator_ip    VARCHAR(20)  DEFAULT '' COMMENT '评论者的ip地址',
    content           VARCHAR(2048) NOT NULL COMMENT '评论内容',

    `root_id`         BIGINT       DEFAULT 0 COMMENT '根级父评论，顶级父评论ID，如果为0表示当前记录没有父评论，也即为顶级评论',
    `pid`             BIGINT       DEFAULT 0 COMMENT '父评论ID，此条评论回复的是谁。如果为0表示当前记录没有父评论',
    `pname` VARCHAR(20) DEFAULT '' COMMENT '父评论名称，此条评论回复的是谁。如果为空则表示没有父评论或者没有赋值',

    `released`        BIT          DEFAULT 0 COMMENT '是否发布评论：0-未审核、不发布，1-审核通过、发布',
    deleted           BIT          DEFAULT 0 COMMENT '0-False-未删除, 1-True-已删除',
    create_by         BIGINT       DEFAULT 0,
    create_time       DATETIME     DEFAULT NOW(),
    update_by         BIGINT       DEFAULT 0,
    update_time       DATETIME     DEFAULT NULL ON UPDATE NOW(),
    INDEX             idx_article_id (article_id)
) COMMENT '文章评论';

DROP TABLE IF EXISTS article_file;
CREATE TABLE article_file
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id  BIGINT   DEFAULT 0 COMMENT '文章id',
    file_link   VARCHAR(512) NOT NULL COMMENT '文件地址',
    file_type   VARCHAR(10)  NOT NULL COMMENT '文件类型：text文本、image图片、video视频、audio音频、zip压缩包、bin二进制数据',

    deleted     BIT      DEFAULT 0 COMMENT '0-False-未删除, 1-True-已删除',
    create_by   BIGINT   DEFAULT 0,
    create_time DATETIME DEFAULT NOW(),
    update_by   BIGINT   DEFAULT 0,
    update_time DATETIME DEFAULT NULL ON UPDATE NOW()
) COMMENT '文章静态资源文件（图片、视频、音乐、文档等）';

DROP TABLE IF EXISTS article_visit_log;
CREATE TABLE article_visit_log
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    article_id    BIGINT  NOT NULL COMMENT '文章ID',
    user_id       BIGINT   DEFAULT 0 COMMENT '用户ID（未登录则为空或0）',

    ip            VARCHAR(64) COMMENT '访问IP',
    ip_location   VARCHAR(128) COMMENT 'IP解析的地理位置（省市区）',
    user_agent    VARCHAR(512) COMMENT 'User-Agent',
    referer_url   VARCHAR(512) COMMENT '来源页面URL',

    time_use    INT      DEFAULT 0  COMMENT '请求耗时',
    visit_time DATETIME DEFAULT NOW() COMMENT '访问时间',
    INDEX idx_article_id (article_id)
) COMMENT ='文章访问日志表';
