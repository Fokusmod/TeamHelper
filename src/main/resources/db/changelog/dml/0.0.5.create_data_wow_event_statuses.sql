--liquibase formatted sql

--changeset WowVendorTeamHelper:wow_event_statuses_data

insert into wow_event_statuses (title) values ('disable'),
                                              ('active'),
                                              ('created')