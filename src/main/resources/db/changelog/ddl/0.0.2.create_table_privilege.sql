
--liquibase formatted sql

--changeset WowVendorTeamHelper:create_tables

create table privileges
(
    id         serial primary key,
    title      varchar(50) not null
);

create table users_privileges
(
    user_id bigint not null,
    privilege_id int    not null,
    primary key (user_id, privilege_id),
    foreign key (user_id) references users (id),
    foreign key (privilege_id) references privileges (id)
);