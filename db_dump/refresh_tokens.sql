create table minimaltodo.refresh_tokens
(
    user_id       int          not null,
    refresh_token varchar(300) not null,
    expires_at    bigint       not null,
    constraint refresh_tokens_users_id_fk
        foreign key (user_id) references minimaltodo.users (id)
);

