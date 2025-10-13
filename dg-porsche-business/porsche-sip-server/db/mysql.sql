create table t_config (
      id int unsigned auto_increment comment '主键id' primary key,
      name varchar(20) not null comment '配置名称',
      value varchar(100) comment '配置值',
      type varchar(20) not null comment '配置类型'
) comment '配置';

DROP TABLE IF EXISTS `t_device`;
CREATE TABLE `t_device` (
    `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `device_id` varchar(22) NOT NULL COMMENT '设备国标id',
    `name` varchar(50) NOT NULL COMMENT '名称',
    `protocol_type`  varchar(30) DEFAULT NULL COMMENT '协议类型:RTMP、PULL、GB28181',
    `channel_count`  tinyint(3)  DEFAULT 1 COMMENT '通道数量',
    `register_password` varchar(22)  NULL COMMENT '注册密码',
    `ip` varchar(50) COMMENT 'IP地址',
    `port` smallint(5) unsigned COMMENT '端口',
    `transport` varchar(10) COMMENT '传输协议(UDP/TCP)',
    `manufacturer` varchar(20) NOT NULL COMMENT '生产厂商:HIKVISION DAHUATECH UNIVIEW',
    `device_type`  varchar(20) NOT NULL COMMENT '设备类型: IPC NVR',
    `model` varchar(50) DEFAULT NULL COMMENT '型号',
    `firmware` varchar(50) DEFAULT NULL COMMENT '固件版本',
    `stream_mode` varchar(50)  COMMENT '数据流传输模式:UDP TCP-ACTIVE TCP-PASSIVE ',
    `stream_url` varchar(100) null comment '流地址',
    `enable` tinyint(1) unsigned DEFAULT 1 COMMENT '是否可用',
    `enable_audio` tinyint(1) unsigned DEFAULT null COMMENT '是否启用音频',
    `online` tinyint default 0 not null comment '在线状态',
    `register_time` datetime(6)  COMMENT '注册时间',
    `keepalive_time` datetime(6) DEFAULT NULL COMMENT '最新的心跳时间',
    `remark` varchar(100) null comment '备注',
    created_by         int unsigned  null comment '创建人',
    created_time       datetime(6)  not null comment '创建时间',
    last_modified_by   int unsigned  null comment '修改人',
    last_modified_time datetime(6)  not null comment '最后修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_device_id` (`device_id`)
) COMMENT='设备表';

DROP TABLE IF EXISTS `t_device_channel`;
CREATE TABLE `t_device_channel` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '通道名称',
  `device_id` varchar(22) NOT NULL COMMENT '设备国标id',
  `channel_id` varchar(22) NOT NULL COMMENT '通道id',
  `enable` tinyint(1) unsigned DEFAULT 1 COMMENT '是否可用',
  `online` tinyint default 0 not null comment '是否在线',
  `recording` tinyint default 0 not null comment '是否录像中',
  `ptz_type` varchar(30) default 'None' not null comment '云台类型',
  `leave_time` datetime null comment '下线时间',
  `register_time` datetime(6)  COMMENT '注册时间',
  created_by         int unsigned  null comment '创建人',
  created_time       datetime(6)  not null comment '创建时间',
  last_modified_by   int unsigned  null comment '修改人',
  last_modified_time datetime(6)  not null comment '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id_channel_id` (`device_id`,`channel_id`)
) COMMENT='设备通道表';


DROP TABLE IF EXISTS t_device_log;
create table t_device_log(
     id int unsigned auto_increment primary key comment '主键id',
     device_id varchar(22) not null comment '设备id',
     channel_id varchar(22) comment '通道id',
     type varchar(20) not null comment '请求类型',
     content varchar(50) not null comment '内容',
     created_time   datetime(6)      null comment '创建时间',
     online tinyint not null comment '在线状态'
);

DROP TABLE IF EXISTS t_operate_log;
create table t_operate_log (
    id                 int unsigned auto_increment comment '主键id'  primary key,
    ip            varchar(100)  null comment 'IP',
    method        varchar(500)  null comment '请求java方法',
    request_param longtext      null comment '请求参数',
    request_type  varchar(10)   null comment '请求类型',
    log_content   varchar(1000) null comment '日志内容',
    operate_type  varchar(30)   null comment '操作类型',
    username      varchar(100)  null comment '操作用户名称',
    created_by     int unsigned   null comment '创建人',
    created_time   datetime      null comment '创建时间',
    last_modified_by   int unsigned  null comment '修改人',
    last_modified_time datetime(6)  not null comment '最后修改时间'
) comment '操作日志表';

create table t_device_communication_log (
  id int unsigned auto_increment primary key comment '主键id',
  device_id varchar(22) not null comment '设备id',
  channel_id varchar(22) not null comment '通道id',
  sip_protocol varchar(30) not null comment 'SIP协议类型',
  method varchar(20) not null comment '请求方式',
  cmd_type varchar(40) not null comment '',
  created_time   datetime(6)  null comment '创建时间'
) comment '设备通信日志';


DROP TABLE IF EXISTS t_user;
create table t_user (
    id                 int unsigned auto_increment comment '主键id' primary key,
    username           varchar(100) null comment '登录账号',
    full_name          varchar(100) null comment '姓名',
    password           varchar(255) null comment '密码',
    salt               varchar(45)  null comment 'md5密码盐',
    enabled           tinyint(1)  unsigned  null comment '启用/禁用',
    phone varchar(20) comment '电话',
    email varchar(100) comment '邮箱',
    remark varchar(100) comment '备注',
    created_by         int unsigned  null comment '创建人',
    created_time       datetime(6)  not null comment '创建时间',
    last_modified_by   int unsigned  null comment '修改人',
    last_modified_time datetime(6)  not null comment '最后修改时间',
    constraint index_user_name unique (username)
) comment '系统用户表';
INSERT INTO t_user (id, username, full_name, password, salt, enabled, created_by, created_time, last_modified_by, last_modified_time) VALUES (1, 'admin', '管理员', 'f85340de7d66d670', 'cvVELhH2', 1, null, '2025-08-29 17:05:27.000000', null, '2025-08-29 17:05:27.000000');

create table t_user_access_key(
  id int unsigned auto_increment comment '主键id' primary key,
  user_id int unsigned not null  comment '用户id',
  access_key varchar(10) not null comment 'access key',
  secret varchar(32) not null comment 'secret',
  created_by     int unsigned   null comment '创建人',
  created_time   datetime     null comment '创建时间',
  last_modified_by   int unsigned  null comment '修改人',
  last_modified_time datetime(6)  null comment '最后修改时间',
  key user_id(user_id),
  unique key access_key(access_key)
) comment '用户秘钥';

DROP TABLE IF EXISTS t_permission;
CREATE TABLE `t_permission` (
                                `id`                 INT unsigned AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                                `name`               VARCHAR(100) NOT NULL COMMENT '权限节点名称（用于前端展示）',
                                `permission`         VARCHAR(100) NOT NULL COMMENT '权限标识（用于后端校验）',
                                `parent_id`          INT unsigned DEFAULT NULL COMMENT '父级权限ID',
                                `sort`               INT DEFAULT 0 COMMENT '排序号',
                                created_by     int unsigned   null comment '创建人',
                                created_time   datetime     null comment '创建时间',
                                last_modified_by   int unsigned  null comment '修改人',
                                last_modified_time datetime(6)  null comment '最后修改时间'
) COMMENT='权限表';
INSERT INTO t_permission (id, name, permission, parent_id, sort, created_by, created_time, last_modified_by, last_modified_time) VALUES (1, '设备管理', 'device', null, 0, 4, '2025-09-22 09:11:23', 4, '2025-09-22 09:11:23.000000');
INSERT INTO t_permission (id, name, permission, parent_id, sort, created_by, created_time, last_modified_by, last_modified_time) VALUES (2, '添加设备', 'device:add', 1, 0, 4, '2025-09-22 09:11:23', 4, '2025-09-22 09:11:23.000000');
INSERT INTO t_permission (id, name, permission, parent_id, sort, created_by, created_time, last_modified_by, last_modified_time) VALUES (3, '修改设备', 'device:edit', 1, 0, 4, '2025-09-22 09:11:23', 4, '2025-09-22 09:11:23.000000');
INSERT INTO t_permission (id, name, permission, parent_id, sort, created_by, created_time, last_modified_by, last_modified_time) VALUES (4, '设备列表', 'device:list', 1, 0, 4, '2025-09-22 09:11:23', 4, '2025-09-22 09:11:23.000000');
INSERT INTO t_permission (id, name, permission, parent_id, sort, created_by, created_time, last_modified_by, last_modified_time) VALUES (5, '导入设备', 'device:import', 1, 0, 4, '2025-09-22 09:11:23', 4, '2025-09-22 09:11:23.000000');
INSERT INTO t_permission (id, name, permission, parent_id, sort, created_by, created_time, last_modified_by, last_modified_time) VALUES (6, '刷新通道', 'device:refresh', 1, 0, 4, '2025-09-22 09:11:23', 4, '2025-09-22 09:11:23.000000');
INSERT INTO t_permission (id, name, permission, parent_id, sort, created_by, created_time, last_modified_by, last_modified_time) VALUES (7, '修改通道', 'device:channel:edit', 1, 0, 4, '2025-09-22 09:11:23', 4, '2025-09-22 09:11:23.000000');
INSERT INTO t_permission (id, name, permission, parent_id, sort, created_by, created_time, last_modified_by, last_modified_time) VALUES (8, '强制下线', 'device:forceLoginOut', 1, 0, 4, '2025-09-22 09:11:23', 4, '2025-09-22 09:11:23.000000');
INSERT INTO t_permission (id, name, permission, parent_id, sort, created_by, created_time, last_modified_by, last_modified_time) VALUES (9, '删除设备', 'device:delete', 1, 0, 4, '2025-09-22 09:11:23', 4, '2025-09-22 09:11:23.000000');
INSERT INTO t_permission (id, name, permission, parent_id, sort, created_by, created_time, last_modified_by, last_modified_time) VALUES (10, '删除通道', 'device:channel:delete', 1, 0, 4, '2025-09-22 09:11:23', 4, '2025-09-22 09:11:23.000000');
INSERT INTO t_permission (id, name, permission, parent_id, sort, created_by, created_time, last_modified_by, last_modified_time) VALUES (11, '录像计划', 'record:plan', null, 0, 4, '2025-09-22 09:11:23', 4, '2025-09-22 09:11:23.000000');
INSERT INTO t_permission (id, name, permission, parent_id, sort, created_by, created_time, last_modified_by, last_modified_time) VALUES (12, '添加录像计划', 'record:plan:add', 11, 0, 4, '2025-09-22 09:11:23', 4, '2025-09-22 09:11:23.000000');
INSERT INTO t_permission (id, name, permission, parent_id, sort, created_by, created_time, last_modified_by, last_modified_time) VALUES (13, '查看回放', 'record:plan:view', 11, 0, 4, '2025-09-22 09:11:23', 4, '2025-09-22 09:11:23.000000');


DROP TABLE IF EXISTS t_role;
CREATE TABLE `t_role` (
      `id`                 INT unsigned AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
      `name`               VARCHAR(100) NOT NULL COMMENT '角色名称',
      `protect`            TINYINT(1) unsigned NOT NULL DEFAULT 0 COMMENT '删除保护（0=否，1=是）',
      created_by     int unsigned   null comment '创建人',
      created_time   datetime     null comment '创建时间',
      last_modified_by   int unsigned  null comment '修改人',
      last_modified_time datetime(6)  null comment '最后修改时间',
      UNIQUE INDEX `uk_role_name`(`name`)
) COMMENT='角色表';
INSERT INTO t_role (id, name, protect, created_by, created_time, last_modified_by, last_modified_time) VALUES (1, 'admin', 1, null, null, null, null);

DROP TABLE IF EXISTS t_user_role;
create table t_user_role
(
    id                 int unsigned auto_increment comment '主键'
        primary key,
    role_id            int  unsigned        not null comment '角色ID',
    user_id      int      unsigned    not null comment '权限ID',
    created_by         int unsigned null comment '创建人',
    created_time       datetime     null comment '创建时间',
    last_modified_by   int unsigned null comment '修改人',
    last_modified_time datetime(6)  null comment '最后修改时间',
    constraint uk_role_permission
        unique (role_id, user_id)
)
    comment '角色-用户关联表';
INSERT INTO t_user_role (id, role_id, user_id, created_by, created_time, last_modified_by, last_modified_time) VALUES (1, 1, 1, null, null, null, null);


DROP TABLE IF EXISTS t_role_device;
CREATE TABLE `t_role_device` (
     `id`                 INT unsigned AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
     `role_id`            INT unsigned NOT NULL COMMENT '角色ID',
     `device_id`          VARCHAR(64) NOT NULL COMMENT '设备ID',
     created_by     int unsigned   null comment '创建人',
     created_time   datetime     null comment '创建时间',
     last_modified_by   int unsigned  null comment '修改人',
     last_modified_time datetime(6)  null comment '最后修改时间',
     UNIQUE KEY `uk_role_device` (`role_id`, `device_id`)
) COMMENT='角色-设备关联表';

DROP TABLE IF EXISTS t_role_permission;
CREATE TABLE `t_role_permission` (
     `id`                 INT unsigned AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
     `role_id`            INT unsigned NOT NULL COMMENT '角色ID',
     `permission_id`      INT unsigned NOT NULL COMMENT '权限ID',
     created_by     int unsigned   null comment '创建人',
     created_time   datetime     null comment '创建时间',
     last_modified_by   int unsigned  null comment '修改人',
     last_modified_time datetime(6)  null comment '最后修改时间',
     UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
) COMMENT='角色-权限关联表';

DROP TABLE IF EXISTS t_user_screen;
create table t_user_screen
(
    id                 int unsigned auto_increment comment '主键id'  primary key,
    name varchar(50) not null comment '预设名称',
    screen_count int unsigned not null comment '屏幕数量',
    created_by     int unsigned   null comment '创建人',
    created_time   datetime      null comment '创建时间',
    last_modified_by   int unsigned  null comment '修改人',
    last_modified_time datetime(6)  null comment '最后修改时间'
)comment '预设预览方案';

DROP TABLE IF EXISTS t_user_screen_device;
create table t_user_screen_device
(
    id                 int unsigned auto_increment comment '主键id'  primary key,
    preset_id int unsigned not null comment '预设id',
    device_id varchar(22)  comment '设备id',
    channel_id varchar(22)  comment '通道id',
    created_by     int unsigned   null comment '创建人',
    created_time   datetime      null comment '创建时间',
    last_modified_by   int unsigned  null comment '修改人',
    last_modified_time datetime(6) null comment '最后修改时间'
)comment '预设预览方案设备列表';

DROP TABLE IF EXISTS t_video_record_plan;
create table t_video_record_plan
(
    id int unsigned auto_increment comment '主键id' primary key,
    name varchar(20)  comment '录像计划名称',
    monday int unsigned not null comment '周一计划',
    tuesday int unsigned not null comment '周二计划',
    wednesday int unsigned not null comment '周三计划',
    thursday int unsigned not null comment '周四计划',
    friday int unsigned not null comment '周五计划',
    saturday int unsigned not null comment '周六计划',
    sunday int unsigned not null comment '周日计划',
    enabled tinyint unsigned not null default 1 comment '启用状态',
    created_by     int unsigned  null comment '创建人',
    created_time   datetime     null comment '创建时间',
    last_modified_by   int unsigned  null comment '修改人',
    last_modified_time datetime(6)  null comment '最后修改时间'
) comment '录像计划';

DROP TABLE IF EXISTS t_video_record_device;
create table t_video_record_device
(
    id int unsigned auto_increment comment '主键id' primary key,
    plan_id int unsigned not null  comment '录像计划id',
    device_id varchar(22) not null comment '设备id',
    channel_id varchar(22) not null comment '通道id',
    created_by     int unsigned   null comment '创建人',
    created_time   datetime     null comment '创建时间',
    last_modified_by   int unsigned  null comment '修改人',
    last_modified_time datetime(6)  null comment '最后修改时间',
    unique key plan_device_channel(plan_id, device_id, channel_id)
) comment '录像计划';

DROP TABLE IF EXISTS t_video_record;
create table t_video_record
(
    id           int unsigned auto_increment comment '主键id'
        primary key,
    created_time datetime        null comment '创建时间',
    folder       varchar(500)    not null comment '目录',
    file_path    varchar(100)    not null comment '文件名称',
    device_id    varchar(22)     not null comment '设备id',
    channel_id   varchar(22)     not null comment '通道id',
    start_time   bigint unsigned not null comment '录像开始时间',
    date         date            not null comment '录像日期',
    duration     int unsigned    null comment '录像时长',
    resolution   varchar(20)     null comment '分辨率',
    audio_codec  varchar(30)     null comment '音频编码格式',
    video_codec  varchar(30)     null comment '视频编码格式',
    bandwidth    int unsigned    null comment '码率',
    size int unsigned not null comment '文件大小',
    key device_channel(device_id, channel_id)
)
    comment '录像记录表';

create table t_alarm_record (
    id                 int unsigned auto_increment comment '主键' primary key,
    sn                 varchar(30)  null comment '命令序列号',
    device_id          varchar(22)  null comment '设备id',
    channel_id         varchar(22)  null comment '通道id',
    alarm_priority     varchar(30)  null comment '报警级别',
    alarm_method       varchar(30)  null comment '报警方式',
    alarm_time         datetime     null comment '报警时间',
    alarm_type         varchar(30)  null comment '报警类型',
    event_type         varchar(30)  null comment '事件类型',
    file_path          varchar(255) null comment '报警截图',
    created_by         int unsigned null comment '创建人',
    created_time       datetime     null comment '创建时间',
    last_modified_by   int unsigned null comment '修改人',
    last_modified_time datetime(6)  null comment '最后修改时间'
) comment '报警记录表';


create table t_alarm_plan (
    id                 int unsigned auto_increment comment '主键' primary key,
    name               varchar(64)  null comment '预案名称',
    alarm_priorities   varchar(255) null comment '报警级别',
    alarm_methods      varchar(255) null comment '报警方式',
    alarm_types        varchar(255) null comment '报警类型',
    event_types        varchar(100) null comment '事件类型',
    status             varchar(64)  null comment '预案状态',
    created_by         int unsigned null comment '创建人',
    created_time       datetime     null comment '创建时间',
    last_modified_by   int unsigned null comment '修改人',
    last_modified_time datetime(6)  null comment '最后修改时间'
) comment '报警预案表';

create table t_alarm_plan_channel_rel (
    id                 int unsigned auto_increment comment '主键' primary key,
    alarm_plan_id      int unsigned  null comment '预案id',
    device_id          varchar(22)  null comment '设备id',
    channel_id         varchar(22)  null comment '通道id',
    expire_time        datetime     null comment '过期时间',
    created_by         int unsigned null comment '创建人',
    created_time       datetime     null comment '创建时间',
    last_modified_by   int unsigned null comment '修改人',
    last_modified_time datetime(6)  null comment '最后修改时间'
) comment '报警预案和通道关系表';

create table t_subscribe (
    id                 int unsigned auto_increment comment '主键' primary key,
    device_id          varchar(22)  null comment '设备id',
    catalog            tinyint(1)    unsigned  null default 0 comment '是否订阅目录',
    alarm              tinyint(1)  unsigned null comment '是否订阅报警',
    position           tinyint(1)    unsigned  null default 0 comment '是否订阅位置',
    catalog_time       datetime     null comment '订阅目录过期时间',
    alarm_time         datetime     null comment '订阅报警过期时间',
    position_time      datetime     null comment '订阅位置过期时间',
    created_by         int unsigned null comment '创建人',
    created_time       datetime     null comment '创建时间',
    last_modified_by   int unsigned null comment '修改人',
    last_modified_time datetime(6)  null comment '最后修改时间'
) comment '订阅信息表';
