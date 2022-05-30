create table minimaltodo.users
(
    id        int auto_increment
        primary key,
    name      varchar(255)         not null,
    ip        varchar(255)         null,
    email     varchar(255)         not null,
    password  varchar(255)         not null,
    confirmed tinyint(1) default 0 not null,
    type      int        default 1 not null comment '0 - admin, 1 - user',
    constraint users_email_uindex
        unique (email),
    constraint users_name_uindex
        unique (name)
);