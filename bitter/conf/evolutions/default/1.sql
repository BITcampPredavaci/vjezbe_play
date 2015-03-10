# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table post (
  id                        bigint not null,
  content                   varchar(255),
  author_id                 bigint,
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint pk_post primary key (id))
;

create table user (
  id                        bigint not null,
  email                     varchar(255),
  username                  varchar(255),
  password                  varchar(255),
  created_at                timestamp not null,
  updated_at                timestamp not null,
  constraint uq_user_email unique (email),
  constraint uq_user_username unique (username),
  constraint pk_user primary key (id))
;


create table follower (
  user_id                        bigint not null,
  user_id                        bigint not null,
  constraint pk_follower primary key (user_id, user_id))
;
create sequence post_seq;

create sequence user_seq;

alter table post add constraint fk_post_author_1 foreign key (author_id) references user (id) on delete restrict on update restrict;
create index ix_post_author_1 on post (author_id);



alter table follower add constraint fk_follower_user_01 foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table follower add constraint fk_follower_user_02 foreign key (user_id) references user (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists post;

drop table if exists user;

drop table if exists follower;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists post_seq;

drop sequence if exists user_seq;

