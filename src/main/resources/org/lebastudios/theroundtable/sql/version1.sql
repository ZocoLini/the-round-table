create table core_account
(
    id                             integer,
    changue_password_on_next_login boolean      not null,
    name                           varchar(255) not null,
    password                       varchar(255) not null,
    type                           varchar(255) not null,
    constraint ck_account_type check (type in ('ROOT', 'ADMIN', 'MANAGER', 'CASHIER', 'ACCOUNTANT')),
    constraint pk_account primary key (id)
);
