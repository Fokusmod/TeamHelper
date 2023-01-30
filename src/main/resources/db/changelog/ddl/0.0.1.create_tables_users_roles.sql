 --liquibase formatted sql

 --changeset WowVendorTeamHelper:create_tables


create table users
(
    id                bigserial primary key,
    username          varchar(50)  not null unique,
    password          varchar(250) not null,
    activation_code   varchar(255),
    email             varchar(150),
    status            varchar(50),
    created_at        timestamp default current_timestamp,
    updated_at        timestamp default current_timestamp
);

create table roles
(
    id         serial primary key,
    title      varchar(50) not null,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

create table users_roles
(
    user_id bigint not null,
    role_id int    not null,
    primary key (user_id, role_id),
    foreign key (user_id) references users (id),
    foreign key (role_id) references roles (id)
);
