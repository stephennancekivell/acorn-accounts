# Password schema
 
# --- !Ups
 
CREATE TABLE Password (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    password varchar(255) NOT NULL,
    title varchar(1000) NOT NULL,
    description varchar(4000) NOT NULL,
    PRIMARY KEY (id)
);
 
# --- !Downs
 
DROP TABLE Password;