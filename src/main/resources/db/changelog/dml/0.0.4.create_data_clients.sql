--liquibase formatted sql

--changeset WowVendorTeamHelper:client_data

insert into order_statuses (title) values
    ('NEW'),
    ('IN_PROCESS'),
    ('WAITING'),
    ('TRANSFERRED'),
    ('READY');