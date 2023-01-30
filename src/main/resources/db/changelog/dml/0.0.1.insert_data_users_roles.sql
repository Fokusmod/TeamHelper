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
