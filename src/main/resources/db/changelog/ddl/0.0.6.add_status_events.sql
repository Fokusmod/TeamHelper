--liquibase formatted sql

--changeset WowVendorTeamHelper:create_table_event_status

create table wow_event_statuses
(
    id         bigserial primary key,
    title      varchar(50) not null
);

alter table wow_events add column status_id bigint references wow_event_statuses(id);