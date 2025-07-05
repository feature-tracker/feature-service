create sequence comments_id_seq start with 100 increment by 50;

create table comments
(
    id         bigint                          not null primary key default nextval('comments_id_seq'),
    code       varchar(50)                     not null,
    feature_id bigint references features (id) not null,
    user_name    varchar(255)                    not null,
    content    text                            not null,
    created_at timestamp                       not null             default now()
);

create sequence comment_code_seq start with 100 increment by 1;