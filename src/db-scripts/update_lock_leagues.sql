-- Migration: Lock-in League Decks
-- Adds support for locking player decks after their first game in a league.

-- Deck lock-in mode on league table (only meaningful for constructed leagues).
-- NULL means the league has no deck-locking rules. Stored as a code string so that
-- additional lock modes (e.g. "after Nth game", "submit before play") can be added
-- in the future without changing the schema.
ALTER TABLE `league` ADD COLUMN `lockedDeckType` VARCHAR(50) CHARACTER SET 'utf8' COLLATE 'utf8_bin' DEFAULT NULL AFTER `invitationOnly`;

-- Locked deck storage on league_participation (per player per league)
ALTER TABLE `league_participation` ADD COLUMN `locked_ls_deck_name` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL;
ALTER TABLE `league_participation` ADD COLUMN `locked_ls_deck` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL;
ALTER TABLE `league_participation` ADD COLUMN `locked_ds_deck_name` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL;
ALTER TABLE `league_participation` ADD COLUMN `locked_ds_deck` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL;
