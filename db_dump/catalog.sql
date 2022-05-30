create table minimaltodo.catalog
(
    id       int auto_increment
        primary key,
    owner_id int  not null,
    title    text null,
    constraint catalog_users_id_fk
        foreign key (owner_id) references minimaltodo.users (id)
);

