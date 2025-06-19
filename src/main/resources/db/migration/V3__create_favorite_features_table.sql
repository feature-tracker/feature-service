create sequence if not exists favorite_features_id_seq start with 100 increment by 50;

create table if not exists favorite_features(
    id bigint not null primary key default nextval('favorite_features_id_seq'),
    feature_id bigint not null,
    user_id varchar(255) not null
);