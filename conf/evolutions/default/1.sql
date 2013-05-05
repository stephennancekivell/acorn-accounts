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

create sequence s_party_id;

create table party (
  partyid    bigint DEFAULT nextval('s_party_id'),
  userid    bigint,
  name varchar(256)
);

create table userparty (
  partyid    bigint,
  userid	 bigint
);



# --- !Downs

drop table user;
drop sequence s_user_id;

drop table password;
drop sequence s_password_id;

drop table party;
drop sequence s_party_id;

drop table userparty;