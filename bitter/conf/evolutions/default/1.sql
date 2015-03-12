# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table post (
  id                        bigint auto_increment not null,
  content                   varchar(255),
  author_id                 bigint,
  created_at                datetime not null,
  updated_at                datetime not null,
  constraint pk_post primary key (id))
;

create table bitter_user (
  id                        bigint auto_increment not null,
  email                     varchar(255),
  username                  varchar(255),
  password                  varchar(255),
  admin                     tinyint(1) default 0,
  created_at                datetime not null,
  updated_at                datetime not null,
  constraint uq_bitter_user_email unique (email),
  constraint uq_bitter_user_username unique (username),
  constraint pk_bitter_user primary key (id))
;


create table followers (
  user_id                        bigint not null,
  follower_id                    bigint not null,
  constraint pk_followers primary key (user_id, follower_id))
;
alter table post add constraint fk_post_author_1 foreign key (author_id) references bitter_user (id) on delete restrict on update restrict;
create index ix_post_author_1 on post (author_id);



alter table followers add constraint fk_followers_bitter_user_01 foreign key (user_id) references bitter_user (id) on delete restrict on update restrict;

alter table followers add constraint fk_followers_bitter_user_02 foreign key (follower_id) references bitter_user (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table post;

drop table bitter_user;

drop table followers;

SET FOREIGN_KEY_CHECKS=1;

