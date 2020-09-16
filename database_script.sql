SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `gemp-swccg` DEFAULT CHARACTER SET utf8 ;
USE `gemp-swccg` ;

-- -----------------------------------------------------
-- Table `gemp-swccg`.`collection`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `gemp-swccg`.`collection` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `player_id` INT(11) NOT NULL ,
  `collection` MEDIUMBLOB NOT NULL ,
  `type` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 5788
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `gemp-swccg`.`deck`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `gemp-swccg`.`deck` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `player_id` INT(11) NOT NULL ,
  `name` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL DEFAULT 'Default' ,
  `type` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL DEFAULT 'Default' ,
  `contents` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `player_deck` (`player_id` ASC, `name` ASC) ,
  INDEX `player_id` (`id` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 25552
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `gemp-swccg`.`game_history`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `gemp-swccg`.`game_history` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `winner` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `loser` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `win_reason` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `lose_reason` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `win_recording_id` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  `lose_recording_id` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  `start_date` DECIMAL(20,0) NOT NULL ,
  `end_date` DECIMAL(20,0) NOT NULL ,
  `format_name` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  `winner_deck_name` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  `loser_deck_name` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  `tournament` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
   `winner_deck_archetype` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  `loser_deck_archetype` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  `winner_side` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 71300
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- View `gemp-swccg`.`deck_archetype_view_public`
-- -----------------------------------------------------
CREATE OR REPLACE VIEW `gemp-swccg`.`deck_archetype_view_public`
AS SELECT id
,winner
,loser
,win_reason
,lose_reason
,start_date
,end_date
,format_name
,tournament
,winner_deck_archetype
,loser_deck_archetype
,winner_side
FROM `gemp-swccg`.`game_history`
WHERE LOWER(format_name) NOT LIKE '%playtest%';


-- -----------------------------------------------------
-- Table `gemp-swccg`.`league`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `gemp-swccg`.`league` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `type` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `class` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `parameters` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `start` INT(11) NOT NULL ,
  `end` INT(11) NOT NULL ,
  `status` INT(11) NOT NULL ,
  `cost` INT(11) NOT NULL DEFAULT '0' ,
  `allowSpectators` BIT DEFAULT 1,
  `allowTimeExtensions` BIT DEFAULT 0,
  `showPlayerNames` BIT DEFAULT 0,
  `decisionTimeoutSeconds` INT(11) DEFAULT 300 ,
  `timePerPlayerMinutes` INT(11) DEFAULT 50 ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 32
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `gemp-swccg`.`league_match`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `gemp-swccg`.`league_match` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `league_type` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `season_type` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `winner` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `loser` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `winner_side` VARCHAR(10) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  `loser_side` VARCHAR(10) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 17753
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `gemp-swccg`.`league_participation`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `gemp-swccg`.`league_participation` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `league_type` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `player_name` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `join_ip` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 4417
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `gemp-swccg`.`merchant_data`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `gemp-swccg`.`merchant_data` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `blueprint_id` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `transaction_date` DATETIME NOT NULL ,
  `transaction_price` FLOAT NOT NULL ,
  `transaction_type` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `sell_count` INT(11) NOT NULL ,
  `buy_count` INT(11) NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `blueprintId_UNIQUE` (`blueprint_id` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 3087
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `gemp-swccg`.`player`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `gemp-swccg`.`player` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(10) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `password` VARCHAR(64) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `type` VARCHAR(5) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL DEFAULT 'u' ,
  `last_login_reward` INT(11) NULL DEFAULT NULL ,
  `last_ip` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  `create_ip` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  `banned_until` DECIMAL(20,0) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) )
ENGINE = InnoDB
AUTO_INCREMENT = 3811
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `gemp-swccg`.`tournament`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `gemp-swccg`.`tournament` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `tournament_id` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `start` DECIMAL(20,0) NOT NULL ,
  `draft_type` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  `name` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  `format` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  `collection` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  `stage` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  `pairing` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  `round` INT(3) NULL DEFAULT NULL ,
  `prizes` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 54
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `gemp-swccg`.`tournament_match`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `gemp-swccg`.`tournament_match` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `tournament_id` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `round` DECIMAL(2,0) NOT NULL ,
  `player_one` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `player_two` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `winner` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 161
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `gemp-swccg`.`tournament_player`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `gemp-swccg`.`tournament_player` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `tournament_id` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `player` VARCHAR(10) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `deck_name` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `deck` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `dropped` BINARY(1) NOT NULL DEFAULT '0' ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 437
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `gemp-swccg`.`transfer`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `gemp-swccg`.`transfer` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `notify` INT(11) NOT NULL ,
  `player` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `reason` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `name` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `currency` INT(11) NOT NULL ,
  `collection` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  `transfer_date` DECIMAL(20,0) NOT NULL ,
  `direction` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `gemp-swccg`.`ip_ban`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gemp-swccg`.`ip_ban` (
  `id` int(11) NOT NULL AUTO_INCREMENT ,
  `ip` varchar(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' DEFAULT NULL ,
  `prefix` int(3) DEFAULT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE KEY `ip_UNIQUE` (`ip`)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_bin;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
