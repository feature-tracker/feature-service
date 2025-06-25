-- Create developers table with sequence
create sequence developer_id_seq start with 100 increment by 50;

create table developers
(
    id            bigint       not null default nextval('developer_id_seq'),
    name          varchar(255) not null,
    email_address varchar(255) unique,
    primary key (id)
);