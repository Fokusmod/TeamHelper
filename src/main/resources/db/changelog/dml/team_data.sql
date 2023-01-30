--liquibase formatted sql

--changeset teams:insert teams


insert into teams (title)
values ('eu-team'),
        ('us-dt-team'),
        ('rv-team'),
        ('us-nt1-team'),
        ('us-nt2-team');

