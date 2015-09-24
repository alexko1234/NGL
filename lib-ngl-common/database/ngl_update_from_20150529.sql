-- MySQL Workbench Synchronization
-- Generated: 2015-09-24 14:36
-- Model: New Model
-- Version: 1.0
-- Project: Name of the project
-- Author: galbini

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

ALTER TABLE `NGL`.`experiment_type` 
ADD COLUMN `short_code` VARCHAR(10) NULL DEFAULT NULL AFTER `atomic_transfert_method`;

DROP TABLE IF EXISTS `NGL`.`resolution_institute` ;

DROP TABLE IF EXISTS `NGL`.`resolution_object_type` ;

DROP TABLE IF EXISTS `NGL`.`valuation_criteria_institute` ;

DROP TABLE IF EXISTS `NGL`.`valuation_criteria` ;

DROP TABLE IF EXISTS `NGL`.`valuation_criteria_common_info_type` ;

DROP TABLE IF EXISTS `NGL`.`resolution_category` ;

DROP TABLE IF EXISTS `NGL`.`protocol_reagent_type` ;

DROP TABLE IF EXISTS `NGL`.`resolution` ;

DROP TABLE IF EXISTS `NGL`.`reagent_type` ;

DROP TABLE IF EXISTS `NGL`.`protocol` ;

DROP TABLE IF EXISTS `NGL`.`common_info_type_resolution` ;

DROP TABLE IF EXISTS `NGL`.`experiment_type_protocol` ;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
