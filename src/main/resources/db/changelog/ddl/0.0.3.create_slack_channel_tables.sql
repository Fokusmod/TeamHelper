--liquibase formatted sql

--changeset WowVendorTeamHelper:create_slack-channels

create table slack_channel_destinations
(
    id bigserial primary key,
    title varchar(50)
);


create table slack_channels
(
    id bigserial primary key,
    channel_id varchar(25),
    title varchar (50)
);


--changeset WowVendorTeamHelper:create_slack-channels_and_destinations

create table channels_and_destinations
(
    slack_channel_id bigint not null,
    destination_id bigint not null ,
    foreign key (slack_channel_id) references slack_channels(id),
    foreign key (destination_id) references slack_channel_destinations(id)
);