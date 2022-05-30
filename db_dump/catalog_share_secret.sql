create table minimaltodo.catalog_share_secret
(
    share_secret varchar(255) not null,
    catalog_id   int          not null,
    constraint share_secret_share_secret_uindex
        unique (share_secret),
    constraint share_secret_catalog_id_fk
        foreign key (catalog_id) references minimaltodo.catalog (id)
);

