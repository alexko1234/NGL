
drop # --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table common_info_type (
  id                        bigint auto_increment not null,
  type_name                 varchar(255) not null,
  type_code                 varchar(255) not null,
  collection_name           varchar(255) not null,
  fk_object_type            bigint not null,
  version                   bigint not null,
  constraint uq_common_info_type_type_name unique (type_name),
  constraint uq_common_info_type_type_code unique (type_code),
  constraint uq_common_info_type_collection_n unique (collection_name),
  constraint pk_common_info_type primary key (id))
;

create table container_support_type (
  id                        bigint auto_increment not null,
  fk_common_info_type       bigint not null,
  constraint pk_container_support_type primary key (id))
;

create table container_type (
  id                        bigint auto_increment not null,
  fk_common_info_type       bigint not null,
  constraint pk_container_type primary key (id))
;

create table content_type (
  id                        bigint auto_increment not null,
  fk_content_type_parent    bigint,
  fk_common_info_type       bigint not null,
  constraint pk_content_type primary key (id))
;

create table default_value (
  defaultValue_id           bigint auto_increment not null,
  value                     varchar(255),
  constraint pk_default_value primary key (defaultValue_id))
;

create table experiment_type (
  id                        bigint auto_increment not null,
  fk_common_info_type       bigint not null,
  constraint pk_experiment_type primary key (id))
;

create table instrument_type (
  id                        bigint auto_increment not null,
  fk_common_info_type       bigint not null,
  constraint pk_instrument_type primary key (id))
;

create table object_type (
  id                        bigint auto_increment not null,
  type                      varchar(255) not null,
  generic                   tinyint(1) default 0 not null,
  constraint pk_object_type primary key (id))
;

create table possible_state (
  id                        bigint auto_increment not null,
  common_info_type_id       bigint not null,
  name                      varchar(255) not null,
  active                    tinyint(1) default 0 not null,
  priority                  integer,
  constraint pk_possible_state primary key (id))
;

create table possible_value (
  id                        bigint auto_increment not null,
  value                     varchar(255) not null,
  default_value             tinyint(1) default 0 not null,
  property_definition_id    bigint,
  constraint pk_possible_value primary key (id))
;

create table property_definition (
  id                        bigint auto_increment not null,
  key_code                  varchar(255) not null,
  key_name                  varchar(255) not null,
  required                  tinyint(1) default 0 not null,
  active                    tinyint(1) default 0 not null,
  choice_in_list            tinyint(1) default 0 not null,
  type                      varchar(255) not null,
  display_format            varchar(255),
  display_order             integer,
  default_value             varchar(255),
  common_info_type_id       bigint,
  version                   bigint not null,
  constraint pk_property_definition primary key (id))
;

create table protocol (
  id                        bigint auto_increment not null,
  fk_experiment_type        bigint not null,
  name                      varchar(255),
  file_path                 varchar(255),
  constraint pk_protocol primary key (id))
;

create table protocol_type (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  constraint pk_protocol_type primary key (id))
;

create table reagent (
  id                        bigint auto_increment not null,
  constraint pk_reagent primary key (id))
;

create table unit_type (
  id                        bigint auto_increment not null,
  fk_common_info_type       bigint not null,
  constraint pk_unit_type primary key (id))
;


create table next_experiment_types (
  fk_experiment_type             bigint not null,
  fk_next_experiment_type        bigint not null,
  constraint pk_next_experiment_types primary key (fk_experiment_type, fk_next_experiment_type))
;

create table experiment_type_instrument_type (
  fk_experiment_type             bigint not null,
  fk_instrument_type             bigint not null,
  constraint pk_experiment_type_instrument_type primary key (fk_experiment_type, fk_instrument_type))
;
alter table common_info_type add constraint fk_common_info_type_objectType_1 foreign key (fk_object_type) references object_type (id) on delete restrict on update restrict;
create index ix_common_info_type_objectType_1 on common_info_type (fk_object_type);
alter table container_support_type add constraint fk_container_support_type_comm_2 foreign key (fk_common_info_type) references common_info_type (id) on delete restrict on update restrict;
create index ix_container_support_type_comm_2 on container_support_type (fk_common_info_type);
alter table container_type add constraint fk_container_type_commonInfoTy_3 foreign key (fk_common_info_type) references common_info_type (id) on delete restrict on update restrict;
create index ix_container_type_commonInfoTy_3 on container_type (fk_common_info_type);
alter table content_type add constraint fk_content_type_contentTypePar_4 foreign key (fk_content_type_parent) references content_type (id) on delete restrict on update restrict;
create index ix_content_type_contentTypePar_4 on content_type (fk_content_type_parent);
alter table content_type add constraint fk_content_type_commonInfoType_5 foreign key (fk_common_info_type) references common_info_type (id) on delete restrict on update restrict;
create index ix_content_type_commonInfoType_5 on content_type (fk_common_info_type);
alter table experiment_type add constraint fk_experiment_type_commonInfoT_6 foreign key (fk_common_info_type) references common_info_type (id) on delete restrict on update restrict;
create index ix_experiment_type_commonInfoT_6 on experiment_type (fk_common_info_type);
alter table instrument_type add constraint fk_instrument_type_commonInfoT_7 foreign key (fk_common_info_type) references common_info_type (id) on delete restrict on update restrict;
create index ix_instrument_type_commonInfoT_7 on instrument_type (fk_common_info_type);
alter table possible_state add constraint fk_possible_state_common_info__8 foreign key (common_info_type_id) references common_info_type (id) on delete restrict on update restrict;
create index ix_possible_state_common_info__8 on possible_state (common_info_type_id);
alter table possible_value add constraint fk_possible_value_propertyDefi_9 foreign key (property_definition_id) references property_definition (id) on delete restrict on update restrict;
create index ix_possible_value_propertyDefi_9 on possible_value (property_definition_id);
alter table property_definition add constraint fk_property_definition_common_10 foreign key (common_info_type_id) references common_info_type (id) on delete restrict on update restrict;
create index ix_property_definition_common_10 on property_definition (common_info_type_id);
alter table protocol add constraint fk_protocol_experiment_type_11 foreign key (fk_experiment_type) references experiment_type (id) on delete restrict on update restrict;
create index ix_protocol_experiment_type_11 on protocol (fk_experiment_type);
alter table unit_type add constraint fk_unit_type_commonInfoType_12 foreign key (fk_common_info_type) references common_info_type (id) on delete restrict on update restrict;
create index ix_unit_type_commonInfoType_12 on unit_type (fk_common_info_type);



alter table next_experiment_types add constraint fk_next_experiment_types_expe_01 foreign key (fk_experiment_type) references experiment_type (id) on delete restrict on update restrict;

alter table next_experiment_types add constraint fk_next_experiment_types_expe_02 foreign key (fk_next_experiment_type) references experiment_type (id) on delete restrict on update restrict;

alter table experiment_type_instrument_type add constraint fk_experiment_type_instrument_01 foreign key (fk_experiment_type) references experiment_type (id) on delete restrict on update restrict;

alter table experiment_type_instrument_type add constraint fk_experiment_type_instrument_02 foreign key (fk_instrument_type) references instrument_type (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table common_info_type;

drop table container_support_type;

drop table container_type;

drop table content_type;

drop table default_value;

drop table experiment_type;

drop table next_experiment_types;

drop table experiment_type_instrument_type;

drop table instrument_type;

drop table object_type;

drop table possible_state;

drop table possible_value;

drop table property_definition;

drop table protocol;

drop table protocol_type;

drop table reagent;

drop table unit_type;

SET FOREIGN_KEY_CHECKS=1;

