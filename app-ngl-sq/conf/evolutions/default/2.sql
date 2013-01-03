# --- Sample dataset

# --- !Ups

insert into object_type (id, type, generic) values (1, 'Project Type', true);
insert into object_type (id, type, generic) values (2, 'Sample Type', true);
insert into object_type (id, type, generic) values (3, 'Library Type', true);
insert into object_type (id, type, generic) values (4, 'Experiment Type', false);
insert into object_type (id, type, generic) values (5, 'Run Type', true);
insert into object_type (id, type, generic) values (6, 'Instrument Type', false);
insert into object_type (id, type, generic) values (7, 'Container Type', true);
insert into object_type (id, type, generic) values (8, 'Content Type', false);
insert into object_type (id, type, generic) values (9, 'Unit Type', true);


insert into common_info_type (id , type_name, type_code, collection_name,  fk_object_type ,version) values (100, 'Exp_type1',1,'Exp_type1',4,  1);
insert into experiment_type values (101,100); 

# --- !Downs

delete from object_type;
delete from common_info_type;
delete from experiment_type;
