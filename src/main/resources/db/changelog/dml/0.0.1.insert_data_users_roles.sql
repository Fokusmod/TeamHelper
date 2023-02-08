--liquibase formatted sql

--changeset WowVendorTeamHelper:insert_data

insert into roles (title)
values ('ROLE_ADMIN'),
       ('ROLE_USER'),
       ('ROLE_RL');

insert into users (username, password, email)
values ('admin', '$2a$12$cVRhalRnnzRlozR2ixuqLuiXlJks2fM7F56gAE3/kgQEYKAqeehlK', null),
       ('user', '$2a$12$3/3pHUymH7E/H/8g5eHQBOtuLu0jn3Ieki8eJtD9HW.e7VyJ.F9AC', 'user@mail.com');

insert into users_roles (user_id, role_id)
VALUES (1, 1),
       (2, 2);

--changeset teams_regions:insert_regions_data

insert into wow_teams_regions (title)
values ('US'),
       ('EU');


--changeset teams:insert_teams_data

insert into teams (title, region_id)
values ('eu-team', 2),
       ('rv-team-us', 1),
       ('rv-team-eu', 2),
       ('dt-team-us', 1),
       ('nt1-team-us', 1),
       ('nt2-team-us', 1),
       ('nt3-team-us', 1),
       ('pr-team-us', 1);

--changeset WowVendorTeamHelper:insert_wow_events_types

insert into wow_events_types (title)
values ('Undefined'),
       ('VOI HC'),
       ('VOI NM');


