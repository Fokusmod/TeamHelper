--liquibase formatted sql

--changeset WowVendorTeamHelper:slack-channels-data

insert into slack_channel_destinations (title) values
    ('RECEIVE_SLACK_MESSAGE'),
    ('SEND_SLACK_MESSAGE');








