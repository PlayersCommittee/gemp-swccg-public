-- Update script: Add bot_player_stats table
-- This script can be run on existing databases to add bot statistics tracking
-- Safe to run multiple times (uses IF NOT EXISTS)

-- -----------------------------------------------------
-- Table `gemp-swccg`.`bot_player_stats`
-- Tracks player statistics when playing against the bot
-- Achievements stored as 144-bit bitfield (18 bytes)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bot_player_stats` (
  `player_id` INT(11) NOT NULL ,
  `wins` INT(11) NOT NULL DEFAULT 0 ,
  `losses` INT(11) NOT NULL DEFAULT 0 ,
  `games_played` INT(11) NOT NULL DEFAULT 0 ,
  `total_ast_score` INT(11) NOT NULL DEFAULT 0 ,
  `best_route_score` INT(11) NOT NULL DEFAULT 0 ,
  `best_damage` INT(11) NOT NULL DEFAULT 0 ,
  `best_force_remaining` INT(11) NOT NULL DEFAULT 0 ,
  `best_time_seconds` INT(11) DEFAULT NULL ,
  `achievements` BINARY(18) NOT NULL DEFAULT 0 ,
  `first_seen` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  `last_seen` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
  PRIMARY KEY (`player_id`) ,
  CONSTRAINT `fk_bot_stats_player` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE ,
  INDEX `idx_bot_stats_wins` (`wins` DESC) ,
  INDEX `idx_bot_stats_ast_score` (`total_ast_score` DESC) ,
  INDEX `idx_bot_stats_games` (`games_played` DESC)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_bin;
