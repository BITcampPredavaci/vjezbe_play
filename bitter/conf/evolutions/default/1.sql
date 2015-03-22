# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table file_helper (
  id                        bigint not null,
  file_name                 varchar(255),
  default_file_path         varchar(255),
  constraint pk_file_helper primary key (id))
;

create table post (
  id                        bigint not null,
  content                   varchar(255),
  author_id                 bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_post primary key (id))
;

create table bitter_user (
  id                        bigint not null,
  email                     varchar(255),
  username                  varchar(255),
  password                  varchar(255),
  admin                     boolean,
  avatar_id                 bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_bitter_user_email unique (email),
  constraint uq_bitter_user_username unique (username),
  constraint pk_bitter_user primary key (id))
;


create table followers (
  user_id                        bigint not null,
  follower_id                    bigint not null,
  constraint pk_followers primary key (user_id, follower_id))
;
create sequence file_helper_seq;

create sequence post_seq;

create sequence bitter_user_seq;

alter table post add constraint fk_post_author_1 foreign key (author_id) references bitter_user (id) on delete restrict on update restrict;
create index ix_post_author_1 on post (author_id);
alter table bitter_user add constraint fk_bitter_user_avatar_2 foreign key (avatar_id) references file_helper (id) on delete restrict on update restrict;
create index ix_bitter_user_avatar_2 on bitter_user (avatar_id);



alter table followers add constraint fk_followers_bitter_user_01 foreign key (user_id) references bitter_user (id) on delete restrict on update restrict;

alter table followers add constraint fk_followers_bitter_user_02 foreign key (follower_id) references bitter_user (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists file_helper;

drop table if exists post;

drop table if exists bitter_user;

drop table if exists followers;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists file_helper_seq;

drop sequence if exists post_seq;

drop sequence if exists bitter_user_seq;

