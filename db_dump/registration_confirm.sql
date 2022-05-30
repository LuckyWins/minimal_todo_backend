create table minimaltodo.registration_confirm
(
    user_id       int          not null,
    confirm_token varchar(300) not null,
    expires_at    bigint       not null,
    constraint registration_confirm_users_id_fk
        foreign key (user_id) references minimaltodo.users (id)
);
