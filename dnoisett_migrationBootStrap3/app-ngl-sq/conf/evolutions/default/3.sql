# --- Sample dataset

# --- !Ups

alter table possible_state drop foreign key fk_possible_state_common_info__8;
alter table possible_state drop index ix_possible_state_common_info__8;
alter table possible_state drop column common_info_type_id;

create table common_info_type_possible_state (
  fk_common_info_type            bigint not null,
  fk_possible_state              bigint not null,
  constraint pk_common_info_type_possible_state primary key (fk_common_info_type, fk_possible_state))
;
alter table common_info_type_possible_state add constraint fk_common_info_type_possible__01 foreign key (fk_common_info_type) references common_info_type (id) on delete restrict on update restrict;

alter table common_info_type_possible_state add constraint fk_common_info_type_possible__02 foreign key (fk_possible_state) references possible_state (id) on delete restrict on update restrict;

# --- !Downs

alter table possible_state add column common_info_type_id bigint not null;
alter table possible_state add constraint fk_possible_state_common_info__8 foreign key (common_info_type_id) references common_info_type (id) on delete restrict on update restrict;
create index ix_possible_state_common_info__8 on possible_state (common_info_type_id);

alter table common_info_type_possible_state drop constraint fk_common_info_type_possible__01;
alter table common_info_type_possible_state drop constraint fk_common_info_type_possible__02;
drop table common_info_type_possible_state;



