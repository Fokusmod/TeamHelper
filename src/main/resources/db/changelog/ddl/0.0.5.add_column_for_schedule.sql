--liquibase formatted sql

--changeset WowVendorTeamHelper:create_table_clients_with_events

create table wow_clients_with_events
(
    event_id  bigint not null,
    client_id bigint not null,
    foreign key (event_id) references wow_events (id),
    foreign key (client_id) references wow_clients (id)
);

--changeset WowVendorTeamHelper:wow_clients_stage
CREATE TABLE clients_bundle_stages
(
    id bigserial primary key,
    client_id       bigint references wow_clients(id),
    bundle_stage_id bigint references bundle_stages(id),
    status_id bigint references order_statuses(id)
);