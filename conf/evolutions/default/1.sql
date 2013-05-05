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
  id    bigint DEFAULT nextval('s_party_id'),
  name varchar(256)
);

create table userparty (
  partyid    bigint,
  userid	 bigint
);
alter table userparty add constraint userparty1 unique (partyid, userid);

create table passwordpermission(
	partyid		bigint,
	passwordid	bigint,
	canwrite	boolean,
	canread		boolean
);
alter table passwordpermission add constraint passwordpermission1 unique (partyid, passwordid);

# --- !Downs

drop table user;
drop sequence s_user_id;

drop table password;
drop sequence s_password_id;

drop table party;
drop sequence s_party_id;

drop table userparty;

drop table passwordpermission;