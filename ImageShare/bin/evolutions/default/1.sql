# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table image (
  id                        integer identity(1,1) not null,
  public_id                 varchar(255),
  image_url                 varchar(255),
  secret_image_url          varchar(255),
  constraint pk_image primary key (id))
;




# --- !Downs

drop table image;

