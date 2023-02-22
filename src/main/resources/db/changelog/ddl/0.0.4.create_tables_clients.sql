--liquibase formatted sql

--changeset WowVendorTeamHelper:create_order_status

CREATE TABLE order_statuses
(
    id    Bigserial PRIMARY KEY,
    title VARCHAR(100) NOT NULL
);

--changeset WowVendorTeamHelper:create_bundles

CREATE TABLE bundle_stages
(
    id    Bigserial PRIMARY KEY,
    title VARCHAR(100) NOT NULL
);

CREATE TABLE bundles
(
    id    Bigserial PRIMARY KEY,
    title VARCHAR(100) NOT NULL
);

CREATE TABLE bundle_with_bundle_stages
(
    bundle_id       bigint not null,
    bundle_stage_id bigint not null,
    foreign key (bundle_id) references bundles (id),
    foreign key (bundle_stage_id) references bundle_stages (id)
);

--changeset WowVendorTeamHelper:create_clients
CREATE TABLE client_stages
(
    id    bigserial primary key,
    title varchar(100)
);

--changeset WowVendorTeamHelper:wow_clients
CREATE TABLE wow_clients
(
    id              bigserial primary key,
    bundle          varchar(10),
    bundle_id       bigint,
    foreign key (bundle_id) references bundles (id),
    order_code      varchar(20),
    battle_tag      varchar(50),
    fraction        varchar(30),
    realm           varchar(100),
    nickname        varchar(100),
    order_date_time varchar(100),
    character_class varchar(100),
    boost_mode      varchar(100),
    playing_type    varchar(100),
    region          varchar(100),
    service         varchar,
    game            varchar(50),
    specific_bosses varchar,
    armory_link     varchar,
    no_parse_info   varchar,
    status_id       bigint,
    foreign key (status_id) references order_statuses (id),
    order_comments  varchar
);

--changeset WowVendorTeamHelper:wow_clients_stage
CREATE TABLE clients_bundle_stages
(
    client_id bigint,
    client_stage_id bigint
);
