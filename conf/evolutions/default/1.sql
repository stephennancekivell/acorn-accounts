# Password schema
 
# --- !Ups
 
CREATE TABLE Password (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    password varchar(255) NOT NULL,
    title varchar(1000) NOT NULL,
    description varchar(4000) NOT NULL,
    PRIMARY KEY (id)
);
 
 

create sequence s_user_id;

create table user (
  id    bigint DEFAULT nextval('s_user_id'),
  name  varchar(128)
);



# --- !Downs
 
DROP TABLE Password;

drop table user;
drop sequence s_user_id;