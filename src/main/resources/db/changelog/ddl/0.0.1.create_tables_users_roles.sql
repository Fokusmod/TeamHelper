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


--changeset WowVendorTeamHelper:wow_events_types

 create table wow_events_types
 (
     id                bigserial primary key,
     title varchar(10) not null
 );

--changeset WowVendorHelper:wow_teams_regions

 create table wow_teams_regions
 (
     id                bigserial primary key,
     title varchar(10) not null
 );


--changeset WowVendorTeamHelper:create_tables_teams


 create table teams
 (
     id                bigserial primary key,
     title varchar(100) not null,
     region_id bigint references wow_teams_regions(id)
 );

--changeset WowVendorTeamHelper:create_tables_events

 create table wow_events
 (
     id                bigserial primary key,
     type_id bigint references wow_events_types(id),
     team_id bigint references teams(id),
     event_date varchar(25) not null,
     started_at varchar(100) not null
 );