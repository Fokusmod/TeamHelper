--liquibase formatted sql

--changeset WowVendorTeamHelper:client_data

insert into order_statuses (title)
values ('NEW'),
       ('IN_PROCESS'),
       ('WAITING'),
       ('TRANSFERRED'),
       ('READY');

--changeset WowVendorTeamHelper:bundle_data

insert into bundle_stages (title)
values ('Heroic Raid'),
       ('Normal Raid');


insert into bundles (title)
values ('Heroic + Normal');

insert into bundle_with_bundle_stages
values (1,1),
       (1,2);

