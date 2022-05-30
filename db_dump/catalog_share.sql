create table minimaltodo.catalog_share
(
    user_id    int not null,
    catalog_id int not null,
    constraint catalog_share_catalog_id_fk
        foreign key (catalog_id) references minimaltodo.catalog (id),
    constraint catalog_share_users_id_fk
        foreign key (user_id) references minimaltodo.users (id)
);

