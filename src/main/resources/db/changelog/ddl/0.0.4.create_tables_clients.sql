--liquibase formatted sql

--changeset WowVendorTeamHelper:create_order_status

CREATE TABLE order_statuses
(
    id    Bigserial PRIMARY KEY,
    title VARCHAR(100) NOT NULL
);