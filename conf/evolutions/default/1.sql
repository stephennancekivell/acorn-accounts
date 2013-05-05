# Password schema
 
# --- !Ups

create sequence s_user_id;

create table user (
  id    bigint DEFAULT nextval('s_user_id'),
  name  varchar(128)
);

create sequence s_password_id;

create table password (
  id    bigint DEFAULT nextval('s_password_id'),
  title  varchar(256),
  password varchar(256),
  description varchar(4000)
);



# --- !Downs

drop table user;
drop sequence s_user_id;

drop table password;
drop sequence s_password_id;