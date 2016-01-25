--Generic reset
DELETE FROM `CNS_NGL`.`role_permission`;
DELETE FROM `CNS_NGL`.`permission`;
--Only if every users are NOT already "reader"
-- DELETE FROM `CNS_NGL`.`user_role`;
-- DELETE FROM `CNS_NGL`.`role`;

--Otherwise ( case "bi" & "qc" roles exist )
DELETE FROM `CNS_NGL`.`role` WHERE `label`= 'bi' ;
DELETE FROM `CNS_NGL`.`role` WHERE `label`= 'qc' ;


--New Roles
INSERT INTO `CNS_NGL`.`role` (`label`) VALUES ('writer');
INSERT INTO `CNS_NGL`.`role` (`label`) VALUES ('admin');

--New Permissions
INSERT INTO `CNS_NGL`.`permission` (`label`, `code`) VALUES ('reading', 'reading');
INSERT INTO `CNS_NGL`.`permission` (`label`, `code`) VALUES ('writing', 'writing');
INSERT INTO `CNS_NGL`.`permission` (`label`, `code`) VALUES ('admin', 'admin');


--Relations between both roles & permissions
INSERT INTO `CNS_NGL`.`role_permission` (`role_id`, `permission_id`) VALUES ('1', '1');
INSERT INTO `CNS_NGL`.`role_permission` (`role_id`, `permission_id`) VALUES ('2', '1');
INSERT INTO `CNS_NGL`.`role_permission` (`role_id`, `permission_id`) VALUES ('2', '2');
INSERT INTO `CNS_NGL`.`role_permission` (`role_id`, `permission_id`) VALUES ('3', '1');
INSERT INTO `CNS_NGL`.`role_permission` (`role_id`, `permission_id`) VALUES ('3', '2');
INSERT INTO `CNS_NGL`.`role_permission` (`role_id`, `permission_id`) VALUES ('3', '3');


--Roles Update for all existing users in database ( becoming writer )
UPDATE `CNS_NGL`.`user_role`
SET `role_id`= '2';

-- End of Script


