package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.cards.conditions.*;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.*;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.InsertCardRevealedResult;

import java.util.*;


// This class contain methods to be used by cards
// to check the conditions of the current game state.
//
// Whenever possible, the goal is to use these methods
// instead of the accessing GameState, etc. directly.
//
public class GameConditions {

    // Checks if player has X Force available to be used.

    /**
     * Determines if player can use at least the specified amount of Force.
     *
     * @param game     the game
     * @param playerId the player
     * @param amount   the amount of Force
     * @return true or false
     */
    public static boolean canUseForce(SwccgGame game, String playerId, float amount) {
        return (amount <= forceAvailableToUse(game, playerId));
    }

    /**
     * Gets the amount of the Force available for the player to use.
     *
     * @param game     the game
     * @param playerId the player
     * @return the amount of Force available to use
     */
    public static int forceAvailableToUse(SwccgGame game, String playerId) {
        return game.getModifiersQuerying().getForceAvailableToUse(game.getGameState(), playerId);
    }

    /**
     * Gets the amount of the Force activate this turn by the player.
     *
     * @param game     the game
     * @param playerId the player
     * @return the amount of Force activated this turn
     */
    public static int forceActivatedThisPhase(SwccgGame game, String playerId) {
        return game.getModifiersQuerying().getForceActivatedThisTurn(playerId, false);
    }

    /**
     * Gets the number of cards in the specified player's Force Pile.
     *
     * @param game     the game
     * @param playerId the player
     * @return the number of cards
     */
    public static int numCardsInForcePile(SwccgGame game, String playerId) {
        return game.getGameState().getForcePileSize(playerId);
    }

    /**
     * Determines if the amount of the Force available for the player to use to play the specified Interrupt (plus any
     * other additional costs).
     *
     * @param game   the game
     * @param card   the Interrupt
     * @param amount the amount of Force
     * @return true or false
     */
    public static boolean canUseForceToPlayInterrupt(SwccgGame game, String playerId, PhysicalCard card, float amount) {
        int cost = additionalForceUseRequiredToPlayInterrupt(game, playerId, card);
        if (!game.getModifiersQuerying().isInterruptPlayForFree(game.getGameState(), card)) {
            cost += amount;
        }
        return (cost <= forceAvailableToUse(game, playerId));
    }

    // Checks how much Force player has available for use to play an interrupt.
    public static int forceAvailableToUseToPlayInterrupt(SwccgGame game, String playerId, PhysicalCard card) {
        return Math.max(0, (int) Math.floor(game.getModifiersQuerying().getForceAvailableToUse(game.getGameState(), playerId) - additionalForceUseRequiredToPlayInterrupt(game, playerId, card)));
    }

    // Checks how much additional Force an Interrupt will cost to play.
    public static int additionalForceUseRequiredToPlayInterrupt(SwccgGame game, String playerId, PhysicalCard card) {
        return game.getModifiersQuerying().getExtraForceRequiredToPlayInterrupt(game.getGameState(), card);
    }

    //
    //
    // Play card option
    //
    //

    /**
     * Determines if card is in play with the specified play card option.
     *
     * @param game             the game
     * @param card             the source card of the action
     * @param playCardOptionId the identifier for the card's play card option
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isPlayCardOption(SwccgGame game, PhysicalCard card, PlayCardOptionId playCardOptionId) {
        return card.getZone().isInPlay() && card.getPlayCardOptionId() == playCardOptionId;
    }

    /**
     * Determines if the card owner (and opponent if needed) can use the required amount of Force to relocate the specified
     * card to a location accepted by the location filter.
     *
     * @param game           the game
     * @param self           the card
     * @param amount         the amount of Force
     * @param locationFilter the location filter
     * @return true if sufficient Force can be used, otherwise false
     */
    public static boolean canUseForceToRelocateCard(SwccgGame game, PhysicalCard self, float amount, Filterable locationFilter) {
        return Filters.canBeRelocatedToLocation(Filters.and(locationFilter), false, false, false, amount, false).accepts(game, self);
    }

    //
    //
    // Usage limits
    //
    //

    /**
     * Determines if card can be deployed "Once per game".
     *
     * @param game the game
     * @param card the source card of the action
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean canDeployOncePerGame(SwccgGame game, PhysicalCard card) {
        return isOncePerGame(game, card, GameTextActionId.DEPLOY_CARD_ACTION);
    }

    /**
     * Determines if card can be 'inserted' "Once per game".
     *
     * @param game the game
     * @param card the source card of the action
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean canInsertOncePerGame(SwccgGame game, PhysicalCard card) {
        return isOncePerGame(game, card, GameTextActionId.DEPLOY_CARD_ACTION);
    }

    /**
     * Checks when action can be performed "Once per game".
     *
     * @param game             the game
     * @param card             the source card of the action
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerGame(SwccgGame game, PhysicalCard card, GameTextActionId gameTextActionId) {
        if (!gameTextActionId.isPerGame())
            throw new UnsupportedOperationException(gameTextActionId + " is not a per game action");

        for (String title : card.getTitles()) {
            if (game.getModifiersQuerying().getUntilEndOfGameLimitCounter(title, gameTextActionId).getUsedLimit() >= 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks when action can be performed "Twice per game".
     *
     * @param game             the game
     * @param card             the source card of the action
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isTwicePerGame(SwccgGame game, PhysicalCard card, GameTextActionId gameTextActionId) {
        if (!gameTextActionId.isPerGame())
            throw new UnsupportedOperationException(gameTextActionId + " is not a per game action");

        for (String title : card.getTitles()) {
            if (game.getModifiersQuerying().getUntilEndOfGameLimitCounter(title, gameTextActionId).getUsedLimit() >= 2) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks when action can be performed "Three times per game".
     *
     * @param game             the game
     * @param card             the source card of the action
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isThreeTimesPerGame(SwccgGame game, PhysicalCard card, GameTextActionId gameTextActionId) {
        if (!gameTextActionId.isPerGame())
            throw new UnsupportedOperationException(gameTextActionId + " is not a per game action");

        for (String title : card.getTitles()) {
            if (game.getModifiersQuerying().getUntilEndOfGameLimitCounter(title, gameTextActionId).getUsedLimit() >= 3) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks when action can be performed "Four times per game".
     *
     * @param game             the game
     * @param card             the source card of the action
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isFourTimesPerGame(SwccgGame game, PhysicalCard card, GameTextActionId gameTextActionId) {
        if (!gameTextActionId.isPerGame())
            throw new UnsupportedOperationException(gameTextActionId + " is not a per game action");

        for (String title : card.getTitles()) {
            if (game.getModifiersQuerying().getUntilEndOfGameLimitCounter(title, gameTextActionId).getUsedLimit() >= 4) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks when action can be performed "Once per captive".
     *
     * @param game             the game
     * @param card             the source card of the action
     * @param captive          the captive
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerCaptive(SwccgGame game, PhysicalCard card, PhysicalCard captive, GameTextActionId gameTextActionId) {
        if (!gameTextActionId.isPerCaptive())
            throw new UnsupportedOperationException(gameTextActionId + " is not a per captive action");

        List<String> titles = card.getTitles();
        for (String title : titles) {
            if (game.getModifiersQuerying().getUntilEndOfCaptivityLimitCounter(title, gameTextActionId, captive).getUsedLimit() >= 1)
                return false;
        }
        return true;
    }

    /**
     * Checks when action can be performed "Once per Force drain".
     *
     * @param game             the game
     * @param card             the source card of the action
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerForceDrain(SwccgGame game, PhysicalCard card, GameTextActionId gameTextActionId) {
        if (!gameTextActionId.isPerForceDrain())
            throw new UnsupportedOperationException(gameTextActionId + " is not a per Force drain action");

        for (String title : card.getTitles()) {
            if (game.getModifiersQuerying().getUntilEndOfForceDrainLimitCounter(title, gameTextActionId).getUsedLimit() >= 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks when action can be performed "Once per race total".
     *
     * @param game             the game
     * @param card             the source card of the action
     * @param raceTotal        the race total
     * @param gameTextActionId the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerRaceTotal(SwccgGame game, PhysicalCard card, float raceTotal, GameTextActionId gameTextActionId) {
        if (!gameTextActionId.isPerRaceTotal())
            throw new UnsupportedOperationException(gameTextActionId + " is not a per race total action");

        for (String title : card.getTitles()) {
            if (game.getModifiersQuerying().getPerRaceTotalLimitCounter(title, gameTextActionId, raceTotal).getUsedLimit() >= 1) {
                return false;
            }
        }
        return true;
    }

    /*


    // Checks that the card (of same title) has not already been used X times this game.
    public static boolean canUseCardCountTimesPerGame(SwccgGame game, PhysicalCard self, GameTextActionId gameTextActionId, int count) {
        if (!gameTextActionId.isPerGame())
            throw new UnsupportedOperationException(gameTextActionId + " is not a per game action");

        for (String title : self.getTitles()) {
            if (game.getModifiersQuerying().getUntilEndOfGameLimitCounter(title, gameTextActionId).getUsedLimit() >= count) {
                return false;
            }
        }
        return true;
    }

    // Checks that the card (of same title) has not been used this Force drain.
    public static boolean canUseCardOncePerForceDrain(SwccgGame game, PhysicalCard self, GameTextActionId gameTextActionId) {
        if (!gameTextActionId.isPerForceDrain())
            throw new UnsupportedOperationException(gameTextActionId + " is not a per Force drain action");

        for (String title : self.getTitles()) {
            if (game.getModifiersQuerying().getUntilEndOfForceDrainLimitCounter(title, gameTextActionId).getUsedLimit() >= 1) {
                return false;
            }
        }
        return true;
    }
    */

    /**
     * Checks when action can be performed "Once per turn".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerTurn(SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return isOncePerTurn(game, self, null, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT);
    }

    /**
     * Checks when action can be performed "Once per turn".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerTurn(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId) {
        return isOncePerTurn(game, self, playerId, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT);
    }

    /**
     * Checks when action can be performed "Once per turn".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerTurn(SwccgGame game, PhysicalCard self, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        return isOncePerTurn(game, self, null, gameTextSourceCardId, gameTextActionId);
    }

    /**
     * Checks when action can be performed "Once per turn".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerTurn(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        return game.getModifiersQuerying().getUntilEndOfTurnLimitCounter(self, playerId, gameTextSourceCardId, gameTextActionId).getUsedLimit() < 1;
    }

    /**
     * Checks when action can be performed "X times per turn".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param count                the value for X
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isNumTimesPerTurn(SwccgGame game, PhysicalCard self, int count, int gameTextSourceCardId) {
        return isNumTimesPerTurn(game, self, null, count, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT);
    }

    /**
     * Checks when action can be performed "X times per turn".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param count                the value for X
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isNumTimesPerTurn(SwccgGame game, PhysicalCard self, String playerId, int count, int gameTextSourceCardId) {
        return isNumTimesPerTurn(game, self, playerId, count, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT);
    }

    /**
     * Checks when action can be performed "X times per turn".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param count                the value for X
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isNumTimesPerTurn(SwccgGame game, PhysicalCard self, int count, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        return isNumTimesPerTurn(game, self, null, count, gameTextSourceCardId, gameTextActionId);
    }

    /**
     * Checks when action can be performed "X times per turn".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param count                the value for X
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isNumTimesPerTurn(SwccgGame game, PhysicalCard self, String playerId, int count, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        return game.getModifiersQuerying().getUntilEndOfTurnLimitCounter(self, playerId, gameTextSourceCardId, gameTextActionId).getUsedLimit() < count;
    }

    /**
     * Checks when action can be performed "Once per turn" for card title.
     *
     * @param game                 the game
     * @param card                 the source card of the action
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerTurnForCardTitle(SwccgGame game, PhysicalCard card, GameTextActionId gameTextActionId) {
        if (!gameTextActionId.isPerTurnForCardTitle())
            throw new UnsupportedOperationException(gameTextActionId + " is not a per turn for card title action");

        List<String> titles = card.getTitles();
        for (String title : titles) {
            if (game.getModifiersQuerying().getUntilEndOfTurnForCardTitleLimitCounter(title, gameTextActionId).getUsedLimit() >= 1)
                return false;
        }
        return true;
    }

    /**
     * Checks when action can be performed "Once during your turn".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringYourTurn(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId) {
        return isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT);
    }

    /**
     * Checks when action can be performed "Once during your turn".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringYourTurn(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        return isDuringYourTurn(game, playerId)
                && game.getModifiersQuerying().getUntilEndOfTurnLimitCounter(self, playerId, gameTextSourceCardId, gameTextActionId).getUsedLimit() < 1;
    }

    /**
     * Checks when action can be performed "Once during opponent's turn".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringOpponentsTurn(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId) {
        return isOnceDuringOpponentsTurn(game, self, playerId, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT);
    }

    /**
     * Checks when action can be performed "Once during opponent's turn".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringOpponentsTurn(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        return isOpponentsTurn(game, playerId)
                && game.getModifiersQuerying().getUntilEndOfTurnLimitCounter(self, playerId, gameTextSourceCardId, gameTextActionId).getUsedLimit() < 1;
    }


    // Checks that the card has not already been used X times during player's turn.
    public static boolean canUseCardCountTimesPerYourTurn(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId, int count) {
        return canUseCardCountTimesPerYourTurn(game, self, playerId, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT, count);
    }

    public static boolean canUseCardCountTimesPerYourTurn(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId, int count) {
        return isDuringYourTurn(game, playerId)
                && game.getModifiersQuerying().getUntilEndOfTurnLimitCounter(self, playerId, gameTextSourceCardId, gameTextActionId).getUsedLimit() < count;
    }

    /**
     * Checks when action can be performed "Once per battle".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerBattle(SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return isOncePerBattle(game, self, null, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT);
    }

    /**
     * Checks when action can be performed "Once per battle".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerBattle(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId) {
        return isOncePerBattle(game, self, playerId, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT);
    }

    /**
     * Checks when action can be performed "Once per battle".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerBattle(SwccgGame game, PhysicalCard self, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        return isOncePerBattle(game, self, null, gameTextSourceCardId, gameTextActionId);
    }

    /**
     * Checks when action can be performed "Once per battle".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerBattle(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        return isDuringBattle(game)
                && game.getModifiersQuerying().getUntilEndOfBattleLimitCounter(self, playerId, gameTextSourceCardId, gameTextActionId).getUsedLimit() < 1;
    }

    /**
     * Checks when action can be performed "X times per battle".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param count                the value for X
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isNumTimesPerBattle(SwccgGame game, PhysicalCard self, int count, int gameTextSourceCardId) {
        return isNumTimesPerBattle(game, self, null, count, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT);
    }

    /**
     * Checks when action can be performed "X times per battle".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param count                the value for X
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isNumTimesPerBattle(SwccgGame game, PhysicalCard self, String playerId, int count, int gameTextSourceCardId) {
        return isNumTimesPerBattle(game, self, playerId, count, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT);
    }

    /**
     * Checks when action can be performed "X times per battle".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param count                the value for X
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isNumTimesPerBattle(SwccgGame game, PhysicalCard self, int count, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        return isNumTimesPerBattle(game, self, null, count, gameTextSourceCardId, gameTextActionId);
    }

    /**
     * Checks when action can be performed "X times per battle".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param count                the value for X
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isNumTimesPerBattle(SwccgGame game, PhysicalCard self, String playerId, int count, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        return isDuringBattle(game)
                && game.getModifiersQuerying().getUntilEndOfBattleLimitCounter(self, playerId, gameTextSourceCardId, gameTextActionId).getUsedLimit() < count;
    }

    /**
     * Checks when action can be performed "Once per duel".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerDuel(SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return isOncePerDuel(game, self, null, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT);
    }

    /**
     * Checks when action can be performed "Once per duel".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerDuel(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId) {
        return isOncePerDuel(game, self, playerId, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT);
    }

    /**
     * Checks when action can be performed "Once per duel".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerDuel(SwccgGame game, PhysicalCard self, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        return isOncePerDuel(game, self, null, gameTextSourceCardId, gameTextActionId);
    }

    /**
     * Checks when action can be performed "Once per duel".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerDuel(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        return isDuringDuel(game)
                && game.getModifiersQuerying().getUntilEndOfDuelLimitCounter(self, playerId, gameTextSourceCardId, gameTextActionId).getUsedLimit() < 1;
    }

    /**
     * Checks when action can be performed "Once during each of ____ phases".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param phaseOwner           the phase owner
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringPlayersPhase(SwccgGame game, PhysicalCard self, int gameTextSourceCardId, String phaseOwner, Phase phase) {
        return isOnceDuringPlayersPhase(game, self, null, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT, phaseOwner, phase);
    }

    /**
     * Checks when action can be performed "Once during each of ____ phases".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param phaseOwner           the phase owner
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringPlayersPhase(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId, String phaseOwner, Phase phase) {
        return isOnceDuringPlayersPhase(game, self, playerId, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT, phaseOwner, phase);
    }

    /**
     * Checks when action can be performed "Once during each of ____ phases".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @param phaseOwner           the phase owner
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringPlayersPhase(SwccgGame game, PhysicalCard self, int gameTextSourceCardId, GameTextActionId gameTextActionId, String phaseOwner, Phase phase) {
        return isOnceDuringPlayersPhase(game, self, null, gameTextSourceCardId, gameTextActionId, phaseOwner, phase);
    }

    /**
     * Checks when action can be performed "Once during each of ____ phases".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @param phaseOwner           the phase owner
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringPlayersPhase(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId, String phaseOwner, Phase phase) {
        return isPhaseForPlayer(game, phase, phaseOwner)
                && game.getModifiersQuerying().getUntilEndOfPhaseLimitCounter(self, playerId, gameTextSourceCardId, gameTextActionId, phase).getUsedLimit() < 1;
    }

    /**
     * Checks when action can be performed "Once during each of your ____ phases".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringYourPhase(SwccgGame game, PhysicalCard self, int gameTextSourceCardId, Phase phase) {
        return isOnceDuringYourPhase(game, self, null, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT, phase);
    }

    /**
     * Checks when action can be performed "Once during each of your ____ phases".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringYourPhase(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId, Phase phase) {
        return isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT, phase);
    }

    /**
     * Checks when action can be performed "Once during each of your ____ phases".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringYourPhase(SwccgGame game, PhysicalCard self, int gameTextSourceCardId, GameTextActionId gameTextActionId, Phase phase) {
        return isOnceDuringYourPhase(game, self, null, gameTextSourceCardId, gameTextActionId, phase);
    }

    /**
     * Checks when action can be performed "X times during your ____ phase".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringYourPhase(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId, Phase phase) {
        return isPhaseForPlayer(game, phase, playerId)
                && game.getModifiersQuerying().getUntilEndOfPhaseLimitCounter(self, playerId, gameTextSourceCardId, gameTextActionId, phase).getUsedLimit() < 1;
    }

    /**
     * Checks when action can be performed "X times during your ____ phase".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param count                the value for X
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isNumTimesDuringYourPhase(SwccgGame game, PhysicalCard self, String playerId, int count, int gameTextSourceCardId, Phase phase) {
        return isNumTimesDuringYourPhase(game, self, playerId, count, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT, phase);
    }

    /**
     * Checks when action can be performed "X times during your ____ phase".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param count                the value for X
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isNumTimesDuringYourPhase(SwccgGame game, PhysicalCard self, String playerId, int count, int gameTextSourceCardId, GameTextActionId gameTextActionId, Phase phase) {
        return isDuringYourPhase(game, playerId, phase)
                && game.getModifiersQuerying().getUntilEndOfPhaseLimitCounter(self, playerId, gameTextSourceCardId, gameTextActionId, phase).getUsedLimit() < count;
    }

    /**
     * Checks when action can be performed "Once during each of opponent's ____ phases".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringOpponentsPhase(SwccgGame game, PhysicalCard self, int gameTextSourceCardId, Phase phase) {
        return isOnceDuringOpponentsPhase(game, self, null, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT, phase);
    }

    /**
     * Checks when action can be performed "Once during each of opponent's ____ phases".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringOpponentsPhase(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId, Phase phase) {
        return isOnceDuringOpponentsPhase(game, self, playerId, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT, phase);
    }

    /**
     * Checks when action can be performed "Once during each of opponent's ____ phases".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringOpponentsPhase(SwccgGame game, PhysicalCard self, int gameTextSourceCardId, GameTextActionId gameTextActionId, Phase phase) {
        return isOnceDuringOpponentsPhase(game, self, null, gameTextSourceCardId, gameTextActionId, phase);
    }

    /**
     * Checks when action can be performed "Once during each of opponent's ____ phases".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringOpponentsPhase(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId, Phase phase) {
        return isPhaseForPlayer(game, phase, game.getOpponent(playerId != null ? playerId : self.getOwner()))
                && game.getModifiersQuerying().getUntilEndOfPhaseLimitCounter(self, playerId, gameTextSourceCardId, gameTextActionId, phase).getUsedLimit() < 1;
    }

    /**
     * Checks when action can be performed "Once during each ____ phase".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringEitherPlayersPhase(SwccgGame game, PhysicalCard self, int gameTextSourceCardId, Phase phase) {
        return isOnceDuringEitherPlayersPhase(game, self, null, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT, phase);
    }

    /**
     * Checks when action can be performed "Once during each ____ phase".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringEitherPlayersPhase(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId, Phase phase) {
        return isOnceDuringEitherPlayersPhase(game, self, playerId, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT, phase);
    }

    /**
     * Checks when action can be performed "Once during each ____ phase".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringEitherPlayersPhase(SwccgGame game, PhysicalCard self, int gameTextSourceCardId, GameTextActionId gameTextActionId, Phase phase) {
        return isOnceDuringEitherPlayersPhase(game, self, null, gameTextSourceCardId, gameTextActionId, phase);
    }

    /**
     * Checks when action can be performed "Once during each of opponent's ____ phases".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOnceDuringEitherPlayersPhase(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId, Phase phase) {
        return isEitherPlayersPhase(game, phase)
                && game.getModifiersQuerying().getUntilEndOfPhaseLimitCounter(self, playerId, gameTextSourceCardId, gameTextActionId, phase).getUsedLimit() < 1;
    }

    /**
     * Checks when action can be performed "X times during any ____ phase".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param count                the value for X
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isNumTimesDuringEitherPlayersPhase(SwccgGame game, PhysicalCard self, int count, int gameTextSourceCardId, Phase phase) {
        return isNumTimesDuringEitherPlayersPhase(game, self, null, count, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT, phase);
    }

    /**
     * Checks when action can be performed "X times during any ____ phase".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param count                the value for X
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isNumTimesDuringEitherPlayersPhase(SwccgGame game, PhysicalCard self, String playerId, int count, int gameTextSourceCardId, Phase phase) {
        return isNumTimesDuringEitherPlayersPhase(game, self, playerId, count, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT, phase);
    }

    /**
     * Checks when action can be performed "X times during any ____ phase".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param count                the value for X
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isNumTimesDuringEitherPlayersPhase(SwccgGame game, PhysicalCard self, int count, int gameTextSourceCardId, GameTextActionId gameTextActionId, Phase phase) {
        return isNumTimesDuringEitherPlayersPhase(game, self, null, count, gameTextSourceCardId, gameTextActionId, phase);
    }

    /**
     * Checks when action can be performed "X times during any ____ phase".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param count                the value for X
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @param phase                the phase
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isNumTimesDuringEitherPlayersPhase(SwccgGame game, PhysicalCard self, String playerId, int count, int gameTextSourceCardId, GameTextActionId gameTextActionId, Phase phase) {
        return isEitherPlayersPhase(game, phase)
                && game.getModifiersQuerying().getUntilEndOfPhaseLimitCounter(self, playerId, gameTextSourceCardId, gameTextActionId, phase).getUsedLimit() < count;
    }

    /**
     * Checks when action can be performed "Once per Force loss".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerForceLoss(SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return isOncePerForceLoss(game, self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT);
    }

    /**
     * Checks when action can be performed "Once per Force loss".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerForceLoss(SwccgGame game, PhysicalCard self, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        return game.getModifiersQuerying().getUntilEndOfForceLossLimitCounter(self, null, gameTextSourceCardId, gameTextActionId).getUsedLimit() < 1;
    }

    /**
     * Checks when action can be performed "Once per Force loss".
     *
     * @param game                 the game
     * @param self                 the source card of the action
     * @param playerId             the player to perform the action
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @param gameTextActionId     the identifier for the card's specific action to check the limit of
     * @return true if condition is satisfied, otherwise false
     */
    public static boolean isOncePerForceLoss(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId) {
        return game.getModifiersQuerying().getUntilEndOfForceLossLimitCounter(self, playerId, gameTextSourceCardId, gameTextActionId).getUsedLimit() < 1;
    }

    // Checks if the device can be used by the user it is attached to.
    public static boolean canUseDevice(SwccgGame game, PhysicalCard device) {
        return canUseDevice(game, device.getAttachedTo(), device);
    }

    // Checks if the device can be used by the specified user.
    public static boolean canUseDevice(SwccgGame game, PhysicalCard user, PhysicalCard device) {
        return Filters.canUseDevice(device).accepts(game.getGameState(), game.getModifiersQuerying(), user);
    }

    /**
     * Determines if the specified weapon can be used by the specified user.
     *
     * @param game   the game
     * @param user   the card to use the weapon
     * @param weapon the weapon
     * @return true or false
     */
    public static boolean canUseWeapon(SwccgGame game, PhysicalCard user, PhysicalCard weapon) {
        return Filters.canUseWeapon(weapon).accepts(game.getGameState(), game.getModifiersQuerying(), user);
    }

    /**
     * Determines if the specified card can be canceled.
     *
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean canBeCanceled(SwccgGame game, PhysicalCard card) {
        return !game.getModifiersQuerying().mayNotBeCanceled(game.getGameState(), card);
    }

    /**
     * Determines if the specified card can be placed out of play.
     *
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean canBePlacedOutOfPlay(SwccgGame game, PhysicalCard card) {
        return !game.getModifiersQuerying().mayNotBePlacedOutOfPlay(game.getGameState(), card);
    }

    /**
     * Determines if the specified Objective card can be flipped.
     *
     * @param game the game
     * @param card the Objective card
     * @return true or false
     */
    public static boolean canBeFlipped(SwccgGame game, PhysicalCard card) {
        return !game.getModifiersQuerying().cannotBeFlipped(game.getGameState(), card);
    }

    /**
     * Determines if the specified card can be grabbed by the specified card.
     *
     * @param game       the game
     * @param source     the card to grab the card
     * @param cardToGrab the card to be grabbed
     * @return true or false
     */
    public static boolean canBeGrabbed(SwccgGame game, PhysicalCard source, PhysicalCard cardToGrab) {
        if (cardToGrab == null) {
            return false;
        }
        if (game.getGameState().getCurrentPhase() == Phase.PLAY_STARTING_CARDS) {
            return false;
        }
        if (cardToGrab.getZone() == Zone.OUT_OF_PLAY) {
            return false;
        }
        if (cardToGrab.getStackedOn() != null && Filters.grabber.accepts(game, cardToGrab.getStackedOn())) {
            return false;
        }
        if (game.getModifiersQuerying().mayNotBeGrabbed(game.getGameState(), cardToGrab)) {
            return false;
        }
        return true;
    }

    /**
     * Determines if the specified Interrupt card can be returned to hand.
     *
     * @param game the game
     * @param card the Interrupt card
     * @return true or false
     */
    public static boolean interruptCanBeReturnedToHand(SwccgGame game, PhysicalCard card) {
        if (card.getBlueprint().getCardCategory() != CardCategory.INTERRUPT) {
            return false;
        }
        if (card.getZone() == Zone.OUT_OF_PLAY) {
            return false;
        }
        if (card.getStackedOn() != null && Filters.grabber.accepts(game, card.getStackedOn())) {
            return false;
        }
        return true;
    }

    /**
     * Determines if the specified Interrupt card can be placed in Lost Pile.
     *
     * @param game the game
     * @param card the Interrupt card
     * @return true or false
     */
    public static boolean interruptCanBePlacedInLostPile(SwccgGame game, PhysicalCard card) {
        if (card.getBlueprint().getCardCategory() != CardCategory.INTERRUPT) {
            return false;
        }
        if (card.getZone() == Zone.OUT_OF_PLAY) {
            return false;
        }
        if (card.getStackedOn() != null && Filters.grabber.accepts(game, card.getStackedOn())) {
            return false;
        }
        return true;
    }

    /**
     * Determines if the specified Interrupt card can be placed out of play.
     *
     * @param game the game
     * @param card the Interrupt card
     * @return true or false
     */
    public static boolean interruptCanBePlacedOutOfPlay(SwccgGame game, PhysicalCard card) {
        if (card.getBlueprint().getCardCategory() != CardCategory.INTERRUPT) {
            return false;
        }
        if (card.getZone() == Zone.OUT_OF_PLAY) {
            return false;
        }
        if (card.getStackedOn() != null && Filters.grabber.accepts(game, card.getStackedOn())) {
            return false;
        }
        return canBePlacedOutOfPlay(game, card);
    }

    /**
     * Determines if the specified binary droid card can be turned off.
     *
     * @param game the game
     * @param card the binary droid card
     * @return true or false
     */
    public static boolean canBeTurnedOff(SwccgGame game, PhysicalCard card) {
        return !card.isBinaryOff();
    }

    /**
     * Determines if the specified binary droid card is only inactive because it is turned off.
     *
     * @param game the game
     * @param card the binary droid card
     * @return true or false
     */
    public static boolean isOnlyTurnedOff(SwccgGame game, PhysicalCard card) {
        return card.isBinaryOff() && game.getGameState().isCardInPlayActive(card, false, false, false, false, false, false, true, false);
    }

    /**
     * Determines if the specified binary droid card can be turned on.
     *
     * @param game the game
     * @param card the binary droid card
     * @return true or false
     */
    public static boolean canBeTurnedOn(SwccgGame game, PhysicalCard card) {
        return card.isBinaryOff() && !game.getModifiersQuerying().cannotTurnOnBinaryDroid(game.getGameState(), card);
    }


    /**
     * Determines if the specified card is only inactive because it is captured (or attached to a card that is captured).
     *
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean isOnlyCaptured(SwccgGame game, PhysicalCard card) {
        return game.getGameState().isCardInPlayActive(card, false, false, true, false, false, false, false, false);
    }

    /**
     * Determines if the specified card is only inactive because it is concealed (or attached to a card that is concealed).
     *
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean isOnlyConcealed(SwccgGame game, PhysicalCard card) {
        return game.getGameState().isCardInPlayActive(card, false, false, false, true, false, false, false, false);
    }

    /**
     * Determines if the specified persona has 'crossed over'.
     *
     * @param game the game
     * @param persona the persona
     * @return true or false
     */
    public static boolean isCrossedOver(SwccgGame game, Persona persona) {
        return game.getModifiersQuerying().isCrossedOver(game.getGameState(), persona);
    }

    // Checks that it is currently the specified player's turn.
    public static boolean isTurnNumber(SwccgGame game, int turnNumber) {
        return game.getGameState().getPlayersLatestTurnNumber(game.getGameState().getCurrentPlayerId()) == turnNumber;
    }

    public static boolean isReachedPlayersTurnNumber(SwccgGame game, String playerId, int turnNumber) {
        return game.getGameState().getPlayersLatestTurnNumber(playerId) >= turnNumber;
    }


    // Checks that it is currently the specified player's turn.
    public static boolean isDuringYourTurn(SwccgGame game, PhysicalCard card) {
        return game.getGameState().getCurrentPlayerId().equals(card.getOwner());
    }

    // Checks that it is currently the specified player's turn.
    public static boolean isDuringYourTurn(SwccgGame game, String playerId) {
        return game.getGameState().getCurrentPlayerId().equals(playerId);
    }

    // Checks that it is currently the specified player's turn.
    public static boolean isOpponentsTurn(SwccgGame game, PhysicalCard card) {
        return game.getGameState().getCurrentPlayerId().equals(game.getOpponent(card.getOwner()));
    }

    // Checks that it is currently the specified player's turn.
    public static boolean isOpponentsTurn(SwccgGame game, String playerId) {
        return game.getGameState().getCurrentPlayerId().equals(game.getOpponent(playerId));
    }

    // Checks that it is currently the specified phase.
    public static boolean isEitherPlayersPhase(SwccgGame game, Phase phase) {
        return (game.getGameState().getCurrentPhase() == phase);
    }

    // Checks that it is currently the specified specified phase of the specified player's turn.

    /**
     * Checks if during start of game.
     *
     * @param game the game
     * @return true or false
     */
    public static boolean isDuringStartOfGame(SwccgGame game) {
        return (game.getGameState().getCurrentPhase() == Phase.PLAY_STARTING_CARDS);
    }

    /**
     * Checks if it is the specified phase for the player.
     *
     * @param game     the game
     * @param phase    the phase
     * @param playerId the player
     * @return true or false
     */
    public static boolean isPhaseForPlayer(SwccgGame game, Phase phase, String playerId) {
        return (game.getGameState().getCurrentPhase() == phase && game.getGameState().getCurrentPlayerId().equals(playerId));
    }

    /**
     * Determines if it is the specified phase during either player's turn.
     *
     * @param game  the game
     * @param phase the phase
     * @return true or false
     */
    public static boolean isDuringEitherPlayersPhase(SwccgGame game, Phase phase) {
        return (game.getGameState().getCurrentPhase() == phase);
    }

    /**
     * Determines if it is the specified phase during the card owner's turn.
     *
     * @param game  the game
     * @param card  the card
     * @param phase the phase
     * @return true or false
     */
    public static boolean isDuringYourPhase(SwccgGame game, PhysicalCard card, Phase phase) {
        return (game.getGameState().getCurrentPhase() == phase && game.getGameState().getCurrentPlayerId().equals(card.getOwner()));
    }

    /**
     * Determines if it is the specified phase during the specified player's turn.
     *
     * @param game     the game
     * @param playerId the player
     * @param phase    the phase
     * @return true or false
     */
    public static boolean isDuringYourPhase(SwccgGame game, String playerId, Phase phase) {
        return (game.getGameState().getCurrentPhase() == phase && game.getGameState().getCurrentPlayerId().equals(playerId));
    }

    // Checks that it is currently the specified specified phase of the specified player's turn.
    public static boolean isDuringOpponentsPhase(SwccgGame game, PhysicalCard card, Phase phase) {
        return (game.getGameState().getCurrentPhase() == phase && game.getGameState().getCurrentPlayerId().equals(game.getOpponent(card.getOwner())));
    }

    // Checks that it is currently the specified specified phase of the specified player's turn.
    public static boolean isDuringOpponentsPhase(SwccgGame game, String playerId, Phase phase) {
        return (game.getGameState().getCurrentPhase() == phase && game.getGameState().getCurrentPlayerId().equals(game.getOpponent(playerId)));
    }

    /**
     * Determines if a Podrace is in progress.
     *
     * @param game the game
     * @return true or false
     */
    public static boolean isDuringPodrace(SwccgGame game) {
        return game.getGameState().isDuringPodrace();
    }

    /**
     * Determines if a Podrace is in progress that was initiated by the specified card.
     *
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean isDuringPodraceInitiatedByCard(SwccgGame game, PhysicalCard card) {
        return game.getGameState().isDuringPodraceStartedByCard(card);
    }

    /**
     * Determines if current Podrace is finishing.
     *
     * @param game the game
     * @return true or false
     */
    public static boolean isPodraceFinishing(SwccgGame game) {
        return game.getGameState().isPodraceFinishing();
    }

    /**
     * Determines if a Force drain is in progress at a location accepted by the location filter.
     *
     * @param game           the game
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean isDuringForceDrainAt(SwccgGame game, Filterable locationFilter) {
        PhysicalCard forceDrainLocation = game.getGameState().getForceDrainLocation();
        return forceDrainLocation != null &&
                Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), forceDrainLocation);
    }

    /**
     * Determines if an attack is in progress.
     *
     * @param game the game
     * @return true or false
     */
    public static boolean isDuringAttack(SwccgGame game) {
        return game.getGameState().isDuringAttack();
    }

    /**
     * Determines if a battle is in progress.
     *
     * @param game the game
     * @return true or false
     */
    public static boolean isDuringBattle(SwccgGame game) {
        return game.getGameState().isDuringBattle();
    }

    /**
     * Determines if a battle is in progress that was initiated by the specified player.
     *
     * @param game     the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean isDuringBattleInitiatedBy(SwccgGame game, String playerId) {
        BattleState battleState = game.getGameState().getBattleState();
        return battleState != null && battleState.getPlayerInitiatedBattle().equals(playerId);
    }

    /**
     * Determines if a battle is in progress at a location accepted by the location filter.
     *
     * @param game           the game
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean isDuringBattleAt(SwccgGame game, Filterable locationFilter) {
        BattleState battleState = game.getGameState().getBattleState();
        return battleState != null &&
                Filters.and(locationFilter).accepts(game, battleState.getBattleLocation());
    }

    /**
     * Determines if a battle is in progress that has a participant accepted by the participant filter.
     *
     * @param game              the game
     * @param participantFilter the participant filter
     * @return true or false
     */
    public static boolean isDuringBattleWithParticipant(SwccgGame game, Filterable participantFilter) {
        BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null)
            return false;

        Filter filterToUse = Filters.or(participantFilter, Filters.hasPermanentAboard(Filters.and(participantFilter)), Filters.hasPermanentWeapon(Filters.and(participantFilter)));
        return Filters.canSpot(battleState.getAllCardsParticipating(), game, 1, filterToUse);
    }

    /**
     * Determines if an Attack Run is in progress that has a participant accepted by the participant filter.
     *
     * @param game              the game
     * @param participantFilter the participant filter
     * @return true or false
     */
    public static boolean isDuringAttackRunWithParticipant(SwccgGame game, Filterable participantFilter) {
        GameState gameState = game.getGameState();
        if (gameState.isDuringAttackRun()) {
            Filter filterToUse = Filters.or(participantFilter, Filters.hasPermanentAboard(Filters.and(participantFilter)), Filters.hasPermanentWeapon(Filters.and(participantFilter)));
            Filter starfighterFilter = Filters.or(Filters.lead_starfighter_in_Attack_Run, Filters.wingmen_in_Attack_Run);
            return Filters.canSpot(game, null, Filters.and(filterToUse, Filters.or(starfighterFilter, Filters.aboard(starfighterFilter))));
        }
        return false;
    }

    /**
     * Determines if a Bombing Run battle is in progress.
     *
     * @param game the game
     * @return true or false
     */
    public static boolean isDuringBombingRunBattle(SwccgGame game) {
        return game.getGameState().isDuringBombingRunBattle();
    }

    /**
     * Determines if a battle is in progress that was won by the specified player.
     *
     * @param game     the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean isDuringBattleWonBy(SwccgGame game, String playerId) {
        BattleState battleState = game.getGameState().getBattleState();
        return battleState != null && battleState.isWinner(playerId);
    }

    /**
     * Determines if a battle is in progress that was lost by the specified player.
     *
     * @param game     the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean isDuringBattleLostBy(SwccgGame game, String playerId) {
        BattleState battleState = game.getGameState().getBattleState();
        return battleState != null && battleState.isLoser(playerId);
    }

    /**
     * Determines if this is during the second battle initiated this turn by the specified player.
     *
     * @param game     the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean isSecondBattleInitiatedThisTurnBy(SwccgGame game, String playerId) {
        return isDuringBattleInitiatedBy(game, playerId) && game.getModifiersQuerying().getNumBattlesInitiatedThisTurn(playerId) == 2;
    }

    /**
     * Determines if a duel is in progress.
     *
     * @param game the game
     * @return true or false
     */
    public static boolean isDuringDuel(SwccgGame game) {
        return game.getGameState().isDuringDuel();
    }

    /**
     * Determines if a duel is in progress that was initiated by the specified player.
     *
     * @param game     the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean isDuringDuelInitiatedBy(SwccgGame game, String playerId) {
        DuelState duelState = game.getGameState().getDuelState();
        return duelState != null && playerId.equals(duelState.getPlayerInitiatedDuel());
    }

    /**
     * Determines if a duel is in progress at a location accepted by the location filter.
     *
     * @param game           the game
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean isDuringDuelAt(SwccgGame game, Filterable locationFilter) {
        GameState gameState = game.getGameState();
        DuelState duelState = gameState.getDuelState();
        return duelState != null
                && Filters.and(locationFilter).accepts(game, duelState.getLocation());
    }

    /**
     * Determines if a duel is in progress that has a participant accepted by the participant filter.
     *
     * @param game              the game
     * @param participantFilter the participant filter
     * @return true or false
     */
    public static boolean isDuringDuelWithParticipant(SwccgGame game, Filterable participantFilter) {
        GameState gameState = game.getGameState();
        DuelState duelState = gameState.getDuelState();
        Filter filterToUse = Filters.or(participantFilter, Filters.hasPermanentAboard(Filters.and(participantFilter)), Filters.hasPermanentWeapon(Filters.and(participantFilter)));
        return duelState != null
                && Filters.canSpot(duelState.getDuelParticipants(), game, 1, filterToUse);
    }

    /**
     * Determines if a duel is in progress that was won by the specified player.
     *
     * @param game     the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean isDuringDuelWonBy(SwccgGame game, String playerId) {
        DuelState duelState = game.getGameState().getDuelState();
        return duelState != null && playerId.equals(duelState.getWinner());
    }

    /**
     * Gets the total power for the specified player in battle.
     *
     * @param game     the game
     * @param playerId the player
     * @return total power
     */
    public static float getBattlePower(SwccgGame game, String playerId) {
        BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null)
            return 0;

        String preBattleDestinyPlayerIdToUse = battleState.isPreBattleDestinyTotalPowerSwitched() ? game.getOpponent(playerId) : playerId;

        float totalPower = game.getModifiersQuerying().getTotalPowerAtLocation(game.getGameState(), battleState.getBattleLocation(), preBattleDestinyPlayerIdToUse, true, false);

        // Add total destiny to power only
        totalPower += battleState.getTotalDestinyToPowerOnly(preBattleDestinyPlayerIdToUse);

        // Add total battle destiny to power
        totalPower += battleState.getTotalBattleDestiny(game, playerId);

        return Math.max(0, totalPower);
    }


    /**
     * Determines if the player has less power in battle than opponent.
     *
     * @param game     the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean hasLessPowerInBattleThanOpponent(SwccgGame game, String playerId) {
        if (isDuringBattle(game)) {
            return (getBattlePower(game, playerId) < getBattlePower(game, game.getOpponent(playerId)));
        }
        return false;
    }

    /**
     * Determines if both players drew one battle destiny.
     *
     * @param game the game
     * @return true or false
     */
    public static boolean didBothPlayersDrawOneBattleDestiny(SwccgGame game) {
        BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null)
            return false;

        return battleState.getNumBattleDestinyDrawn(game.getDarkPlayer()) == 1 && battleState.getNumBattleDestinyDrawn(game.getLightPlayer()) == 1;
    }

    /**
     * Determines if specified player drew more than the specified number of battle destinies.
     *
     * @param game     the game
     * @param playerId the player
     * @param number   the number of battle destinies
     * @return true or false
     */
    public static boolean didDrawMoreThanBattleDestinies(SwccgGame game, String playerId, int number) {
        BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null)
            return false;

        return battleState.getNumBattleDestinyDrawn(playerId) > number;
    }

    /**
     * Determines if the specified player has a greater battle destiny total than opponent.
     *
     * @param game     the game
     * @param playerId the player
     * @param fromDrawsOnly true if modifications to total destiny are not included, otherwise false
     * @return true or false
     */
    public static boolean hasGreaterBattleDestinyTotal(SwccgGame game, String playerId, boolean fromDrawsOnly) {
        BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null)
            return false;

        if (fromDrawsOnly) {
            return battleState.getTotalBattleDestinyFromDrawsOnly(game, playerId) > battleState.getTotalBattleDestinyFromDrawsOnly(game, game.getOpponent(playerId));
        }
        else {
            return battleState.getTotalBattleDestiny(game, playerId) > battleState.getTotalBattleDestiny(game, game.getOpponent(playerId));
        }
    }


    /**
     * Determines if it is currently the damage segment of a battle.
     *
     * @param game the game
     * @return true or false
     */
    public static boolean isDamageSegmentOfBattle(SwccgGame game) {
        return (game.getGameState().isDuringBattle() && game.getGameState().getBattleState().isReachedDamageSegment());
    }

    /**
     * Determines if there is battle damage remaining against the specified player.
     *
     * @param game     the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean isBattleDamageRemaining(SwccgGame game, String playerId) {
        BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null)
            return false;

        return battleState.getBattleDamageRemaining(game, playerId) > 0;
    }

    /**
     * Determines if there is attrition remaining against the specified player.
     *
     * @param game     the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean isAttritionRemaining(SwccgGame game, String playerId) {
        BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null)
            return false;

        return battleState.getAttritionRemaining(game, playerId) > 0;
    }

    /**
     * Gets the attrition remaining against the specified player.
     *
     * @param game     the game
     * @param playerId the player
     * @return attrition amount
     */
    public static float getAttritionRemaining(SwccgGame game, String playerId) {
        BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null)
            return 0;

        return battleState.getAttritionRemaining(game, playerId);
    }


    /**
     * Determines if a weapon accepted by the weapon filter is being fired at a card accepted by the target filter.
     *
     * @param game         the game
     * @param weaponFilter the weapon filter
     * @param targetFilter the target filter
     * @return true or false
     */
    public static boolean isDuringWeaponFiringAtTarget(SwccgGame game, Filterable weaponFilter, Filterable targetFilter) {
        WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();
        if (weaponFiringState == null)
            return false;

        Collection<PhysicalCard> targets = weaponFiringState.getTargets();
        PhysicalCard weapon = weaponFiringState.getCardFiring();
        SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();

        return Filters.canSpot(targets, game, targetFilter)
                && ((weapon != null && Filters.and(weaponFilter).accepts(game, weapon))
                || (permanentWeapon != null && Filters.and(weaponFilter).accepts(game, permanentWeapon)));
    }

    /**
     * Determines if the specified card is a starting location.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean isStartingLocation(SwccgGame game, PhysicalCard card) {
        return card.isStartingLocation();
    }

    /**
     * Determines if specified player's Rep is accepted by the specified filter.
     * @param game the game
     * @param playerId the player
     * @param repFilter the filter
     * @return true or false
     */
    public static boolean hasRep(SwccgGame game, String playerId, Filterable repFilter) {
        PhysicalCard rep = game.getGameState().getRep(playerId);
        return rep != null && Filters.and(repFilter).accepts(game, rep);
    }

    //
    //
    //  Methods for spotting cards
    //
    //


    // Checks if a card can be spotted that fits the filters.
    public static boolean canSpot(SwccgGame game, PhysicalCard self, int count, boolean useAcceptsCount, Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {

        Map<TargetingReason, Filterable> targetFiltersMapToUse = targetFiltersMap;

        // If source card is targeting cards for specific reasons, then add extra filter for cards that cannot be targeted by the source card for those reasons
        if (!targetFiltersMap.keySet().isEmpty()) {
            targetFiltersMapToUse = new HashMap<TargetingReason, Filterable>();

            for (TargetingReason targetingReason : targetFiltersMap.keySet()) {
                if (targetingReason != TargetingReason.NONE) {
                    Filterable updatedFilter = targetFiltersMap.get(targetingReason);
                    updatedFilter = Filters.and(updatedFilter, Filters.canBeTargetedBy(self, targetingReason));
                    targetFiltersMapToUse.put(targetingReason, updatedFilter);
                }
            }
        }

        return Filters.canSpot(game, self, count, useAcceptsCount, spotOverrides, targetFiltersMapToUse);
    }

    // Checks if a card can be spotted that fits the filters.
    public static boolean canSpot(SwccgGame game, PhysicalCard self, int count, Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        return canSpot(game, self, count, true, spotOverrides, targetFiltersMap);
    }

    public static boolean canSpot(SwccgGame game, PhysicalCard self, boolean useAcceptsCount, Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        return canSpot(game, self, 1, useAcceptsCount, spotOverrides, targetFiltersMap);
    }

    public static boolean canSpot(SwccgGame game, PhysicalCard self, Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        return canSpot(game, self, 1, spotOverrides, targetFiltersMap);
    }

    public static boolean canSpot(SwccgGame game, PhysicalCard self, int count, boolean useAcceptsCount, Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        Map<TargetingReason, Filterable> targetFiltersMap = new HashMap<TargetingReason, Filterable>();
        targetFiltersMap.put(targetingReason, targetFilters);
        return canSpot(game, self, count, useAcceptsCount, spotOverrides, targetFiltersMap);
    }

    public static boolean canSpot(SwccgGame game, PhysicalCard self, int count, Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        return canSpot(game, self, count, true, spotOverrides, targetingReason, targetFilters);
    }

    public static boolean canSpot(SwccgGame game, PhysicalCard self, boolean useAcceptsCount, Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        return canSpot(game, self, 1, useAcceptsCount, spotOverrides, targetingReason, targetFilters);
    }

    public static boolean canSpot(SwccgGame game, PhysicalCard self, Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        return canSpot(game, self, true, spotOverrides, targetingReason, targetFilters);
    }

    public static boolean canSpot(SwccgGame game, PhysicalCard self, int count, boolean useAcceptsCount, Map<InactiveReason, Boolean> spotOverrides, Filterable targetFilters) {
        Map<TargetingReason, Filterable> targetFiltersMap = new HashMap<TargetingReason, Filterable>();
        targetFiltersMap.put(TargetingReason.OTHER, targetFilters);
        return canSpot(game, self, count, useAcceptsCount, spotOverrides, targetFiltersMap);
    }

    public static boolean canSpot(SwccgGame game, PhysicalCard self, int count, Map<InactiveReason, Boolean> spotOverrides, Filterable targetFilters) {
        return canSpot(game, self, count, true, spotOverrides, targetFilters);
    }

    public static boolean canSpot(SwccgGame game, PhysicalCard self, boolean useAcceptsCount, Map<InactiveReason, Boolean> spotOverrides, Filterable targetFilters) {
        return canSpot(game, self, 1, useAcceptsCount, spotOverrides, targetFilters);
    }

    public static boolean canSpot(SwccgGame game, PhysicalCard self, Map<InactiveReason, Boolean> spotOverrides, Filterable targetFilters) {
        return canSpot(game, self, true, spotOverrides, targetFilters);
    }

    public static boolean canSpot(SwccgGame game, PhysicalCard self, int count, boolean useAcceptsCount, Map<TargetingReason, Filterable> targetFiltersMap) {
        return canSpot(game, self, count, useAcceptsCount, null, targetFiltersMap);
    }

    public static boolean canSpot(SwccgGame game, PhysicalCard self, int count, Map<TargetingReason, Filterable> targetFiltersMap) {
        return canSpot(game, self, count, true, targetFiltersMap);
    }

    public static boolean canSpot(SwccgGame game, PhysicalCard self, boolean useAcceptsCount, Map<TargetingReason, Filterable> targetFiltersMap) {
        return canSpot(game, self, 1, useAcceptsCount, targetFiltersMap);
    }

    public static boolean canSpot(SwccgGame game, PhysicalCard self, Map<TargetingReason, Filterable> targetFiltersMap) {
        return canSpot(game, self, true, targetFiltersMap);
    }

    public static boolean canSpot(SwccgGame game, PhysicalCard self, int count, boolean useAcceptsCount, TargetingReason targetingReason, Filterable targetFilters) {
        Map<TargetingReason, Filterable> targetFiltersMap = new HashMap<TargetingReason, Filterable>();
        targetFiltersMap.put(targetingReason, targetFilters);
        return canSpot(game, self, count, useAcceptsCount, null, targetFiltersMap);
    }

    public static boolean canSpot(SwccgGame game, PhysicalCard self, int count, TargetingReason targetingReason, Filterable targetFilters) {
        return canSpot(game, self, count, true, targetingReason, targetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filter. This is used when checking for an action
     * that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param targetFilters the target filter
     * @return true if a card can be spotted, otherwise false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, Filterable targetFilters) {
        return canTarget(game, card, true, targetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filter. This is used when checking for an action
     * that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param count the number of cards to target
     * @param targetFilters the target filter
     * @return true if a card can be spotted, otherwise false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, int count, Filterable targetFilters) {
        return canTarget(game, card, true, targetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filter. This is used when checking for an action
     * that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param targetFilters the target filter
     * @return true if a card can be spotted, otherwise false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, boolean useAcceptsCount, Filterable targetFilters) {
        return canTarget(game, card, useAcceptsCount, TargetingReason.OTHER, targetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filter. This is used when checking for an action
     * that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param count the number of cards to target
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param targetFilters the target filter
     * @return true if a card can be spotted, otherwise false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, int count, boolean useAcceptsCount, Filterable targetFilters) {
        return canTarget(game, card, count, useAcceptsCount, TargetingReason.OTHER, targetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filter. This is used when checking for an action
     * that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetFilters the target filter
     * @return true if a card can be spotted, otherwise false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, Map<InactiveReason, Boolean> spotOverrides, Filterable targetFilters) {
        return canTarget(game, card, true, spotOverrides, targetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filter. This is used when checking for an action
     * that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetFilters the target filter
     * @return true if a card can be spotted, otherwise false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, boolean useAcceptsCount, Map<InactiveReason, Boolean> spotOverrides, Filterable targetFilters) {
        TargetingReason targetingReason = TargetingReason.OTHER;
        Filterable updatedTargetFilters = Filters.and(targetFilters, Filters.canBeTargetedBy(card, targetingReason));
        return Filters.canSpot(game, card, useAcceptsCount, spotOverrides, targetingReason, updatedTargetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filters for the specified reason. This is used when
     * checking for an action that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param targetingReason the reason to target
     * @param targetFilters the target filters
     * @return true or false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, TargetingReason targetingReason, Filterable targetFilters) {
        return canTarget(game, card, true, targetingReason, targetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filters for the specified reason. This is used when
     * checking for an action that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param targetingReason the reason to target
     * @param targetFilters the target filters
     * @return true or false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, boolean useAcceptsCount, TargetingReason targetingReason, Filterable targetFilters) {
        return canTarget(game, card, useAcceptsCount, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filters for the specified reason. This is used when
     * checking for an action that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param count the number of cards to target
     * @param targetingReason the reason to target
     * @param targetFilters the target filters
     * @return true or false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, int count, TargetingReason targetingReason, Filterable targetFilters) {
        return canTarget(game, card, count, true, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filters for the specified reason. This is used when
     * checking for an action that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param count the number of cards to target
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param targetingReason the reason to target
     * @param targetFilters the target filters
     * @return true or false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, int count, boolean useAcceptsCount, TargetingReason targetingReason, Filterable targetFilters) {
        return canTarget(game, card, count, useAcceptsCount, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filters for the specified reason. This is used when
     * checking for an action that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReason the reason to target
     * @param targetFilters the target filters
     * @return true or false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        return canTarget(game, card, true, spotOverrides, targetingReason, targetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filters for the specified reason. This is used when
     * checking for an action that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param targetingReason the reason to target
     * @param targetFilters the target filters
     * @return true or false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, boolean useAcceptsCount, Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        return canTarget(game, card, useAcceptsCount, spotOverrides, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filters for the specified reasons. This is used
     * when checking for an action that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param targetingReasons the reasons to target
     * @param targetFilters the target filters
     * @return true or false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return canTarget(game, card, true, targetingReasons, targetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filters for the specified reasons. This is used
     * when checking for an action that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param targetingReasons the reasons to target
     * @param targetFilters the target filters
     * @return true or false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, boolean useAcceptsCount, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return canTarget(game, card, useAcceptsCount, null, targetingReasons, targetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filters for the specified reasons. This is used
     * when checking for an action that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param count the number of cards to target
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param targetingReasons the reasons to target
     * @param targetFilters the target filters
     * @return true or false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, int count, boolean useAcceptsCount, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return canTarget(game, card, count, useAcceptsCount, null, targetingReasons, targetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filters for the specified reasons. This is used
     * when checking for an action that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReasons the reasons to target
     * @param targetFilters the target filters
     * @return true or false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return canTarget(game, card, true, spotOverrides, targetingReasons, targetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filters for the specified reasons. This is used
     * when checking for an action that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReasons the reasons to target
     * @param targetFilters the target filters
     * @return true or false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, boolean useAcceptsCount, Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return canTarget(game, card, 1, useAcceptsCount, spotOverrides, targetingReasons, targetFilters);
    }

    /**
     * Determines if the card can target a card accepted by the target filters for the specified reasons. This is used
     * when checking for an action that is going to target a card during the action.
     * @param game the game
     * @param card the card
     * @param count the number of cards to target
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReasons the reasons to target
     * @param targetFilters the target filters
     * @return true or false
     */
    public static boolean canTarget(SwccgGame game, PhysicalCard card, int count, boolean useAcceptsCount, Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        Filterable updatedTargetFilters = targetFilters;

        // Add filter for cards that can be targeted by source card for each specified reason.
        for (TargetingReason targetingReason : targetingReasons) {
            if (targetingReason != TargetingReason.NONE) {
                // Use getActionAttachedToCard so card being played is used
                updatedTargetFilters = Filters.and(updatedTargetFilters, Filters.canBeTargetedBy(card, targetingReason));
            }
        }

        return Filters.canSpot(game, card, count, useAcceptsCount, spotOverrides, targetingReasons, updatedTargetFilters);
    }

    /**
     * Determines if the card can target a card on table accepted by the filter to be canceled.
     * @param game the game
     * @param card the card
     * @param filter the filter
     * @return true or false
     */
    public static boolean canTargetToCancel(SwccgGame game, PhysicalCard card, Filterable filter) {
        return canTarget(game, card, TargetingReason.TO_BE_CANCELED, filter);
    }

    /**
     * Determines if the card can target a card on table accepted by the filter to be canceled.
     * @param game the game
     * @param card the card
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param filter the filter
     * @return true or false
     */
    public static boolean canTargetToCancel(SwccgGame game, PhysicalCard card, Map<InactiveReason, Boolean> spotOverrides, Filterable filter) {
        return canTarget(game, card, spotOverrides, TargetingReason.TO_BE_CANCELED, filter);
    }


    public static boolean canSpot(SwccgGame game, PhysicalCard self, int count, Filterable targetFilters) {
        return canSpot(game, self, count, true, targetFilters);
    }

    public static boolean canSpot(SwccgGame game, PhysicalCard self, int count, boolean useAcceptsCount, Filterable targetFilters) {
        Map<TargetingReason, Filterable> targetFiltersMap = new HashMap<TargetingReason, Filterable>();
        targetFiltersMap.put(TargetingReason.OTHER, targetFilters);
        return canSpot(game, self, count, useAcceptsCount, null, targetFiltersMap);
    }

    /**
     * Determines if the card can spot on table a card accepted by the spot filter. This is only used when checking if a
     * card can be spotted. If an action is actually going to target a card, then use the canTarget method instead.
     * @param game the game
     * @param self the self
     * @param spotFilters the spot filter
     * @return true if a card can be spotted, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard self, Filterable spotFilters) {
        return canSpot(game, self, 1, spotFilters);
    }

    /**
     * Determines if the card can spot on table a card accepted by the spot filter. This is only used when checking if a
     * card can be spotted. If an action is actually going to target a card, then use the canTarget method instead.
     * @param game the game
     * @param self the self
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param spotFilters the spot filter
     * @return true if a card can be spotted, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard self, boolean useAcceptsCount, Filterable spotFilters) {
        return canSpot(game, self, 1, useAcceptsCount, spotFilters);
    }

    /**
     * Determines if a location accepted by the spot filter can be spotted on table.
     * @param game the game
     * @param spotFilters the spot filter
     * @return true if a card can be spotted, otherwise false
     */
    public static boolean canSpotLocation(SwccgGame game, Filterable spotFilters) {
        return Filters.canSpotFromTopLocationsOnTable(game, spotFilters);
    }

    /**
     * Determines if at least a specified number of locations accepted by the spot filter can be spotted on table.
     * @param game the game
     * @param count the count
     * @param spotFilters the spot filter
     * @return true if a card can be spotted, otherwise false
     */
    public static boolean canSpotLocation(SwccgGame game, int count, Filterable spotFilters) {
        return Filters.canSpotFromTopLocationsOnTable(game, count, spotFilters);
    }

    /**
     * Determines if a converted location accepted by the spot filter can be spotted on table.
     * @param game the game
     * @param spotFilters the spot filter
     * @return true if a card can be spotted, otherwise false
     */
    public static boolean canSpotConvertedLocation(SwccgGame game, Filterable spotFilters) {
        return Filters.canSpotFromConvertedLocationsOnTable(game, spotFilters);
    }

    /**
     * Determines if a card (or card with a permanent built-in) accepted by the filter is out of play.
     * @param game the game
     * @param filter the filter
     * @return true or false
     */
    public static boolean isOutOfPlay(SwccgGame game, Filter filter) {
        Filter filterToUse = Filters.or(filter, Filters.hasPermanentAboard(filter), Filters.hasPermanentWeapon(filter));
        return !Filters.filterCount(game.getGameState().getAllOutOfPlayCards(), game, 1, filterToUse).isEmpty();
    }

    /**
     * Determines if the card can cancel the card currently being played.
     * @param game the game
     * @param card the card
     * @param effect the effect
     * @return true or false
     */
    public static boolean canCancelCardBeingPlayed(SwccgGame game, PhysicalCard card, Effect effect) {
        if (effect.getType() == Effect.Type.PLAYING_CARD_EFFECT) {
            if (!effect.isCanceled()) {
                RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;
                return Filters.canBeTargetedBy(card, TargetingReason.TO_BE_CANCELED).accepts(game.getGameState(),
                        game.getModifiersQuerying(), respondableEffect.getCard());
            }
        }
        return false;
    }

    /**
     * Determines if the card can cancel the card currently being played.
     * @param game the game
     * @param card the card
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean canCancelRevealedInsertCard(SwccgGame game, PhysicalCard card, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.INSERT_CARD_REVEALED) {
            PhysicalCard insertCard = ((InsertCardRevealedResult) effectResult).getCard();
            if (insertCard.isInsertCardRevealed()) {
                return Filters.canBeTargetedBy(card, TargetingReason.TO_BE_CANCELED).accepts(game, insertCard);
            }
        }
        return false;
    }

    /**
     * Determines if the card can re-target the card currently being played.
     * @param game the game
     * @param card the card
     * @param effect the effect
     * @return true or false
     */
    public static boolean canRetargetCardBeingPlayed(SwccgGame game, PhysicalCard card, Effect effect) {
        if (effect.getType() == Effect.Type.PLAYING_CARD_EFFECT) {
            if (!effect.isCanceled()) {
                RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;
                return Filters.canBeTargetedBy(card).accepts(game, respondableEffect.getCard());
            }
        }
        return false;
    }

    /**
     * Determines if the specified player can use the specified card to steal cards from opponent's Lost Pile.
     * @param game the game
     * @param playerId the player
     * @param card the card
     * @param gameTextActionId the game text action id
     * @return true or false
     */
    public static boolean canStealCardsFromLostPile(SwccgGame game, String playerId, PhysicalCard card, GameTextActionId gameTextActionId) {
        // Check the player is able to use the card action to search the card pile
        if (!canSearchOpponentsLostPile(game, playerId, card, gameTextActionId))
            return false;

        // Check that the source card is allowed to steal cards
        if (game.getModifiersQuerying().cannotSteal(game.getGameState(), card))
            return false;

        return true;
    }

    /**
     * Determines if the specified card to steal cards.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean canSteal(SwccgGame game, PhysicalCard card) {
        // Check that the source card is allowed to steal cards
        if (game.getModifiersQuerying().cannotSteal(game.getGameState(), card))
            return false;

        return true;
    }

    /**
     * Determines if the card is in battle.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean isInBattle(SwccgGame game, PhysicalCard card) {
        return isDuringBattle(game)
                && Filters.participatingInBattle.accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is in battle at a location accepted by the location filter.
     * @param game the game
     * @param card the card
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean isInBattleAt(SwccgGame game, PhysicalCard card, Filterable locationFilter) {
        return isDuringBattleAt(game, locationFilter)
                && Filters.participatingInBattle.accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is in battle with cards accepted by the card filter.
     * @param game the game
     * @param card the card
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean isInBattleWith(SwccgGame game, PhysicalCard card, Filter cardFilter) {
        return Filters.inBattleWith(cardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is in battle with at least a specified number of cards accepted by the card filter.
     * @param game the game
     * @param card the card
     * @param count the number of cards
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean isInBattleWith(SwccgGame game, PhysicalCard card, int count, Filter cardFilter) {
        return new InBattleWithCondition(card, count, cardFilter).isFulfilled(game.getGameState(), game.getModifiersQuerying());
    }

    /**
     * Determines if the card is in battle against cards accepted by the card filter.
     * @param game the game
     * @param card the card
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean isInBattleAgainst(SwccgGame game, PhysicalCard card, Filterable cardFilter) {
        return Filters.inBattleWith(Filters.and(Filters.opponents(card), cardFilter)).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is present with cards accepted by the card filter.
     * @param game the game
     * @param card the card
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean isPresentWith(SwccgGame game, PhysicalCard card, Filterable cardFilter) {
        return Filters.presentWith(card, Filters.and(cardFilter)).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is present with at least a specified number of cards accepted by the card filter.
     * @param game the game
     * @param card the card
     * @param count the number of cards
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean isPresentWith(SwccgGame game, PhysicalCard card, int count, Filterable cardFilter) {
        return Filters.presentWith(card, count, Filters.and(cardFilter)).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is present with cards accepted by the card filter.
     * @param game the game
     * @param source the card performing the query
     * @param card the card
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean isPresentWith(SwccgGame game, PhysicalCard source, PhysicalCard card, Filterable cardFilter) {
        return Filters.presentWith(source, Filters.and(cardFilter)).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is in battle.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean hasParticipatedInBattleThisTurn(SwccgGame game, PhysicalCard card) {
        return game.getModifiersQuerying().hasParticipatedInBattle(card);
    }

    /**
     * Determines if card is escorting any captives.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean isEscortingCaptive(SwccgGame game, PhysicalCard card) {
        return !game.getGameState().getCaptivesOfEscort(card).isEmpty();
    }

    /**
     * Determines if card is escorting any captives accepted by the captive filter.
     * @param game the game
     * @param card the card
     * @param captiveFilter the captive filter
     * @return true or false
     */
    public static boolean isEscortingCaptive(SwccgGame game, PhysicalCard card, Filterable captiveFilter) {
        return Filters.canSpot(game.getGameState().getCaptivesOfEscort(card), game, captiveFilter);
    }

    /**
     * Determines if a card is under 'nighttime' conditions.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean isUnderNighttimeConditions(SwccgGame game, PhysicalCard card) {
        return new UnderNighttimeConditionConditions(card).isFulfilled(game.getGameState(), game.getModifiersQuerying());
    }

    /**
     * Gets number of cards in player's hand.
     * @param game the game
     * @param playerId the player
     * @return the number of cards
     */
    public static int numCardsInHand(SwccgGame game, String playerId) {
        return game.getGameState().getHand(playerId).size();
    }

    /**
     * Determines if the player has any cards in hand accepted by the card filter.
     * @param game the game
     * @param playerId the player
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean hasInHand(SwccgGame game, String playerId, Filter cardFilter) {
        return hasInHand(game, playerId, 1, cardFilter);
    }

    /**
     * Determines if the player has at least a specified number of cards in hand accepted by the card filter.
     * @param game the game
     * @param playerId the player
     * @param count the number of cards
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean hasInHand(SwccgGame game, String playerId, int count, Filter cardFilter) {
        return !Filters.filterCount(game.getGameState().getHand(playerId), game, count, cardFilter).isEmpty();
    }

    /**
     * Determines if the player has any cards in hand accepted by the card filter.
     * @param game the game
     * @param playerId the player
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean hasInHandOrDeployableAsIfFromHand(SwccgGame game, String playerId, Filter cardFilter) {
        if (hasInHand(game, playerId, 1, cardFilter)) {
            return true;
        }
        return !Filters.filterCount(game.getGameState().getAllStackedCards(), game, 1, Filters.and(Filters.owner(playerId), Filters.canDeployAsIfFromHand)).isEmpty();
    }

    /**
     * Determines if the specified card is an Undercover spy.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean isUndercover(SwccgGame game, PhysicalCard card) {
        return card.isUndercover();
    }

    /**
     * Determines if the player has any cards in hand.
     * @param game the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean hasHand(SwccgGame game, String playerId) {
        return !game.getGameState().isZoneEmpty(playerId, Zone.HAND);
    }

    /**
     * Checks if the player can draw destiny.
     * @param game the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean canDrawDestiny(SwccgGame game, String playerId) {
        return hasReserveDeck(game, playerId);
    }

    /**
     * Checks if the player can draw destiny.
     * @param game the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean canDrawRaceDestiny(SwccgGame game, String playerId) {
        return hasReserveDeck(game, playerId) && !game.getModifiersQuerying().mayNotDrawRaceDestiny(game.getGameState(), playerId);
    }

    /**
     * Checks if the player's Reserve Deck is not empty.
     * @param game the game
     * @param playerId the player
     * @return true if player's Reserve Deck is not empty, otherwise false
     */
    public static boolean hasReserveDeck(SwccgGame game, String playerId) {
        return hasCardPile(game, playerId, Zone.RESERVE_DECK);
    }

    /**
     * Determines if the player has any cards in Reserve Deck accepted by the card filter.
     * @param game the game
     * @param playerId the player
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean hasInReserveDeck(SwccgGame game, String playerId, Filter cardFilter) {
        return !Filters.filterCount(game.getGameState().getReserveDeck(playerId), game, 1, cardFilter).isEmpty();
    }

    /**
     * Checks if the player's Force Pile is not empty.
     * @param game the game
     * @param playerId the player
     * @return true if player's Force Pile is not empty, otherwise false
     */
    public static boolean hasForcePile(SwccgGame game, String playerId) {
        return hasCardPile(game, playerId, Zone.FORCE_PILE);
    }

    /**
     * Checks if the player's Used Pile is not empty.
     * @param game the game
     * @param playerId the player
     * @return true if player's Used Pile is not empty, otherwise false
     */
    public static boolean hasUsedPile(SwccgGame game, String playerId) {
        return hasCardPile(game, playerId, Zone.USED_PILE);
    }

    /**
     * Checks if the player's Lost Pile is not empty.
     * @param game the game
     * @param playerId the player
     * @return true if player's Lost Pile is not empty, otherwise false
     */
    public static boolean hasLostPile(SwccgGame game, String playerId) {
        return hasCardPile(game, playerId, Zone.LOST_PILE);
    }

    /**
     * Checks if the player has any cards in the specified card pile.
     * @param game the game
     * @param playerId the player
     * @param cardPile the card pile
     * @return true if any cards are in the card pile, otherwise false
     */
    private static boolean hasCardPile(SwccgGame game, String playerId, Zone cardPile) {
        return !game.getGameState().isZoneEmpty(playerId, cardPile);
    }

    /**
     * Determines if any cards are stacked on the specified card.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean hasStackedCards(SwccgGame game, PhysicalCard card) {
        return !game.getGameState().getStackedCards(card).isEmpty();
    }

    /**
     * Determines if at least a specified number of cards are stacked on the specified card.
     * @param game the game
     * @param card the card
     * @param count the number of cards
     * @return true or false
     */
    public static boolean hasStackedCards(SwccgGame game, PhysicalCard card, int count) {
        return game.getGameState().getStackedCards(card).size() >= count;
    }

    /**
     * Determines if any cards accepted by the filter are stacked on the specified card.
     * @param game the game
     * @param card the card
     * @param filter the filter
     * @return true or false
     */
    public static boolean hasStackedCards(SwccgGame game, PhysicalCard card, Filterable filter) {
        return !Filters.filter(game.getGameState().getStackedCards(card), game, filter).isEmpty();
    }

    /**
     * Checks if the player has a race destiny.
     * @param game the game
     * @param playerId the player
     * @return true if player has a race destiny, otherwise false
     */
    public static boolean hasRaceDestiny(SwccgGame game, String playerId) {
        return Filters.canSpotFromStacked(game, Filters.raceDestinyForPlayer(playerId));
    }

    /**
     * Checks if the top card of player's Lost Pile is accepted by the filter.
     * @param game the game
     * @param playerId the player
     * @param filter the filter
     * @return true or false
     */
    public static boolean isTopCardOfLostPileMatchTo(SwccgGame game, String playerId, Filterable filter) {
        if (game.getGameState().isCardPileFaceUp(playerId, Zone.LOST_PILE)) {
            PhysicalCard topCard = game.getGameState().getTopOfLostPile(playerId);
            return topCard != null
                    && Filters.and(filter).accepts(game, topCard);
        }
        return false;
    }

    /**
     * Determines if the player's Lost Pile is turned over.
     * @param game the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean isLostPileTurnedOver(SwccgGame game, String playerId) {
        return game.getGameState().isLostPileTurnedOver(playerId);
    }

    // Checks if player can deploy cards from Reserve Deck.

    /**
     * Checks if the player can deploy a card from Reserve Deck.
     * @param game the game
     * @param playerId the player
     * @param self the self
     * @param gameTextActionId the identifier for the card's specific action to perform the search
     * @return true or false
     */
    public static boolean canDeployCardFromReserveDeck(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId) {
        return canDeployCardFromCardPile(game, playerId, self, Zone.RESERVE_DECK, gameTextActionId, false, false, Collections.<Persona>emptySet(), Collections.<String>emptyList());
    }

    /**
     * Checks if the player can deploy a card from Reserve Deck.
     * @param game the game
     * @param playerId the player
     * @param self the self
     * @param gameTextActionId the identifier for the card's specific action to perform the search
     * @param asReact true if as a 'react', otherwise false
     * @return true or false
     */
    public static boolean canDeployCardFromReserveDeck(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId, boolean asReact) {
        return canDeployCardFromCardPile(game, playerId, self, Zone.RESERVE_DECK, gameTextActionId, asReact, asReact, Collections.<Persona>emptySet(), Collections.<String>emptyList());
    }

    /**
     * Checks if the player can deploy a card from Reserve Deck.
     * @param game the game
     * @param playerId the player
     * @param self the self
     * @param gameTextActionId the identifier for the card's specific action to perform the search
     * @param skipDeployPhaseCheck true if checking it is the player's deploy phase is skipped, otherwise false
     * @param asReact true if as a 'react', otherwise false
     * @return true or false
     */
    public static boolean canDeployCardFromReserveDeck(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId, boolean skipDeployPhaseCheck, boolean asReact) {
        return canDeployCardFromCardPile(game, playerId, self, Zone.RESERVE_DECK, gameTextActionId, skipDeployPhaseCheck, asReact, Collections.<Persona>emptySet(), Collections.<String>emptyList());
    }

    /**
     * Checks if the player can deploy a card from Reserve Deck.
     * @param game the game
     * @param playerId the player
     * @param self the self
     * @param gameTextActionId the identifier for the card's specific action to perform the search
     * @param persona persona that can be chosen to deploy that are identified by persona
     * @return true or false
     */
    public static boolean canDeployCardFromReserveDeck(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId, Persona persona) {
        return canDeployCardFromCardPile(game, playerId, self, Zone.RESERVE_DECK, gameTextActionId, false, false, Collections.singleton(persona), Collections.<String>emptyList());
    }

    /**
     * Checks if the player can deploy a card from Reserve Deck.
     * @param game the game
     * @param playerId the player
     * @param self the self
     * @param gameTextActionId the identifier for the card's specific action to perform the search
     * @param asReact true if as a 'react', otherwise false
     * @param persona persona that can be chosen to deploy that are identified by persona
     * @return true or false
     */
    public static boolean canDeployCardFromReserveDeck(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId, boolean asReact, Persona persona) {
        return canDeployCardFromCardPile(game, playerId, self, Zone.RESERVE_DECK, gameTextActionId, false, asReact, Collections.singleton(persona), Collections.<String>emptyList());
    }

    /**
     * Checks if the player can deploy a card from Reserve Deck.
     * @param game the game
     * @param playerId the player
     * @param self the self
     * @param gameTextActionId the identifier for the card's specific action to perform the search
     * @param title card that can be chosen to deploy that is identified by title
     * @return true or false
     */
    public static boolean canDeployCardFromReserveDeck(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId, String title) {
        return canDeployCardFromCardPile(game, playerId, self, Zone.RESERVE_DECK, gameTextActionId, false, false, Collections.<Persona>emptySet(), Collections.singletonList(title));
    }

    /**
     * Checks if the player can deploy a card from Reserve Deck.
     * @param game the game
     * @param playerId the player
     * @param self the self
     * @param gameTextActionId the identifier for the card's specific action to perform the search
     * @param title card that can be chosen to deploy that is identified by title
     * @return true or false
     */
    public static boolean canDeployCardFromReserveDeck(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId, String title, boolean skipDeployPhaseCheck) {
        return canDeployCardFromCardPile(game, playerId, self, Zone.RESERVE_DECK, gameTextActionId, skipDeployPhaseCheck, false, Collections.<Persona>emptySet(), Collections.singletonList(title));
    }

    /**
     * Checks if the player can deploy a card from Reserve Deck.
     * @param game the game
     * @param playerId the player
     * @param self the self
     * @param gameTextActionId the identifier for the card's specific action to perform the search
     * @param personas personas that can be chosen to deploy that are identified by persona
     * @return true or false
     */
    public static boolean canDeployCardFromReserveDeck(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId, Set<Persona> personas) {
        return canDeployCardFromCardPile(game, playerId, self, Zone.RESERVE_DECK, gameTextActionId, false, false, personas, Collections.<String>emptyList());
    }

    /**
     * Checks if the player can deploy a card from Reserve Deck.
     * @param game the game
     * @param playerId the player
     * @param self the self
     * @param gameTextActionId the identifier for the card's specific action to perform the search
     * @param titles cards that can be chosen to deploy that are identified by title
     * @return true or false
     */
    public static boolean canDeployCardFromReserveDeck(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId, List<String> titles) {
        return canDeployCardFromCardPile(game, playerId, self, Zone.RESERVE_DECK, gameTextActionId, false, false, Collections.<Persona>emptySet(), titles);
    }

    /**
     * Checks if the player can deploy a card from Reserve Deck.
     * @param game the game
     * @param playerId the player
     * @param self the self
     * @param gameTextActionId the identifier for the card's specific action to perform the search
     * @param persona persona that can be chosen to deploy that are identified by persona
     * @param titles cards that can be chosen to deploy that are identified by title
     * @return true or false
     */
    public static boolean canDeployCardFromReserveDeck(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId, Persona persona, List<String> titles) {
        return canDeployCardFromCardPile(game, playerId, self, Zone.RESERVE_DECK, gameTextActionId, false, false, Collections.singleton(persona), titles);
    }

    /**
     * Checks if the player can deploy a card from Reserve Deck.
     * @param game the game
     * @param playerId the player
     * @param self the self
     * @param gameTextActionId the identifier for the card's specific action to perform the search
     * @param personas personas that can be chosen to deploy that are identified by persona.
     * @param title card that can be chosen to deploy that is identified by title
     * @return true or false
     */
    public static boolean canDeployCardFromReserveDeck(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId, Set<Persona> personas, String title) {
        return canDeployCardFromCardPile(game, playerId, self, Zone.RESERVE_DECK, gameTextActionId, false, false, personas, Collections.singletonList(title));
    }

    /**
     * Checks if the player can deploy a card from Reserve Deck.
     * @param game the game
     * @param playerId the player
     * @param self the self
     * @param gameTextActionId the identifier for the card's specific action to perform the search
     * @param personas personas that can be chosen to deploy that are identified by persona.
     * @param titles cards that can be chosen to deploy that are identified by title
     * @return true or false
     */
    public static boolean canDeployCardFromReserveDeck(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId, Set<Persona> personas, List<String> titles) {
        return canDeployCardFromCardPile(game, playerId, self, Zone.RESERVE_DECK, gameTextActionId, false, false, personas, titles);
    }

    /**
     * Checks if the player can deploy a card from Lost Pile.
     * @param game the game
     * @param playerId the player
     * @param self the self
     * @param gameTextActionId the identifier for the card's specific action to perform the search
     * @return true or false
     */
    public static boolean canDeployCardFromLostPile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId) {
        return canDeployCardFromLostPile(game, playerId, self, gameTextActionId, false);
    }

    /**
     * Checks if the player can deploy a card from Lost Pile.
     * @param game the game
     * @param playerId the player
     * @param self the self
     * @param gameTextActionId the identifier for the card's specific action to perform the search
     * @param skipDeployPhaseCheck true if checking it is the player's deploy phase is skipped, otherwise false
     * @return true or false
     */
    public static boolean canDeployCardFromLostPile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId, boolean skipDeployPhaseCheck) {
        return canDeployCardFromCardPile(game, playerId, self, Zone.LOST_PILE, gameTextActionId, skipDeployPhaseCheck, false, Collections.<Persona>emptySet(), Collections.<String>emptyList());
    }

    /**
     * Checks if the player can deploy a card from Lost Pile.
     * @param game the game
     * @param playerId the player
     * @param self the self
     * @param gameTextActionId the identifier for the card's specific action to perform the search
     * @param persona persona that can be chosen to deploy that are identified by persona
     * @return true or false
     */
    public static boolean canDeployCardFromLostPile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId, Persona persona) {
        return canDeployCardFromLostPile(game, playerId, self, gameTextActionId, false, persona);
    }

    /**
     * Checks if the player can deploy a card from Lost Pile.
     * @param game the game
     * @param playerId the player
     * @param self the self
     * @param gameTextActionId the identifier for the card's specific action to perform the search
     * @param skipDeployPhaseCheck true if checking it is the player's deploy phase is skipped, otherwise false
     * @param persona persona that can be chosen to deploy that are identified by persona
     * @return true or false
     */
    public static boolean canDeployCardFromLostPile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId, boolean skipDeployPhaseCheck, Persona persona) {
        return canDeployCardFromCardPile(game, playerId, self, Zone.LOST_PILE, gameTextActionId, skipDeployPhaseCheck, false, Collections.singleton(persona), Collections.<String>emptyList());
    }

    /**
     * Checks if the player can deploy a card from the specified card pile.
     * @param game the game
     * @param playerId the player
     * @param self the self
     * @param cardPile the card pile
     * @param gameTextActionId the identifier for the card's specific action to perform the search
     * @param skipDeployPhaseCheck true if checking it is the player's deploy phase is skipped, otherwise false
     * @param asReact true if as a 'react', otherwise false
     * @param personas personas that can be chosen to deploy that are identified by persona
     * @param titles cards that can be chosen to deploy that are identified by title
     * @return true or false
     */
    private static boolean canDeployCardFromCardPile(SwccgGame game, String playerId, PhysicalCard self, Zone cardPile, GameTextActionId gameTextActionId, boolean skipDeployPhaseCheck, boolean asReact, Set<Persona> personas, List<String> titles) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Check is the the player's deploy phase
        if (!asReact && !skipDeployPhaseCheck && !isPhaseForPlayer(game, Phase.DEPLOY, playerId))
            return false;

        // Check the player is able to use the card action to search the card pile
        if (!canSearchCardPile(game, playerId, cardPile, playerId, self, gameTextActionId, false))
            return false;

        // Check if source card is prohibited from allowing player to download cards
        if (modifiersQuerying.isProhibitedFromAllowingPlayerToDownloadCards(gameState, self, playerId))
            return false;

        // If as a 'react', check that the player can deploy cards as a 'react'
        if (asReact && modifiersQuerying.isProhibitedFromDeployingAsReact(gameState, playerId))
            return false;

        // Check for limit reached for any personas/titles to deploy
        if (!personas.isEmpty() || !titles.isEmpty()) {

            List<PhysicalCard> outOfPlay = gameState.getAllOutOfPlayCards();

            for (Persona persona : personas) {
                // Check if persona is out of play
                if (Filters.canSpot(outOfPlay, game, 1, persona))
                    continue;

                // Check if persona is in play (and cannot be converted by player)
                if (Filters.canSpotForUniquenessChecking(game, Filters.and(persona, Filters.not(Filters.canBeConvertedByDeployment(playerId)))))
                    continue;

                return true;
            }

            for (String title : titles) {

                // Check if card title is out of play (only if unique character, starship, or vehicle)
                if (Filters.canSpot(outOfPlay, game, 1, Filters.and(Filters.unique, Filters.title(title),
                        Filters.or(Filters.character, Filters.starship, Filters.vehicle))))
                    continue;

                // Find cards in play with same title (and cannot be converted by player)
                Collection<PhysicalCard> cardsInPlay = Filters.filterForUniquenessChecking(game, Filters.and(Filters.title(title),
                        Filters.not(Filters.or(Filters.non_unique, Filters.perSystemUniqueness, Filters.collapsed, Filters.canBeConvertedByDeployment(playerId)))));
                if (!cardsInPlay.isEmpty()) {
                    Uniqueness uniqueness = modifiersQuerying.getUniqueness(gameState, cardsInPlay.iterator().next());

                    // Check number in play compared to uniqueness
                    if (uniqueness != null && !uniqueness.isPerSystem() && uniqueness.getValue() <= cardsInPlay.size())
                        continue;
                }

                return true;
            }

            return false;
        }

        return true;
    }

    /**
     * Checks if the player can search own Research deck to take cards into hand with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @return true if card pile is allowed to be searched to take cards into hand, otherwise false
     */
    public static boolean canTakeCardsIntoHandFromReserveDeck(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId) {
        return canSearchReserveDeck(game, playerId, self, gameTextActionId);
    }

    /**
     * Checks if the player can search own Force Pile to take cards into hand with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @return true if card pile is allowed to be searched to take cards into hand, otherwise false
     */
    public static boolean canTakeCardsIntoHandFromForcePile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId) {
        return canSearchForcePile(game, playerId, self, gameTextActionId);
    }

    /**
     * Checks if the player can search own Used Pile to take cards into hand with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @return true if card pile is allowed to be searched to take cards into hand, otherwise false
     */
    public static boolean canTakeCardsIntoHandFromUsedPile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId) {
        return canSearchUsedPile(game, playerId, self, gameTextActionId);
    }

    /**
     * Checks if the player can search own Used Pile to take cards into hand with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @param skipCardPileExistsCheck true if card pile does not need to exist at the time, otherwise false
     * @return true if card pile is allowed to be searched to take cards into hand, otherwise false
     */
    public static boolean canTakeCardsIntoHandFromUsedPile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId, boolean skipCardPileExistsCheck) {
        return canSearchUsedPile(game, playerId, self, gameTextActionId, skipCardPileExistsCheck);
    }

    /**
     * Checks if the player can search own Lost Pile to take cards into hand with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @return true if card pile is allowed to be searched to take cards into hand, otherwise false
     */
    public static boolean canTakeCardsIntoHandFromLostPile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId) {
        return canSearchLostPile(game, playerId, self, gameTextActionId);
    }

    /**
     * Checks if the player can search opponent's Lost Pile to take cards into hand with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @return true if card pile is allowed to be searched to take cards into hand, otherwise false
     */
    public static boolean canTakeCardsIntoHandFromOpponentsLostPile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId) {
        return canSearchOpponentsLostPile(game, playerId, self, gameTextActionId);
    }

    /**
     * Checks if the player can search own Reserve Deck with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @return true if card pile is allowed to be searched, otherwise false
     */
    public static boolean canSearchReserveDeck(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId) {
        return canSearchCardPile(game, playerId, Zone.RESERVE_DECK, playerId, self, gameTextActionId, false);
    }

    /**
     * Checks if the player can search own Reserve Deck to play an Interrupt with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @return true if card pile is allowed to be searched to play an Interrupt, otherwise false
     */
    public static boolean canPlayInterruptFromReserveDeck(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId) {
        return canSearchReserveDeck(game, playerId, self, gameTextActionId);
    }

    /**
     * Checks if the player can search own Lost Pile to play an Interrupt with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @return true if card pile is allowed to be searched to play an Interrupt, otherwise false
     */
    public static boolean canPlayInterruptFromLostPile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId) {
        return canSearchLostPile(game, playerId, self, gameTextActionId);
    }

    /**
     * Checks if the player can search own Lost Pile to play an Interrupt as a response to the specified effect with the
     * specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param effect the effect
     * @param gameTextActionId the game text action id
     * @return true if card pile is allowed to be searched to play an Interrupt, otherwise false
     */
    public static boolean canPlayInterruptAsResponseFromLostPile(SwccgGame game, String playerId, PhysicalCard self, Effect effect, GameTextActionId gameTextActionId) {
        if (!canSearchLostPile(game, playerId, self, gameTextActionId)) {
            return false;
        }
        Collection<PhysicalCard> lostPile = game.getGameState().getLostPile(playerId);
        for (PhysicalCard cardInLostPile : lostPile) {
            if (Filters.playableInterruptAsResponse(self, effect).accepts(game, cardInLostPile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the player can search own Lost Pile to play an Interrupt with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param effectResult the effect result
     * @param gameTextActionId the game text action id
     * @return true if card pile is allowed to be searched to play an Interrupt, otherwise false
     */
    public static boolean canPlayInterruptAsResponseFromLostPile(SwccgGame game, String playerId, PhysicalCard self, EffectResult effectResult, GameTextActionId gameTextActionId) {
        if (!canSearchLostPile(game, playerId, self, gameTextActionId)) {
            return false;
        }
        Collection<PhysicalCard> lostPile = game.getGameState().getLostPile(playerId);
        for (PhysicalCard cardInLostPile : lostPile) {
            if (Filters.playableInterruptAsResponse(self, effectResult).accepts(game, cardInLostPile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the player can search own Force Pile with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @return true if card pile is allowed to be searched, otherwise false
     */
    public static boolean canSearchForcePile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId) {
        return canSearchCardPile(game, playerId, Zone.FORCE_PILE, playerId, self, gameTextActionId, false);
    }

    /**
     * Checks if the player can search own Used Pile with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @return true if card pile is allowed to be searched, otherwise false
     */
    public static boolean canSearchUsedPile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId) {
        return canSearchCardPile(game, playerId, Zone.USED_PILE, playerId, self, gameTextActionId, false);
    }

    /**
     * Checks if the player can search own Used Pile with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @param skipCardPileExistsCheck true if card pile does not need to exist at the time, otherwise false
     * @return true if card pile is allowed to be searched, otherwise false
     */
    private static boolean canSearchUsedPile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId, boolean skipCardPileExistsCheck) {
        return canSearchCardPile(game, playerId, Zone.USED_PILE, playerId, self, gameTextActionId, skipCardPileExistsCheck);
    }

    /**
     * Checks if the player can search own Lost Pile with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @return true if card pile is allowed to be searched, otherwise false
     */
    public static boolean canSearchLostPile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId) {
        return canSearchCardPile(game, playerId, Zone.LOST_PILE, playerId, self, gameTextActionId, false);
    }

    /**
     * Checks if the player can search own Lost Pile with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @param skipCardPileExistsCheck true if card pile does not need to exist at the time, otherwise false
     * @return true if card pile is allowed to be searched, otherwise false
     */
    public static boolean canSearchLostPile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId, boolean skipCardPileExistsCheck) {
        return canSearchCardPile(game, playerId, Zone.LOST_PILE, playerId, self, gameTextActionId, skipCardPileExistsCheck);
    }

    /**
     * Checks if the player can search opponent's Reserve Deck with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @return true if card pile is allowed to be searched, otherwise false
     */
    public static boolean canSearchOpponentsReserveDeck(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId) {
        return canSearchCardPile(game, playerId, Zone.RESERVE_DECK, game.getOpponent(playerId), self, gameTextActionId, false);
    }

    /**
     * Checks if the player can search opponent's Force Pile with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @return true if card pile is allowed to be searched, otherwise false
     */
    public static boolean canSearchOpponentsForcePile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId) {
        return canSearchCardPile(game, playerId, Zone.FORCE_PILE, game.getOpponent(playerId), self, gameTextActionId, false);
    }

    /**
     * Checks if the player can search opponent's Used Pile with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @return true if card pile is allowed to be searched, otherwise false
     */
    public static boolean canSearchOpponentsUsedPile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId) {
        return canSearchCardPile(game, playerId, Zone.USED_PILE, game.getOpponent(playerId), self, gameTextActionId, false);
    }

    /**
     * Checks if the player can search opponent's Lost Pile with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param self the card
     * @param gameTextActionId the game text action id
     * @return true if card pile is allowed to be searched, otherwise false
     */
    public static boolean canSearchOpponentsLostPile(SwccgGame game, String playerId, PhysicalCard self, GameTextActionId gameTextActionId) {
        return canSearchCardPile(game, playerId, Zone.LOST_PILE, game.getOpponent(playerId), self, gameTextActionId, false);
    }

    /**
     * Checks if the player can search the specified card pile with the specified game text action on the specified card.
     * @param game the game
     * @param playerId the player
     * @param cardPile the card pile
     * @param cardPileOwner the card pile owner
     * @param self the card
     * @param gameTextActionId the game text action id
     * @param skipCardPileExistsCheck true if card pile does not need to exist at the time, otherwise false
     * @return true if card pile is allowed to be searched, otherwise false
     */
    private static boolean canSearchCardPile(SwccgGame game, String playerId, Zone cardPile, String cardPileOwner, PhysicalCard self, GameTextActionId gameTextActionId, boolean skipCardPileExistsCheck) {
        if (!gameTextActionId.isSearchCardPile())
            throw new UnsupportedOperationException(gameTextActionId + " is not a search card pile action");

        if (!skipCardPileExistsCheck && !hasCardPile(game, cardPileOwner, cardPile))
            return false;

        // Check if the search action is not allowed
        return !game.getModifiersQuerying().isSearchingCardPileProhibited(game.getGameState(), self, playerId,
                cardPile, cardPileOwner, gameTextActionId);
    }

    /**
     * Determines if the a card accepted by the filter is "here" (from perspective of the specified card).
     * @param game the game
     * @param card the card
     * @param filter the filter
     * @return true or false
     */
    public static boolean isHere(SwccgGame game, PhysicalCard card, Filterable filter) {
        return Filters.canSpot(game, card, Filters.and(filter, Filters.here(card)));
    }

    /**
     * Determines if the card 'hit'.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean isHit(SwccgGame game, PhysicalCard card) {
        return card.isHit();
    }

    // Checks if a card can be spotted that fits the filters.

    /**
     * Determines if the card is alone.
     * @param game the game
     * @param card the card
=     * @return true or false
     */
    public static boolean isAlone(SwccgGame game, PhysicalCard card) {
        return Filters.alone.accepts(game, card);
    }

    /**
     * Determines if the card has a card accepted by the filter attached to it.
     * @param game the game
     * @param card the card
     * @param filter the filter
     * @return true or false
     */
    public static boolean hasAttached(SwccgGame game, PhysicalCard card, Filter filter) {
        return Filters.hasAttached(filter).accepts(game, card);
    }

    /**
     * Determines if the card is attached to a card accepted by the filter.
     * @param game the game
     * @param card the card
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAttachedTo(SwccgGame game, PhysicalCard card, Filter filter) {
        return Filters.attachedTo(filter).accepts(game, card);
    }

    /**
     * Determines if the card is "present" at the location it is "at".
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean isPresent(SwccgGame game, PhysicalCard card) {
        return Filters.present(card).accepts(game, card);
    }

    /**
     * Determines if the card is "present" at locations accepted by the location filter.
     * @param game the game
     * @param card the card
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean isPresentAt(SwccgGame game, PhysicalCard card, Filter locationFilter) {
        return Filters.presentAt(locationFilter).accepts(game, card);
    }

    /**
     * Determines if the card is "at" a location accepted by the location filter.
     * @param game the game
     * @param card the card
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean isAtLocation(SwccgGame game, PhysicalCard card, Filter locationFilter) {
        return Filters.at(locationFilter).accepts(game, card);
    }

    /**
     * Determines if the card occupies a location accepted by the location filter.
     * @param game the game
     * @param card the card
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean occupiesLocation(SwccgGame game, PhysicalCard card, Filter locationFilter) {
        return !Filters.undercover_spy.accepts(game.getGameState(), game.getModifiersQuerying(), card)
                && Filters.at(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card controls a location accepted by the location filter.
     * @param game the game
     * @param card the card
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean controlsLocation(SwccgGame game, PhysicalCard card, Filter locationFilter) {
        return !Filters.undercover_spy.accepts(game.getGameState(), game.getModifiersQuerying(), card)
                && Filters.at(Filters.and(locationFilter, Filters.controls(card.getOwner()))).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is on Cloud City.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean isOnCloudCity(SwccgGame game, PhysicalCard card) {
        return Filters.on_Cloud_City.accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if a card accepted by the filter is on Cloud City.
     * @param game the game
     * @param card the card
     * @param filter the filter
     * @return true or false
     */
    public static boolean isOnCloudCity(SwccgGame game, PhysicalCard card, Filter filter) {
        return isOnCloudCity(game, card, null, filter);
    }

    /**
     * Determines if a card accepted by the filter is on Cloud City.
     * @param game the game
     * @param card the card
     * @param filter the filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @return true or false
     */
    public static boolean isOnCloudCity(SwccgGame game, PhysicalCard card, Map<InactiveReason, Boolean> spotOverrides, Filter filter) {
        return new OnCloudCityCondition(card, spotOverrides, filter).isFulfilled(game.getGameState(), game.getModifiersQuerying());
    }

    /**
     * Determines if the card is "at" the specified system.
     * @param game the game
     * @param card the card
     * @param system the system
     * @return true or false
     */
    public static boolean isAtSystem(SwccgGame game, PhysicalCard card, String system) {
        return Filters.at(system).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is "on" the specified system.
     * @param game the game
     * @param card the card
     * @param system the system
     * @return true or false
     */
    public static boolean isOnSystem(SwccgGame game, PhysicalCard card, String system) {
        return Filters.on(system).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is "aboard" any starship accepted by the filter.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean isAboardAnyStarship(SwccgGame game, PhysicalCard card) {
        return Filters.aboardAnyStarship.accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is "aboard" a starship or vehicle accepted by the filter.
     * @param game the game
     * @param card the card
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAboard(SwccgGame game, PhysicalCard card, Filter filter) {
        return Filters.aboard(filter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is "aboard" the starship or vehicle of the specified persona, including any related starship
     * or vehicle sites.
     * @param game the game
     * @param card the card
     * @param persona a starship or vehicle persona
     * @return true or false
     */
    public static boolean isAboard(SwccgGame game, PhysicalCard card, Persona persona) {
        return Filters.aboardStarshipOrVehicleOfPersona(persona).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is landed "aboard" the starship or vehicle of the specified persona at related starship or
     * vehicle site.
     * @param game the game
     * @param card the card
     * @param persona a starship or vehicle persona
     * @return true or false
     */
    public static boolean isLandedAboard(SwccgGame game, PhysicalCard card, Persona persona) {
        return card.getAtLocation() != null
                && Filters.siteOfStarshipOrVehicle(persona, false).accepts(game, card.getAtLocation());
    }

    /**
     * Determines if the starship or vehicle has a card "aboard" that is accepted by the filter.
     * @param game the game
     * @param card the card
     * @param filter the filter
     * @return true or false
     */
    public static boolean hasAboard(SwccgGame game, PhysicalCard card, Filter filter) {
        return Filters.hasAboard(card, filter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is "with" a card accepted by the filter.
     * @param game the game
     * @param source the card performing the query
     * @param card the card
     * @param filter the filter
     * @return true or false
     */
    public static boolean isWith(SwccgGame game, PhysicalCard source, PhysicalCard card, Filterable filter) {
        return Filters.with(source, Filters.and(filter)).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is "with" a card accepted by the filter.
     * @param game the game
     * @param card the card
     * @param filter the filter
     * @return true or false
     */
    public static boolean isWith(SwccgGame game, PhysicalCard card, Filterable filter) {
        return isWith(game, card, 1, filter);
    }

    /**
     * Determines if the card is "with" at least a specified number of cards accepted by the filter.
     * @param game the game
     * @param card the card
     * @param count the number of cards
     * @param filter the filter
     * @return true or false
     */
    public static boolean isWith(SwccgGame game, PhysicalCard card, int count, Filterable filter) {
        return new WithCondition(card, count, filter).isFulfilled(game.getGameState(), game.getModifiersQuerying());
    }

    /**
     * Determines if the card is "with" a card accepted by the filter.
     * @param game the game
     * @param card the card
     * @param filter the filter
     * @return true or false
     */
    public static boolean isWith(SwccgGame game, PhysicalCard card, Map<InactiveReason, Boolean> spotOverrides, Filterable filter) {
        return isWith(game, card, 1, spotOverrides, filter);
    }

    /**
     * Determines if the card is "with" at least a specified number of cards accepted by the filter.
     * @param game the game
     * @param card the card
     * @param count the number of cards
     * @param filter the filter
     * @return true or false
     */
    public static boolean isWith(SwccgGame game, PhysicalCard card, int count, Map<InactiveReason, Boolean> spotOverrides, Filterable filter) {
        return new WithCondition(card, count, spotOverrides, filter).isFulfilled(game.getGameState(), game.getModifiersQuerying());
    }

    // Checks if a card can be spotted that fits the filters.

    /**
     * Determines if the card is being piloted.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean isPiloted(SwccgGame game, PhysicalCard card) {
        return !Filters.transport_vehicle.accepts(game, card)
                && game.getModifiersQuerying().isPiloted(game.getGameState(), card, false);
    }

    /**
     * Determines if the card is piloting a card accepted by the filter.
     * @param game the game
     * @param card the card
     * @param filter the filter
     * @return true or false
     */
    public static boolean isPiloting(SwccgGame game, PhysicalCard card, Filterable filter) {
        return Filters.piloting(Filters.and(filter)).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is piloting at a location accepted by the location filter.
     * @param game the game
     * @param card the card
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean isPilotingAt(SwccgGame game, PhysicalCard card, Filterable locationFilter) {
        return Filters.and(Filters.piloting(Filters.any), Filters.at(Filters.and(locationFilter))).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is piloted by a card accepted by the pilot filter.
     * @param game the game
     * @param card the card
     * @param pilotFilter the pilot filter
     * @return true or false
     */
    public static boolean hasPiloting(SwccgGame game, PhysicalCard card, Filterable pilotFilter) {
        return Filters.hasPiloting(card, Filters.and(pilotFilter)).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is being driven.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean isDriven(SwccgGame game, PhysicalCard card) {
        return Filters.transport_vehicle.accepts(game, card)
                && game.getModifiersQuerying().isPiloted(game.getGameState(), card, false);
    }

    /**
     * Determines if the specified card is at a Scomp link.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean isAtScompLink(SwccgGame game, PhysicalCard card) {
        return Filters.at_Scomp_Link.accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    /**
     * Determines if the card is armed with a weapon accepted by the filter.
     * @param game the game
     * @param card the card
     * @param weaponFilter the filter
     * @return true or false
     */
    public static boolean isArmedWith(SwccgGame game, PhysicalCard card, Filter weaponFilter) {
        return Filters.armedWith(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    // Checks if a card has its "while in play" data set.
    public static boolean cardHasWhileInPlayDataSet(PhysicalCard card) {
        return card.getWhileInPlayData() != null;
    }

    // Checks if a card has its "while in play" data equal to the specified boolean value.
    public static boolean cardHasWhileInPlayDataEquals(PhysicalCard card, boolean value) {
        return card.getWhileInPlayData() != null && card.getWhileInPlayData().getBooleanValue() == value;
    }

    // Checks if a card has its "while in play" data equal to the specified text value.
    public static boolean cardHasWhileInPlayDataEquals(PhysicalCard card, String value) {
        return card.getWhileInPlayData() != null && value.equals(card.getWhileInPlayData().getTextValue());
    }

    // Checks if a card has any "for remainder of game" data set.
    public static boolean cardHasAnyForRemainderOfGameDataSet(PhysicalCard card) {
        return !card.getForRemainderOfGameData().isEmpty();
    }

    // Checks if a card has any "for remainder of game" data set.
    public static boolean cardHasForRemainderOfGameDataEquals(PhysicalCard card, int cardId, boolean value) {
        ForRemainderOfGameData curValue = card.getForRemainderOfGameData().get(cardId);
        return curValue != null && curValue.getBooleanValue() == value;
    }

    // Checks if the total amount Force drained this turn was at least X.
    public static boolean wasDrainedThisTurnAtLeastForce(SwccgGame game, int amount) {
        return game.getModifiersQuerying().getTotalForceDrainedThisTurn() >= amount;
    }

    // Checks if the player has less than X Life Force remaining.
    public static boolean hasLifeForceLessThan(SwccgGame game, String playerId, int amount) {
        return game.getGameState().getPlayerLifeForce(playerId) < amount;
    }

    /**
     * Determines if the player controls the specified location.
     * @param game the game
     * @param playerId the player
     * @param location the location
     * @return true or false
     */
    public static boolean controls(SwccgGame game, String playerId, PhysicalCard location) {
        return Filters.controls(playerId).accepts(game.getGameState(), game.getModifiersQuerying(), location);
    }

    /**
     * Determines if the player controls a location accepted by the location filter.
     * @param game the game
     * @param playerId the player
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean controls(SwccgGame game, String playerId, Filter locationFilter) {
        return controls(game, playerId, 1, locationFilter);
    }

    /**
     * Determines if the player controls a location accepted by the location filter.
     * @param game the game
     * @param playerId the player
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean controls(SwccgGame game, String playerId, Map<InactiveReason, Boolean> spotOverrides, Filter locationFilter) {
        return controls(game, playerId, 1, spotOverrides, locationFilter);
    }

    /**
     * Determines if the player controls at least a specified number of locations accepted by the location filter.
     * @param game the game
     * @param playerId the player
     * @param count the number of locations
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean controls(SwccgGame game, String playerId, int count, Filter locationFilter) {
        return controls(game, playerId, count, null, locationFilter);
    }

    /**
     * Determines if the player controls at least a specified number of locations accepted by the location filter.
     * @param game the game
     * @param playerId the player
     * @param count the number of locations
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean controls(SwccgGame game, String playerId, int count, Map<InactiveReason, Boolean> spotOverrides, Filter locationFilter) {
        return new ControlsCondition(playerId, count, spotOverrides, locationFilter).isFulfilled(game.getGameState(), game.getModifiersQuerying());
    }

    /**
     * Determines if the player controls the specified location with cards accepted by the card filter.
     * @param game the game
     * @param playerId the player
     * @param location the location
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean controlsWith(SwccgGame game, String playerId, PhysicalCard location, Filter cardFilter) {
        return Filters.controlsWith(playerId, location, cardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), location);
    }

    /**
     * Determines if the player controls a location accepted by the location filter with cards accepted by the card filter.
     * @param game the game
     * @param card the card performing the query
     * @param playerId the player
     * @param locationFilter the location filter
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean controlsWith(SwccgGame game, PhysicalCard card, String playerId, Filter locationFilter, Filter cardFilter) {
        return controlsWith(game, card, playerId, 1, locationFilter, cardFilter);
    }

    /**
     * Determines if the player controls a location accepted by the location filter with cards accepted by the card filter.
     * @param game the game
     * @param card the card performing the query
     * @param playerId the player
     * @param locationFilter the location filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean controlsWith(SwccgGame game, PhysicalCard card, String playerId, Filter locationFilter, Map<InactiveReason, Boolean> spotOverrides, Filter cardFilter) {
        return controlsWith(game, card, playerId, 1, locationFilter, spotOverrides, cardFilter);
    }

    /**
     * Determines if the player controls at least a specified number of locations accepted by the location filter with
     * cards accepted by the card filter.
     * @param game the game
     * @param card the card performing the query
     * @param playerId the player
     * @param count the number of locations
     * @param locationFilter the location filter
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean controlsWith(SwccgGame game, PhysicalCard card, String playerId, int count, Filter locationFilter, Filter cardFilter) {
        return controlsWith(game, card, playerId, count, locationFilter, null, cardFilter);
    }

    /**
     * Determines if the player controls at least a specified number of locations accepted by the location filter with
     * cards accepted by the card filter.
     * @param game the game
     * @param card the card performing the query
     * @param playerId the player
     * @param count the number of locations
     * @param locationFilter the location filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean controlsWith(SwccgGame game, PhysicalCard card, String playerId, int count, Filter locationFilter, Map<InactiveReason, Boolean> spotOverrides, Filter cardFilter) {
        return new ControlsWithCondition(card, playerId, count, locationFilter, spotOverrides, cardFilter).isFulfilled(game.getGameState(), game.getModifiersQuerying());
    }

    /**
     * Determines if the player occupies a location accepted by the location filter.
     * @param game the game
     * @param playerId the player
     * @param location the location
     * @return true or false
     */
    public static boolean occupies(SwccgGame game, String playerId, PhysicalCard location) {
        return Filters.occupies(playerId).accepts(game.getGameState(), game.getModifiersQuerying(), location);
    }

    /**
     * Determines if the player occupies a location accepted by the location filter.
     * @param game the game
     * @param playerId the player
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean occupies(SwccgGame game, String playerId, Filter locationFilter) {
        return occupies(game, playerId, 1, locationFilter);
    }

    /**
     * Determines if the player occupies a location accepted by the location filter.
     * @param game the game
     * @param playerId the player
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean occupies(SwccgGame game, String playerId, Map<InactiveReason, Boolean> spotOverrides, Filter locationFilter) {
        return occupies(game, playerId, 1, spotOverrides, locationFilter);
    }

    /**
     * Determines if the player occupies at least a specified number of locations accepted by the location filter.
     * @param game the game
     * @param playerId the player
     * @param count the number of locations
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean occupies(SwccgGame game, String playerId, int count, Filter locationFilter) {
        return occupies(game, playerId, count, null, locationFilter);
    }

    /**
     * Determines if the player occupies at least a specified number of locations accepted by the location filter.
     * @param game the game
     * @param playerId the player
     * @param count the number of locations
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean occupies(SwccgGame game, String playerId, int count, Map<InactiveReason, Boolean> spotOverrides, Filter locationFilter) {
        return new OccupiesCondition(playerId, count, spotOverrides, locationFilter).isFulfilled(game.getGameState(), game.getModifiersQuerying());
    }

    /**
     * Determines if the player occupies a location accepted by the location filter with cards accepted by the card filter.
     * @param game the game
     * @param card the card performing the query
     * @param playerId the player
     * @param locationFilter the location filter
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean occupiesWith(SwccgGame game, PhysicalCard card, String playerId, Filter locationFilter, Filter cardFilter) {
        return occupiesWith(game, card, playerId, 1, locationFilter, null, cardFilter);
    }

    /**
     * Determines if the player occupies a location accepted by the location filter with cards accepted by the card filter.
     * @param game the game
     * @param card the card performing the query
     * @param playerId the player
     * @param locationFilter the location filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean occupiesWith(SwccgGame game, PhysicalCard card, String playerId, Filter locationFilter, Map<InactiveReason, Boolean> spotOverrides, Filter cardFilter) {
        return occupiesWith(game, card, playerId, 1, locationFilter, spotOverrides, cardFilter);
    }

    /**
     * Determines if the player occupies at least a specified number of locations accepted by the location filter with
     * cards accepted by the card filter.
     * @param game the game
     * @param card the card performing the query
     * @param playerId the player
     * @param count the number of locations
     * @param locationFilter the location filter
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean occupiesWith(SwccgGame game, PhysicalCard card, String playerId, int count, Filter locationFilter, Filter cardFilter) {
        return occupiesWith(game, card, playerId, count, locationFilter, null, cardFilter);
    }

    /**
     * Determines if the player occupies at least a specified number of locations accepted by the location filter with
     * cards accepted by the card filter.
     * @param game the game
     * @param card the card performing the query
     * @param playerId the player
     * @param count the number of locations
     * @param locationFilter the location filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean occupiesWith(SwccgGame game, PhysicalCard card, String playerId, int count, Filter locationFilter, Map<InactiveReason, Boolean> spotOverrides, Filter cardFilter) {
        return new OccupiesWithCondition(card, playerId, count, locationFilter, spotOverrides, cardFilter).isFulfilled(game.getGameState(), game.getModifiersQuerying());
    }

    /**
     * Determines if the player occupies a location accepted by the location filter without cards accepted by the card filter.
     * @param game the game
     * @param card the card performing the query
     * @param playerId the player
     * @param locationFilter the location filter
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean occupiesWithout(SwccgGame game, PhysicalCard card, String playerId, Filter locationFilter, Filter cardFilter) {
        return occupiesWithout(game, card, playerId, locationFilter, null, cardFilter);
    }

    /**
     * Determines if the player occupies a location accepted by the location filter without cards accepted by the card filter.
     * @param game the game
     * @param card the card performing the query
     * @param playerId the player
     * @param locationFilter the location filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean occupiesWithout(SwccgGame game, PhysicalCard card, String playerId, Filter locationFilter, Map<InactiveReason, Boolean> spotOverrides, Filter cardFilter) {
        return occupies(game, playerId, spotOverrides, locationFilter) && !occupiesWith(game, card, playerId, locationFilter, spotOverrides, cardFilter);
    }

    /**
     * Determines if the character card is playing sabacc.
     * @param game the game
     * @param card the character card
     * @return true or false
     */
    public static boolean isPlayingSabacc(SwccgGame game, PhysicalCard card) {
        SabaccState sabaccState = game.getGameState().getSabaccState();
        if (sabaccState == null)
            return false;

        List<PhysicalCard> sabaccPlayers = sabaccState.getSabaccPlayers();
        return sabaccPlayers.contains(card);
    }

    /**
     * Determines if the current Force loss is not prohibited from being reduced.
     * @param game the game
     * @return true or false
     */
    public static boolean canReduceForceLoss(SwccgGame game) {
        ForceLossState forceLossState = game.getGameState().getTopForceLossState();
        if (forceLossState != null) {
            return !forceLossState.getLoseForceEffect().isCannotBeReduced();
        }
        BattleState battleState = game.getGameState().getBattleState();
        if (battleState != null) {
            return battleState.isReachedDamageSegment();
        }
        return false;
    }

    /**
     * Determines if an attempt to 'blow away' Death Star II is currently in progress.
     * @param game the game
     * @return true or false
     */
    public static boolean isDuringAttemptToBlowAwayDeathStarII(SwccgGame game) {
        return game.getGameState().getEpicEventState() != null && game.getGameState().getEpicEventState().getEpicEventType() == EpicEventState.Type.ATTEMPT_TO_BLOW_AWAY_DEATH_STAR_II;
    }

    // Checks if a card that fits the filter is "blown away".
    public static boolean isBlownAway(SwccgGame game, Filterable filters) {
        return game.getModifiersQuerying().isBlownAway(game.getGameState(), filters);
    }

    /**
     * Determines if an Utinni Effect accepted by the filter has been completed.
     * @param game the game
     * @param utinniEffectFilter the filter
     * @return true or false
     */
    public static boolean hasCompletedUtinniEffect(SwccgGame game, Filterable utinniEffectFilter) {
        return hasCompletedUtinniEffect(game, null, 1, utinniEffectFilter);
    }

    /**
     * Determines if the player has completed an Utinni Effect accepted by the filter.
     * @param game the game
     * @param playerId the player
     * @param utinniEffectFilter the filter
     * @return true or false
     */
    public static boolean hasCompletedUtinniEffect(SwccgGame game, String playerId, Filterable utinniEffectFilter) {
        return hasCompletedUtinniEffect(game, playerId, 1, utinniEffectFilter);
    }

    /**
     * Determines if the player has completed at least a specified number of Utinni Effects accepted by the filter.
     * @param game the game
     * @param playerId the player
     * @param numTimes the number of Utinni Effects completed
     * @param utinniEffectFilter the filter
     * @return true or false
     */
    public static boolean hasCompletedUtinniEffect(SwccgGame game, String playerId, int numTimes, Filterable utinniEffectFilter) {
        return game.getModifiersQuerying().hasCompletedUtinniEffect(game.getGameState(), playerId, numTimes, utinniEffectFilter);
    }

    /**
     * Determines if the specified Utinni Effect is reached.
     * @param game the game
     * @param utinniEffect the Utinni Effect
     * @return true or false
     */
    public static boolean isUtinniEffectReached(SwccgGame game, PhysicalCard utinniEffect) {
        return utinniEffect.getUtinniEffectStatus() == UtinniEffectStatus.REACHED
                || utinniEffect.getUtinniEffectStatus() == UtinniEffectStatus.COMPLETED;
    }

    /**
     * Determines if the specified Utinni Effect is completed.
     * @param game the game
     * @param utinniEffect the Utinni Effect
     * @return true or false
     */
    public static boolean isUtinniEffectCompleted(SwccgGame game, PhysicalCard utinniEffect) {
        return utinniEffect.getUtinniEffectStatus() == UtinniEffectStatus.COMPLETED;
    }

    /**
     * Determines if the specified Jedi Test being attempted.
     * @param game the game
     * @param jediTest the Jedi Test
     * @return true or false
     */
    public static boolean isJediTestBeingAttempted(SwccgGame game, PhysicalCard jediTest) {
        return jediTest.getJediTestStatus() == JediTestStatus.ATTEMPTING;
    }

    /**
     * Determines if the specified Jedi Test is completed.
     * @param game the game
     * @param jediTest the Jedi Test
     * @return true or false
     */
    public static boolean isJediTestCompleted(SwccgGame game, PhysicalCard jediTest) {
        return jediTest.getJediTestStatus() == JediTestStatus.COMPLETED;
    }

    /**
     * Determines if player1 is generating at least X Force more than player2.
     * @param game the game
     * @param playerId1 player1
     * @param playerId2 player2
     * @param difference the value for X
     * @return true or false
     */
    public static boolean isGeneratingAtLeastXForceMoreThan(SwccgGame game, String playerId1, String playerId2, int difference) {
        return (game.getGameState().getPlayersTotalForceGeneration(playerId1) - difference) >= game.getGameState().getPlayersTotalForceGeneration(playerId2);
    }

    // Checks if flag is active.
    public static boolean isFlagActive(SwccgGame game, ModifierFlag flag) {
        return game.getModifiersQuerying().hasFlagActive(game.getGameState(), flag);
    }

    // Checks if player flag is active for player.
    public static boolean isFlagActiveForPlayer(SwccgGame game, ModifierFlag flag, String playerId) {
        return game.getModifiersQuerying().hasFlagActive(game.getGameState(), flag, playerId);
    }

    /**
     * Determines if the card has the specified game text modification.
     * @param game the game
     * @param card the card
     * @param type the gametext modification type
     * @return true or false
     */
    public static boolean hasGameTextModification(SwccgGame game, PhysicalCard card, ModifyGameTextType type) {
        return game.getModifiersQuerying().hasGameTextModification(game.getGameState(), card, type);
    }


    /**
     * Gets the number of times the card has the specified gametext modification applied cumulatively.
     * @param game the game
     * @param card the card
     * @param type the gametext modification type
     * @return the number of times the card has the specified gametext modification
     */
    public static int getGameTextModificationCount(SwccgGame game, PhysicalCard card, ModifyGameTextType type) {
        return game.getModifiersQuerying().getGameTextModificationCount(game.getGameState(), card, type);
    }

    /**
     * Determines if all ability on table for the specified player is provided by cards accepted by the card filter.
     * @param game the game
     * @param card the card performing the query
     * @param playerId the player
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean isAllAbilityOnTableProvidedBy(SwccgGame game, PhysicalCard card, String playerId, Filterable cardFilter) {
        return new AllAbilityOnTableProvidedByCondition(card, playerId, cardFilter).isFulfilled(game.getGameState(), game.getModifiersQuerying());
    }

    /**
     * Determines if all ability at locations accepted by the location filter for the specified player is provided by cards
     * accepted by the card filter.
     * @param game the game
     * @param card the card performing the query
     * @param playerId the player
     * @param locationFilter the location filter
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean isAllAbilityAtLocationProvidedBy(SwccgGame game, PhysicalCard card, String playerId, Filterable locationFilter, Filterable cardFilter) {
        return new AllAbilityAtLocationProvidedByCondition(card, playerId, locationFilter, cardFilter).isFulfilled(game.getGameState(), game.getModifiersQuerying());
    }

    /**
     * Determines if all ability at in battle for the specified player is provided by cards accepted by the card filter.
     * @param game the game
     * @param playerId the player
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean isAllAbilityInBattleProvidedBy(SwccgGame game, String playerId, Filterable cardFilter) {
        return new AllAbilityInBattleProvidedByCondition(playerId, cardFilter).isFulfilled(game.getGameState(), game.getModifiersQuerying());
    }

    /**
     * Gets the number of Force icons at the location.
     * @param game the game
     * @param card a card at the location, or the location itself
     * @param darkForce true if Dark Force icons are counted, otherwise false
     * @param lightForce true if Light Force icons are counted, otherwise false
     * @return the number of Force icons at the location
     */
    public static int getNumForceIconsHere(SwccgGame game, PhysicalCard card, boolean darkForce, boolean lightForce) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        PhysicalCard location = modifiersQuerying.getLocationHere(gameState, card);
        if (location == null)
            return 0;

        Collection<PhysicalCard> cardsAtLocation = Filters.filterActive(game, card, Filters.at(location));

        int count = 0;
        if (darkForce) {
            count += modifiersQuerying.getIconCount(gameState, location, Icon.DARK_FORCE);
            for (PhysicalCard cardAtLocation : cardsAtLocation) {
                count += modifiersQuerying.getIconCount(gameState, cardAtLocation, Icon.DARK_FORCE);
            }
        }
        if (lightForce) {
            count += modifiersQuerying.getIconCount(gameState, location, Icon.LIGHT_FORCE);
            for (PhysicalCard cardAtLocation : cardsAtLocation) {
                count += modifiersQuerying.getIconCount(gameState, cardAtLocation, Icon.LIGHT_FORCE);
            }
        }

        return count;
    }

    /**
     * Determines if all characters for the specified player on the specified system are accepted by the specified filter.
     * @param game the game
     * @param card the card checking this condition
     * @param playerId the player
     * @param system the system
     * @param filters the filter
     * @return true or false
     */
    public static boolean isAllCharactersOnSystem(SwccgGame game, PhysicalCard card, String playerId, String system, Filterable filters) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        Collection<PhysicalCard> characters = Filters.filterActive(game, card, Filters.and(Filters.owner(playerId), Filters.character, Filters.on(system)));
        if (characters.isEmpty())
            return false;

        for (PhysicalCard character : characters) {
            if (!Filters.and(filters).accepts(gameState, modifiersQuerying, character))
                return false;
        }
        return true;
    }

    /**
     * Checks if the player can activate Force.
     * @param game the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean canActivateForce(SwccgGame game, String playerId) {
        if (hasReserveDeck(game, playerId)
                && !game.getModifiersQuerying().isActivatingForceProhibited(game.getGameState(), playerId)) {
            return true;
        }
        return false;
    }

    /**
     * Gets the current Force drain amount.
     * @param game the game
     * @return the Force drain amount
     */
    public static float getForceDrainAmount(SwccgGame game) {
        ForceDrainState forceDrainState = game.getGameState().getForceDrainState();
        if (forceDrainState != null && forceDrainState.canContinue()) {
            return game.getModifiersQuerying().getForceDrainAmount(game.getGameState(), forceDrainState.getLocation(), forceDrainState.getPlayerId());
        }
        return 0;
    }

    /**
     * Determines if the current Force drain can be canceled by the owner of the specified card.
     * @param game the game
     * @param card the card
     * @return true if Force can be canceled, otherwise false
     */
    public static boolean canCancelForceDrain(SwccgGame game, PhysicalCard card) {
        return canCancelForceDrain(game, card.getOwner(), card);
    }

    /**
     * Determines if the current Force drain can be canceled by the specified player using the specified card.
     * @param game the game
     * @param playerId the player
     * @param card the card
     * @return true if Force can be canceled, otherwise false
     */
    public static boolean canCancelForceDrain(SwccgGame game, String playerId, PhysicalCard card) {
        // Check that a not-already-canceled Force drain is in progress
        ForceDrainState forceDrainState = game.getGameState().getForceDrainState();
        if (forceDrainState != null && forceDrainState.canContinue()) {
            return !game.getModifiersQuerying().cantCancelForceDrainAtLocation(game.getGameState(), forceDrainState.getLocation(), card, playerId, forceDrainState.getPlayerId());
        }
        return false;
    }

    //
    //
    // The methods below are for use by Abstract card classes to implement "built-in" actions, such as
    // activating Force, Force draining, deploying, moving, drawing, etc.
    //
    //

    // Checks if player can Force drain at the location.


    /**
     * Determines if the player can Force drain at the location.
     * @param game the game
     * @param playerId the player
     * @param location the location
     * @return true or false
     */
    public static boolean canInitiateForceDrainAtLocation(SwccgGame game, String playerId, PhysicalCard location) {
        return canForceDrainAtLocation(game, playerId, location, false);
    }

    /**
     * Determines if the player can perform an action "instead of Force draining" at the location.
     * @param game the game
     * @param playerId the player
     * @param location the location
     * @return true or false
     */
    public static boolean canInsteadOfForceDrainingAtLocation(SwccgGame game, String playerId, PhysicalCard location) {
        return canForceDrainAtLocation(game, playerId, location, true);
    }

    /**
     * Determines if the player can Force drain at the location.
     * @param game the game
     * @param playerId the player
     * @param location the location
     * @param ignoreCost true if checking any costs involved in initiating Force drain is skipped, otherwise false
     * @return true or false
     */
    private static boolean canForceDrainAtLocation(SwccgGame game, String playerId, PhysicalCard location, boolean ignoreCost) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        if (location == null
                || location.getZone() != Zone.LOCATIONS
                || !gameState.getCurrentPlayerId().equals(playerId)
                || gameState.getCurrentPhase() != Phase.CONTROL
                || !Filters.controlsForForceDrain(playerId).accepts(gameState, modifiersQuerying, location)
                || modifiersQuerying.isForceDrainAttemptedThisTurn(location)
                || Filters.canSpot(game, null, Filters.and(Filters.owner(playerId), Filters.at(location), Filters.cannotParticipateInForceDrain)))
            return false;


        // Check if player is prevented from Force draining at location
        if (modifiersQuerying.isProhibitedFromForceDrainingAtLocation(gameState, location, playerId))
            return false;

        // Check if player can use force to initiate force drain
        if (!ignoreCost) {
            float forceDrainCost = modifiersQuerying.getInitiateForceDrainCost(gameState, location, playerId);
            int forceAvailableToUse = forceAvailableToUse(gameState.getGame(), playerId);
            if (forceAvailableToUse < forceDrainCost)
                return false;
        }

        return true;
    }

    /**
     * Determines if the player can draw asteroid destiny against the specified starship.
     * @param game the game
     * @param playerId the player
     * @param starship the starship
     * @return true or false
     */
    public static boolean canDrawAsteroidDestinyAgainstStarship(SwccgGame game, String playerId, PhysicalCard starship) {
        if (starship.getBlueprint().getCardCategory() != CardCategory.STARSHIP || starship.getOwner().equals(playerId))
            return false;

        if (!isDuringOpponentsPhase(game, playerId, Phase.CONTROL))
            return false;

        PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsPresentAt(game.getGameState(), starship);
        if (location == null || !Filters.asteroidRulesInEffect.accepts(game, location))
            return false;

        if (game.getModifiersQuerying().hadAsteroidDestinyDrawnAgainstThisTurn(starship, location))
            return false;

        return game.getGameState().isCardInPlayActive(starship, false, false, false, false, false, false, false, false);
    }

    /**
     * Determines if the card can 'cloak'.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean canCloak(SwccgGame game, PhysicalCard card) {
        return !game.getModifiersQuerying().isCloakingCardProhibited(game.getGameState(), card);
    }

    /**
     * Determines if the card can 'attach'.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean canAttach(SwccgGame game, PhysicalCard card) {
        return !game.getModifiersQuerying().isAttachingCardProhibited(game.getGameState(), card);
    }

    // Checks if player can stack a 'bluff card' at location.
    public static boolean canStackBluffCardAtLocation(String player, SwccgGame game, PhysicalCard self) {
        return self.getZone() == Zone.LOCATIONS
                && !game.getModifiersQuerying().isBluffCardStackedThisTurn()
                && self.getBlueprint().isSpecialRuleInEffectHere(SpecialRule.BLUFF_RULES, self)
                && player.equals(game.getDarkPlayer())
                && isDuringYourTurn(game, player)
                && hasHand(game, player);
    }

    // Checks if player can flip a 'bluff card' at location.
    public static boolean canFlipBluffCardAtLocation(String player, SwccgGame game, PhysicalCard self) {
        return self.getZone() == Zone.LOCATIONS
                && player.equals(game.getLightPlayer())
                && self.getBlueprint().isSpecialRuleInEffectHere(SpecialRule.BLUFF_RULES, self)
                && isPhaseForPlayer(game, Phase.DEPLOY, game.getDarkPlayer())
                && Filters.canSpot(game.getGameState().getStackedCards(self), game, Filters.bluffCard);
    }

    /**
     * Determines if the specified player can use a combat card.
     * @param game the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean canUseCombatCard(SwccgGame game, String playerId) {
        return !game.getModifiersQuerying().isCombatCardUsedThisTurn(playerId)
                || !game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.MAY_ONLY_USE_ONE_COMBAT_CARD_PER_TURN, playerId)
                || !canSubstituteDestiny(game);
    }

    /**
     * Determines if the player can performing docking bay transit from the location.
     * @param playerId the player
     * @param game the game
     * @param location the filter for card to move
     * @return true or false
     */
    public static boolean canDockingBayTransitFromLocation(String playerId, SwccgGame game, PhysicalCard location) {
        if (!Filters.docking_bay.accepts(game, location)
                || game.getGameState().getCurrentPhase() != Phase.MOVE) {
            return false;
        }

        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Get other docking bays
        Collection<PhysicalCard> otherDockingBays = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.other(location), Filters.docking_bay));
        if (otherDockingBays.isEmpty()) {
            return false;
        }

        // Get cards at docking bay
        Filter cardFilter = Filters.and(Filters.your(playerId), Filters.hasNotPerformedRegularMove, Filters.or(Filters.character, Filters.vehicle, Filters.weapon), Filters.atLocation(location));
        if (gameState.getCurrentPlayerId().equals(playerId)) {
            cardFilter = Filters.and(cardFilter, Filters.not(Filters.or(Filters.undercover_spy, Filters.deploysAndMovesLikeUndercoverSpy)));
        }
        else {
            cardFilter = Filters.and(cardFilter, Filters.or(Filters.undercover_spy, Filters.deploysAndMovesLikeUndercoverSpy));
        }

        Collection<PhysicalCard> cardsAtDockingBay = Filters.filterActive(game, null, SpotOverride.INCLUDE_UNDERCOVER, cardFilter);
        if (cardsAtDockingBay.isEmpty()) {
            return false;
        }

        for (PhysicalCard cardAtDockingBay : cardsAtDockingBay) {
            for (PhysicalCard otherDockingBay : otherDockingBays) {
                // Check if card can move to destination card
                if (Filters.canMoveToUsingDockingBayTransit(cardAtDockingBay, false, 0).accepts(gameState, modifiersQuerying, otherDockingBay)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines if the specified card is prohibited from moving.
     * @param game the game
     * @param card the card
     * @return true if card is prohibited from moving, otherwise false
     */
    public static boolean mayNotMove(SwccgGame game, PhysicalCard card) {
        return game.getModifiersQuerying().mayNotMove(game.getGameState(), card);
    }

    /**
     * Determines if the player can use location's game text to perform a move.
     * @param playerId the player
     * @param game the game
     * @param cardToMoveFilter the filter for card to move
     * @param fromCardFilter the filter for card to move from
     * @param toCardFilter the filter for card to move to
     * @param forFree true if moving for free, otherwise false
     * @return true or false
     */
    public static boolean canPerformMovementUsingLocationText(String playerId, SwccgGame game, Filterable cardToMoveFilter, Filterable fromCardFilter, Filterable toCardFilter, boolean forFree) {
        return canPerformMovementUsingLocationText(playerId, game, cardToMoveFilter, fromCardFilter, toCardFilter, forFree, 1);
    }

    /**
     * Determines if the player can use location's game text to perform a move.
     * @param playerId the player
     * @param game the game
     * @param cardToMoveFilter the filter for card to move
     * @param fromCardFilter the filter for card to move from
     * @param toCardFilter the filter for card to move to
     * @param forFree true if moving for free, otherwise false
     * @param baseCost base cost in amount of Force required to perform the movement
     * @return true or false
     */
    public static boolean canPerformMovementUsingLocationText(String playerId, SwccgGame game, Filterable cardToMoveFilter, Filterable fromCardFilter, Filterable toCardFilter, boolean forFree, float baseCost) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        Collection<PhysicalCard> fromCards = Filters.filterActive(game, null, fromCardFilter);
        if (fromCards.isEmpty())
            return false;

        Collection<PhysicalCard> toCards = Filters.filterActive(game, null, toCardFilter);
        if (toCards.isEmpty())
            return false;

        // Figure out which from locations (or starships/vehicles) contain any of the cards can move to valid to locations (or starship/vehicles)
        for (PhysicalCard fromCard : fromCards) {
            final Collection<PhysicalCard> cardsToMove = Filters.filterActive(game, null,
                    Filters.and(Filters.owner(playerId), cardToMoveFilter, Filters.hasNotPerformedRegularMove, Filters.or(Filters.atLocation(fromCard), Filters.aboardExceptRelatedSites(fromCard))));

            for (PhysicalCard cardToMove : cardsToMove) {
                for (PhysicalCard toCard : toCards) {
                    // Check if card can move to destination card
                    if (Filters.canMoveToUsingLocationText(cardToMove, forFree, baseCost, 0).accepts(gameState, modifiersQuerying, toCard)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if sabacc can be played.
     * @param game the game
     * @return true or false
     */
    public static boolean canPlaySabacc(SwccgGame game) {
        if (isDuringBattle(game) || isDuringAttack(game)) {
            return false;
        }

        if (game.getGameState().getReserveDeckSize(game.getDarkPlayer()) < 2) {
            return false;
        }

        if (game.getGameState().getReserveDeckSize(game.getLightPlayer()) < 2) {
            return false;
        }

        return true;
    }

    /**
     * Determines if the specified player can initiate an attack on a creature at the location.
     * @param player the player
     * @param game the game
     * @param location the location
     * @return true or false
     */
    public static boolean canInitiateAttackCreatureAtLocation(String player, SwccgGame game, PhysicalCard location) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        if (location.getZone() != Zone.LOCATIONS || !gameState.getCurrentPlayerId().equals(player)
                || gameState.getCurrentPhase() != Phase.BATTLE || gameState.isDuringBattle() || gameState.isDuringAttack())
            return false;

        // Check if player is prevented from initiating attacks at location
        if (modifiersQuerying.mayNotInitiateAttacksAtLocation(gameState, location, player))
            return false;

        // Check that a non-creature attack on a creature has not already happened at this location this turn
        if (modifiersQuerying.isAttackOnCreatureOccurredAtLocationThisTurn(location))
            return false;

        return Filters.canSpot(game, null, Filters.creatureAtLocationCanBeAttackedByPlayer(player, location));
    }

    /**
     * Determines if the specified player can initiate battle at the location.
     * @param player the player
     * @param game the game
     * @param location the location
     * @return true or false
     */
    public static boolean canInitiateBattleAtLocation(String player, SwccgGame game, PhysicalCard location) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        if (location.getZone() != Zone.LOCATIONS || !gameState.getCurrentPlayerId().equals(player)
                || gameState.getCurrentPhase() != Phase.BATTLE || gameState.isDuringBattle() || gameState.isDuringAttack())
            return false;

        // Check if player is prevented from initiating battle at location
        if (modifiersQuerying.mayNotInitiateBattleAtLocation(gameState, location, player))
            return false;

        // Check if player can use force to initiate battle
        float battleCost = modifiersQuerying.getInitiateBattleCost(gameState, location, player);
        int forceAvailableToUse = forceAvailableToUse(gameState.getGame(), player);
        if (forceAvailableToUse < battleCost)
            return false;

        // Check that a battle has not already happened at this location this turn
        if (modifiersQuerying.isBattleOccurredAtLocationThisTurn(location))
            return false;

        boolean foundMayInitiateBattle = Filters.canSpot(game, null, Filters.and(Filters.owner(player), Filters.mayInitiateBattle, Filters.canParticipateInBattleAt(location, player)));
        boolean foundMayBeBattled = Filters.canSpot(game, null, Filters.and(Filters.opponents(player), Filters.mayBeBattled, Filters.canParticipateInBattleAt(location, player)));

        // Both sides occupy location (excluding cards that cannot participate in battle)
        return ((foundMayInitiateBattle || modifiersQuerying.hasPresenceAt(gameState, player, location, true, player, null))
                && (foundMayBeBattled || modifiersQuerying.hasPresenceAt(gameState, gameState.getOpponent(player), location, true, player, null)));
    }

    /**
     * Determines if the specified player is not prevented from adding destiny draws to power.
     * @param game the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean canAddDestinyDrawsToPower(SwccgGame game, String playerId) {
        return !game.getModifiersQuerying().mayNotAddDestinyDrawsToPower(game.getGameState(), playerId);
    }

    /**
     * Determines if the specified player is not prevented from adding destiny draws to attrition.
     * @param game the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean canAddDestinyDrawsToAttrition(SwccgGame game, String playerId) {
        return !game.getModifiersQuerying().mayNotAddDestinyDrawsToAttrition(game.getGameState(), playerId);
    }

    /**
     * Determines if the specified card is not prevented from adding battle destiny draws.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean canAddBattleDestinyDraws(SwccgGame game, PhysicalCard card) {
        return !game.getModifiersQuerying().mayNotAddBattleDestinyDraws(game.getGameState(), card);
    }

    /**
     * Determines if just drawn destiny can be canceled by the specified player.
     * @param game the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean canCancelDestiny(SwccgGame game, String playerId) {
        return !game.getModifiersQuerying().mayNotCancelDestinyDraw(game.getGameState(), playerId, false);
    }

    /**
     * Determines if just drawn destiny can be canceled by the specified player (and cause a redraw).
     * @param game the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean canCancelDestinyAndCauseRedraw(SwccgGame game, String playerId) {
        return !game.getModifiersQuerying().mayNotCancelDestinyDraw(game.getGameState(), playerId, true);
    }

    /**
     * Determines if at least one battle destiny drawn by the opponent can be canceled by another specified player.
     * @param game the game
     * @param playerId the player to cancel battle destinies
     * @return true or false
     */
    public static boolean canCancelOpponentsPreviouslyDrawnBattleDestiny(SwccgGame game, String playerId) {
        BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null)
            return false;

        return battleState.getNumBattleDestinyDrawnCancelableByOpponent(game.getOpponent(playerId)) > 0;
    }

    /**
     * Determines if just drawn destiny can be substituted.
     * @param game the game
     * @return true or false
     */
    public static boolean canSubstituteDestiny(SwccgGame game) {
        return !game.getModifiersQuerying().mayNotSubstituteDestinyDraw(game.getGameState());
    }

    /**
     * Determines if attrition against the specified player can be modified.
     * @param game the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean canModifyAttritionAgainst(SwccgGame game, String playerId) {
        BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null)
            return false;

        if (battleState.getNumBattleDestinyDrawn(game.getOpponent(playerId)) == 0)
            return false;

        if (battleState.isReachedDamageSegment())
            return false;


        // TODO: Add code here to check if attrition has been reset

        return true;
    }

    /**
     * Determines if attrition against the specified player can be satisfied by specified card by forfeiting it.
     * @param game the game
     * @param playerId the player
     * @param card the card
     * @return true or false
     */
    public static boolean canForfeitToSatisfyAttrition(SwccgGame game, String playerId, PhysicalCard card) {
        BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null)
            return false;

        if (!battleState.isCardParticipatingInBattle(card))
            return false;

        if (battleState.getAttritionRemaining(game, playerId) == 0)
            return false;

        if (game.getModifiersQuerying().cannotSatisfyAttrition(game.getGameState(), card))
            return false;

        if (Filters.and(Filters.character, Filters.participatingInBattle, Filters.not(Filters.mustBeForfeitedBeforeOtherCharacters)).accepts(game, card)
                && Filters.canSpot(battleState.getCardsParticipating(playerId), game, Filters.mustBeForfeitedBeforeOtherCharacters)) {
            return false;
        }

        return true;
    }

    /**
     * Determines if battle damage against the specified player can be satisfied by specified card.
     * @param game the game
     * @param playerId the player
     * @param card the card
     * @return true or false
     */
    public static boolean canForfeitToSatisfyBattleDamage(SwccgGame game, String playerId, PhysicalCard card) {
        BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null)
            return false;

        if (!battleState.isCardParticipatingInBattle(card))
            return false;

        if (battleState.getBattleDamageRemaining(game, playerId) == 0)
            return false;

        if (Filters.and(Filters.character, Filters.participatingInBattle, Filters.not(Filters.mustBeForfeitedBeforeOtherCharacters)).accepts(game, card)
                && Filters.canSpot(battleState.getCardsParticipating(playerId), game, Filters.mustBeForfeitedBeforeOtherCharacters)) {
            return false;
        }

        return true;
    }

    /**
     * Determines if attrition and/or battle damage against the specified player can be satisfied by specified card.
     * @param game the game
     * @param playerId the player
     * @param card the card
     * @return true or false
     */
    public static boolean canForfeitToSatisfyAttritionAndBattleDamage(SwccgGame game, String playerId, PhysicalCard card) {
        BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null)
            return false;

        if (!battleState.isCardParticipatingInBattle(card))
            return false;

        if ((battleState.getAttritionRemaining(game, playerId) == 0
                || game.getModifiersQuerying().cannotSatisfyAttrition(game.getGameState(), card))
                && battleState.getBattleDamageRemaining(game, playerId) == 0)
            return false;

        if (Filters.and(Filters.character, Filters.participatingInBattle, Filters.not(Filters.mustBeForfeitedBeforeOtherCharacters)).accepts(game, card)
                && Filters.canSpot(battleState.getCardsParticipating(playerId), game, Filters.mustBeForfeitedBeforeOtherCharacters)) {
            return false;
        }

        return true;
    }

    /**
     * Determines if just drawn destiny card is accepted by the specified filter.
     * @param game the game
     * @param filter the filter
     * @return true or false
     */
    public static boolean isDestinyCardMatchTo(SwccgGame game, Filterable filter) {
        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState == null)
            return false;

        PhysicalCard destinyCard = drawDestinyState.getDrawDestinyEffect().getDrawnDestinyCard();
        return destinyCard != null
                && Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), destinyCard);
    }

    /**
     * Determines if just drawn destiny card is deployable for free by the specified player.
     * @param game the game
     * @param playerId the player
     * @param self the card performing the query
     * @return true or false
     */
    public static boolean isDestinyCardDeployableForFree(SwccgGame game, String playerId, PhysicalCard self) {
        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState == null)
            return false;

        // Card must still be in the "unresolved destiny draw" pile to be deployable
        PhysicalCard destinyCard = drawDestinyState.getDrawDestinyEffect().getDrawnDestinyCard();
        return destinyCard != null
                && destinyCard.getZone().isUnresolvedDestinyDraw()
                && playerId.equals(destinyCard.getOwner())
                && Filters.deployable(self, null, true, 0).accepts(game.getGameState(), game.getModifiersQuerying(), destinyCard);
    }

    /**
     * Determines if just drawn destiny card can be taken into hand by the specified player.
     * @param game the game
     * @return true or false
     */
    public static boolean canTakeDestinyCardIntoHand(SwccgGame game, String playerId) {
        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState == null)
            return false;

        // Card must still be in the "unresolved destiny draw" pile to be taken into hand
        PhysicalCard destinyCard = drawDestinyState.getDrawDestinyEffect().getDrawnDestinyCard();
        return destinyCard != null
                && destinyCard.getZone().isUnresolvedDestinyDraw()
                && playerId.equals(destinyCard.getOwner());
    }

    /**
     * Determines if just drawn destiny card can be stacked.
     * @param game the game
     * @return true or false
     */
    public static boolean canStackDestinyCard(SwccgGame game) {
        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState == null)
            return false;

        // Card must still be in the "unresolved destiny draw" pile to be stacked
        PhysicalCard destinyCard = drawDestinyState.getDrawDestinyEffect().getDrawnDestinyCard();
        return destinyCard != null
                && destinyCard.getZone().isUnresolvedDestinyDraw();
    }

    /**
     * Determines if just drawn destiny card can be placed in another card pile.
     * @param game the game
     * @return true or false
     */
    public static boolean canPlaceDestinyCardInCardPile(SwccgGame game) {
        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState == null)
            return false;

        // Card must still be in the "unresolved destiny draw" pile to be placed in another card pile
        PhysicalCard destinyCard = drawDestinyState.getDrawDestinyEffect().getDrawnDestinyCard();
        return destinyCard != null
                && destinyCard.getZone().isUnresolvedDestinyDraw();
    }

    /**
     * Determines if just drawn destiny card can be made lost.
     * @param game the game
     * @return true or false
     */
    public static boolean canMakeDestinyCardLost(SwccgGame game) {
        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState == null)
            return false;

        // Card must still be in the "unresolved destiny draw" pile to be make lost
        PhysicalCard destinyCard = drawDestinyState.getDrawDestinyEffect().getDrawnDestinyCard();
        return destinyCard != null
                && destinyCard.getZone().isUnresolvedDestinyDraw();
    }

    /**
     * Determines if just drawn destiny card can be exchanged with another card by the specified player.
     * @param game the game
     * @return true or false
     */
    public static boolean canExchangeDestinyCard(SwccgGame game, String playerId) {
        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState == null)
            return false;

        // Card must still be in the "unresolved destiny draw" pile to be exchanged with another card
        PhysicalCard destinyCard = drawDestinyState.getDrawDestinyEffect().getDrawnDestinyCard();
        return destinyCard != null
                && destinyCard.getZone().isUnresolvedDestinyDraw()
                && playerId.equals(destinyCard.getOwner());
    }

    /**
     * Determines if just drawn destiny card can be placed out of play.
     * @param game the game
     * @return true or false
     */
    public static boolean canPlaceDestinyCardOutOfPlay(SwccgGame game) {
        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState == null)
            return false;

        // Card must still be in the "unresolved destiny draw" pile to be make lost
        PhysicalCard destinyCard = drawDestinyState.getDrawDestinyEffect().getDrawnDestinyCard();
        return destinyCard != null
                && destinyCard.getZone().isUnresolvedDestinyDraw();
    }

    /**
     * Determines if player drawing destiny can instead draw X and choose Y.
     * @param game the game
     * @return true or false
     */
    public static boolean canDrawDestinyAndChoose(SwccgGame game, int drawX) {
        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState == null)
            return false;

        return drawDestinyState.getDrawDestinyEffect().canDrawAndChoose(game, drawX);
    }

    /**
     * Determines if just drawn destiny value is equal to the specified value.
     * @param game the game
     * @param value the value
     * @return true or false
     */
    public static boolean isDestinyValueEqualTo(SwccgGame game, float value) {
        return isDestinyValueInRange(game, value, value);
    }

    /**
     * Determines if just drawn destiny value is greater than the specified value.
     * @param game the game
     * @param specifiedValue the value to compare to
     * @return true or false
     */
    public static boolean isDestinyValueGreaterThan(SwccgGame game, float specifiedValue) {
        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState == null)
            return false;

        DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
        float value = drawDestinyEffect.getDestinyDrawValue();

        return !drawDestinyEffect.isDestinyCanceled()
                && value > specifiedValue;
    }

    /**
     * Determines if just drawn destiny value is less than the specified value.
     * @param game the game
     * @param specifiedValue the value to compare to
     * @return true or false
     */
    public static boolean isDestinyValueLessThan(SwccgGame game, float specifiedValue) {
        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState == null)
            return false;

        DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
        float value = drawDestinyEffect.getDestinyDrawValue();

        return !drawDestinyEffect.isDestinyCanceled()
                && value < specifiedValue;
    }

    /**
     * Determines if just drawn destiny value in range.
     * @param game the game
     * @param min the minimum value of the range
     * @param max the minimum value of the range
     * @return true or false
     */
    public static boolean isDestinyValueInRange(SwccgGame game, float min, float max) {
        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState == null)
            return false;

        DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
        float value = drawDestinyEffect.getDestinyDrawValue();

        return !drawDestinyEffect.isDestinyCanceled()
                && value >= min && value <= max;
    }

    /**
     * Determines if just drawn destiny value is even.
     * @param game the game
     * @return true or false
     */
    public static boolean isDestinyValueEven(SwccgGame game) {
        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState == null)
            return false;

        DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
        float value = drawDestinyEffect.getDestinyDrawValue();

        return !drawDestinyEffect.isDestinyCanceled()
                && (value % 2 == 0);
    }

    /**
     * Determines if just drawn destiny value is odd.
     * @param game the game
     * @return true or false
     */
    public static boolean isDestinyValueOdd(SwccgGame game) {
        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState == null)
            return false;

        DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
        float value = drawDestinyEffect.getDestinyDrawValue();

        return !drawDestinyEffect.isDestinyCanceled()
                && (value % 2 == 1);
    }

    /**
     * Checks if the specified player has a higher race total.
     * @param game the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean hasHigherRaceTotal(SwccgGame game, String playerId) {
        GameState gameState = game.getGameState();
        if (!gameState.isDuringPodrace())
            return false;

        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        return (modifiersQuerying.getHighestRaceTotal(gameState, playerId) > modifiersQuerying.getHighestRaceTotal(gameState, game.getOpponent(playerId)));
    }

    /**
     * Checks if the specified player has won a Podrace.
     * @param game the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean hasWonPodrace(SwccgGame game, String playerId) {
        return playerId.equals(game.getGameState().getPodraceWinner());
    }

    /**
     * Checks if the Podracer leading the Podrace is accepted by the filter.
     * @param game the game
     * @param podracerFilter the Podracer filter
     * @return true or false
     */
    public static boolean isLeadingPodrace(SwccgGame game, Filterable podracerFilter) {
        if (!game.getGameState().isDuringPodrace())
            return false;

        Collection<PhysicalCard> podracersLeading = game.getModifiersQuerying().getPodracersLeadingPodrace(game.getGameState());
        return Filters.canSpot(podracersLeading, game, podracerFilter);
    }

    /**
     * Checks if the Podracer behind in the Podrace is accepted by the filter.
     * @param game the game
     * @param podracerFilter the Podracer filter
     * @return true or false
     */
    public static boolean isBehindInPodrace(SwccgGame game, Filterable podracerFilter) {
        if (!game.getGameState().isDuringPodrace())
            return false;

        Collection<PhysicalCard> podracersBehind = game.getModifiersQuerying().getPodracersBehindInPodrace(game.getGameState());
        return Filters.canSpot(podracersBehind, game, podracerFilter);
    }

    /**
     * Determines if the player has a senate majority.
     * @param game the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean hasSenateMajority(SwccgGame game, String playerId) {
        String playerWithSenateMajority = game.getModifiersQuerying().getPlayerWithSenateMajority(game.getGameState());
        return playerId.equals(playerWithSenateMajority);
    }

    /**
     * Determines if the card is in a senate majority.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean isInSenateMajority(SwccgGame game, PhysicalCard card) {
        return new InSenateMajorityCondition(card).isFulfilled(game.getGameState(), game.getModifiersQuerying());
    }

    /**
     * Determines if all pilots aboard the specified card are in a search party.
     * @param game the game
     * @param card the card
     * @return true or false
     */
    public static boolean isAllPilotsAboardInSearchParty(SwccgGame game, PhysicalCard card) {
        Collection<PhysicalCard> searchParty = game.getGameState().getSearchParty();
        return Filters.piloted.accepts(game, card)
                && !Filters.canSpot(game, card, Filters.and(Filters.pilot, Filters.aboard(card), Filters.not(Filters.in(searchParty))));
    }


    // Checks if player release an unattended 'frozen' captive at the location.

    /**
     * Determines if the player can form a search party at the site.
     * @param game the game
     * @param playerId the player
     * @param self the site
     */
    public static boolean canFormSearchPartyAtLocation(SwccgGame game, String playerId, PhysicalCard self) {
        if (!Filters.site.accepts(game, self)
                || !isPhaseForPlayer(game, Phase.CONTROL, playerId))
            return false;

        // Check that player has at least one missing character at the site, and at least one character that can join a search party
        if (!Filters.canSpot(game, null, SpotOverride.INCLUDE_MISSING_AND_UNDERCOVER, Filters.and(Filters.owner(playerId), Filters.missing, Filters.at(self))))
            return false;

        if (!Filters.canSpot(game, null, Filters.canJoinSearchPartyAt(playerId, self)))
            return false;

        return true;
    }

    /**
     * Determines if the player can release an unattended 'frozen' captive at the location.
     * @param game the game
     * @param player the player
     * @param self the location
     * @return
     */
    public static boolean canReleaseUnattendedFrozenCaptiveAtLocation(SwccgGame game, String player, PhysicalCard self) {
        // Check that player controls the location and there is an unattended frozen captive at the location
        if (player.equals(game.getLightPlayer())
                && controls(game, player, self)
                && Filters.canSpot(game, null, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.unattendedFrozenCaptive, Filters.at(self))))
            return true;

        return false;
    }

    /**
     * Determines if the card owner (and opponent if needed) can use the required amount of Force to deploy the card to
     * table (not to a specific target).
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the deployment
     * @param playCardOption the play card option
     * @param forFree true if deploying for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param isDejarikRules the card is being deployed using 'Dejarik Rules'
     * @return true if sufficient Force can be used, otherwise false
     */
    public static boolean canUseForceToDeployCard(SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, PlayCardOption playCardOption, boolean forFree, float changeInCost, boolean isDejarikRules) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        float deployCostForPlayer = modifiersQuerying.getDeployCost(gameState, sourceCard, self, null, isDejarikRules, playCardOption, forFree, changeInCost, null, true);
        float deployCostForOpponent = 0;
        boolean useBothForcePiles = modifiersQuerying.isDeployUsingBothForcePiles(gameState, self);
        if (useBothForcePiles) {
            deployCostForOpponent = modifiersQuerying.getDeployCost(gameState, sourceCard, self, null, isDejarikRules, playCardOption, forFree, changeInCost, null, false);
        }

        if (deployCostForPlayer <= 0 && deployCostForOpponent <= 0) {
            return true;
        }

        if (useBothForcePiles) {
            int maxOpponentsForceToUse = modifiersQuerying.getOpponentsForceAvailableToUse(gameState, self.getOwner());

            if (maxOpponentsForceToUse > 1) {
                if (deployCostForOpponent > forceAvailableToUse(game, game.getOpponent(self.getOwner())))
                    return false;

                if ((maxOpponentsForceToUse + deployCostForPlayer) > forceAvailableToUse(game, self.getOwner()))
                    return false;

                return true;
            }
            return (deployCostForPlayer <= forceAvailableToUse(game, self.getOwner()))
                    && (deployCostForOpponent <= forceAvailableToUse(game, game.getOpponent(self.getOwner())));
        }
        return (deployCostForPlayer <= forceAvailableToUse(game, self.getOwner()));
    }

    /**
     * Determines if player can activate Force using the "activate Force" card pile action.
     * @param playerId the player
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param self the card on the top of the Reserve Deck
     * @return true if activate Force with card pile action can be performed, otherwise false
     */
    public static boolean canActivateForceWithCardPileAction(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return self.getZone() == Zone.TOP_OF_RESERVE_DECK && self.getZoneOwner().equals(playerId)
                && isDuringYourPhase(gameState.getGame(), playerId, Phase.ACTIVATE)
                && !modifiersQuerying.isActivatingForceProhibited(gameState, playerId)
                && !modifiersQuerying.isActivateForceFromForceGenerationLimitReached(gameState, playerId);
    }

    /**
     * Determines if player can draw a card using the "draw card" card pile action.
     * @param playerId the player
     * @param game the game
     * @param self the card on the top of the Force Pile
     * @return true if draw card with card pile action can be performed, otherwise false
     */
    public static boolean canDrawCardWithCardPileAction(String playerId, SwccgGame game, PhysicalCard self) {
        return self.getZone() == Zone.TOP_OF_FORCE_PILE && self.getZoneOwner().equals(playerId)
                && isDuringYourPhase(game, playerId, Phase.DRAW);
    }

    /**
     * Determines if player can perform special "once per game battleground download".
     * @param playerId the player
     * @param game the game
     * @param self the card on the top of the Reserve Deck
     * @return true if special battleground download with card pile action can be performed, otherwise false
     */
    public static boolean canPerformSpecialBattlegroundDownload(String playerId, SwccgGame game, PhysicalCard self) {
        return self.getZone() == Zone.TOP_OF_RESERVE_DECK && self.getZoneOwner().equals(playerId)
                && isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && hasReserveDeck(game, playerId)
                && game.getModifiersQuerying().canPerformSpecialBattlegroundDownload(game.getGameState(), playerId);
    }

    /**
     * Determines if player can perform special "upload any card" playtesting action.
     * @param playerId the player
     * @param game the game
     * @param self the card on the top of the Reserve Deck
     * @return true if special playtesting upload with card pile action can be performed, otherwise false
     */
    public static boolean canPerformSpecialPlaytestingUpload(String playerId, SwccgGame game, PhysicalCard self) {
        return self.getZone() == Zone.TOP_OF_RESERVE_DECK && self.getZoneOwner().equals(playerId)
                && hasReserveDeck(game, playerId)
                && game.getFormat().isPlaytesting();
    }

    /**
     * Determines if a card accepted by the forfeit filter was forfeited from a location accepted by the location filter
     * this turn.
     * @param game the game
     * @param forfeitFilter the forfeit filter
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean wasForfeitedFromLocationThisTurn(SwccgGame game, Filterable forfeitFilter, Filterable locationFilter) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        Map<PhysicalCard, Set<PhysicalCard>> locationsMap = modifiersQuerying.getForfeitedFromLocationsThisTurn();
        for (PhysicalCard location : locationsMap.keySet()) {
            if (Filters.and(locationFilter).accepts(gameState, modifiersQuerying, location)) {
                for (PhysicalCard forfeitedCard : locationsMap.get(location)) {
                    if (Filters.and(forfeitFilter).accepts(gameState, modifiersQuerying, forfeitedCard)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the specified player did not deploy an Objective this game.
     * @param game the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean didNotDeployAnObjective(SwccgGame game, String playerId) {
        return game.getGameState().getObjectivePlayed(playerId) == null;
    }

    /**
     * Determines if the specified player has initiated any battles this turn.
     * @param game the game
     * @param playerId the player
     * @return true or false
     */
    public static boolean hasInitiatedBattleThisTurn(SwccgGame game, String playerId) {
        return game.getModifiersQuerying().getNumBattlesInitiatedThisTurn(playerId) > 0;
    }

    /**
     * Determines if the specified player has deployed at least the specified number of cards accepted by the filter this game.
     * @param game the game
     * @param playerId the player
     * @param count the number of cards
     * @param filter the filter
     * @return true or false
     */
    public static boolean hasDeployedAtLeastXCardsThisGame(SwccgGame game, String playerId, int count, Filterable filter) {
        List<PhysicalCard> cardsDeployed = game.getModifiersQuerying().getCardsPlayedThisGame(playerId);
        return Filters.filterCount(cardsDeployed, game, count, filter).size() >= count;
    }

    /**
     * Determines if the specified player has deployed at least the specified number of cards accepted by the filter this turn.
     * @param game the game
     * @param playerId the player
     * @param count the number of cards
     * @param filter the filter
     * @return true or false
     */
    public static boolean hasDeployedAtLeastXCardsThisTurn(SwccgGame game, String playerId, int count, Filterable filter) {
        List<PhysicalCard> cardsDeployed = game.getModifiersQuerying().getCardsPlayedThisTurn(playerId);
        return Filters.filterCount(cardsDeployed, game, count, filter).size() >= count;
    }

    /**
     * Determines if the specified player has deployed as least the specified number cards with ability accepted by the filter this turn.
     * @param game the game
     * @param playerId the player
     * @param count the number of cards
     * @param filter the filter
     * @return true or false
     */
    public static boolean hasDeployedAtLeastXCardsWithAbilityThisTurn(SwccgGame game, String playerId, int count, Filterable filter) {
        List<PhysicalCard> cardsDeployed = game.getModifiersQuerying().getCardsWithAbilityDeployedThisTurn(playerId);
        return Filters.filterCount(cardsDeployed, game, count, filter).size() >= count;
    }

    /**
     * Determines if the specified player has deployed as least the specified number cards accepted by the card filter to
     * the specified location this turn.
     * @param game the game
     * @param playerId the player
     * @param count the number of cards
     * @param cardFilter the card filter
     * @param location the location
     * @return true or false
     */
    public static boolean hasDeployedAtLeastXCardsToLocationThisTurn(SwccgGame game, String playerId, int count, Filterable cardFilter, PhysicalCard location) {
        List<PhysicalCard> cardsDeployed = game.getModifiersQuerying().getCardsPlayedThisTurnToLocation(playerId, location);
        return Filters.filterCount(cardsDeployed, game, count, cardFilter).size() >= count;
    }

    /**
     * Determines if any player has flipped SYCFA with Inkling Of It's Destructive Potential this turn
     * the specified location this turn.
     * @param game the game
     * @return true or false
     */

    public static boolean hasFlippedSYCFAWithInklingThisTurn(SwccgGame game) {
        return game.getModifiersQuerying().hasFlippedSYCFAWithInklingThisTurn();
    }

}
