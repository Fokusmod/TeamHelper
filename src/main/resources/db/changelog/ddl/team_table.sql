--liquibase formatted sql

 --changeset teams:create_tables


create table teams
(
    id                bigserial primary key,
    title varchar(100) not null
);

