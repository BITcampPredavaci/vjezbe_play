# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table post (
  id                        bigint not null,
  content                   varchar(255),
  author_id                 bigint,
  constraint pk_post primary key (id))
;

create table user (
  id                        bigint not null,
  email                     varchar(255),
  password                  varchar(255),
  constraint pk_user primary key (id))
;

create sequence post_seq;

create sequence user_seq;

alter table post add constraint fk_post_author_1 foreign key (author_id) references user (id) on delete restrict on update restrict;
create index ix_post_author_1 on post (author_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists post;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists post_seq;

drop sequence if exists user_seq;

