# --- Created by Ebean DDL
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

create table container_category (
  id                        bigint auto_increment not null,
  name                      varchar(255) not null,
  code                      varchar(255) not null,
  version                   bigint not null,
  constraint uq_container_category_code unique (code),
  constraint pk_container_category primary key (id))
;

create table container_support_category (
  id                        bigint auto_increment not null,
  name                      varchar(255) not null,
  code                      varchar(255) not null,
  version                   bigint not null,
  constraint uq_container_support_category_co unique (code),
  constraint pk_container_support_category primary key (id))
;

create table default_value (
  defaultValue_id           bigint auto_increment not null,
  value                     varchar(255),
  version                   bigint not null,
  constraint pk_default_value primary key (defaultValue_id))
;

create table equipe (
  id                        integer auto_increment not null,
  nom                       varchar(255),
  constraint pk_equipe primary key (id))
;

create table experiment_type (
  id                        bigint auto_increment not null,
  fk_common_info_type       bigint not null,
  version                   bigint not null,
  constraint pk_experiment_type primary key (id))
;

create table instrument (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  code                      varchar(255) not null,
  instrument_used_type_id   bigint,
  version                   bigint not null,
  constraint uq_instrument_code unique (code),
  constraint pk_instrument primary key (id))
;

create table instrument_category (
  id                        bigint auto_increment not null,
  name                      varchar(255) not null,
  code                      varchar(255) not null,
  version                   bigint not null,
  constraint uq_instrument_category_code unique (code),
  constraint pk_instrument_category primary key (id))
;

create table instrument_used_type (
  id                        bigint auto_increment not null,
  fk_common_info_type       bigint not null,
  version                   bigint not null,
  constraint pk_instrument_used_type primary key (id))
;

create table measure_category (
  id                        bigint auto_increment not null,
  name                      varchar(255) not null,
  code                      varchar(255) not null,
  version                   bigint not null,
  constraint uq_measure_category_code unique (code),
  constraint pk_measure_category primary key (id))
;

create table measure_possible_value (
  id                        bigint auto_increment not null,
  value                     varchar(255) not null,
  default_value             tinyint(1) default 0 not null,
  measure_category_id       bigint,
  version                   bigint not null,
  constraint pk_measure_possible_value primary key (id))
;

create table object_type (
  id                        bigint auto_increment not null,
  type                      varchar(255) not null,
  generic                   tinyint(1) default 0 not null,
  version                   bigint not null,
  constraint pk_object_type primary key (id))
;

create table permission (
  id                        integer auto_increment not null,
  label                     varchar(255),
  code                      varchar(255),
  constraint pk_permission primary key (id))
;

create table possible_state (
  id                        bigint auto_increment not null,
  name                      varchar(255) not null,
  active                    tinyint(1) default 0 not null,
  priority                  integer,
  version                   bigint not null,
  constraint pk_possible_state primary key (id))
;

create table possible_value (
  id                        bigint auto_increment not null,
  value                     varchar(255) not null,
  default_value             tinyint(1) default 0 not null,
  property_definition_id    bigint,
  version                   bigint not null,
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
  measure_category_id       bigint,
  measure_value_id          bigint,
  common_info_type_id       bigint,
  version                   bigint not null,
  constraint pk_property_definition primary key (id))
;

create table protocol (
  id                        bigint auto_increment not null,
  fk_experiment_type        bigint not null,
  name                      varchar(255),
  file_path                 varchar(255),
  version                   bigint not null,
  constraint pk_protocol primary key (id))
;

create table purification_method_type (
  id                        bigint auto_increment not null,
  fk_common_info_type       bigint not null,
  version                   bigint not null,
  constraint pk_purification_method_type primary key (id))
;

create table quality_control_type (
  id                        bigint auto_increment not null,
  fk_common_info_type       bigint not null,
  version                   bigint not null,
  constraint pk_quality_control_type primary key (id))
;

create table reagent_type (
  id                        bigint auto_increment not null,
  fk_common_info_type       bigint not null,
  protocol_id               bigint,
  version                   bigint not null,
  constraint pk_reagent_type primary key (id))
;

create table role (
  id                        integer auto_increment not null,
  label                     varchar(255),
  constraint pk_role primary key (id))
;

create table sample_category (
  id                        bigint auto_increment not null,
  name                      varchar(255) not null,
  code                      varchar(255) not null,
  version                   bigint not null,
  constraint uq_sample_category_code unique (code),
  constraint pk_sample_category primary key (id))
;

create table sample_type (
  id                        bigint auto_increment not null,
  fk_common_info_type       bigint not null,
  version                   bigint not null,
  constraint pk_sample_type primary key (id))
;

create table transfer_method_type (
  id                        bigint auto_increment not null,
  fk_common_info_type       bigint not null,
  version                   bigint not null,
  constraint pk_transfer_method_type primary key (id))
;

create table user (
  id                        integer auto_increment not null,
  login                     varchar(255),
  firstname                 varchar(255),
  lastname                  varchar(255),
  email                     varchar(255),
  technicaluser             integer,
  password                  varchar(255),
  confirmpassword           varchar(255),
  constraint pk_user primary key (id))
;


create table common_info_type_possible_state (
  fk_common_info_type            bigint not null,
  fk_possible_state              bigint not null,
  constraint pk_common_info_type_possible_state primary key (fk_common_info_type, fk_possible_state))
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

create table role_permission (
  role_id                        integer not null,
  permission_id                  integer not null,
  constraint pk_role_permission primary key (role_id, permission_id))
;

create table user_role (
  user_id                        integer not null,
  role_id                        integer not null,
  constraint pk_user_role primary key (user_id, role_id))
;

create table user_equipe (
  user_id                        integer not null,
  equipe_id                      integer not null,
  constraint pk_user_equipe primary key (user_id, equipe_id))
;
alter table common_info_type add constraint fk_common_info_type_objectType_1 foreign key (fk_object_type) references object_type (id) on delete restrict on update restrict;
create index ix_common_info_type_objectType_1 on common_info_type (fk_object_type);
alter table experiment_type add constraint fk_experiment_type_commonInfoT_2 foreign key (fk_common_info_type) references common_info_type (id) on delete restrict on update restrict;
create index ix_experiment_type_commonInfoT_2 on experiment_type (fk_common_info_type);
alter table instrument add constraint fk_instrument_instrumentUsedTy_3 foreign key (instrument_used_type_id) references instrument_used_type (id) on delete restrict on update restrict;
create index ix_instrument_instrumentUsedTy_3 on instrument (instrument_used_type_id);
alter table instrument_used_type add constraint fk_instrument_used_type_common_4 foreign key (fk_common_info_type) references common_info_type (id) on delete restrict on update restrict;
create index ix_instrument_used_type_common_4 on instrument_used_type (fk_common_info_type);
alter table measure_possible_value add constraint fk_measure_possible_value_meas_5 foreign key (measure_category_id) references measure_category (id) on delete restrict on update restrict;
create index ix_measure_possible_value_meas_5 on measure_possible_value (measure_category_id);
alter table possible_value add constraint fk_possible_value_propertyDefi_6 foreign key (property_definition_id) references property_definition (id) on delete restrict on update restrict;
create index ix_possible_value_propertyDefi_6 on possible_value (property_definition_id);
alter table property_definition add constraint fk_property_definition_measure_7 foreign key (measure_category_id) references measure_category (id) on delete restrict on update restrict;
create index ix_property_definition_measure_7 on property_definition (measure_category_id);
alter table property_definition add constraint fk_property_definition_measure_8 foreign key (measure_value_id) references measure_possible_value (id) on delete restrict on update restrict;
create index ix_property_definition_measure_8 on property_definition (measure_value_id);
alter table property_definition add constraint fk_property_definition_commonI_9 foreign key (common_info_type_id) references common_info_type (id) on delete restrict on update restrict;
create index ix_property_definition_commonI_9 on property_definition (common_info_type_id);
alter table protocol add constraint fk_protocol_quality_control_t_10 foreign key (fk_experiment_type) references quality_control_type (id) on delete restrict on update restrict;
create index ix_protocol_quality_control_t_10 on protocol (fk_experiment_type);
alter table purification_method_type add constraint fk_purification_method_type_c_11 foreign key (fk_common_info_type) references common_info_type (id) on delete restrict on update restrict;
create index ix_purification_method_type_c_11 on purification_method_type (fk_common_info_type);
alter table quality_control_type add constraint fk_quality_control_type_commo_12 foreign key (fk_common_info_type) references common_info_type (id) on delete restrict on update restrict;
create index ix_quality_control_type_commo_12 on quality_control_type (fk_common_info_type);
alter table reagent_type add constraint fk_reagent_type_commonInfoTyp_13 foreign key (fk_common_info_type) references common_info_type (id) on delete restrict on update restrict;
create index ix_reagent_type_commonInfoTyp_13 on reagent_type (fk_common_info_type);
alter table reagent_type add constraint fk_reagent_type_protocol_14 foreign key (protocol_id) references protocol (id) on delete restrict on update restrict;
create index ix_reagent_type_protocol_14 on reagent_type (protocol_id);
alter table sample_type add constraint fk_sample_type_commonInfoType_15 foreign key (fk_common_info_type) references common_info_type (id) on delete restrict on update restrict;
create index ix_sample_type_commonInfoType_15 on sample_type (fk_common_info_type);
alter table transfer_method_type add constraint fk_transfer_method_type_commo_16 foreign key (fk_common_info_type) references common_info_type (id) on delete restrict on update restrict;
create index ix_transfer_method_type_commo_16 on transfer_method_type (fk_common_info_type);



alter table common_info_type_possible_state add constraint fk_common_info_type_possible__01 foreign key (fk_common_info_type) references common_info_type (id) on delete restrict on update restrict;

alter table common_info_type_possible_state add constraint fk_common_info_type_possible__02 foreign key (fk_possible_state) references possible_state (id) on delete restrict on update restrict;

alter table next_experiment_types add constraint fk_next_experiment_types_expe_01 foreign key (fk_experiment_type) references experiment_type (id) on delete restrict on update restrict;

alter table next_experiment_types add constraint fk_next_experiment_types_expe_02 foreign key (fk_next_experiment_type) references experiment_type (id) on delete restrict on update restrict;

alter table experiment_type_instrument_type add constraint fk_experiment_type_instrument_01 foreign key (fk_experiment_type) references experiment_type (id) on delete restrict on update restrict;

alter table experiment_type_instrument_type add constraint fk_experiment_type_instrument_02 foreign key (fk_instrument_type) references instrument_used_type (id) on delete restrict on update restrict;

alter table role_permission add constraint fk_role_permission_role_01 foreign key (role_id) references role (id) on delete restrict on update restrict;

alter table role_permission add constraint fk_role_permission_permission_02 foreign key (permission_id) references permission (id) on delete restrict on update restrict;

alter table user_role add constraint fk_user_role_user_01 foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table user_role add constraint fk_user_role_role_02 foreign key (role_id) references role (id) on delete restrict on update restrict;

alter table user_equipe add constraint fk_user_equipe_user_01 foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table user_equipe add constraint fk_user_equipe_equipe_02 foreign key (equipe_id) references equipe (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table common_info_type;

drop table common_info_type_possible_state;

drop table container_category;

drop table container_support_category;

drop table default_value;

drop table equipe;

drop table experiment_type;

drop table next_experiment_types;

drop table experiment_type_instrument_type;

drop table instrument;

drop table instrument_category;

drop table instrument_used_type;

drop table measure_category;

drop table measure_possible_value;

drop table object_type;

drop table permission;

drop table possible_state;

drop table possible_value;

drop table property_definition;

drop table protocol;

drop table purification_method_type;

drop table quality_control_type;

drop table reagent_type;

drop table role;

drop table role_permission;

drop table sample_category;

drop table sample_type;

drop table transfer_method_type;

drop table user;

drop table user_role;

drop table user_equipe;

SET FOREIGN_KEY_CHECKS=1;

