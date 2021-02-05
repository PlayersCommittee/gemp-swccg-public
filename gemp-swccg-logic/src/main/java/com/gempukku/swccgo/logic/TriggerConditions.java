package com.gempukku.swccgo.logic;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.*;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.TargetingActionUtils;
import com.gempukku.swccgo.logic.timing.results.*;

import java.util.Collection;

// This class contain methods to be used by cards
// to check if the current trigger (i.e. Effect or EffectResult)
// is one that the card is interested in.
//
public class TriggerConditions {

    /**
     * Determines if the beginning of game step of deploying starting locations and Objectives just completed.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isStartingLocationsAndObjectivesCompletedStep(SwccgGame game, EffectResult effectResult) {
        return effectResult.getType() == EffectResult.Type.STARTING_LOCATIONS_AND_OBJECTIVES_STEP_COMPLETE;
    }

    /**
     * Determines if it is the start of a turn.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isStartOfEachTurn(SwccgGame game, EffectResult effectResult) {
        return effectResult.getType() == EffectResult.Type.START_OF_TURN;
    }

    /**
     * Determines if it is the start of the card owner's turn.
     * @param game the game
     * @param effectResult the effect result
     * @param card the card
     * @return true or false
     */
    public static boolean isStartOfYourTurn(SwccgGame game, EffectResult effectResult, PhysicalCard card) {
        return isStartOfYourTurn(game, effectResult, card.getOwner());
    }

    /**
     * Determines if it is the start of the specified player's turn.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isStartOfYourTurn(SwccgGame game, EffectResult effectResult, String playerId) {
        return effectResult.getType() == EffectResult.Type.START_OF_TURN
                && game.getGameState().getCurrentPlayerId().equals(playerId);
    }

    /**
     * Determines if it is the start of the turn for the opponent of the card owner.
     * @param game the game
     * @param effectResult the effect result
     * @param card the card
     * @return true or false
     */
    public static boolean isStartOfOpponentsTurn(SwccgGame game, EffectResult effectResult, PhysicalCard card) {
        return isStartOfOpponentsTurn(game, effectResult, card.getOwner());
    }

    /**
     * Determines if it is the start of the turn for the opponent of the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isStartOfOpponentsTurn(SwccgGame game, EffectResult effectResult, String playerId) {
        return effectResult.getType() == EffectResult.Type.START_OF_TURN
                && !game.getGameState().getCurrentPlayerId().equals(playerId);
    }

    /**
     * Determines if it is the start of the specified phase during either player's turn.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isStartOfEachPhase(SwccgGame game, EffectResult effectResult, Phase phase) {
        return effectResult.getType() == EffectResult.Type.START_OF_PHASE
                && game.getGameState().getCurrentPhase() == phase;
    }

    /**
     * Determines if it is the start of the specified phase during the card owner's turn.
     * @param game the game
     * @param card the card
     * @param effectResult the effect result
     * @param phase the phase
     * @return true or false
     */
    public static boolean isStartOfYourPhase(SwccgGame game, PhysicalCard card, EffectResult effectResult, Phase phase) {
        return isStartOfYourPhase(game, effectResult, phase, card.getOwner());
    }

    /**
     * Determines if it is the start of the specified phase during the specified player's turn.
     * @param game the game
     * @param effectResult the effect result
     * @param phase the phase
     * @param playerId the player
     * @return true or false
     */
    public static boolean isStartOfYourPhase(SwccgGame game, EffectResult effectResult, Phase phase, String playerId) {
        return effectResult.getType() == EffectResult.Type.START_OF_PHASE
                && game.getGameState().getCurrentPhase() == phase
                && game.getGameState().getCurrentPlayerId().equals(playerId);
    }

    /**
     * Determines if it is the start of the specified phase during the card owner's opponent's turn.
     * @param game the game
     * @param card the card
     * @param effectResult the effect result
     * @param phase the phase
     * @return true or false
     */
    public static boolean isStartOfOpponentsPhase(SwccgGame game, PhysicalCard card, EffectResult effectResult, Phase phase) {
        return isStartOfOpponentsPhase(game, effectResult, phase, card.getOwner());
    }

    /**
     * Determines if it is the start of the specified phase during the specified player's opponent's turn.
     * @param game the game
     * @param effectResult the effect result
     * @param phase the phase
     * @param playerId the player
     * @return true or false
     */
    public static boolean isStartOfOpponentsPhase(SwccgGame game, EffectResult effectResult, Phase phase, String playerId) {
        return effectResult.getType() == EffectResult.Type.START_OF_PHASE
                && game.getGameState().getCurrentPhase() == phase
                && !game.getGameState().getCurrentPlayerId().equals(playerId);
    }

    /**
     * Determines if it is the end of a phase.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    private static boolean isEndOfAnyPhase(SwccgGame game, EffectResult effectResult) {
        return effectResult.getType() == EffectResult.Type.END_OF_PHASE;
    }

    /**
     * Determines if it is the end of the specified phase during either player's turn.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isEndOfEachPhase(SwccgGame game, EffectResult effectResult, Phase phase) {
        return effectResult.getType() == EffectResult.Type.END_OF_PHASE
                && game.getGameState().getCurrentPhase() == phase;
    }

    /**
     * Determines if it is the end of the specified phase during the specified player's turn.
     * @param game the game
     * @param effectResult the effect result
     * @param phase the phase
     * @param playerId the player
     * @return true or false
     */
    public static boolean isEndOfPlayersPhase(SwccgGame game, EffectResult effectResult, Phase phase, String playerId) {
        return effectResult.getType() == EffectResult.Type.END_OF_PHASE
                && game.getGameState().getCurrentPhase() == phase
                && game.getGameState().getCurrentPlayerId().equals(playerId);
    }

    /**
     * Determines if it is the end of the specified phase during the card owner's turn.
     * @param game the game
     * @param card the card
     * @param effectResult the effect result
     * @param phase the phase
     * @return true or false
     */
    public static boolean isEndOfYourPhase(SwccgGame game, PhysicalCard card, EffectResult effectResult, Phase phase) {
        return isEndOfYourPhase(game, effectResult, phase, card.getOwner());
    }

    /**
     * Determines if it is the end of the specified phase during the specified player's turn.
     * @param game the game
     * @param effectResult the effect result
     * @param phase the phase
     * @param playerId the player
     * @return true or false
     */
    public static boolean isEndOfYourPhase(SwccgGame game, EffectResult effectResult, Phase phase, String playerId) {
        return effectResult.getType() == EffectResult.Type.END_OF_PHASE
                && game.getGameState().getCurrentPhase() == phase
                && game.getGameState().getCurrentPlayerId().equals(playerId);
    }

    /**
     * Determines if it is the end of the specified phase during the card owner's opponent's turn.
     * @param game the game
     * @param card the card
     * @param effectResult the effect result
     * @param phase the phase
     * @return true or false
     */
    public static boolean isEndOfOpponentsPhase(SwccgGame game, PhysicalCard card, EffectResult effectResult, Phase phase) {
        return isEndOfOpponentsPhase(game, effectResult, phase, card.getOwner());
    }

    /**
     * Determines if it is the end of the specified phase during the specified player's opponent's turn.
     * @param game the game
     * @param effectResult the effect result
     * @param phase the phase
     * @param playerId the player
     * @return true or false
     */
    public static boolean isEndOfOpponentsPhase(SwccgGame game, EffectResult effectResult, Phase phase, String playerId) {
        return effectResult.getType() == EffectResult.Type.END_OF_PHASE
                && game.getGameState().getCurrentPhase() == phase
                && !game.getGameState().getCurrentPlayerId().equals(playerId);
    }

    /**
     * Determines if it is the end of a turn.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isEndOfEachTurn(SwccgGame game, EffectResult effectResult) {
        return effectResult.getType() == EffectResult.Type.END_OF_TURN;
    }

    /**
     * Determines if it is the end of the card owner's turn.
     * @param game the game
     * @param effectResult the effect result
     * @param card the card
     * @return true or false
     */
    public static boolean isEndOfYourTurn(SwccgGame game, EffectResult effectResult, PhysicalCard card) {
        return isEndOfYourTurn(game, effectResult, card.getOwner());
    }

    /**
     * Determines if it is the end of the specified player's turn.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isEndOfYourTurn(SwccgGame game, EffectResult effectResult, String playerId) {
        return effectResult.getType() == EffectResult.Type.END_OF_TURN
                && game.getGameState().getCurrentPlayerId().equals(playerId);
    }

    /**
     * Determines if it is the end of the turn for the opponent of the card owner.
     * @param game the game
     * @param effectResult the effect result
     * @param card the card
     * @return true or false
     */
    public static boolean isEndOfOpponentsTurn(SwccgGame game, EffectResult effectResult, PhysicalCard card) {
        return isEndOfOpponentsTurn(game, effectResult, card.getOwner());
    }

    /**
     * Determines if it is the end of the turn for the opponent of the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isEndOfOpponentsTurn(SwccgGame game, EffectResult effectResult, String playerId) {
        return effectResult.getType() == EffectResult.Type.END_OF_TURN
                && !game.getGameState().getCurrentPlayerId().equals(playerId);
    }


    //
    // These are used for cards that are constantly checking for a condition (so they only fully check that condition
    // after certain triggers happen).
    //

    public static boolean isTableChanged(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.PLAY
                || effectResult.getType() == EffectResult.Type.PLACE_IN_PLAY
                || effectResult.getType() == EffectResult.Type.PERSONA_REPLACED_CHARACTER
                || effectResult.getType() == EffectResult.Type.CONVERT_CHARACTER
                || effectResult.getType() == EffectResult.Type.CROSSED_OVER_CHARACTER
                || effectResult.getType() == EffectResult.Type.SQUADRON_REPLACEMENT
                || leavesTable(game, effectResult, Filters.any)
                || justExcludedFromBattle(game, effectResult, Filters.any)
                || battleInitiated(game, effectResult)
                || battleEnded(game, effectResult)
                || duelInitiated(game, effectResult)
                || duelCanceled(game, effectResult)
                || duelEnded(game, effectResult)
                || effectResult.getType() == EffectResult.Type.START_OF_TURN
                || effectResult.getType() == EffectResult.Type.START_OF_PHASE
                || effectResult.getType() == EffectResult.Type.END_OF_PHASE
                || effectResult.getType() == EffectResult.Type.END_OF_TURN
                || gameTextCanceled(game, effectResult)
                || gameTextRestored(game, effectResult)
                || cardSuspended(game, effectResult)
                || cardResumed(game, effectResult)
                || binaryDroidTurnedOff(game, effectResult)
                || binaryDroidTurnedOn(game, effectResult)
                || cardStatModifiedOrReset(game, effectResult)
                || moved(game, effectResult, Filters.any)
                || putUndercover(game, effectResult, Filters.any)
                || coverBroken(game, effectResult, Filters.any)
                || goneMissing(game, effectResult, Filters.any)
                || missingCharacterFound(game, effectResult, Filters.any)
                || captured(game, effectResult, Filters.any)
                || released(game, effectResult, Filters.any)
                || captiveDelivered(game, effectResult, Filters.any)
                || tookImprisonedCaptiveIntoCustody(game, effectResult, Filters.any)
                || leftFrozenCaptiveUnattended(game, effectResult, Filters.any)
                || tookUnattendedFrozenCaptiveIntoCustody(game, effectResult, Filters.any)
                || cardFlipped(game, effectResult, Filters.any)
                || forEachAsteroidCaveOrSpaceSlugBellyChanged(game, effectResult)
                || forEachSpaceSlugMouthChanged(game, effectResult)
                || transferredBetweenDockedStarships(game, effectResult)
                || isBlownAwayLastStep(game, effectResult, Filters.any)
                || effectResult.getType() == EffectResult.Type.HIT
                || effectResult.getType() == EffectResult.Type.RETURNED_TO_HAND_FROM_OFF_TABLE
                || effectResult.getType() == EffectResult.Type.REMOVED_COAXIUM_CARD
                || effectResult.getType() == EffectResult.Type.STACKED_FROM_CARD_PILE
                || effectResult.getType() == EffectResult.Type.STACKED_FROM_HAND
                || effectResult.getType() == EffectResult.Type.STACKED_FROM_TABLE
                || effectResult.getType() == EffectResult.Type.CONCEALED
                || effectResult.getType() == EffectResult.Type.UNCONCEALED
                || effectResult.getType() == EffectResult.Type.UTINNI_EFFECT_COMPLETED
                || effectResult.getType() == EffectResult.Type.ATTACH_FROM_TABLE
                || effectResult.getType() == EffectResult.Type.RELOCATE_TO_SIDE_OF_TABLE
                || effectResult.getType() == EffectResult.Type.RESTORED_TO_NORMAL
                || effectResult.getType() == EffectResult.Type.COLLAPSED_SITE
                || effectResult.getType() == EffectResult.Type.RELOCATED_TO_WEATHER_VANE
                || effectResult.getType() == EffectResult.Type.RELOCATED_FROM_LOST_IN_SPACE_OR_WEATHER_VANE_TO_LOCATION
                || effectResult.getType() == EffectResult.Type.STOLEN
                || effectResult.getType() == EffectResult.Type.TRANSFERRED_CAPTIVE_TO_NEW_ESCORT
                || effectResult.getType() == EffectResult.Type.ROTATE_CARD
                || effectResult.getType() == EffectResult.Type.TRANSFERRED_DEVICE_OR_WEAPON
                || effectResult.getType() == EffectResult.Type.CONVERT_LOCATION
                || effectResult.getType() == EffectResult.Type.BATTLE_CANCELED
                || effectResult.getType() == EffectResult.Type.ATTACK_INITIATED
                || effectResult.getType() == EffectResult.Type.ATTACK_CANCELED
                || effectResult.getType() == EffectResult.Type.ATTACK_ENDED
                || effectResult.getType() == EffectResult.Type.PODRACE_INITIATED
                || effectResult.getType() == EffectResult.Type.PODRACE_FINISHED
                || effectResult.getType() == EffectResult.Type.RACE_DESTINY_STACKED
                || effectResult.getType() == EffectResult.Type.PODRACER_DAMAGED
                || effectResult.getType() == EffectResult.Type.PODRACER_REPAIRED
                || effectResult.getType() == EffectResult.Type.JEDI_TEST_COMPLETED
                || effectResult.getType() == EffectResult.Type.LIGHTSABER_COMBAT_INITIATED
                || effectResult.getType() == EffectResult.Type.FORCE_LOST
                || effectResult.getType() == EffectResult.Type.BULLSEYED
                || effectResult.getType() == EffectResult.Type.DEFEATED
                || effectResult.getType() == EffectResult.Type.EATEN
                || effectResult.getType() == EffectResult.Type.PARASITE_ATTACHED
                || effectResult.getType() == EffectResult.Type.PARASITE_DETACHED
                || effectResult.getType() == EffectResult.Type.RETARGETED_EFFECT
                || effectResult.getType() == EffectResult.Type.DARK_HOURS_EFFECT);

        // TODO: Just checking EffectResult.getType() would be faster???

        // TODO: Add effect result when "modifiers" are removed at end of turn, etc., since want this to trigger then, too???
    }


    private static boolean gameTextCanceled(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.CANCELED_GAME_TEXT);
    }

    private static boolean gameTextRestored(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.RESTORED_GAME_TEXT);
    }

    private static boolean cardSuspended(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.SUSPEND_CARD);
    }

    private static boolean cardResumed(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.RESUME_CARD);
    }

    private static boolean binaryDroidTurnedOff(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.TURN_OFF_BINARY_DROID);
    }

    private static boolean binaryDroidTurnedOn(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.TURN_ON_BINARY_DROID);
    }

    public static boolean cardStatModifiedOrReset(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.ATTRIBUTE_RESET_OR_MODIFIED);
    }

    public static boolean transferredBetweenDockedStarships(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.TRANSFERRED_BETWEEN_DOCKED_STARSHIPS);
    }

    /**
     * Determines if a card accepeted by the played card filter is being played/deployed.
     * @param game the game
     * @param effect the effect
     * @param playedCardFilter the played card filter
     * @return true or false
     */
    public static boolean isPlayingCard(SwccgGame game, Effect effect, Filterable playedCardFilter) {
        if (effect.getType() == Effect.Type.PLAYING_CARD_EFFECT) {
            if (!effect.isCanceled()) {
                RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;

                // Checked card being played/deployed
                return Filters.and(playedCardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), respondableEffect.getCard());
            }
        }
        return false;
    }

    /**
     * Determines if a card accepeted by the played card filter is being deployed for specified reason.
     * @param game the game
     * @param effect the effect
     * @param playedCardFilter the played card filter
     * @param actionReason the reason card is being played
     * @return true or false
     */
    public static boolean isPlayingCardForReason(SwccgGame game, Effect effect, Filterable playedCardFilter, PlayCardActionReason actionReason) {
        if (effect.getType() == Effect.Type.PLAYING_CARD_EFFECT) {
            if (!effect.isCanceled()) {
                RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;
                if (respondableEffect.isPlayingForReason(actionReason)) {
                    // Checked card being played/deployed
                    return Filters.and(playedCardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), respondableEffect.getCard());
                }
            }
        }
        return false;
    }

    /**
     * Determines if a card accepeted by the played card filter is being played/deployed as an 'insert' card.
     * @param game the game
     * @param effect the effect
     * @param playedCardFilter the played card filter
     * @return true or false
     */
    public static boolean isPlayingCardAsInsertCard(SwccgGame game, Effect effect, Filterable playedCardFilter) {
        if (effect.getType() == Effect.Type.PLAYING_CARD_EFFECT) {
            if (!effect.isCanceled()) {
                RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;
                if (respondableEffect.isAsInsertCard()) {
                    // Checked card being played/deployed
                    return Filters.and(playedCardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), respondableEffect.getCard());
                }
            }
        }
        return false;
    }

    /**
     * Determines if a card accepeted by the played card filter is being played/deployed by the specified player.
     * @param game the game
     * @param effect the effect
     * @param playerId the player
     * @param playedCardFilter the played card filter
     * @return true or false
     */
    public static boolean isPlayingCard(SwccgGame game, Effect effect, String playerId, Filterable playedCardFilter) {
        if (effect.getType() == Effect.Type.PLAYING_CARD_EFFECT) {
            if (playerId.equals(effect.getAction().getPerformingPlayer()) && !effect.isCanceled()) {
                RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;

                // Checked card being played/deployed
                return Filters.and(playedCardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), respondableEffect.getCard());
            }
        }
        return false;
    }

    /**
     * Determines if a card accepeted by the played card filter is being played/deployed by the specified player.
     * @param game the game
     * @param effect the effect
     * @param playerId the player
     * @param playedCardFilter the played card filter
     * @return true or false
     */
    public static boolean isPlayingCardFromLostPile(SwccgGame game, Effect effect, String playerId, Filterable playedCardFilter) {
        if (effect.getType() == Effect.Type.PLAYING_CARD_EFFECT) {
            if (playerId.equals(effect.getAction().getPerformingPlayer()) && !effect.isCanceled()) {
                RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;

                // Checked card being played/deployed from Lost Pile
                return GameUtils.getZoneFromZoneTop(respondableEffect.getPlayingFromZone()) == Zone.LOST_PILE
                    && Filters.and(playedCardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), respondableEffect.getCard());
            }
        }
        return false;
    }


    /**
     * Determines if a card accepted by the played card filter is being played/deployed and is targeting a card accepted
     * by the target filter.
     * @param game the game
     * @param effect the effect
     * @param playedCardFilter the played card filter
     * @param targetFilter the target filter
     * @return true or false
     */
    public static boolean isPlayingCardTargeting(SwccgGame game, Effect effect, Filterable playedCardFilter, Filterable targetFilter) {
        if (effect.getType() == Effect.Type.PLAYING_CARD_EFFECT) {
            if (!effect.isCanceled()) {
                RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;
                PhysicalCard cardBeingPlayed = respondableEffect.getCard();

                // Checked card being played/deployed
                if (Filters.and(playedCardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), cardBeingPlayed)) {
                    // Check targeting
                    return TargetingActionUtils.isTargeting(game, respondableEffect.getTargetingAction(), Filters.and(targetFilter));
                }
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by the played card filter is being played/deployed by the specified player and is
     * targeting a card accepted by the target filter.
     * @param game the game
     * @param effect the effect
     * @param playerId the player
     * @param playedCardFilter the played card filter
     * @param targetFilter the target filter
     * @return true or false
     */
    public static boolean isPlayingCardTargeting(SwccgGame game, Effect effect, String playerId, Filterable playedCardFilter, Filterable targetFilter) {
        if (effect.getType() == Effect.Type.PLAYING_CARD_EFFECT) {
            if (playerId.equals(effect.getAction().getPerformingPlayer()) && !effect.isCanceled()) {
                RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;
                PhysicalCard cardBeingPlayed = respondableEffect.getCard();

                // Checked card being played/deployed
                if (Filters.and(playedCardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), cardBeingPlayed)) {
                    // Check targeting
                    return TargetingActionUtils.isTargeting(game, respondableEffect.getTargetingAction(), Filters.and(targetFilter));
                }
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by the played card filter is being played/deployed and is targeting a card accepted
     * by the target filter with the specified targeting reason.
     * @param game the game
     * @param effect the effect
     * @param playedCardFilter the played card filter
     * @param targetingReason the reason to target
     * @param targetFilter the target filter
     * @return true or false
     */
    public static boolean isPlayingCardTargeting(SwccgGame game, Effect effect, Filterable playedCardFilter, TargetingReason targetingReason, Filterable targetFilter) {
        if (effect.getType() == Effect.Type.PLAYING_CARD_EFFECT) {
            if (!effect.isCanceled()) {
                RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;
                PhysicalCard cardBeingPlayed = respondableEffect.getCard();

                // Checked card being played/deployed
                if (Filters.and(playedCardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), cardBeingPlayed)) {
                    // Check targeting
                    return TargetingActionUtils.isTargeting(game, respondableEffect.getTargetingAction(), targetingReason, Filters.and(targetFilter));
                }
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by the card filter is performing a game text action.
     * @param game the game
     * @param effect the effect
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean isPerformingGameTextAction(SwccgGame game, Effect effect, Filterable cardFilter) {
        if (effect.getType() == Effect.Type.RESPONDABLE_EFFECT) {
            if (!effect.isCanceled()) {
                Action action = effect.getAction();
                // Checked card performing action
                return action.isFromGameText()
                        && Filters.and(cardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), action.getActionSource());
            }
        }
        return false;
    }

    public static boolean isPerformingGameTextActionType(SwccgGame game, Effect effect, Filterable cardFilter, GameTextActionId gameTextActionId)
    {
        if (effect.getType() == Effect.Type.RESPONDABLE_EFFECT
            && effect.getAction().getGameTextActionId() == gameTextActionId) {
            if (!effect.isCanceled()) {
                Action action = effect.getAction();
                // Checked card performing action
                return action.isFromGameText()
                        && Filters.and(cardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), action.getActionSource());
            }
        }
        return false;
    }

    /**
     * Determine if a card accepted by the card to stack filter was just stacked on a card accepted by the stacked on filter.
     * @param game the game
     * @param effectResult the effect result
     * @param cardToStackFilter the card to stack filter
     * @param stackedOnFilter the stacked on filter
     * @return true or false
     */
    public static boolean justStackedCardOn(SwccgGame game, EffectResult effectResult, Filterable cardToStackFilter, Filterable stackedOnFilter) {
        if (effectResult.getType() == EffectResult.Type.STACKED_FROM_HAND) {
            StackedFromHandResult stackedFromHandResult = (StackedFromHandResult) effectResult;
            return Filters.and(cardToStackFilter).accepts(game, stackedFromHandResult.getCard())
                    && Filters.and(stackedOnFilter).accepts(game, stackedFromHandResult.getStackedOn());
        }
        if (effectResult.getType() == EffectResult.Type.STACKED_FROM_CARD_PILE) {
            StackedFromCardPileResult stackedFromCardPileResult = (StackedFromCardPileResult) effectResult;
            return Filters.and(cardToStackFilter).accepts(game, stackedFromCardPileResult.getCard())
                    && Filters.and(stackedOnFilter).accepts(game, stackedFromCardPileResult.getStackedOn());
        }
        if (effectResult.getType() == EffectResult.Type.STACKED_FROM_TABLE) {
            StackedFromTableResult stackedFromTableResult = (StackedFromTableResult) effectResult;
            return Filters.and(cardToStackFilter).accepts(game, stackedFromTableResult.getCard())
                    && Filters.and(stackedOnFilter).accepts(game, stackedFromTableResult.getStackedOn());
        }
        if (effectResult.getType() == EffectResult.Type.FORCE_LOST) {
            LostForceResult lostForceResult = (LostForceResult) effectResult;
            PhysicalCard stackedOn = lostForceResult.getForceStackedOn();
            return stackedOn != null
                    && Filters.and(cardToStackFilter).accepts(game, lostForceResult.getCardLost())
                    && Filters.and(stackedOnFilter).accepts(game, stackedOn);
        }
        return false;
    }


    /**
     * Determine if a card accepted by the filter was just deployed (and is still in play).
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean justDeployed(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.PLAY) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            if (playCardResult.isDeployed()) {
                return Filters.and(Filters.in_play, filter).accepts(game.getGameState(), game.getModifiersQuerying(), playCardResult.getPlayedCard());
            }
        }
        return false;
    }

    /**
     * Determine if a card accepted by the filter was just deployed by the specified player (and is still in play).
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param filter the filter
     * @return true or false
     */
    public static boolean justDeployed(SwccgGame game, EffectResult effectResult, String playerId, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.PLAY) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            if (playerId.equals(playCardResult.getPerformingPlayerId())
                    && playCardResult.isDeployed()) {
                return Filters.and(Filters.in_play, filter).accepts(game.getGameState(), game.getModifiersQuerying(), playCardResult.getPlayedCard());
            }
        }
        return false;
    }

    /**
     * Determine if a card accepted by the filter was just deployed to a location accepted by the location filter (and is still in play).
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean justDeployedTo(SwccgGame game, EffectResult effectResult, Filterable filter, Filter locationFilter) {
        if (effectResult.getType() == EffectResult.Type.PLAY) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            if (playCardResult.isDeployed()) {
                return Filters.and(filter, Filters.at(locationFilter)).accepts(game.getGameState(), game.getModifiersQuerying(), playCardResult.getPlayedCard());
            }
        }
        return false;
    }

    /**
     * Determine if a card accepted by the filter was just deployed by the specified player to a location accepted by the location filter (and is still in play).
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param filter the filter
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean justDeployedTo(SwccgGame game, EffectResult effectResult, String playerId, Filterable filter, Filter locationFilter) {
        if (effectResult.getType() == EffectResult.Type.PLAY) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            if (playerId.equals(playCardResult.getPerformingPlayerId())
                    && playCardResult.isDeployed()) {
                return Filters.and(filter, Filters.at(locationFilter)).accepts(game.getGameState(), game.getModifiersQuerying(), playCardResult.getPlayedCard());
            }
        }
        return false;
    }

    /**
     * Determine if a card accepted by the filter was just deployed aboard a starship or vehicle accepted by the starship/vehicle filter.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @param starshipOrVehicleFilter the starship/vehicle filter
     * @return true or false
     */
    public static boolean justDeployedAboard(SwccgGame game, EffectResult effectResult, Filterable filter, Filterable starshipOrVehicleFilter) {
        if (effectResult.getType() == EffectResult.Type.PLAY) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            if (playCardResult.isDeployed()) {
                return Filters.and(filter, Filters.aboard(Filters.and(starshipOrVehicleFilter))).accepts(game.getGameState(), game.getModifiersQuerying(), playCardResult.getPlayedCard());
            }
        }
        return false;
    }

    /**
     * Determine if a character accepted by the filter just persona replaced another version of the same persona (and is still in play).
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean justPersonaReplacedCharacter(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.PERSONA_REPLACED_CHARACTER) {
            PersonaReplacedCharacterResult personaReplacedCharacterResult = (PersonaReplacedCharacterResult) effectResult;
            return Filters.and(Filters.in_play, filter).accepts(game.getGameState(), game.getModifiersQuerying(), personaReplacedCharacterResult.getNewCharacter());
        }
        return false;
    }

    /**
     * Determines a card accepted by the deployed card filter was just deployed to the specified system.
     * @param game the game
     * @param effectResult the effect result
     * @param deployedCardFilter the deployed card filter
     * @param system the system name
     * @return true or false
     */
    public static boolean justDeployedToSystem(SwccgGame game, EffectResult effectResult, Filterable deployedCardFilter, String system) {
        if (effectResult.getType() == EffectResult.Type.PLAY) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            if (playCardResult.isDeployed()
                    && playCardResult.getToLocation() != null) {
                GameState gameState = game.getGameState();
                ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                // The just deployed card must still be 'in play'
                return Filters.and(Filters.in_play, deployedCardFilter).accepts(gameState, modifiersQuerying, playCardResult.getPlayedCard())
                        && Filters.and(Filters.location, Filters.partOfSystem(system)).accepts(gameState, modifiersQuerying, playCardResult.getToLocation());
            }
        }
        return false;
    }

    /**
     * Determines a card accepted by the deployed card filter was just deployed to a target accepted by the target filter.
     * @param game the game
     * @param effectResult the effect result
     * @param deployedCardFilter the deployed card filter
     * @param targetFilter the target filter
     * @return true or false
     */
    public static boolean justDeployedToTarget(SwccgGame game, EffectResult effectResult, Filterable deployedCardFilter, Filterable targetFilter) {
        if (effectResult.getType() == EffectResult.Type.PLAY) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            if (playCardResult.isDeployed()
                    && (playCardResult.getAttachedTo() != null || playCardResult.getAtLocation() != null)) {
                GameState gameState = game.getGameState();
                ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                // The just deployed card must still be 'in play'
                return Filters.and(Filters.in_play, deployedCardFilter).accepts(gameState, modifiersQuerying, playCardResult.getPlayedCard())
                        && ((playCardResult.getAttachedTo() != null && Filters.and(targetFilter).accepts(gameState, modifiersQuerying, playCardResult.getAttachedTo()))
                        || (playCardResult.getAtLocation() != null && Filters.and(targetFilter).accepts(gameState, modifiersQuerying, playCardResult.getAtLocation())));
            }
        }
        return false;
    }

    /**
     * Determines a card accepted by the deployed card filter was just deployed to a location accepted by the location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param deployedCardFilter the deployed card filter
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean justDeployedToLocation(SwccgGame game, EffectResult effectResult, Filterable deployedCardFilter, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.PLAY) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            if (playCardResult.isDeployed()
                    && playCardResult.getToLocation() != null) {
                GameState gameState = game.getGameState();
                ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                // The just deployed card must still be 'in play'
                return Filters.and(Filters.in_play, deployedCardFilter).accepts(gameState, modifiersQuerying, playCardResult.getPlayedCard())
                        && Filters.and(Filters.location, locationFilter).accepts(gameState, modifiersQuerying, playCardResult.getToLocation());
            }
        }
        return false;
    }

    /**
     * Determines if the specified player just deployed a card accepted by the deployed card filter to a location accepted
     * by the location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param deployedCardFilter the deployed card filter
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean justDeployedToLocation(SwccgGame game, EffectResult effectResult, String playerId,
                                                 Filterable deployedCardFilter, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.PLAY) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            if (playerId.equals(playCardResult.getPerformingPlayerId())
                    && playCardResult.isDeployed()
                    && playCardResult.getToLocation() != null) {
                GameState gameState = game.getGameState();
                ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                // The just deployed card must still be 'in play'
                return Filters.and(Filters.in_play, deployedCardFilter).accepts(gameState, modifiersQuerying, playCardResult.getPlayedCard())
                        && Filters.and(Filters.location, locationFilter).accepts(gameState, modifiersQuerying, playCardResult.getToLocation());
            }
        }
        return false;
    }

    /**
     * Determine if a card accepted by the filter was just deployed from hand to a location accepted by the location filter (and is still in play).
     * @param game the game
     * @param effectResult the effect result
     * @param deployedCardFilter the deployed card filter
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean justDeployedFromHandToLocation(SwccgGame game, EffectResult effectResult, Filterable deployedCardFilter, Filter locationFilter) {
        if (effectResult.getType() == EffectResult.Type.PLAY) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            if (playCardResult.isDeployed()
                    && playCardResult.getPlayedCardFrom() == Zone.HAND
                    && playCardResult.getToLocation() != null) {
                GameState gameState = game.getGameState();
                ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                // The just deployed card must still be 'in play'
                return Filters.and(Filters.in_play, deployedCardFilter).accepts(gameState, modifiersQuerying, playCardResult.getPlayedCard())
                        && Filters.and(Filters.location, locationFilter).accepts(gameState, modifiersQuerying, playCardResult.getToLocation());
            }
        }
        return false;
    }


    /**
     * Determine if a card accepted by the filter was just placed in play.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean justPlacedInPlay(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.PLACE_IN_PLAY) {
            PlaceCardInPlayResult placeCardInPlayResult = (PlaceCardInPlayResult) effectResult;
            return Filters.and(Filters.in_play, filter).accepts(game.getGameState(), game.getModifiersQuerying(), placeCardInPlayResult.getPlacedCard());
        }
        return false;
    }

    /**
     * Determines a device or weapon accepted by the device or weapon filter was just transferred to a target accepted by the target filter.
     * @param game the game
     * @param effectResult the effect result
     * @param deviceOrWeaponFilter the device or weapon filter
     * @param targetFilter the target filter
     * @return true or false
     */
    public static boolean justTransferredDeviceOrWeaponToTarget(SwccgGame game, EffectResult effectResult, Filterable deviceOrWeaponFilter, Filterable targetFilter) {
        if (effectResult.getType() == EffectResult.Type.TRANSFERRED_DEVICE_OR_WEAPON) {
            TransferredDeviceOrWeaponResult transferredDeviceOrWeaponResult = (TransferredDeviceOrWeaponResult) effectResult;
            return Filters.and(deviceOrWeaponFilter).accepts(game, transferredDeviceOrWeaponResult.getDeviceOrWeapon())
                    && Filters.and(targetFilter).accepts(game, transferredDeviceOrWeaponResult.getTransferredTo());
        }
        return false;
    }

    /**
     * Determine if an 'insert' card accepted by the filter was just revealed
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean justRevealedInsertCard(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.INSERT_CARD_REVEALED) {
            PhysicalCard insertCard = ((InsertCardRevealedResult) effectResult).getCard();
            if (insertCard.isInsertCardRevealed()) {
                return Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), insertCard);
            }
        }
        return false;
    }

    /**
     * Determines if the specified player just 'reacted' to a battle.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean reactedToBattle(SwccgGame game, EffectResult effectResult, String playerId) {
        if (game.getGameState().isDuringBattle()
                && playerId.equals(effectResult.getPerformingPlayerId())) {
            return reactedToLocation(game, effectResult, playerId, Filters.battleLocation);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the card filter just 'reacted' to a location accepted by the location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean reactedToLocation(SwccgGame game, EffectResult effectResult, Filterable cardFilter, Filter locationFilter) {
        if (game.getGameState().isDuringMoveAsReact() && game.getGameState().getMoveAsReactState().canContinue()) {
            return movedFromLocationToLocation(game, effectResult, cardFilter, Filters.any, Filters.and(locationFilter));
        }
        if (effectResult.getType() == EffectResult.Type.PLAY) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            if (playCardResult.isDeployed() && playCardResult.isAsReact()) {
                return Filters.and(cardFilter).accepts(game, playCardResult.getPlayedCard())
                        && Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), playCardResult.getToLocation());
            }
        }
        return false;
    }

    /**
     * Determines if the specified player just 'reacted' to a location accepted by the location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean reactedToLocation(SwccgGame game, EffectResult effectResult, String playerId, Filter locationFilter) {
        if (game.getGameState().isDuringMoveAsReact() && game.getGameState().getMoveAsReactState().canContinue()) {
            return movedFromLocationToLocation(game, effectResult, Filters.owner(playerId), Filters.any, Filters.and(locationFilter));
        }
        if (effectResult.getType() == EffectResult.Type.PLAY) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            if (playerId.equals(playCardResult.getPerformingPlayerId())
                    && playCardResult.isDeployed() && playCardResult.isAsReact()) {
                return Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), playCardResult.getToLocation());
            }
        }
        return false;
    }

    /**
     * Determines if the specified player is about to use Force.
     * @param game the game
     * @param effect the effect
     * @param playerId the player
     * @return true or false
     */
    public static boolean isUsingForce(SwccgGame game, Effect effect, String playerId) {
        if (effect.getType() == Effect.Type.BEFORE_USE_FORCE) {
            return playerId.equals(((UseForceEffect) effect).getPlayerId());
        }
        return false;
    }

    /**
     * Determines if the specified player is about to draw a card from Force Pile.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isAboutToDrawCardFromForcePile(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_DRAW_CARD_FROM_FORCE_PILE) {
            return playerId.equals(effectResult.getPerformingPlayerId());
        }
        return false;
    }

    /**
     * Determines if a card accepted by the weapon user filter is firing a weapon accepted by the weapon filter.
     * @param game the game
     * @param effect the effect
     * @return true or false
     */
    public static boolean isFiringWeapon(SwccgGame game, Effect effect) {
        if (effect.getType() == Effect.Type.WEAPON_FIRING_EFFECT) {
            if (!effect.isCanceled()) {
                WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();

                if (weaponFiringState != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by the weapon user filter is firing a weapon accepted by the weapon filter.
     * @param game the game
     * @param effect the effect
     * @param weaponFilter the weapon filter
     * @param weaponUserFilter the weapon user filter
     * @return true or false
     */
    public static boolean isFiringWeapon(SwccgGame game, Effect effect, Filterable weaponFilter, Filterable weaponUserFilter) {
        if (effect.getType() == Effect.Type.WEAPON_FIRING_EFFECT) {
            if (!effect.isCanceled()) {
                WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();

                if (weaponFiringState != null) {
                    PhysicalCard weapon = weaponFiringState.getCardFiring();
                    SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();
                    PhysicalCard weaponUser = weaponFiringState.getCardFiringWeapon();

                    return ((weapon != null && Filters.and(weaponFilter).accepts(game, weapon))
                            || (permanentWeapon != null && Filters.and(weaponFilter).accepts(game, permanentWeapon)))
                            && (weaponUser != null && Filters.and(weaponUserFilter).accepts(game, weaponUser));
                }
            }
        }
        return false;
    }

    /**
     * Determines if the specified player is firing a weapon accepted by the weapon filter.
     * @param game the game
     * @param effect the effect
     * @param playerId the player
     * @param weaponFilter the weapon filter
     * @return true or false
     */
    public static boolean isFiringWeapon(SwccgGame game, Effect effect, String playerId, Filterable weaponFilter) {
        if (effect.getType() == Effect.Type.WEAPON_FIRING_EFFECT) {
            if (!effect.isCanceled()) {
                WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();

                if (weaponFiringState != null) {
                    PhysicalCard weapon = weaponFiringState.getCardFiring();
                    SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();

                    return ((weapon != null && Filters.and(Filters.owner(playerId), weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), weapon))
                            || (permanentWeapon != null && Filters.and(Filters.owner(playerId), weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), permanentWeapon)));
                }
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by the target filter is being targeted by a weapon accepted by the weapon filter.
     * @param game the game
     * @param effect the effect
     * @param targetFilter the target filter
     * @param weaponFilter the weapon filter
     * @return true or false
     */
    public static boolean isTargetedByWeapon(SwccgGame game, Effect effect, Filterable targetFilter, Filterable weaponFilter) {
        if (effect.getType() == Effect.Type.WEAPON_FIRING_EFFECT) {
            if (!effect.isCanceled()) {
                WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();

                if (weaponFiringState != null) {
                    Collection<PhysicalCard> targets = weaponFiringState.getTargets();
                    PhysicalCard weapon = weaponFiringState.getCardFiring();
                    SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();

                    return Filters.canSpot(targets, game, targetFilter)
                            && ((weapon != null && Filters.and(weaponFilter).accepts(game, weapon))
                            || (permanentWeapon != null && Filters.and(weaponFilter).accepts(game, permanentWeapon)));
                }
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by the target filter is being targeted by any of the specified reasons.
     * @param game the game
     * @param effect the effect
     * @param playerId the player
     * @param targetFilter the target filter
     * @param targetingReasons the targeting reasons
     * @return true or false
     */
    public static boolean isTargetedForReason(SwccgGame game, Effect effect, String playerId, Filterable targetFilter, Collection<TargetingReason> targetingReasons) {
        if (effect.getType() == Effect.Type.PLAYING_CARD_EFFECT
                || effect.getType() == Effect.Type.RESPONDABLE_EFFECT
                || effect.getType() == Effect.Type.WEAPON_FIRING_EFFECT) {
            if (!effect.isCanceled()) {
                Action targetingAction = effect.getAction();
                if (playerId.equals(targetingAction.getPerformingPlayer())) {
                    return TargetingActionUtils.isTargeting(game, targetingAction, targetingReasons, Filters.and(targetFilter));
                }
            }
        }
        return false;
    }
    
    /**
     * Determines if a card accepted by the card filter is beginning to move through hyperspace.
     * @param game the game
     * @param effectResult the effect result
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean movingThroughHyperspace(SwccgGame game, EffectResult effectResult, Filterable cardFilter) {
        if (effectResult.getType() == EffectResult.Type.MOVING_USING_HYPERSPEED) {
            MovingResult movingResult = (MovingResult) effectResult;
            PhysicalCard cardMoving = movingResult.getCardMoving();
            if (cardMoving != null
                    && (movingResult.getPreventableCardEffect() == null
                    || !movingResult.getPreventableCardEffect().isEffectOnCardPrevented(cardMoving))) {
                if (Filters.in_play.accepts(game, cardMoving)
                        && Filters.and(cardFilter).accepts(game, cardMoving)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by the card filter is beginning to move using landspeed.
     * @param game the game
     * @param effectResult the effect result
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean movingUsingLandspeed(SwccgGame game, EffectResult effectResult, Filterable cardFilter) {
        if (effectResult.getType() == EffectResult.Type.MOVING_USING_LANDSPEED) {
            MovingResult movingResult = (MovingResult) effectResult;
            PhysicalCard cardMoving = movingResult.getCardMoving();
            if (cardMoving != null
                    && (movingResult.getPreventableCardEffect() == null
                    || !movingResult.getPreventableCardEffect().isEffectOnCardPrevented(cardMoving))) {
                if (Filters.in_play.accepts(game, cardMoving)
                        && Filters.and(cardFilter).accepts(game, cardMoving)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by the card filter is beginning to move.
     * @param game the game
     * @param effectResult the effect result
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean moving(SwccgGame game, EffectResult effectResult, Filterable cardFilter) {
        if (effectResult.getType() == EffectResult.Type.MOVING_USING_LANDSPEED
                || effectResult.getType() == EffectResult.Type.MOVING_USING_HYPERSPEED
                || effectResult.getType() == EffectResult.Type.MOVING_WITHOUT_USING_HYPERSPEED
                || effectResult.getType() == EffectResult.Type.MOVING_USING_SECTOR_MOVEMENT
                || effectResult.getType() == EffectResult.Type.LANDING
                || effectResult.getType() == EffectResult.Type.TAKING_OFF
                || effectResult.getType() == EffectResult.Type.MOVING_TO_RELATED_STARSHIP_OR_VEHICLE
                || effectResult.getType() == EffectResult.Type.MOVING_TO_RELATED_STARSHIP_OR_VEHICLE_SITE
                || effectResult.getType() == EffectResult.Type.ENTERING_STARSHIP_OR_VEHICLE_SITE
                || effectResult.getType() == EffectResult.Type.EXITING_STARSHIP_OR_VEHICLE_SITE
                || effectResult.getType() == EffectResult.Type.EMBARKING
                || effectResult.getType() == EffectResult.Type.DISEMBARKING
                || effectResult.getType() == EffectResult.Type.SHUTTLING
                || effectResult.getType() == EffectResult.Type.MOVING_USING_LOCATION_TEXT
                || effectResult.getType() == EffectResult.Type.DOCKING_BAY_TRANSITING
                || effectResult.getType() == EffectResult.Type.RELOCATING_BETWEEN_LOCATIONS
                || effectResult.getType() == EffectResult.Type.MOVING_TO_START_BOMBING_RUN
                || effectResult.getType() == EffectResult.Type.MOVING_TO_END_BOMBING_RUN
                || effectResult.getType() == EffectResult.Type.MOVING_AT_START_OF_ATTACK_RUN
                || effectResult.getType() == EffectResult.Type.MOVING_AT_END_OF_ATTACK_RUN) {
            MovingResult movingResult = (MovingResult) effectResult;
            PhysicalCard cardMoving = movingResult.getCardMoving();
            if (cardMoving != null
                    && (movingResult.getPreventableCardEffect() == null
                    || !movingResult.getPreventableCardEffect().isEffectOnCardPrevented(cardMoving))) {
                if (Filters.in_play.accepts(game, cardMoving)
                        && Filters.and(cardFilter).accepts(game, cardMoving)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines if a card accepted by the card filter is beginning to move from a location accepted by the location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean movingFromLocation(SwccgGame game, EffectResult effectResult, Filterable cardFilter, Filterable locationFilter) {
        if (moving(game, effectResult, cardFilter)) {
            MovingResult moveEffect = (MovingResult) effectResult;
            PhysicalCard movingFrom = moveEffect.getMovingFrom();
            if (movingFrom != null) {
                PhysicalCard fromLocation = game.getModifiersQuerying().getLocationHere(game.getGameState(), movingFrom);
                if (fromLocation != null
                        && Filters.and(locationFilter).accepts(game, fromLocation)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by the card filter is beginning to move to a location accepted by the location filter.
     *
     * @param game           the game
     * @param effectResult   the effect result
     * @param cardFilter     the card filter
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean movingToLocation(SwccgGame game, EffectResult effectResult, Filterable cardFilter, Filterable locationFilter) {
        if (moving(game, effectResult, cardFilter)) {
            MovingResult moveEffect = (MovingResult) effectResult;
            PhysicalCard movingTo = moveEffect.getMovingTo();
            if (movingTo != null) {
                PhysicalCard fromLocation = game.getModifiersQuerying().getLocationHere(game.getGameState(), movingTo);
                if (fromLocation != null
                        && Filters.and(locationFilter).accepts(game, fromLocation)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if a card owned by the specified player is beginning to 'move away' from a battle location.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean movingAwayFromBattle(SwccgGame game, EffectResult effectResult, String playerId) {
        if (moving(game, effectResult, Filters.owner(playerId))) {
            MovingResult moveEffect = (MovingResult) effectResult;
            if (moveEffect.isMoveAway()) {
                PhysicalCard movingFrom = moveEffect.getMovingFrom();
                if (movingFrom != null) {
                    PhysicalCard fromLocation = game.getModifiersQuerying().getLocationHere(game.getGameState(), movingFrom);
                    if (fromLocation != null
                            && Filters.battleLocation.accepts(game, fromLocation)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if a 'react' is being performed by the specified player.
     * @param game the game
     * @param effect the effect
     * @param playerId the player
     * @return true or false
     */
    public static boolean isReactJustInitiatedBy(SwccgGame game, Effect effect, String playerId) {
        if (playerId.equals(effect.getAction().getPerformingPlayer())) {
            return isReact(game, effect);
        }
        return false;
    }

    /**
     * Determines if a 'react' is being performed.
     * @param game the game
     * @param effect the effect
     * @return true or false
     */
    public static boolean isReact(SwccgGame game, Effect effect) {
        if (effect.isCanceled()) {
            return false;
        }

        if (effect.getType() == Effect.Type.PLAYING_CARD_EFFECT) {
            RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;
            if (respondableEffect.isAsReact()) {
                return true;
            }
        }
        else if (effect.getType() == Effect.Type.PLAYING_CARDS_EFFECT) {
            RespondableDeployMultipleCardsSimultaneouslyEffect respondableEffect = (RespondableDeployMultipleCardsSimultaneouslyEffect) effect;
            if (respondableEffect.isAsReact()) {
                return true;
            }
        }
        else if (effect.getType() == Effect.Type.MOVING_AS_REACT_USING_LANDSPEED
                || effect.getType() == Effect.Type.MOVING_AS_REACT_USING_HYPERSPEED
                || effect.getType() == Effect.Type.MOVING_AS_REACT_WITHOUT_USING_HYPERSPEED
                || effect.getType() == Effect.Type.MOVING_AS_REACT_USING_SECTOR_MOVEMENT
                || effect.getType() == Effect.Type.LANDING_AS_REACT
                || effect.getType() == Effect.Type.TAKING_OFF_AS_REACT
                || effect.getType() == Effect.Type.ENTERING_STARSHIP_VEHICLE_SITE_AS_REACT
                || effect.getType() == Effect.Type.EXITING_STARSHIP_VEHICLE_SITE_AS_REACT) {
            return true;
        }

        return false;
    }

    /**
     * Determines if a card accepted by the card filter is beginning to move as a 'react'.
     * @param game the game
     * @param effect the effect
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean isMovingAsReact(SwccgGame game, Effect effect, Filterable cardFilter) {
        if (effect.isCanceled()) {
            return false;
        }

        if (effect.getType() == Effect.Type.MOVING_AS_REACT_USING_LANDSPEED
                || effect.getType() == Effect.Type.MOVING_AS_REACT_USING_HYPERSPEED
                || effect.getType() == Effect.Type.MOVING_AS_REACT_WITHOUT_USING_HYPERSPEED
                || effect.getType() == Effect.Type.MOVING_AS_REACT_USING_SECTOR_MOVEMENT
                || effect.getType() == Effect.Type.LANDING_AS_REACT
                || effect.getType() == Effect.Type.TAKING_OFF_AS_REACT
                || effect.getType() == Effect.Type.ENTERING_STARSHIP_VEHICLE_SITE_AS_REACT
                || effect.getType() == Effect.Type.EXITING_STARSHIP_VEHICLE_SITE_AS_REACT) {
            MovingAsReactEffect movingAsReactEffect = (MovingAsReactEffect) effect;
            Collection<PhysicalCard> cardsMoving = movingAsReactEffect.getCardsMoving();
            return Filters.canSpot(cardsMoving, game, Filters.and(cardFilter));
        }

        return false;
    }

    /**
     * Determines if a card accepted by the moved card filter just moved.
     * @param game the game
     * @param effectResult the effect result
     * @param movedCardFilter the moved card filter
     * @return true or false
     */
    public static boolean moved(SwccgGame game, EffectResult effectResult, Filterable movedCardFilter) {
        if (effectResult.getType() == EffectResult.Type.MOVED_USING_HYPERSPEED
                || effectResult.getType() == EffectResult.Type.MOVED_WITHOUT_USING_HYPERSPEED
                || effectResult.getType() == EffectResult.Type.MOVED_USING_SECTOR_MOVEMENT
                || effectResult.getType() == EffectResult.Type.MOVED_TO_RELATED_STARSHIP_OR_VEHICLE
                || effectResult.getType() == EffectResult.Type.MOVED_TO_RELATED_STARSHIP_OR_VEHICLE_SITE
                || effectResult.getType() == EffectResult.Type.MOVED_AT_START_OF_ATTACK_RUN
                || effectResult.getType() == EffectResult.Type.MOVED_AT_END_OF_ATTACK_RUN
                || effectResult.getType() == EffectResult.Type.MOVED_TO_START_BOMBING_RUN
                || effectResult.getType() == EffectResult.Type.MOVED_TO_END_BOMBING_RUN
                || effectResult.getType() == EffectResult.Type.ENTERED_STARSHIP_OR_VEHICLE_SITE
                || effectResult.getType() == EffectResult.Type.EXITED_STARSHIP_OR_VEHICLE_SITE
                || effectResult.getType() == EffectResult.Type.MOVED_USING_LANDSPEED
                || effectResult.getType() == EffectResult.Type.LANDED
                || effectResult.getType() == EffectResult.Type.TOOK_OFF
                || effectResult.getType() == EffectResult.Type.EMBARKED
                || effectResult.getType() == EffectResult.Type.DISEMBARKED
                || effectResult.getType() == EffectResult.Type.SHUTTLED
                || effectResult.getType() == EffectResult.Type.DOCKING_BAY_TRANSITED
                || effectResult.getType() == EffectResult.Type.MOVED_USING_LOCATION_TEXT
                || effectResult.getType() == EffectResult.Type.RELOCATED_BETWEEN_LOCATIONS
                || effectResult.getType() == EffectResult.Type.MOVED_MOBILE_EFFECT
                || effectResult.getType() == EffectResult.Type.CHANGED_CAPACITY_SLOT
                || effectResult.getType() == EffectResult.Type.EMBARKED
                || effectResult.getType() == EffectResult.Type.DISEMBARKED
                || effectResult.getType() == EffectResult.Type.SHIPDOCKED) {
            MovedResult movedResult = (MovedResult) effectResult;
            Collection<PhysicalCard> movedCards = movedResult.getMovedCards();
            return Filters.canSpot(movedCards, game, movedCardFilter);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the moved card filter was just moved by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param movedCardFilter the moved card filter
     * @return true or false
     */
    public static boolean moved(SwccgGame game, EffectResult effectResult, String playerId, Filterable movedCardFilter) {
        return playerId.equals(effectResult.getPerformingPlayerId()) && moved(game, effectResult, movedCardFilter);
    }

    /**
     * Determines if a card accepted by the moved card filter just moved from a location accepted by the from location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param movedCardFilter the moved card filter
     * @param fromLocationFilter the from location filter
     * @return true or false
     */
    public static boolean movedFromLocation(SwccgGame game, EffectResult effectResult, Filterable movedCardFilter, Filterable fromLocationFilter) {
        return movedFromLocationToLocation(game, effectResult, movedCardFilter, fromLocationFilter, Filters.any);
    }

    /**
     * Determines if a card accepted by the moved card filter just moved to a location accepted by the to location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param movedCardFilter the moved card filter
     * @param toLocationFilter the to location filter
     * @return true or false
     */
    public static boolean movedToLocation(SwccgGame game, EffectResult effectResult, Filterable movedCardFilter, Filterable toLocationFilter) {
        return movedFromLocationToLocation(game, effectResult, movedCardFilter, Filters.any, toLocationFilter);
    }

    /**
     * Determines if a card accepted by the moved card filter just moved by the specified player to a location accepted
     * by the to location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param movedCardFilter the moved card filter
     * @param toLocationFilter the to location filter
     * @return true or false
     */
    public static boolean movedToLocationBy(SwccgGame game, EffectResult effectResult, String playerId, Filterable movedCardFilter, Filterable toLocationFilter) {
        return playerId.equals(effectResult.getPerformingPlayerId()) && movedFromLocationToLocation(game, effectResult, movedCardFilter, Filters.any, toLocationFilter);
    }

    /**
     * Determines if a card accepted by the moved card filter just moved by the specified player from a location accepted
     * by the from location filter to a location accepted by the to location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param movedCardFilter the moved card filter
     * @param fromLocationFilter the from location filter
     * @param toLocationFilter the to location filter
     * @return true or false
     */
    public static boolean movedFromLocationToLocationBy(SwccgGame game, EffectResult effectResult, String playerId, Filterable movedCardFilter, Filterable fromLocationFilter, Filterable toLocationFilter) {
        return playerId.equals(effectResult.getPerformingPlayerId()) && movedFromLocationToLocation(game, effectResult, movedCardFilter, fromLocationFilter, toLocationFilter);
    }

    /**
     * Determines if a card accepted by the moved card filter just moved from a location accepted by the from location filter to a
     * location accepted by the to location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param movedCardFilter the moved card filter
     * @param fromLocationFilter the from location filter
     * @param toLocationFilter the to location filter
     * @return true or false
     */
    public static boolean movedFromLocationToLocation(SwccgGame game, EffectResult effectResult, Filterable movedCardFilter, Filterable fromLocationFilter, Filterable toLocationFilter) {
        if (effectResult.getType() == EffectResult.Type.MOVED_USING_HYPERSPEED
                || effectResult.getType() == EffectResult.Type.MOVED_WITHOUT_USING_HYPERSPEED
                || effectResult.getType() == EffectResult.Type.MOVED_USING_SECTOR_MOVEMENT
                || effectResult.getType() == EffectResult.Type.MOVED_TO_RELATED_STARSHIP_OR_VEHICLE
                || effectResult.getType() == EffectResult.Type.MOVED_TO_RELATED_STARSHIP_OR_VEHICLE_SITE
                || effectResult.getType() == EffectResult.Type.MOVED_AT_START_OF_ATTACK_RUN
                || effectResult.getType() == EffectResult.Type.MOVED_AT_END_OF_ATTACK_RUN
                || effectResult.getType() == EffectResult.Type.MOVED_TO_START_BOMBING_RUN
                || effectResult.getType() == EffectResult.Type.MOVED_TO_END_BOMBING_RUN
                || effectResult.getType() == EffectResult.Type.ENTERED_STARSHIP_OR_VEHICLE_SITE
                || effectResult.getType() == EffectResult.Type.EXITED_STARSHIP_OR_VEHICLE_SITE
                || effectResult.getType() == EffectResult.Type.MOVED_USING_LANDSPEED
                || effectResult.getType() == EffectResult.Type.LANDED
                || effectResult.getType() == EffectResult.Type.TOOK_OFF
                || effectResult.getType() == EffectResult.Type.EMBARKED
                || effectResult.getType() == EffectResult.Type.DISEMBARKED
                || effectResult.getType() == EffectResult.Type.SHUTTLED
                || effectResult.getType() == EffectResult.Type.DOCKING_BAY_TRANSITED
                || effectResult.getType() == EffectResult.Type.MOVED_USING_LOCATION_TEXT
                || effectResult.getType() == EffectResult.Type.RELOCATED_BETWEEN_LOCATIONS
                || effectResult.getType() == EffectResult.Type.MOVED_MOBILE_EFFECT) {

            MovedResult movedResult = (MovedResult) effectResult;
            Collection<PhysicalCard> movedCards = movedResult.getMovedCards();
            PhysicalCard fromCard = movedResult.getMovedFrom();
            PhysicalCard toCard = movedResult.getMovedTo();
            if (!movedCards.isEmpty() && fromCard != null && toCard != null) {
                GameState gameState = game.getGameState();
                ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                PhysicalCard fromLocation = modifiersQuerying.getLocationHere(gameState, fromCard);
                PhysicalCard toLocation = modifiersQuerying.getLocationHere(gameState, toCard);
                if (fromLocation != null && toLocation != null && fromLocation.getCardId() != toLocation.getCardId()) {

                    return Filters.canSpot(movedCards, game, movedCardFilter)
                            && Filters.and(fromLocationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), fromLocation)
                            && Filters.and(toLocationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), toLocation);
                }
            }
        }

        return false;
    }

    /**
     * Determines if a transport (relocating cards between locations) was just completed by a card accepted by the source card filter.
     * @param game the game
     * @param effectResult the effect result
     * @param sourceCardFilter the source card filter
     * @return true or false
     */
    public static boolean transportCompletedBy(SwccgGame game, EffectResult effectResult, Filterable sourceCardFilter) {
        if (effectResult.getType() == EffectResult.Type.RELOCATED_BETWEEN_LOCATIONS) {
            PhysicalCard source = ((RelocatedBetweenLocationsResult) effectResult).getActionSource();
            return Filters.and(sourceCardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), source);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the moved card filter was just moved using landspeed.
     * @param game the game
     * @param effectResult the effect result
     * @param movedCardFilter the moved card filter
     * @return true or false
     */
    public static boolean movedUsingLandspeed(SwccgGame game, EffectResult effectResult, Filterable movedCardFilter) {
        if (effectResult.getType() == EffectResult.Type.MOVED_USING_LANDSPEED) {
            MovedResult movedResult = (MovedResult) effectResult;
            Collection<PhysicalCard> movedCards = movedResult.getMovedCards();
            return Filters.canSpot(movedCards, game, movedCardFilter);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the moved card filter was just shuttled to a card accepted by the to card filter.
     * @param game the game
     * @param effectResult the effect result
     * @param movedCardFilter the moved card filter
     * @param toCardFilter the to card filter
     * @return true or false
     */
    public static boolean justShuttledTo(SwccgGame game, EffectResult effectResult, Filterable movedCardFilter, Filterable toCardFilter) {
        if (effectResult.getType() == EffectResult.Type.SHUTTLED) {
            MovedResult movedResult = (MovedResult) effectResult;
            Collection<PhysicalCard> movedCards = movedResult.getMovedCards();
            PhysicalCard movedTo = movedResult.getMovedTo();
            return Filters.canSpot(movedCards, game, movedCardFilter)
                    && Filters.and(toCardFilter).accepts(game, movedTo);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the moved card filter just took off.
     *
     * @param game            the game
     * @param effectResult    the effect result
     * @param movedCardFilter the moved card filter
     * @return true or false
     */
    public static boolean justTookOff(SwccgGame game, EffectResult effectResult, Filterable movedCardFilter) {
        if (effectResult.getType() == EffectResult.Type.TOOK_OFF) {
            MovedResult movedResult = (MovedResult) effectResult;
            Collection<PhysicalCard> movedCards = movedResult.getMovedCards();
            return Filters.canSpot(movedCards, game, movedCardFilter);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the moved card filter just landed at a location accepted by the to card filter.
     * @param game the game
     * @param effectResult the effect result
     * @param movedCardFilter the moved card filter
     * @param toLocationFilter the to location filter
     * @return true or false
     */
    public static boolean justLandedAt(SwccgGame game, EffectResult effectResult, Filterable movedCardFilter, Filterable toLocationFilter) {
        if (effectResult.getType() == EffectResult.Type.LANDED) {
            MovedResult movedResult = (MovedResult) effectResult;
            Collection<PhysicalCard> movedCards = movedResult.getMovedCards();
            PhysicalCard movedTo = movedResult.getMovedTo();
            return Filters.canSpot(movedCards, game, movedCardFilter)
                    && Filters.and(toLocationFilter).accepts(game, movedTo);
        }
        return false;
    }

    /**
     * Determines if the specified player landed a card accepted by the moved card filter at a location accepted by the to card filter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param movedCardFilter the moved card filter
     * @param toLocationFilter the to location filter
     * @return true or false
     */
    public static boolean justLandedAt(SwccgGame game, EffectResult effectResult, String playerId, Filterable movedCardFilter, Filterable toLocationFilter) {
        return playerId.equals(effectResult.getPerformingPlayerId())
                && justLandedAt(game, effectResult, movedCardFilter, toLocationFilter);
    }

    /**
     * Determines if card pile was just shuffled by a card accepted by the card filter.
     * @param game the game
     * @param effectResult the effect result
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean cardPileJustShuffledBy(SwccgGame game, EffectResult effectResult, Filterable cardFilter) {
        if (effectResult.getType() == EffectResult.Type.SHUFFLE_CARD_PILE) {
            PhysicalCard source = ((ShufflingResult) effectResult).getSource();
            return source != null && Filters.and(cardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), source);
        }
        return false;
    }

    /**
     * Determine if a player just looked at cards in that player's own card pile.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean justLookedAtCardsInOwnCardPile(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.LOOKED_AT_OWN_CARD_PILE) {
            return playerId.equals(effectResult.getPerformingPlayerId());
        }
        return false;
    }

    /**
     * Determines if the specified player is about to look at opponents hand by using a card accepted by the card filter.
     * @param game the game
     * @param effect the effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean isAboutToLookAtOpponentsHand(SwccgGame game, Effect effect, String playerId, Filterable cardFilter) {
        if (effect.getType() == Effect.Type.BEFORE_LOOKING_AT_OPPONENTS_HAND) {
            if (playerId.equals(effect.getAction().getPerformingPlayer())) {
                PhysicalCard sourceCard = ((LookAtCardsInOpponentsHandEffect) effect).getCardAllowingScan();
                return sourceCard != null && Filters.and(cardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), sourceCard);
            }
        }
        return false;
    }

    /**
     * Determines if the specified player just verified opponent's Reserve Deck.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean justVerifiedOpponentsReserveDeck(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.VERIFY_CARD_PILE) {
            VerifiedCardPileResult result = (VerifiedCardPileResult) effectResult;
            return playerId.equals(result.getPerformingPlayerId())
                    && result.getZoneOwner().equals(game.getOpponent(playerId))
                    && result.getCardPile() == Zone.RESERVE_DECK;
        }
        return false;
    }

    /**
     * Determines if a Force drain was just initiated (and not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean forceDrainInitiated(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.FORCE_DRAIN_INITIATED) {
            return game.getGameState().isDuringForceDrain() && game.getGameState().getForceDrainState().canContinue();
        }
        return false;
    }

    /**
     * Determines if a Force drain was just initiated by the specified player (and not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean forceDrainInitiatedBy(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.FORCE_DRAIN_INITIATED) {
            if (game.getGameState().isDuringForceDrain() && game.getGameState().getForceDrainState().canContinue()) {
                ForceDrainInitiatedResult forceDrainResult = (ForceDrainInitiatedResult) effectResult;
                return playerId.equals(forceDrainResult.getPerformingPlayerId());
            }
        }
        return false;
    }

    /**
     * Determines if a Force drain was just initiated (and not canceled) at a location accepted by the specified location
     * filter.
     * @param game the game
     * @param effectResult the effect result
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean forceDrainInitiatedAt(SwccgGame game, EffectResult effectResult, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.FORCE_DRAIN_INITIATED) {
            ForceDrainState forceDrainState = game.getGameState().getForceDrainState();
            if (forceDrainState != null && forceDrainState.canContinue()) {
                ForceDrainInitiatedResult forceDrainResult = (ForceDrainInitiatedResult) effectResult;
                return Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), forceDrainResult.getLocation());
            }
        }
        return false;
    }

    /**
     * Determines if a Force drain was just initiated by the specified player (and not canceled) at a location accepted
     * by the specified location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean forceDrainInitiatedBy(SwccgGame game, EffectResult effectResult, String playerId, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.FORCE_DRAIN_INITIATED) {
            ForceDrainState forceDrainState = game.getGameState().getForceDrainState();
            if (forceDrainState != null && forceDrainState.canContinue()) {
                ForceDrainInitiatedResult forceDrainResult = (ForceDrainInitiatedResult) effectResult;
                return forceDrainResult.getPerformingPlayerId().equals(playerId)
                        && Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), forceDrainResult.getLocation());
            }
        }
        return false;
    }

    /**
     * Determines if a Force drain was just completed by the specified player (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean forceDrainCompleted(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.FORCE_DRAIN_COMPLETED) {
            ForceDrainCompletedResult forceDrainCompleted = (ForceDrainCompletedResult) effectResult;
            return playerId.equals(forceDrainCompleted.getPerformingPlayerId());
        }
        return false;
    }

    /**
     * Determines if a Force drain was just completed (and was not canceled) at a location accepted by the specified location filter.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean forceDrainCompleted(SwccgGame game, EffectResult effectResult, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.FORCE_DRAIN_COMPLETED) {
            ForceDrainCompletedResult forceDrainCompleted = (ForceDrainCompletedResult) effectResult;
            return Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), forceDrainCompleted.getLocation());
        }
        return false;
    }

    /**
     * Determines if a Force drain was just completed by the specified player (and was not canceled) at a location accepted
     * by the specified location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean forceDrainCompleted(SwccgGame game, EffectResult effectResult, String playerId, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.FORCE_DRAIN_COMPLETED) {
            ForceDrainCompletedResult forceDrainCompleted = (ForceDrainCompletedResult) effectResult;
            return playerId.equals(forceDrainCompleted.getPerformingPlayerId())
                    && Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), forceDrainCompleted.getLocation());
        }
        return false;
    }

    /**
     * Determines if a Force drain was just enhanced by a weapon accepted by the weapon filter.
     * @param game the game
     * @param effectResult the effect result
     * @param weaponFilter the weapon filter
     * @return true or false
     */
    public static boolean forceDrainEnhancedByWeapon(SwccgGame game, EffectResult effectResult, Filterable weaponFilter) {
        if (effectResult.getType() == EffectResult.Type.FORCE_DRAIN_ENHANCED_BY_WEAPON) {
            if (!game.getGameState().getForceDrainState().canContinue())
                return false;

            EnhanceForceDrainResult result = (EnhanceForceDrainResult) effectResult;
            return Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), result.getWeapon());
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter just left the table.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean leavesTable(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.LOST_FROM_TABLE || effectResult.getType() == EffectResult.Type.FORFEITED_TO_LOST_PILE_FROM_TABLE)
            return Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), ((LostFromTableResult) effectResult).getCard());
        else if (effectResult.getType() == EffectResult.Type.FORFEITED_TO_USED_PILE_FROM_TABLE)
            return Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), ((ForfeitedCardToUsedPileFromTableResult) effectResult).getCard());
        else if (effectResult.getType() == EffectResult.Type.CANCELED_ON_TABLE)
            return Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), ((CancelCardOnTableResult) effectResult).getCard());
        else if (effectResult.getType() == EffectResult.Type.STACKED_FROM_TABLE)
            return Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), ((StackedFromTableResult) effectResult).getCard());
        else if (effectResult.getType() == EffectResult.Type.RETURNED_TO_HAND_FROM_TABLE)
            return Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), ((ReturnedCardToHandFromTableResult) effectResult).getCard());
        else if (effectResult.getType() == EffectResult.Type.PUT_IN_RESERVE_DECK_FROM_TABLE)
            return Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), ((PutCardInReserveDeckFromTableResult) effectResult).getCard());
        else if (effectResult.getType() == EffectResult.Type.PUT_IN_FORCE_PILE_FROM_TABLE)
            return Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), ((PutCardInForcePileFromTableResult) effectResult).getCard());
        else if (effectResult.getType() == EffectResult.Type.PUT_IN_USED_PILE_FROM_TABLE)
            return Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), ((PutCardInUsedPileFromTableResult) effectResult).getCard());
        else if (effectResult.getType() == EffectResult.Type.PLACED_OUT_OF_PLAY_FROM_TABLE)
            return Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), ((PlacedCardOutOfPlayFromTableResult) effectResult).getCard());
        else if (effectResult.getType() == EffectResult.Type.CAPTURED) {
            CaptureCharacterResult captureCharacterResult = (CaptureCharacterResult) effectResult;
            return (captureCharacterResult.getOption()== CaptureOption.ESCAPE
                    && Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), captureCharacterResult.getCapturedCard()));
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter is about to leave the table.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAboutToLeaveTable(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_CANCELED_ON_TABLE
                || effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_LOST_FROM_TABLE
                || effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_FORFEITED_TO_FROM_TABLE
                || effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_PLACED_OUT_OF_PLAY_FROM_TABLE
                || effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_RETURNED_TO_HAND_FROM_TABLE
                || effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_PLACE_IN_CARD_PILE_FROM_TABLE
                || effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_STACK_CARD_FROM_TABLE) {
            AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
            PhysicalCard cardToLeaveTable = result.getCardAboutToLeaveTable();
            return cardToLeaveTable.getZone().isInPlay()
                    && (result.getPreventableCardEffect() == null
                    || !result.getPreventableCardEffect().isEffectOnCardPrevented(cardToLeaveTable))
                    && Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), cardToLeaveTable);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter is about to leave the table, excluding by a specific card.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @param sourceCard the source card that is the exception
     * @return true or false
     */
    public static boolean isAboutToLeaveTableExceptFromSourceCard(SwccgGame game, EffectResult effectResult, Filterable filter, Filterable sourceCard) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_CANCELED_ON_TABLE
                || effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_LOST_FROM_TABLE
                || effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_FORFEITED_TO_FROM_TABLE
                || effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_PLACED_OUT_OF_PLAY_FROM_TABLE
                || effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_RETURNED_TO_HAND_FROM_TABLE
                || effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_STACK_CARD_FROM_TABLE) {
            return isAboutToLeaveTable(game, effectResult, filter);
        }
        else if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_PLACE_IN_CARD_PILE_FROM_TABLE) {
            AboutToPlaceCardInCardPileFromTableResult result = (AboutToPlaceCardInCardPileFromTableResult) effectResult;

            // If there is a known source AND the source is the specific card
            // For game rules, like "escape vs rally", replacing cards, there is no 'source'
            if ((result.getSourceCard() != null)  && Filters.and(sourceCard).accepts(game, result.getSourceCard())) {
                return false;
            } else {
                return isAboutToLeaveTable(game, effectResult, filter);
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter is about to be lost from table.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAboutToBeLost(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_LOST_FROM_TABLE) {
            AboutToLoseCardFromTableResult aboutToLoseCardFromTableResult = (AboutToLoseCardFromTableResult) effectResult;
            PhysicalCard card = aboutToLoseCardFromTableResult.getCardToBeLost();

            return !aboutToLoseCardFromTableResult.isAllCardsSituation()
                    && card.getZone().isInPlay()
                    && (aboutToLoseCardFromTableResult.getPreventableCardEffect() == null
                    || !aboutToLoseCardFromTableResult.getPreventableCardEffect().isEffectOnCardPrevented(card))
                    && Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter is about to be lost from table (including an "all cards situation").
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAboutToBeLostIncludingAllCardsSituation(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_LOST_FROM_TABLE) {
            AboutToLoseCardFromTableResult aboutToLoseCardFromTableResult = (AboutToLoseCardFromTableResult) effectResult;
            PhysicalCard card = aboutToLoseCardFromTableResult.getCardToBeLost();

            return card.getZone().isInPlay()
                    && (aboutToLoseCardFromTableResult.getPreventableCardEffect() == null
                    || !aboutToLoseCardFromTableResult.getPreventableCardEffect().isEffectOnCardPrevented(card))
                    && Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        return false;
    }


    /**
     * Determines if a card accepted by the filter is about to be forfeited from table.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAboutToBeForfeited(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_FORFEITED_TO_FROM_TABLE) {
            AboutToForfeitCardFromTableResult aboutToForfeitCardFromTableResult = (AboutToForfeitCardFromTableResult) effectResult;
            PhysicalCard card = aboutToForfeitCardFromTableResult.getCardToBeForfeited();

            return card.getZone().isInPlay()
                    && (aboutToForfeitCardFromTableResult.getPreventableCardEffect() == null
                    || !aboutToForfeitCardFromTableResult.getPreventableCardEffect().isEffectOnCardPrevented(card))
                    && Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter is about to be forfeited to Lost Pile from table.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAboutToBeForfeitedToLostPile(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_FORFEITED_TO_FROM_TABLE) {
            AboutToForfeitCardFromTableResult aboutToForfeitCardFromTableResult = (AboutToForfeitCardFromTableResult) effectResult;
            PhysicalCard card = aboutToForfeitCardFromTableResult.getCardToBeForfeited();

            return card.getZone().isInPlay()
                    && (aboutToForfeitCardFromTableResult.getPreventableCardEffect() == null
                    || !aboutToForfeitCardFromTableResult.getPreventableCardEffect().isEffectOnCardPrevented(card))
                    && aboutToForfeitCardFromTableResult.getForfeitCardEffect().isForfeitToLostPile()
                    && Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter is about to be placed out of play from table.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAboutToBePlacedOutOfPlayFromTable(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_PLACED_OUT_OF_PLAY_FROM_TABLE) {
            AboutToPlaceCardOutOfPlayFromTableResult result = (AboutToPlaceCardOutOfPlayFromTableResult) effectResult;
            PhysicalCard card = result.getCardAboutToLeaveTable();

            return (result.getPreventableCardEffect() == null
                    || !result.getPreventableCardEffect().isEffectOnCardPrevented(card))
                    && Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter is about to be placed out of play from table by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAboutToBePlacedOutOfPlayFromTable(SwccgGame game, EffectResult effectResult, String playerId, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_PLACED_OUT_OF_PLAY_FROM_TABLE) {
            AboutToPlaceCardOutOfPlayFromTableResult result = (AboutToPlaceCardOutOfPlayFromTableResult) effectResult;
            PhysicalCard card = result.getCardAboutToLeaveTable();

            return playerId.equals(effectResult.getPerformingPlayerId())
                    && (result.getPreventableCardEffect() == null
                    || !result.getPreventableCardEffect().isEffectOnCardPrevented(card))
                    && Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter is about to be placed out of play from off table by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAboutToBePlacedOutOfPlayFromOffTable(SwccgGame game, EffectResult effectResult, String playerId, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_PLACED_OUT_OF_PLAY_FROM_OFF_TABLE) {
            AboutToPlaceCardOutOfPlayFromOffTableResult result = (AboutToPlaceCardOutOfPlayFromOffTableResult) effectResult;
            PhysicalCard card = result.getCardToBePlacedOutOfPlay();

            return playerId.equals(effectResult.getPerformingPlayerId())
                    && (result.getPreventableCardEffect() == null
                    || !result.getPreventableCardEffect().isEffectOnCardPrevented(card))
                    && Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter is about to be captured.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAboutToBeCaptured(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_CAPTURED) {
            AboutToCaptureCardResult aboutToCaptureCardResult = (AboutToCaptureCardResult) effectResult;
            PhysicalCard card = aboutToCaptureCardResult.getCardToBeCaptured();

            return (aboutToCaptureCardResult.getPreventableCardEffect() == null
                    || !aboutToCaptureCardResult.getPreventableCardEffect().isEffectOnCardPrevented(card))
                    && Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter was just lost (and still in Lost Pile).
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean justLost(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.LOST_FROM_TABLE
                || effectResult.getType() == EffectResult.Type.CANCELED_ON_TABLE
                || effectResult.getType() == EffectResult.Type.FORFEITED_TO_LOST_PILE_FROM_TABLE) {
            PhysicalCard cardLost = ((LostFromTableResult) effectResult).getCard();

            return GameUtils.getZoneFromZoneTop(cardLost.getZone()) == Zone.LOST_PILE
                    && Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), cardLost);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter was just lost (and still in Lost Pile).
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param filter the filter
     * @return true or false
     */
    public static boolean justLost(SwccgGame game, EffectResult effectResult, String playerId, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.LOST_FROM_TABLE
                || effectResult.getType() == EffectResult.Type.CANCELED_ON_TABLE
                || effectResult.getType() == EffectResult.Type.FORFEITED_TO_LOST_PILE_FROM_TABLE) {
            PhysicalCard cardLost = ((LostFromTableResult) effectResult).getCard();
            if (playerId.equals(effectResult.getPerformingPlayerId())
                || (effectResult.getPerformingPlayerId() == null && playerId.equals(cardLost.getOwner()))) {
                return GameUtils.getZoneFromZoneTop(cardLost.getZone()) == Zone.LOST_PILE
                        && Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), cardLost);
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by the card filter was just lost (and still in Lost Pile) and was present with a card
     * accepted by the present with filter when it was lost.
     * @param game the game
     * @param effectResult the effect result
     * @param cardFilter the card filter
     * @param presentWithFilter the present with filter
     * @return true or false
     */
    public static boolean justLostWasPresentWith(SwccgGame game, EffectResult effectResult, Filterable cardFilter, Filterable presentWithFilter) {
        if (effectResult.getType() == EffectResult.Type.LOST_FROM_TABLE
                || effectResult.getType() == EffectResult.Type.CANCELED_ON_TABLE
                || effectResult.getType() == EffectResult.Type.FORFEITED_TO_LOST_PILE_FROM_TABLE) {
            LostFromTableResult lostFromTableResult = (LostFromTableResult) effectResult;
            PhysicalCard cardLost = lostFromTableResult.getCard();
            Collection<PhysicalCard> wasPresentWith = lostFromTableResult.getWasPresentWith();

            return GameUtils.getZoneFromZoneTop(cardLost.getZone()) == Zone.LOST_PILE
                    && Filters.and(cardFilter).accepts(game, cardLost)
                    && !Filters.filterCount(wasPresentWith, game, 1, presentWithFilter).isEmpty();
        }
        return false;
    }

    /**
     * Determines if a card accepted by the card filter was just lost from being attached to a card accepted by the attachedTo filter (and still in Lost Pile).
     * @param game the game
     * @param effectResult the effect result
     * @param cardFilter the card filter
     * @param attachedToFilter the attached to filter
     * @return true or false
     */
    public static boolean justLostFromAttachedTo(SwccgGame game, EffectResult effectResult, Filterable cardFilter, Filterable attachedToFilter) {
        if (effectResult.getType() == EffectResult.Type.LOST_FROM_TABLE
                || effectResult.getType() == EffectResult.Type.CANCELED_ON_TABLE
                || effectResult.getType() == EffectResult.Type.FORFEITED_TO_LOST_PILE_FROM_TABLE) {
            LostFromTableResult lostFromTableResult = (LostFromTableResult) effectResult;
            PhysicalCard cardLost = lostFromTableResult.getCard();
            PhysicalCard fromAttachedTo = lostFromTableResult.getFromAttachedTo();

            return GameUtils.getZoneFromZoneTop(cardLost.getZone()) == Zone.LOST_PILE
                    && Filters.and(cardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), cardLost)
                    && fromAttachedTo != null && Filters.and(attachedToFilter).accepts(game.getGameState(), game.getModifiersQuerying(), fromAttachedTo);
        }
        return false;
    }

    public static boolean justPutCoaxiumCardInCardPile(EffectResult effectResult, Zone cardPile) {
        if (effectResult.getType() == EffectResult.Type.REMOVED_COAXIUM_CARD) {
            RemovedCoaxiumCardResult removedCoaxiumCardResult = (RemovedCoaxiumCardResult) effectResult;
            if (removedCoaxiumCardResult.getCardPile() == cardPile) {
                PhysicalCard card = removedCoaxiumCardResult.getCard();
                if (card != null
                        && GameUtils.getZoneFromZoneTop(card.getZone()) == cardPile)
                    return true;
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by the card filter was just lost from a location accepted by the location filter (and still in Lost Pile).
     *
     * @param game           the game
     * @param effectResult   the effect result
     * @param cardFilter     the card filter
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean justLostFromLocation(SwccgGame game, EffectResult effectResult, Filterable cardFilter, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.LOST_FROM_TABLE
                || effectResult.getType() == EffectResult.Type.CANCELED_ON_TABLE
                || effectResult.getType() == EffectResult.Type.FORFEITED_TO_LOST_PILE_FROM_TABLE) {
            LostFromTableResult lostFromTableResult = (LostFromTableResult) effectResult;
            PhysicalCard cardLost = lostFromTableResult.getCard();
            PhysicalCard location = lostFromTableResult.getFromLocation();

            return GameUtils.getZoneFromZoneTop(cardLost.getZone()) == Zone.LOST_PILE
                    && Filters.and(cardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), cardLost)
                    && location != null && Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), location);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the card filter was just forfeited by the specified player to lost pile (and still
     * in Lost Pile).
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean justForfeited(SwccgGame game, EffectResult effectResult, String playerId, Filterable cardFilter) {
        if (effectResult.getType() == EffectResult.Type.FORFEITED_TO_LOST_PILE_FROM_TABLE) {
            LostFromTableResult lostFromTableResult = (LostFromTableResult) effectResult;
            PhysicalCard cardLost = lostFromTableResult.getCard();

            return playerId.equals(effectResult.getPerformingPlayerId())
                    && GameUtils.getZoneFromZoneTop(cardLost.getZone())== Zone.LOST_PILE
                    && Filters.and(cardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), cardLost);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the card filter was just forfeited to lost pile from a location accepted by the
     * location filter (and still in Lost Pile).
     * @param game the game
     * @param effectResult the effect result
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean justForfeitedToLostPileFromLocation(SwccgGame game, EffectResult effectResult, Filterable cardFilter, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.FORFEITED_TO_LOST_PILE_FROM_TABLE) {
            LostFromTableResult lostFromTableResult = (LostFromTableResult) effectResult;
            PhysicalCard cardLost = lostFromTableResult.getCard();
            PhysicalCard location = lostFromTableResult.getFromLocation();

            return GameUtils.getZoneFromZoneTop(cardLost.getZone())== Zone.LOST_PILE
                    && Filters.and(cardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), cardLost)
                    && location != null && Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), location);
        }
        return false;
    }

    /**
     * Determines if a 'hit' card accepted by the card filter was just forfeited to lost pile from a location accepted by the
     * location filter (and still in Lost Pile).
     * @param game the game
     * @param effectResult the effect result
     * @param hitCardFilter the card filter
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean justForfeitedHitToLostPileFromLocation(SwccgGame game, EffectResult effectResult, Filterable hitCardFilter, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.FORFEITED_TO_LOST_PILE_FROM_TABLE) {
            LostFromTableResult lostFromTableResult = (LostFromTableResult) effectResult;
            PhysicalCard cardLost = lostFromTableResult.getCard();
            PhysicalCard location = lostFromTableResult.getFromLocation();

            return GameUtils.getZoneFromZoneTop(cardLost.getZone())== Zone.LOST_PILE
                    && cardLost.wasPreviouslyHit()
                    && Filters.and(hitCardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), cardLost)
                    && location != null && Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), location);
        }
        return false;
    }

    /**
     * Determines if a just-lost card accepted by the card filter is about to be removed from Lost Pile by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean isAboutToRemoveJustLostCardFromLostPile(SwccgGame game, EffectResult effectResult, String playerId, Filterable cardFilter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_REMOVE_JUST_LOST_CARD_FROM_LOST_PILE) {
            AboutToRemoveJustLostCardFromLostPileResult result = (AboutToRemoveJustLostCardFromLostPileResult) effectResult;
            PhysicalCard card = result.getCardToRemoveFromLostPile();

            return playerId.equals(effectResult.getPerformingPlayerId())
                    && (result.getPreventableCardEffect() == null
                    || !result.getPreventableCardEffect().isEffectOnCardPrevented(card))
                    && Filters.and(cardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter was placed in Used Pile from table (and still in Used Pile).
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean justPlacedInUsedPileFromTable(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.PUT_IN_USED_PILE_FROM_TABLE) {
            PhysicalCard card = ((PutCardInUsedPileFromTableResult) effectResult).getCard();

            return GameUtils.getZoneFromZoneTop(card.getZone()) == Zone.USED_PILE
                    && Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter was placed in Used Pile from table (and still in Used Pile).
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean justPlacedPlayedInterruptInLostPile(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.PUT_IN_CARD_PILE_FROM_OFF_TABLE) {
            PutCardInCardPileFromOffTableResult result = (PutCardInCardPileFromOffTableResult) effectResult;
            if (result.isPlayedInterrupt()
                    && result.getCardPile() == Zone.LOST_PILE) {
                PhysicalCard card = result.getCard();
                if (card != null
                        && GameUtils.getZoneFromZoneTop(card.getZone()) == Zone.LOST_PILE
                        && Filters.and(filter).accepts(game, card)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter was just placed out of play from table.
     *
     * @param game         the game
     * @param effectResult the effect result
     * @param filter       the filter
     * @return true or false
     */
    public static boolean justPlacedOutOfPlay(SwccgGame game, EffectResult effectResult, Filterable filter) {
        return justPlacedOutOfPlayFromTable(game, effectResult, filter) || justPlacedOutOfPlayFromOffTable(game, effectResult, filter);
    }

    /**
     * Determines if a card accepted by the filter was just placed out of play from table.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean justPlacedOutOfPlayFromTable(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.PLACED_OUT_OF_PLAY_FROM_TABLE) {
            PhysicalCard cardPlacedOutOfPlay = ((PlacedCardOutOfPlayFromTableResult) effectResult).getCard();

            return Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), cardPlacedOutOfPlay);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter was just placed out of play from off table.
     *
     * @param game         the game
     * @param effectResult the effect result
     * @param filter       the filter
     * @return true or false
     */
    public static boolean justPlacedOutOfPlayFromOffTable(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.PLACED_OUT_OF_PLAY_FROM_OFF_TABLE) {
            PhysicalCard cardPlacedOutOfPlay = ((PlacedCardOutOfPlayFromOffTableResult) effectResult).getCard();

            return Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), cardPlacedOutOfPlay);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter is being canceled from table by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAboutToBeCanceledFromTableBy(SwccgGame game, EffectResult effectResult, String playerId, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_CANCELED_ON_TABLE) {
            AboutToCancelCardOnTableResult aboutToCancelCardFromTableResult = (AboutToCancelCardOnTableResult) effectResult;
            PhysicalCard card = aboutToCancelCardFromTableResult.getCardToBeCanceled();

            return card.getZone().isInPlay()
                    && playerId.equals(aboutToCancelCardFromTableResult.getPerformingPlayerId())
                    && !aboutToCancelCardFromTableResult.getPreventableCardEffect().isEffectOnCardPrevented(card)
                    && Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the card filter was just canceled.
     * @param game the game
     * @param effectResult the effect result
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean justCanceled(SwccgGame game, EffectResult effectResult, Filterable cardFilter) {
        if (effectResult.getType() == EffectResult.Type.CANCELED_ON_TABLE) {
            CancelCardOnTableResult cancelCardOnTableResult = (CancelCardOnTableResult) effectResult;
            PhysicalCard cardCanceled = cancelCardOnTableResult.getCard();

            return Filters.and(cardFilter).accepts(game, cardCanceled);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the card filter that was attached to a card accepted by the attachedToFilter was
     * just canceled by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param cardFilter the card filter
     * @param attachedToFilter the attached to filter
     * @return true or false
     */
    public static boolean justCanceledFromAttachedTo(SwccgGame game, EffectResult effectResult, String playerId, Filterable cardFilter, Filterable attachedToFilter) {
        if (effectResult.getType() == EffectResult.Type.CANCELED_ON_TABLE) {
            CancelCardOnTableResult cancelCardOnTableResult = (CancelCardOnTableResult) effectResult;
            PhysicalCard cardCanceled = cancelCardOnTableResult.getCard();
            PhysicalCard wasAttachedTo = cancelCardOnTableResult.getFromAttachedTo();

            return playerId.equals(cancelCardOnTableResult.getPerformingPlayerId())
                    && Filters.and(cardFilter).accepts(game, cardCanceled)
                    && wasAttachedTo != null && Filters.and(attachedToFilter).accepts(game, wasAttachedTo);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the card filter was just stolen by the specified player from a location accepted by the location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean justStolenFromLocation(SwccgGame game, EffectResult effectResult, String playerId, Filterable cardFilter, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.STOLEN) {
            StolenResult stolenResult = (StolenResult) effectResult;
            PhysicalCard cardStolen = stolenResult.getStolenCard();
            PhysicalCard location = stolenResult.getStolenFromLocation();

            return playerId.equals(stolenResult.getPerformingPlayerId())
                    && Filters.and(cardFilter).accepts(game, cardStolen)
                    && location != null && Filters.and(locationFilter).accepts(game, location);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter is about to 'hide' from battle.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAboutToHideFromBattle(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_HIDE_FROM_BATTLE) {
            AboutToHideFromBattleResult aboutToHideFromBattleResult = (AboutToHideFromBattleResult) effectResult;
            PhysicalCard card = aboutToHideFromBattleResult.getCardToHideFromBattle();
            BattleState battleState = game.getGameState().getBattleState();

            return battleState != null && battleState.isCardParticipatingInBattle(card)
                    && !aboutToHideFromBattleResult.getPreventableCardEffect().isEffectOnCardPrevented(card)
                    && Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter is about to be used instead of a normal destiny draw by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isAboutToUseCombatCardInsteadOfDestinyDraw(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_USE_COMBAT_CARD_INSTEAD_OF_DESTINY_DRAW) {
            AboutToUseCombatCardInsteadOfDestinyDrawResult aboutToUseCardResult = (AboutToUseCombatCardInsteadOfDestinyDrawResult) effectResult;
            PhysicalCard card = aboutToUseCardResult.getSourceCard();

            return playerId.equals(aboutToUseCardResult.getPerformingPlayerId())
                    && !aboutToUseCardResult.getPreventableCardEffect().isEffectOnCardPrevented(card);
        }
        return false;
    }

    /**
     * Determines if a site was just 'collapsed' by a card accepted by the filter.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean siteCollapsedBy(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.COLLAPSED_SITE) {
            CollapsedSiteResult collapsedSiteResult = (CollapsedSiteResult) effectResult;
            return Filters.and(filter).accepts(game.getGameState(), game.getModifiersQuerying(), collapsedSiteResult.getCollapsedBy());
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter just had its forfeit reduced to 0.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean forfeitJustReducedToZero(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.FORFEIT_REDUCED_TO_ZERO) {
            ForfeitReducedToZeroResult result = (ForfeitReducedToZeroResult) effectResult;
            return Filters.and(filter, Filters.forfeitValueEqualTo(0)).accepts(game, result.getCard());
        }
        return false;
    }

    /**
     * Determines if a duel was just initiated.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean duelInitiated(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.DUEL_INITIATED);
    }

    /**
     * Determines if a duel was just initiated at a location accepted by the location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean duelInitiatedAt(SwccgGame game, EffectResult effectResult, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.DUEL_INITIATED) {
            return game.getGameState().getDuelState().canContinue(game)
                    && Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), game.getGameState().getDuelState().getLocation());
        }
        return false;
    }

    /**
     * Determines if a duel was just initiated by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean duelInitiatedBy(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.DUEL_INITIATED) {
            DuelInitiatedResult initiateDuelResult = (DuelInitiatedResult) effectResult;
            return playerId.equals(initiateDuelResult.getPerformingPlayerId());
        }
        return false;
    }

    /**
     * Determines if a non-Epic duel was just initiated by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean nonEpicDuelInitiatedBy(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.DUEL_INITIATED) {
            DuelInitiatedResult initiateDuelResult = (DuelInitiatedResult) effectResult;
            return playerId.equals(initiateDuelResult.getPerformingPlayerId()) && !initiateDuelResult.isEpicDuel();
        }
        return false;
    }

    /**
     * Determines if the specified player just initiated a duel using a character accepted by the character filter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param characterFilter the character filter
     * @return true or false
     */
    public static boolean duelInitiatedBy(SwccgGame game, EffectResult effectResult, String playerId, Filterable characterFilter) {
        if (effectResult.getType() == EffectResult.Type.DUEL_INITIATED) {
            DuelState duelState = game.getGameState().getDuelState();
            if (duelState != null && duelState.canContinue(game) && duelState.getPlayerInitiatedDuel().equals(playerId)) {
                PhysicalCard initiatingCharacter = duelState.getCharacter(playerId);

                return (initiatingCharacter != null && Filters.and(characterFilter).accepts(game, initiatingCharacter));
            }
        }
        return false;
    }

    /**
     * Determines if the specified player just initiated a duel against a character accepted by the character filter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param characterFilter the character filter
     * @return true or false
     */
    public static boolean duelInitiatedAgainst(SwccgGame game, EffectResult effectResult, String playerId, Filterable characterFilter) {
        if (effectResult.getType() == EffectResult.Type.DUEL_INITIATED) {
            DuelState duelState = game.getGameState().getDuelState();
            if (duelState != null && duelState.canContinue(game) && duelState.getPlayerInitiatedDuel().equals(playerId)) {
                String againstPlayerId = game.getOpponent(playerId);
                PhysicalCard againstCharacter = duelState.getCharacter(againstPlayerId);

                return (againstCharacter != null && Filters.and(characterFilter).accepts(game, againstCharacter));
            }
        }
        return false;
    }

    /**
     * Determines if an attack was just initiated at a location accepted by the location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean attackInitiatedAt(SwccgGame game, EffectResult effectResult, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.ATTACK_INITIATED) {
            AttackState attackState = game.getGameState().getAttackState();
            return attackState != null
                    && attackState.canContinue()
                    && Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), attackState.getAttackLocation());
        }
        return false;
    }

    /**
     * Determines if an attack was just initiated by the specified player at a location accepted by the location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean attackInitiatedAt(SwccgGame game, EffectResult effectResult, String playerId, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.ATTACK_INITIATED) {
            AttackState attackState = game.getGameState().getAttackState();
            return attackState != null
                    && !attackState.isCreaturesAttackingEachOther()
                    && attackState.canContinue()
                    && playerId.equals(attackState.getAttackerOwner())
                    && Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), attackState.getAttackLocation());
        }
        return false;
    }

    /**
     * Determines if a battle was just initiated.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean battleInitiated(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.BATTLE_INITIATED) {
            return game.getGameState().getBattleState().canContinue(game);
        }
        return false;
    }

    /**
     * Determines if a battle was just initiated by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean battleInitiated(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.BATTLE_INITIATED) {
            BattleInitiatedResult initiateBattleResult = (BattleInitiatedResult) effectResult;
            return playerId.equals(initiateBattleResult.getPerformingPlayerId()) && game.getGameState().getBattleState().canContinue(game);
        }
        return false;
    }

    /**
     * Determines if a battle was just initiated at a location accepted by the location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean battleInitiatedAt(SwccgGame game, EffectResult effectResult, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.BATTLE_INITIATED) {
            BattleInitiatedResult initiateBattleResult = (BattleInitiatedResult) effectResult;
            return game.getGameState().getBattleState().canContinue(game)
                    && Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), initiateBattleResult.getLocation());
        }
        return false;
    }

    /**
     * Determines if a battle was just initiated by the specified player at a location accepted by the location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean battleInitiatedAt(SwccgGame game, EffectResult effectResult, String playerId, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.BATTLE_INITIATED) {
            BattleInitiatedResult initiateBattleResult = (BattleInitiatedResult) effectResult;
            return playerId.equals(initiateBattleResult.getPerformingPlayerId())
                    && game.getGameState().getBattleState().canContinue(game)
                    && Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), initiateBattleResult.getLocation());
        }
        return false;
    }

    /**
     * Determines if a battle was just canceled.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean battleCanceled(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.BATTLE_CANCELED) {
            return true;
        }
        return false;
    }

    /**
     * Determines if a battle at a location accepted by the location filter was just canceled by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean battleCanceledAt(SwccgGame game, EffectResult effectResult, String playerId, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.BATTLE_CANCELED) {
            CancelBattleResult cancelBattleResult = (CancelBattleResult) effectResult;
            return playerId.equals(cancelBattleResult.getPerformingPlayerId())
                    && Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), cancelBattleResult.getLocation());
        }
        return false;
    }

    /**
     * Determines if a battle is ending.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean battleEnding(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.BATTLE_ENDING) {
            return true;
        }
        return false;
    }

    /**
     * Determines if a battle is ending at a location accepted by the location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean battleEndingAt(SwccgGame game, EffectResult effectResult, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.BATTLE_ENDING) {
            BattleEndingResult battleEndingResult = (BattleEndingResult) effectResult;
            return Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), battleEndingResult.getLocation());
        }
        return false;
    }

    /**
     * Determines if a battle just ended.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean battleEnded(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.BATTLE_ENDED) {
            return true;
        }
        return false;
    }

    /**
     * Determines if a battle just ended at a location accepted by the location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean battleEndedAt(SwccgGame game, EffectResult effectResult, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.BATTLE_ENDED) {
            BattleEndedResult battleEndedResult = (BattleEndedResult) effectResult;
            return Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), battleEndedResult.getLocation());
        }
        return false;
    }

    /**
     * Determines if the battle resulted in a tie.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean battleResultDetermined(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.BATTLE_RESULT_DETERMINED);
    }

    /**
     * Determines if the battle resulted in a tie.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean battleTied(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.BATTLE_RESULT_DETERMINED) {
            BattleResultDeterminedResult result = (BattleResultDeterminedResult) effectResult;
            return result.getWinner()==null;
        }
        return false;
    }

    /**
     * Determines if the specified player just won a battle.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean wonBattle(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.BATTLE_RESULT_DETERMINED) {
            BattleResultDeterminedResult wonResult = (BattleResultDeterminedResult) effectResult;
            return wonResult.getWinner() != null && wonResult.getWinner().equals(playerId);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the winner filter just won a battle.
     * @param game the game
     * @param effectResult the effect result
     * @param winnerFilter the winner filter
     * @return true or false
     */
    public static boolean wonBattle(SwccgGame game, EffectResult effectResult, Filterable winnerFilter) {
        if (effectResult.getType() == EffectResult.Type.BATTLE_RESULT_DETERMINED) {
            BattleResultDeterminedResult battleResult = (BattleResultDeterminedResult) effectResult;
            GameState gameState = game.getGameState();
            BattleState battleState = gameState.getBattleState();
            String winningPlayer = battleResult.getWinner();
            if (winningPlayer != null) {
                if (!Filters.filterCount(battleState.getCardsParticipating(winningPlayer), game, 1, winnerFilter).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean wonBattleAt(SwccgGame game, EffectResult effectResult, String playerId, Filterable filters) {
        if (effectResult.getType() == EffectResult.Type.BATTLE_RESULT_DETERMINED) {
            BattleResultDeterminedResult wonResult = (BattleResultDeterminedResult) effectResult;
            return wonResult.getWinner() != null && wonResult.getWinner().equals(playerId)
                    && Filters.and(filters).accepts(game.getGameState(), game.getModifiersQuerying(), wonResult.getLocation());
        }
        return false;
    }

    /**
     * Determines if a card accepted by the winner filter just won a battle against a card accepted by the loser filter.
     * @param game the game
     * @param effectResult the effect result
     * @param winnerFilter the winner filter
     * @param loserFilter the loser filter
     * @return true or false
     */
    public static boolean wonBattleAgainst(SwccgGame game, EffectResult effectResult, Filterable winnerFilter, Filterable loserFilter) {
        if (effectResult.getType() == EffectResult.Type.BATTLE_RESULT_DETERMINED) {
            BattleResultDeterminedResult battleResult = (BattleResultDeterminedResult) effectResult;
            GameState gameState = game.getGameState();
            BattleState battleState = gameState.getBattleState();
            String winningPlayer = battleResult.getWinner();
            String losingPlayer = battleResult.getLoser();
            if (winningPlayer != null && losingPlayer != null) {
                if (!Filters.filterCount(battleState.getCardsParticipating(winningPlayer), game, 1, winnerFilter).isEmpty()
                        && !Filters.filterCount(battleState.getCardsParticipating(losingPlayer), game, 1, loserFilter).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if specified player just won a duel.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean wonDuel(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.DUEL_ENDING) {
            DuelEndingResult duelEndingResult = (DuelEndingResult) effectResult;
            return playerId.equals(duelEndingResult.getWinner());
        }
        return false;
    }

    /**
     * Determines if a character accepted by the winner filter just won a duel.
     * @param game the game
     * @param effectResult the effect result
     * @param winnerFilter the winner filter
     * @return true or false
     */
    public static boolean wonDuel(SwccgGame game, EffectResult effectResult, Filterable winnerFilter) {
        if (effectResult.getType() == EffectResult.Type.DUEL_ENDING) {
            DuelEndingResult duelEndingResult = (DuelEndingResult) effectResult;
            PhysicalCard winningCharacter = duelEndingResult.getWinningCharacter();
            if (winningCharacter != null) {
                if (Filters.and(winnerFilter).accepts(game, winningCharacter)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if a character accepted by the winner filter just won a duel against a character accepted by the loser filter.
     * @param game the game
     * @param effectResult the effect result
     * @param winnerFilter the winner filter
     * @param loserFilter the loser filter
     * @return true or false
     */
    public static boolean wonDuelAgainst(SwccgGame game, EffectResult effectResult, Filterable winnerFilter, Filterable loserFilter) {
        if (effectResult.getType() == EffectResult.Type.DUEL_ENDING) {
            DuelEndingResult duelEndingResult = (DuelEndingResult) effectResult;
            PhysicalCard winningCharacter = duelEndingResult.getWinningCharacter();
            PhysicalCard losingCharacter = duelEndingResult.getLosingCharacter();
            if (winningCharacter != null && losingCharacter != null) {
                if (Filters.and(winnerFilter).accepts(game, winningCharacter)
                        && Filters.and(loserFilter).accepts(game, losingCharacter)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if specified player just lost a duel (before duel has any results).
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean lostDuelBeforeAnyResults(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.DUEL_RESULT_DETERMINED) {
            DuelResultDeterminedResult duelResultDeterminedResult = (DuelResultDeterminedResult) effectResult;
            return playerId.equals(duelResultDeterminedResult.getLoser());
        }
        return false;
    }

    /**
     * Determines if specified player just lost a duel.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean lostDuel(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.DUEL_ENDING) {
            DuelEndingResult duelEndingResult = (DuelEndingResult) effectResult;
            return playerId.equals(duelEndingResult.getLoser());
        }
        return false;
    }

    /**
     * Determines if the specified player just lost a battle.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean lostBattle(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.BATTLE_RESULT_DETERMINED) {
            BattleResultDeterminedResult lostResult = (BattleResultDeterminedResult) effectResult;
            return lostResult.getLoser() != null && lostResult.getLoser().equals(playerId);
        }
        return false;
    }

    /**
     * Determines if the specified player just lost a battle at a location accepted by the location filter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean lostBattleAt(SwccgGame game, EffectResult effectResult, String playerId, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.BATTLE_RESULT_DETERMINED) {
            BattleResultDeterminedResult lostResult = (BattleResultDeterminedResult) effectResult;
            return lostResult.getLoser() != null && lostResult.getLoser().equals(playerId)
                    && Filters.and(locationFilter).accepts(game, lostResult.getLocation());
        }
        return false;
    }

    /**
     * Determines if the specified player just initiated lightsaber combat.
     *
     * @param game         the game
     * @param effectResult the effect result
     * @param playerId     the player
     * @return true or false
     */
    public static boolean lightsaberCombatInitiated(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.LIGHTSABER_COMBAT_INITIATED) {
            LightsaberCombatInitiatedResult lightsaberCombatInitiatedResult = (LightsaberCombatInitiatedResult) effectResult;
            return playerId.equals(lightsaberCombatInitiatedResult.getPerformingPlayerId()) && game.getGameState().getLightsaberCombatState().canContinue(game);
        }
        return false;
    }

    /**
     * Determines if a character accepted by the winner filter just won lightsaber combat against a character accepted by the loser filter.
     * @param game the game
     * @param effectResult the effect result
     * @param winnerFilter the winner filter
     * @param loserFilter the loser filter
     * @return true or false
     */
    public static boolean wonLightsaberCombatAgainst(SwccgGame game, EffectResult effectResult, Filterable winnerFilter, Filterable loserFilter) {
        if (effectResult.getType() == EffectResult.Type.LIGHTSABER_COMBAT_RESULT_DETERMINED) {
            LightsaberCombatResultDeterminedResult lightsaberCombatResultDeterminedResult = (LightsaberCombatResultDeterminedResult) effectResult;
            PhysicalCard winningCharacter = lightsaberCombatResultDeterminedResult.getWinningCharacter();
            PhysicalCard losingCharacter = lightsaberCombatResultDeterminedResult.getLosingCharacter();
            if (winningCharacter != null && losingCharacter != null) {
                if (Filters.and(winnerFilter).accepts(game, winningCharacter)
                        && Filters.and(loserFilter).accepts(game, losingCharacter)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if it is just after the weapons segment of a battle.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean justAfterWeaponsSegmentOfBattle(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.BATTLE_WEAPONS_SEGMENT_COMPLETED);
    }

    /**
     * Determines if it is just before players draw battle destiny.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean justBeforePlayersDrawBattleDestiny(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.BEFORE_BATTLE_DESTINY_DRAWS);
    }

    /**
     * Determines if the initial attrition values in a battle were just calculated. This is when actions that modify attrition
     * are able to trigger.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isInitialAttritionJustCalculated(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.INITIAL_ATTRITION_CALCULATED);
    }


    /**
     * Determines if it is the specified player's turn to lose Force or forfeit cards from battle damage or attrition.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isResolvingBattleDamageAndAttrition(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_LOSE_OR_FORFEIT_DURING_DAMAGE_SEGMENT) {
            AboutToLoseOrForfeitDuringDamageSegmentResult resolveDamageEffect = (AboutToLoseOrForfeitDuringDamageSegmentResult) effectResult;
            return resolveDamageEffect.getPlayerToLoseForce().equals(playerId);
        }
        return false;
    }

    /**
     * Determines if an asteroid destiny was just drawn (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isAsteroidDestinyJustDrawn(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            return !destinyDrawnResult.isCanceled()
                    && destinyDrawnResult.getDestinyType() == DestinyType.ASTEROID_DESTINY;
        }
        return false;
    }

    /**
     * Determines if an asteroid destiny was just drawn (and was not canceled) and is a card accepted by the filter.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAsteroidDestinyJustDrawnMatching(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            if (!destinyDrawnResult.isCanceled()
                    && destinyDrawnResult.getDestinyType() == DestinyType.ASTEROID_DESTINY) {
                PhysicalCard cardDrawn = destinyDrawnResult.getCard();
                return (cardDrawn != null
                            && Filters.and(filter).accepts(game, cardDrawn));
            }
        }
        return false;
    }

    /**
     * Determines if a destiny was just drawn (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isDestinyJustDrawn(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            return !destinyDrawnResult.isCanceled();
        }
        return false;
    }

    /**
     * Determines if a destiny draw was just drawn (and was not canceled) for the specified destiny draw state.
     * @param game the game
     * @param effectResult the effect result
     * @param drawDestinyState the draw destiny state to check if its the current draw destiny state
     * @return true or false
     */
    public static boolean isDestinyJustDrawn(SwccgGame game, EffectResult effectResult, DrawDestinyState drawDestinyState) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            return !destinyDrawnResult.isCanceled() && drawDestinyState.getId().equals(game.getGameState().getTopDrawDestinyState().getId());
        }
        return false;
    }

    /**
     * Determines if a destiny was just drawn by the specified player (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isDestinyJustDrawnBy(SwccgGame game, EffectResult effectResult, String playerId) {
        return isDestinyJustDrawnBy(game, effectResult, playerId, false);
    }

    /**
     * Determines if a destiny was just drawn by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param skipCanceledCheck true if check for canceled destiny is skipped, otherwise false
     * @return true or false
     */
    public static boolean isDestinyJustDrawnBy(SwccgGame game, EffectResult effectResult, String playerId, boolean skipCanceledCheck) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            return (skipCanceledCheck || !destinyDrawnResult.isCanceled())
                    && playerId.equals(effectResult.getPerformingPlayerId());
        }
        return false;
    }

    /**
     * Determines if a destiny was just drawn for a card accepted by the source card filter (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @param sourceCardFilter the source card filter
     * @return true or false
     */
    public static boolean isDestinyJustDrawnFor(SwccgGame game, EffectResult effectResult, Filterable sourceCardFilter) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            if (!destinyDrawnResult.isCanceled()) {
                Action sourceAction = destinyDrawnResult.getSourceAction();
                PhysicalCard sourceCard = sourceAction.getActionSource();
                return (sourceCard != null && Filters.and(sourceCardFilter).accepts(game, sourceCard));
            }
        }
        return false;
    }

    /**
     * Determines if a destiny was just drawn by the specified player for a card accepted by the source card filter (and
     * was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @param sourceCardFilter the source card filter
     * @return true or false
     */
    public static boolean isDestinyJustDrawnFor(SwccgGame game, EffectResult effectResult, String playerId, Filterable sourceCardFilter) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            if (!destinyDrawnResult.isCanceled()
                    && playerId.equals(effectResult.getPerformingPlayerId())) {
                Action sourceAction = destinyDrawnResult.getSourceAction();
                PhysicalCard sourceCard = sourceAction.getActionSource();
                return (sourceCard != null && Filters.and(sourceCardFilter).accepts(game, sourceCard));
            }
        }
        return false;
    }

    /**
     * Determines if a destiny was just drawn by a card accepted by the source card filter targeting a card accepted by
     * the target card filter (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @param sourceCardFilter the source card filter
     * @param targetCardFilter the target card filter
     * @return true or false
     */
    public static boolean isDestinyJustDrawnTargeting(SwccgGame game, EffectResult effectResult, Filterable sourceCardFilter, Filterable targetCardFilter) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;

            if (!destinyDrawnResult.isCanceled()) {
                Action sourceAction = destinyDrawnResult.getSourceAction();
                PhysicalCard sourceCard = sourceAction.getActionSource();

                if (sourceCard != null
                        && Filters.and(sourceCardFilter).accepts(game, sourceCard)) {
                    return TargetingActionUtils.isTargeting(game, sourceAction, Filters.and(targetCardFilter));
                }
            }
        }
        return false;
    }

    /**
     * Determines if a destiny was just drawn (and was not canceled) that is to be used in a comparison (greater than, less than,
     * or equal to) with the maneuver or defense value (or a calculation that involves said maneuver or defense value) of
     * a card accepted by the targetCardFilter.
     * @param game the game
     * @param effectResult the effect result
     * @param targetCardFilter the target card filter
     * @return true or false
     */
    public static boolean isDestinyJustDrawnTargetingAbilityManeuverOrDefenseValue(SwccgGame game, EffectResult effectResult, Filterable targetCardFilter) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            if (!destinyDrawnResult.isCanceled()) {
                Collection<PhysicalCard> targetedCards = destinyDrawnResult.getAbilityManeuverOrDefenseValueTargeted();
                return (targetedCards != null
                        && !Filters.filterCount(targetedCards, game, 1, targetCardFilter).isEmpty());
            }
        }
        return false;
    }

    /**
     * Determines if a destiny draw was just completed for the specified destiny draw state.
     * @param game the game
     * @param effectResult the effect result
     * @param drawDestinyState the draw destiny state to check if its the current draw destiny state
     * @return true or false
     */
    public static boolean isDestinyDrawComplete(SwccgGame game, EffectResult effectResult, DrawDestinyState drawDestinyState) {
        if (effectResult.getType() == EffectResult.Type.COMPLETE_DESTINY_DRAW) {
            return drawDestinyState.getId().equals(game.getGameState().getTopDrawDestinyState().getId());
        }
        return false;
    }


    /**
     * Determins if a destiny draw was just drawn for the given Destiny Type
     * @param game the game
     * @param effectResult the effect result
     * @param type  Destiny Draw type  (Battle Destiny, To Power, etc)
     * @return true or false
     */
    public static boolean isDestinyDrawType(SwccgGame game, EffectResult effectResult, DestinyType type) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;

            return destinyDrawnResult.getDestinyType() == type;
        }
        return false;
    }

    /**
     * Determines if a destiny draw was just completed during a battle destiny draw by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isBattleDestinyDrawComplete(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.COMPLETE_DESTINY_DRAW) {
            DestinyDrawCompleteResult destinyDrawCompleteResult = (DestinyDrawCompleteResult) effectResult;
            return playerId.equals(destinyDrawCompleteResult.getPerformingPlayerId())
                    && destinyDrawCompleteResult.getDestinyType() == DestinyType.BATTLE_DESTINY;
        }
        return false;
    }

    /**
     * Determines if the specified player is about to draw battle destiny.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isCheckingCostsToDrawBattleDestiny(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.COST_TO_DRAW_DESTINY_CARD) {
            CostToDrawDestinyCardResult result = (CostToDrawDestinyCardResult) effectResult;
            return playerId.equals(effectResult.getPerformingPlayerId())
                    && result.getDestinyType() == DestinyType.BATTLE_DESTINY
                    && !result.isCostToDrawCardFailed();
        }
        return false;
    }

    /**
     * Determines if the specified player is about to draw destiny.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isAboutToDrawDestiny(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_DRAW_DESTINY_CARD) {
            AboutToDrawDestinyCardResult aboutToDrawDestinyCardResult = (AboutToDrawDestinyCardResult) effectResult;
            return playerId.equals(effectResult.getPerformingPlayerId())
                    && !aboutToDrawDestinyCardResult.isSubstituteDestiny();
        }
        return false;
    }

    /**
     * Determines if the specified player is about to draw battle destiny.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isAboutToDrawBattleDestiny(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_DRAW_DESTINY_CARD) {
            AboutToDrawDestinyCardResult aboutToDrawDestinyCardResult = (AboutToDrawDestinyCardResult) effectResult;
            return playerId.equals(effectResult.getPerformingPlayerId())
                    && !aboutToDrawDestinyCardResult.isSubstituteDestiny()
                    && aboutToDrawDestinyCardResult.getDestinyType() == DestinyType.BATTLE_DESTINY;
        }
        return false;
    }

    /**
     * Determines if the specified player is about to draw duel destiny.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isAboutToDrawDuelDestiny(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_DRAW_DESTINY_CARD) {
            AboutToDrawDestinyCardResult aboutToDrawDestinyCardResult = (AboutToDrawDestinyCardResult) effectResult;
            return playerId.equals(effectResult.getPerformingPlayerId())
                    && !aboutToDrawDestinyCardResult.isSubstituteDestiny()
                    && aboutToDrawDestinyCardResult.getDestinyType() == DestinyType.DUEL_DESTINY;
        }
        return false;
    }

    /**
     * Determines if the specified player is about to draw lightsaber combat destiny.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isAboutToDrawLightsaberCombatDestiny(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_DRAW_DESTINY_CARD) {
            AboutToDrawDestinyCardResult aboutToDrawDestinyCardResult = (AboutToDrawDestinyCardResult) effectResult;
            return playerId.equals(effectResult.getPerformingPlayerId())
                    && !aboutToDrawDestinyCardResult.isSubstituteDestiny()
                    && aboutToDrawDestinyCardResult.getDestinyType() == DestinyType.LIGHTSABER_COMBAT_DESTINY;
        }
        return false;
    }

    /**
     * Determines if the specified player is about to draw race destiny.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isAboutToDrawRaceDestiny(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_DRAW_DESTINY_CARD) {
            AboutToDrawDestinyCardResult aboutToDrawDestinyCardResult = (AboutToDrawDestinyCardResult) effectResult;
            return playerId.equals(effectResult.getPerformingPlayerId())
                    && !aboutToDrawDestinyCardResult.isSubstituteDestiny()
                    && aboutToDrawDestinyCardResult.getDestinyType() == DestinyType.RACE_DESTINY;
        }
        return false;
    }

    /**
     * Determines if a battle destiny was just drawn (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isBattleDestinyJustDrawn(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            return !destinyDrawnResult.isCanceled()
                    && destinyDrawnResult.getDestinyType() == DestinyType.BATTLE_DESTINY;
        }
        return false;
    }

    /**
     * Determines if a battle destiny was just drawn by the specified player (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isBattleDestinyJustDrawnBy(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            return !destinyDrawnResult.isCanceled()
                    && destinyDrawnResult.getDestinyType() == DestinyType.BATTLE_DESTINY
                    && playerId.equals(effectResult.getPerformingPlayerId());
        }
        return false;
    }

    /**
     * Determines if a carbon-freezing destiny was just drawn (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isCarbonFreezingDestinyJustDrawn(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            return !destinyDrawnResult.isCanceled()
                    && destinyDrawnResult.getDestinyType() == DestinyType.CARBON_FREEZING_DESTINY;
        }
        return false;
    }

    /**
     * Determines if drawing carbon-freezing destiny is about to complete. This is when the carbon-freezing destiny total
     * can be modified.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isAboutToCompleteCarbonFreezingDestinyDraw(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.DRAWING_DESTINY_COMPLETE) {
            AboutToCompleteDrawingDestinyResult result = (AboutToCompleteDrawingDestinyResult) effectResult;
            return result.getDestinyType() == DestinyType.CARBON_FREEZING_DESTINY;
        }
        return false;
    }

    /**
     * Determines if a duel destiny was just drawn (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isDuelDestinyJustDrawn(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            return !destinyDrawnResult.isCanceled()
                    && destinyDrawnResult.getDestinyType() == DestinyType.DUEL_DESTINY;
        }
        return false;
    }

    /**
     * Determines if a duel destiny was just drawn by the specified player (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isDuelDestinyJustDrawnBy(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            return !destinyDrawnResult.isCanceled()
                    && destinyDrawnResult.getDestinyType() == DestinyType.DUEL_DESTINY
                    && playerId.equals(effectResult.getPerformingPlayerId());
        }
        return false;
    }

    /**
     * Determines if a race destiny was just drawn (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isRaceDestinyJustDrawn(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            return !destinyDrawnResult.isCanceled()
                    && destinyDrawnResult.getDestinyType() == DestinyType.RACE_DESTINY;
        }
        return false;
    }

    /**
     * Determines if a race destiny was just drawn by the specified player (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isRaceDestinyJustDrawnBy(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            return !destinyDrawnResult.isCanceled()
                    && destinyDrawnResult.getDestinyType() == DestinyType.RACE_DESTINY
                    && playerId.equals(effectResult.getPerformingPlayerId());
        }
        return false;
    }

    /**
     * Determines if tractor beam destiny is about to be drawn targeting a card accepted by the targetFilter.
     * @param game the game
     * @param effectResult the effect result
     * @param targetFilter the target filter
     * @return true or false
     */
    public static boolean isAboutToDrawTractorBeamDestinyTargeting(SwccgGame game, EffectResult effectResult, Filterable targetFilter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_DRAW_DESTINY_CARD) {
            AboutToDrawDestinyCardResult aboutToDrawDestinyCardResult = (AboutToDrawDestinyCardResult) effectResult;

            if (!aboutToDrawDestinyCardResult.isSubstituteDestiny()
                    && aboutToDrawDestinyCardResult.getDestinyType() == DestinyType.TRACTOR_BEAM_DESTINY) {

                // TODO: Add checking here.
                return false;
            }
        }
        return false;
    }

    /**
     * Determines if a tractor beam destiny was just drawn (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isTractorBeamDestinyJustDrawn(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            return !destinyDrawnResult.isCanceled()
                    && destinyDrawnResult.getDestinyType() == DestinyType.TRACTOR_BEAM_DESTINY;
        }
        return false;
    }

    /**
     * Determines if weapon destiny is about to be drawn by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isAboutToDrawWeaponDestiny(SwccgGame game, EffectResult effectResult, String playerId) {
        return isAboutToDrawWeaponDestiny(game, effectResult, playerId, Filters.any);
    }

    /**
     * Determines if weapon destiny is about to be drawn by the specified player for a weapon accepted by the weapon filter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param weaponFilter the weapon filter
     * @return true or false
     */
    public static boolean isAboutToDrawWeaponDestiny(SwccgGame game, EffectResult effectResult, String playerId, Filterable weaponFilter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_DRAW_DESTINY_CARD) {
            AboutToDrawDestinyCardResult aboutToDrawDestinyCardResult = (AboutToDrawDestinyCardResult) effectResult;

            if (!aboutToDrawDestinyCardResult.isSubstituteDestiny()
                    && playerId.equals(aboutToDrawDestinyCardResult.getPerformingPlayerId())
                    && (aboutToDrawDestinyCardResult.getDestinyType() == DestinyType.WEAPON_DESTINY
                    || aboutToDrawDestinyCardResult.getDestinyType() == DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY)) {

                WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();

                if (weaponFiringState != null) {
                    PhysicalCard weapon = weaponFiringState.getCardFiring();
                    SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();

                    return ((weapon != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), weapon))
                            || (permanentWeapon != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), permanentWeapon)));
                }
            }
        }
        return false;
    }

    /**
     * Determines if weapon destiny is about to be drawn for a weapon accepted by the weapon filter targeting a card accepted by the target filter.
     * @param game the game
     * @param effectResult the effect result
     * @param weaponFilter the weapon filter
     * @param targetFilter the target filter
     * @return true or false
     */
    public static boolean isAboutToDrawWeaponDestinyTargeting(SwccgGame game, EffectResult effectResult, Filterable weaponFilter, Filterable targetFilter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_DRAW_DESTINY_CARD) {
            AboutToDrawDestinyCardResult aboutToDrawDestinyCardResult = (AboutToDrawDestinyCardResult) effectResult;

            if (!aboutToDrawDestinyCardResult.isSubstituteDestiny()
                    && (aboutToDrawDestinyCardResult.getDestinyType() == DestinyType.WEAPON_DESTINY
                    || aboutToDrawDestinyCardResult.getDestinyType() == DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY)) {

                WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();

                if (weaponFiringState != null) {
                    PhysicalCard weapon = weaponFiringState.getCardFiring();
                    SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();
                    Collection<PhysicalCard> targets = weaponFiringState.getTargets();

                    return ((weapon != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), weapon))
                            || (permanentWeapon != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), permanentWeapon)))
                            && Filters.canSpot(targets, game, targetFilter);
                }
            }
        }
        return false;
    }

    /**
     * Determines if a weapon destiny was just drawn (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isWeaponDestinyJustDrawn(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            return !destinyDrawnResult.isCanceled()
                    && (destinyDrawnResult.getDestinyType() == DestinyType.WEAPON_DESTINY
                    || destinyDrawnResult.getDestinyType() == DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY);
        }
        return false;
    }

    /**
     * Determines if a weapon destiny was just drawn for a weapon accepted by the weapon filter (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @param weaponFilter the weapon filter
     * @return true or false
     */
    public static boolean isWeaponDestinyJustDrawn(SwccgGame game, EffectResult effectResult, Filterable weaponFilter) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;

            if (!destinyDrawnResult.isCanceled()
                    && (destinyDrawnResult.getDestinyType() == DestinyType.WEAPON_DESTINY
                        || destinyDrawnResult.getDestinyType() == DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY)) {
                WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();

                if (weaponFiringState != null) {
                    PhysicalCard weapon = weaponFiringState.getCardFiring();
                    SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();

                    return ((weapon != null && Filters.and(weaponFilter).accepts(game, weapon))
                            || (permanentWeapon != null && Filters.and(weaponFilter).accepts(game, permanentWeapon)));
                }
            }
        }
        return false;
    }

    /**
     * Determines if a weapon destiny was just drawn for a weapon accepted by the weapon filter that is being fired by
     * a card accepted by the weapon user filter (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @param weaponFilter the weapon filter
     * @param weaponUserFilter the weapon user filter
     * @return true or false
     */
    public static boolean isWeaponDestinyJustDrawn(SwccgGame game, EffectResult effectResult, Filterable weaponFilter, Filterable weaponUserFilter) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;

            if (!destinyDrawnResult.isCanceled()
                    && (destinyDrawnResult.getDestinyType() == DestinyType.WEAPON_DESTINY
                    || destinyDrawnResult.getDestinyType() == DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY)) {
                WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();

                if (weaponFiringState != null) {
                    PhysicalCard weapon = weaponFiringState.getCardFiring();
                    SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();
                    PhysicalCard weaponUser = weaponFiringState.getCardFiringWeapon();

                    return ((weapon != null && Filters.and(weaponFilter).accepts(game, weapon))
                            || (permanentWeapon != null && Filters.and(weaponFilter).accepts(game, permanentWeapon)))
                            && (weaponUser != null && Filters.and(weaponUserFilter).accepts(game, weaponUser));
                }
            }
        }
        return false;
    }


    /**
     * Determines if a weapon destiny was just drawn by the specified player (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isWeaponDestinyJustDrawnBy(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            return !destinyDrawnResult.isCanceled()
                    && (destinyDrawnResult.getDestinyType() == DestinyType.WEAPON_DESTINY
                        || destinyDrawnResult.getDestinyType() == DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY)
                    && playerId.equals(effectResult.getPerformingPlayerId());
        }
        return false;
    }

    /**
     * Determines if a weapon destiny was just drawn by the specified player for a weapon accepted by the weapon filter
     * (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param weaponFilter the weapon filter
     * @return true or false
     */
    public static boolean isWeaponDestinyJustDrawnBy(SwccgGame game, EffectResult effectResult, String playerId, Filterable weaponFilter) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;

            if (!destinyDrawnResult.isCanceled()
                    && (destinyDrawnResult.getDestinyType() == DestinyType.WEAPON_DESTINY
                    || destinyDrawnResult.getDestinyType() == DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY)
                    && playerId.equals(effectResult.getPerformingPlayerId())) {

                WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();

                if (weaponFiringState != null) {
                    PhysicalCard weapon = weaponFiringState.getCardFiring();
                    SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();

                    return ((weapon != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), weapon))
                            || (permanentWeapon != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), permanentWeapon)));
                }
            }
        }
        return false;
    }

    /**
     * Determines if a weapon destiny was just drawn by the specified player for a weapon accepted by the weapon filter
     * (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param weaponFilter the weapon filter
     * @param weaponUserFilter the weapon user filter
     * @return true or false
     */
    public static boolean isWeaponDestinyJustDrawnBy(SwccgGame game, EffectResult effectResult, String playerId, Filterable weaponFilter, Filterable weaponUserFilter) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;

            if (!destinyDrawnResult.isCanceled()
                    && (destinyDrawnResult.getDestinyType() == DestinyType.WEAPON_DESTINY
                    || destinyDrawnResult.getDestinyType() == DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY)
                    && playerId.equals(effectResult.getPerformingPlayerId())) {

                WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();

                if (weaponFiringState != null) {
                    PhysicalCard weapon = weaponFiringState.getCardFiring();
                    SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();
                    PhysicalCard weaponFiredBy = weaponFiringState.getCardFiringWeapon();

                    return ((weapon != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), weapon))
                            || (permanentWeapon != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), permanentWeapon)))
                            && (weaponFiredBy != null && Filters.and(weaponUserFilter).accepts(game.getGameState(), game.getModifiersQuerying(), weaponFiredBy));
                }
            }
        }
        return false;
    }

    /**
     * Determines if a weapon destiny was just drawn for a weapon accepted by the weapon filter targeting a card accepted
     * by the target filter (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @param weaponFilter the weapon filter
     * @param targetFilter the target filter
     * @return true or false
     */
    public static boolean isWeaponDestinyJustDrawnTargeting(SwccgGame game, EffectResult effectResult, Filterable weaponFilter, Filterable targetFilter) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;

            if (!destinyDrawnResult.isCanceled()
                    && (destinyDrawnResult.getDestinyType() == DestinyType.WEAPON_DESTINY
                    || destinyDrawnResult.getDestinyType() == DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY)) {
                WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();

                if (weaponFiringState != null) {
                    PhysicalCard weapon = weaponFiringState.getCardFiring();
                    SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();
                    Collection<PhysicalCard> targets = weaponFiringState.getTargets();

                    return ((weapon != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), weapon))
                            || (permanentWeapon != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), permanentWeapon)))
                            && Filters.canSpot(targets, game, targetFilter);
                }
            }
        }
        return false;
    }

    /**
     * Determines if a search party destiny was just drawn by the specified player (and was not canceled).
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isSearchPartyDestinyJustDrawnBy(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.DESTINY_DRAWN) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            return !destinyDrawnResult.isCanceled()
                    && destinyDrawnResult.getDestinyType() == DestinyType.SEARCH_PARTY_DESTINY
                    && playerId.equals(effectResult.getPerformingPlayerId());
        }
        return false;
    }

    /**
     * Determines if battle destiny draws for the specified player just completed.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isBattleDestinyDrawingJustCompletedForPlayer(SwccgGame game, EffectResult effectResult, String playerId) {
        return effectResult.getType() == EffectResult.Type.BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER
                && playerId.equals(effectResult.getPerformingPlayerId());
    }

    /**
     * Determines if battle destiny draws for both players just completed.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isBattleDestinyDrawingJustCompletedForBothPlayers(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS);
    }

    /**
     * Determines if a parasite creature accepted by the parasite filter just attached to a host accepted by the host filter.
     * @param game the game
     * @param effectResult the effect result
     * @param parasiteFilter the parasite filter
     * @param hostFilter the host filter
     * @return true or false
     */
    public static boolean justAttachedParasiteToHost(SwccgGame game, EffectResult effectResult, Filterable parasiteFilter, Filterable hostFilter) {
        if (effectResult.getType() == EffectResult.Type.PARASITE_ATTACHED) {
            ParasiteAttachedResult parasiteAttachedResult = (ParasiteAttachedResult) effectResult;
            PhysicalCard parasite = parasiteAttachedResult.getParasite();
            PhysicalCard host = parasiteAttachedResult.getHost();
            return (parasite != null && Filters.and(parasiteFilter).accepts(game, parasite)
                    && host != null && Filters.and(hostFilter).accepts(game, host));
        }
        return false;
    }

    public static boolean justDefeatedBy(SwccgGame game, EffectResult effectResult, Filterable cardDefeatedFilter, Filterable defeatedByFilter) {
        if (effectResult.getType() == EffectResult.Type.DEFEATED) {
            DefeatedResult defeatedResult = (DefeatedResult) effectResult;
            PhysicalCard cardDefeated = defeatedResult.getCardDefeated();
            return (cardDefeated != null && Filters.and(cardDefeatedFilter).accepts(game, cardDefeated)
                    && Filters.canSpot(defeatedResult.getDefeatedByCards(), game, Filters.and(defeatedByFilter)));
        }
        return false;
    }

    public static boolean justEatenBy(SwccgGame game, EffectResult effectResult, Filterable cardEatenFilter, Filterable eatenByFilter) {
        if (effectResult.getType() == EffectResult.Type.EATEN) {
            EatenResult eatenResult = (EatenResult) effectResult;
            PhysicalCard cardEaten = eatenResult.getCardEaten();
            PhysicalCard eatenByCard = eatenResult.getEatenByCard();
            return (cardEaten != null && Filters.and(cardEatenFilter).accepts(game, cardEaten)
                    && eatenByCard != null && Filters.and(eatenByFilter).accepts(game, eatenByCard));
        }
        return false;
    }

    public static boolean justEatenAt(SwccgGame game, EffectResult effectResult, Filterable cardEatenFilter, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.EATEN) {
            EatenResult eatenResult = (EatenResult) effectResult;
            PhysicalCard cardEaten = eatenResult.getCardEaten();
            PhysicalCard location = eatenResult.getEatenAtLocation();
            return (cardEaten != null && Filters.and(cardEatenFilter).accepts(game, cardEaten)
                        && location != null && Filters.and(locationFilter).accepts(game, location));
        }
        return false;
    }


    /*
    public static boolean forEachHit(SwccgGame game, EffectResult effectResult, Filterable... filters) {
        if (effectResult.getType() == EffectResult.Type.FOR_EACH_HIT) {
            PhysicalCard card = ((HitResult) effectResult).getCardHit();
            return card.isHit() && Filters.and(filters).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        return false;
    }   */


 /*
    public static boolean forEachWeaponFired(SwccgGame game, EffectResult effectResult, Filterable... filters) {
        if (effectResult.getType() == EffectResult.Type.FIRED_WEAPON) {
            PhysicalCard card = ((FiredWeaponResult) effectResult).getWeaponFired();
            return card!=null && Filters.and(filters).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        return false;
    }

*/

    /**
     * Determines if a weapon accepted by the weapon filter was just 'thrown'.
     *
     * @param game         the game
     * @param effectResult the effect result
     * @param weaponFilter the weapon filter
     * @return true or false
     */
    public static boolean weaponJustThrown(SwccgGame game, EffectResult effectResult, Filterable weaponFilter) {
        if (effectResult.getType() == EffectResult.Type.FIRED_WEAPON) {
            FiredWeaponResult weaponFiredResult = (FiredWeaponResult) effectResult;
            PhysicalCard weaponCardFired = weaponFiredResult.getWeaponCardFired();
            SwccgBuiltInCardBlueprint permanentWeaponFired = weaponFiredResult.getPermanentWeaponFired();

            return (weaponCardFired != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), weaponCardFired))
                    || (permanentWeaponFired != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), permanentWeaponFired))
                    && weaponFiredResult.wasThrown();
        }
        return false;
    }

    /**
     * Determines if a weapon accepted by the weapon filter was just fired.
     * @param game the game
     * @param effectResult the effect result
     * @param weaponFilter the weapon filter
     * @return true or false
     */
    public static boolean weaponJustFired(SwccgGame game, EffectResult effectResult, Filterable weaponFilter) {
        if (effectResult.getType() == EffectResult.Type.FIRED_WEAPON) {
            FiredWeaponResult weaponFiredResult = (FiredWeaponResult) effectResult;
            PhysicalCard weaponCardFired = weaponFiredResult.getWeaponCardFired();
            SwccgBuiltInCardBlueprint permanentWeaponFired = weaponFiredResult.getPermanentWeaponFired();

            return (weaponCardFired != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), weaponCardFired))
                    || (permanentWeaponFired != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), permanentWeaponFired));
        }
        return false;
    }

    /**
     * Determines if a card accepted by the firedByFilter just fired a weapon.
     * @param game the game
     * @param effectResult the effect result
     * @param weaponFilter the weapon filter
     * @param firedByFilter the fired by filter
     * @return true or false
     */
    public static boolean weaponJustFiredBy(SwccgGame game, EffectResult effectResult, Filterable weaponFilter, Filterable firedByFilter) {
        if (effectResult.getType() == EffectResult.Type.FIRED_WEAPON) {
            FiredWeaponResult weaponFiredResult = (FiredWeaponResult) effectResult;
            PhysicalCard weaponCardFired = weaponFiredResult.getWeaponCardFired();
            SwccgBuiltInCardBlueprint permanentWeaponFired = weaponFiredResult.getPermanentWeaponFired();
            PhysicalCard cardFiringWeapon = weaponFiredResult.getCardFiringWeapon();

            return ((weaponCardFired != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), weaponCardFired))
                    || (permanentWeaponFired != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), permanentWeaponFired)))
                    && (cardFiringWeapon != null && Filters.and(firedByFilter).accepts(game.getGameState(), game.getModifiersQuerying(), cardFiringWeapon));
        }
        return false;
    }

    /**
     * Determines if a card accepted by cardHitFilter was just 'hit'.
     * @param game the game
     * @param effectResult the effect result
     * @param cardHitFilter the card hit filter
     * @return true or false
     */
    public static boolean justHit(SwccgGame game, EffectResult effectResult, Filterable cardHitFilter) {
        if (effectResult.getType() == EffectResult.Type.HIT) {
            HitResult hitResult = (HitResult) effectResult;
            PhysicalCard hitCard = hitResult.getCardHit();

            return hitCard.isHit() && Filters.and(cardHitFilter).accepts(game.getGameState(), game.getModifiersQuerying(), hitCard);
        }
        return false;
    }

    /**
     * Determines if a card accepted by cardHitFilter was just 'hit' by a card (for weapons this includes both the weapon,
     * including permanent weapon, and the card that fired the weapon) accepted by hitByCardFilter.
     * @param game the game
     * @param effectResult the effect result
     * @param cardHitFilter the card hit filter
     * @param hitByCardFilter the hit by card filter
     * @return true or false
     */
    public static boolean justHitBy(SwccgGame game, EffectResult effectResult, Filterable cardHitFilter, Filterable hitByCardFilter) {
        if (effectResult.getType() == EffectResult.Type.HIT) {
            HitResult hitResult = (HitResult) effectResult;
            PhysicalCard hitCard = hitResult.getCardHit();

            if (hitCard.isHit() && Filters.and(cardHitFilter).accepts(game.getGameState(), game.getModifiersQuerying(), hitCard)) {
                PhysicalCard hitByCard = hitResult.getHitByCard();
                SwccgBuiltInCardBlueprint hitByPermanentWeapon = hitResult.getHitByPermanentWeapon();
                PhysicalCard weaponFiredBy = hitResult.getCardFiringWeapon();

                return (hitByCard != null && Filters.and(hitByCardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), hitByCard))
                        || (hitByPermanentWeapon != null && Filters.and(hitByCardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), hitByPermanentWeapon))
                        || (weaponFiredBy != null && Filters.and(hitByCardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), weaponFiredBy));
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by cardHitFilter was just 'hit' by a card (including permanent weapon) accepted by
     * hitByCardFilter that was fired by a card accepted by cardFiringWeaponFilter.
     * @param game the game
     * @param effectResult the effect result
     * @param cardHitFilter the card hit filter
     * @param hitByCardFilter the hit by card filter
     * @param cardFiringWeaponFilter the card firing weapon filter
     * @return true or false
     */
    public static boolean justHitBy(SwccgGame game, EffectResult effectResult, Filterable cardHitFilter, Filterable hitByCardFilter, Filterable cardFiringWeaponFilter) {
        if (effectResult.getType() == EffectResult.Type.HIT) {
            HitResult hitResult = (HitResult) effectResult;
            PhysicalCard hitCard = hitResult.getCardHit();

            if (hitCard.isHit() && Filters.and(cardHitFilter).accepts(game.getGameState(), game.getModifiersQuerying(), hitCard)) {
                PhysicalCard hitByCard = hitResult.getHitByCard();
                SwccgBuiltInCardBlueprint hitByPermanentWeapon = hitResult.getHitByPermanentWeapon();
                PhysicalCard weaponFiredBy = hitResult.getCardFiringWeapon();

                return ((hitByCard != null && Filters.and(hitByCardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), hitByCard))
                        || (hitByPermanentWeapon != null && Filters.and(hitByCardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), hitByPermanentWeapon)))
                        && (weaponFiredBy != null && Filters.and(cardFiringWeaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), weaponFiredBy));
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by cardHitFilter was just Disarmed.
     * @param game the game
     * @param effectResult the effect result
     * @param cardDisarmedFilter the card disarmed filter
     * @return true or false
     */
    public static boolean justDisarmed(SwccgGame game, EffectResult effectResult, Filterable cardDisarmedFilter) {
        if (effectResult.getType() == EffectResult.Type.DISARMED) {
            PhysicalCard card = ((DisarmedResult) effectResult).getCardDisarmed();
            return card.isDisarmed() && Filters.and(cardDisarmedFilter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        return false;
    }

    /**
     * Determines if a card accepted by cardBullseyedFilter was just 'bullseyed' by a card accepted by bullseyedByCardFilter.
     * @param game the game
     * @param effectResult the effect result
     * @param cardBullseyedFilter the card bullseyed filter
     * @param bullseyedByCardFilter the bullseyed by card filter
     * @return true or false
     */
    public static boolean justBullseyedBy(SwccgGame game, EffectResult effectResult, Filterable cardBullseyedFilter, Filterable bullseyedByCardFilter) {
        if (effectResult.getType() == EffectResult.Type.BULLSEYED) {
            BullseyedResult bullseyedResult = (BullseyedResult) effectResult;
            PhysicalCard bullseyedCard = bullseyedResult.getCardBullseyed();
            PhysicalCard bullseyedByCard = bullseyedResult.getBullseyedByCard();

            return Filters.and(cardBullseyedFilter).accepts(game.getGameState(), game.getModifiersQuerying(), bullseyedCard)
                    && Filters.and(bullseyedByCardFilter).accepts(game.getGameState(), game.getModifiersQuerying(), bullseyedByCard);
        }
        return false;
    }

    /**
     * Determines if a card accepted by cardExcludedFilter was just excluded from battle by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player that excluded cards from battle
     * @param cardExcludedFilter the card excluded filter
     * @return true or false
     */
    public static boolean justExcludedFromBattle(SwccgGame game, EffectResult effectResult, String playerId, Filterable cardExcludedFilter) {
        if (effectResult.getType() == EffectResult.Type.EXCLUDED_FROM_BATTLE) {
            ExcludedFromBattleResult excludedResult = (ExcludedFromBattleResult) effectResult;
            Collection<PhysicalCard> excludedCards = Filters.filter(excludedResult.getCardsExcluded(), game, cardExcludedFilter);
            for (PhysicalCard excludedCard : excludedCards) {
                PhysicalCard excludedByCard = excludedResult.getExcludedByCard(excludedCard);
                if (excludedByCard != null && playerId.equals(excludedByCard.getOwner())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by cardExcludedFilter was just excluded from battle.
     * @param game the game
     * @param effectResult the effect result
     * @param cardExcludedFilter the card excluded filter
     * @return true or false
     */
    public static boolean justExcludedFromBattle(SwccgGame game, EffectResult effectResult, Filterable cardExcludedFilter) {
        if (effectResult.getType() == EffectResult.Type.EXCLUDED_FROM_BATTLE) {
            ExcludedFromBattleResult excludedResult = (ExcludedFromBattleResult) effectResult;
            Collection<PhysicalCard> excludedCards = excludedResult.getCardsExcluded();

            return Filters.canSpot(excludedCards, game, Filters.and(cardExcludedFilter));
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter cardExcludedFilter is about to be excluded from battle by a weapon
     * (for weapons this also includes a permanent weapon) accepted by the weaponFilter that was fired by a card accepted
     * by weaponUserFilter.
     * @param game the game
     * @param effectResult the effect result
     * @param cardToBeExcludedFilter the card excluded filter
     * @param weaponFilter the excluded by card filter
     * @param weaponUserFilter the excluded by card filter
     * @return true or false
     */
    public static boolean isAboutToBeExcludedFromBattleByWeaponFiredBy(SwccgGame game, EffectResult effectResult, Filterable cardToBeExcludedFilter, Filterable weaponFilter, Filterable weaponUserFilter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_EXCLUDED_FROM_BATTLE) {
            AboutToBeExcludedFromBattleResult result = (AboutToBeExcludedFromBattleResult) effectResult;
            PhysicalCard card = result.getCardToBeExcluded();

            if (!result.getPreventableCardEffect().isEffectOnCardPrevented(card)
                    && Filters.and(cardToBeExcludedFilter).accepts(game, card)) {

                PhysicalCard excludedByCard = result.getExcludedByCard();
                SwccgBuiltInCardBlueprint excludedByPermanentWeapon = result.getExcludedByPermanentWeapon();
                PhysicalCard weaponFiredBy = result.getCardFiringWeapon();

                if (((excludedByCard != null && Filters.and(Filters.weapon, weaponFilter).accepts(game, excludedByCard))
                        || (excludedByPermanentWeapon != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), excludedByPermanentWeapon)))
                        && (weaponFiredBy != null && Filters.and(weaponUserFilter).accepts(game.getGameState(), game.getModifiersQuerying(), weaponFiredBy))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by cardExcludedFilter was just excluded from battle by a weapon (for weapons this also
     * includes a permanent weapon) accepted by the weaponFilter that was fired by a card accepted by weaponUserFilter.
     * @param game the game
     * @param effectResult the effect result
     * @param cardExcludedFilter the card excluded filter
     * @param weaponFilter the excluded by card filter
     * @param weaponUserFilter the excluded by card filter
     * @return true or false
     */
    public static boolean justExcludedFromBattleByWeaponFiredBy(SwccgGame game, EffectResult effectResult, Filterable cardExcludedFilter, Filterable weaponFilter, Filterable weaponUserFilter) {
        if (effectResult.getType() == EffectResult.Type.EXCLUDED_FROM_BATTLE) {
            ExcludedFromBattleResult excludedResult = (ExcludedFromBattleResult) effectResult;
            Collection<PhysicalCard> excludedCards = excludedResult.getCardsExcluded();

            for (PhysicalCard excludedCard : Filters.filter(excludedCards, game, Filters.and(cardExcludedFilter))) {
                PhysicalCard excludedByCard = excludedResult.getExcludedByCard(excludedCard);
                SwccgBuiltInCardBlueprint excludedByPermanentWeapon = excludedResult.getExcludedByPermanentWeapon();
                PhysicalCard weaponFiredBy = excludedResult.getCardFiringWeapon();

                if (((excludedByCard != null && Filters.and(Filters.weapon, weaponFilter).accepts(game, excludedByCard))
                        || (excludedByPermanentWeapon != null && Filters.and(weaponFilter).accepts(game.getGameState(), game.getModifiersQuerying(), excludedByPermanentWeapon)))
                        && (weaponFiredBy != null && Filters.and(weaponUserFilter).accepts(game.getGameState(), game.getModifiersQuerying(), weaponFiredBy))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the specified player is about to lose Force.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isAboutToLoseForce(SwccgGame game, EffectResult effectResult, String playerId) {
        return isAboutToLoseForceMoreThan(game, effectResult, playerId, 0);
    }

    /**
     * Determines if the specified player is about to lose more than a specified amount of Force.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param amount the amount of Force
     * @return true or false
     */
    public static boolean isAboutToLoseForceMoreThan(SwccgGame game, EffectResult effectResult, String playerId, int amount) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE
                || effectResult.getType() == EffectResult.Type.ABOUT_TO_LOSE_OR_FORFEIT_DURING_DAMAGE_SEGMENT) {
            AboutToLoseForceResult result = (AboutToLoseForceResult) effectResult;

            return (playerId.equals(result.getPlayerToLoseForce())
                    && result.getForceLossAmount(game) > amount);
        }
        return false;
    }

    /**
     * Determines if any player is about to lose Force from a card accepted by the card filter.
     * @param game the game
     * @param effectResult the effect result
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean isAboutToLoseForceFromCard(SwccgGame game, EffectResult effectResult, Filterable cardFilter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE) {
            AboutToLoseForceResult result = (AboutToLoseForceResult) effectResult;

            if (result.getForceLossAmount(game) > 0) {
                PhysicalCard sourceCard = result.getSourceCard();

                return sourceCard != null
                        && Filters.and(cardFilter).accepts(game, sourceCard);
            }
        }
        return false;
    }

    /**
     * Determines if the specified player is about to lose Force from a card accepted by the card filter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean isAboutToLoseForceFromCard(SwccgGame game, EffectResult effectResult, String playerId, Filterable cardFilter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE) {
            AboutToLoseForceResult result = (AboutToLoseForceResult) effectResult;

            if (playerId.equals(result.getPlayerToLoseForce())
                    && result.getForceLossAmount(game) > 0) {
                PhysicalCard sourceCard = result.getSourceCard();

                return sourceCard != null
                        && Filters.and(cardFilter).accepts(game, sourceCard);
            }
        }
        return false;
    }

    /**
     * Determines if the specified player is about to lose Force.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isAboutToLoseForceFromBattleDamage(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_LOSE_OR_FORFEIT_DURING_DAMAGE_SEGMENT) {
            AboutToLoseForceResult result = (AboutToLoseForceResult) effectResult;

            return (result.isBattleDamage()
                    && playerId.equals(result.getPlayerToLoseForce())
                    && result.getForceLossAmount(game) > 0);
        }
        return false;
    }

    /**
     * Determines if the specified player is about to lose Force from a Force drain at a location accepted by locationFilter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param locationFilter the location filter
     * @return true or false
     */
    public static boolean isAboutToLoseForceFromForceDrainAt(SwccgGame game, EffectResult effectResult, String playerId, Filterable locationFilter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE) {
            AboutToLoseForceResult result = (AboutToLoseForceResult) effectResult;

            if (result.isForceDrain()
                    && playerId.equals(result.getPlayerToLoseForce())
                    && result.getForceLossAmount(game) > 0) {
                PhysicalCard forceDrainLocation = game.getGameState().getForceDrainLocation();

                return forceDrainLocation != null
                        && Filters.and(locationFilter).accepts(game.getGameState(), game.getModifiersQuerying(), forceDrainLocation);
            }
        }
        return false;
    }

    /**
     * Determines if the specified player is about to lose Force from an 'insert' card.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isAboutToLoseForceFromInsertCard(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE) {
            AboutToLoseForceResult result = (AboutToLoseForceResult) effectResult;

            return (result.isFromInsertCard()
                    && playerId.equals(result.getPlayerToLoseForce())
                    && result.getForceLossAmount(game) > 0);
        }
        return false;
    }



    public static boolean isBeingLostFromTable(SwccgGame game, Effect effect, Filterable... filters) {
        return false;
        // TODO: Fix this

/*        if (effect.getType() == Effect.Type.BEFORE_LOST_FROM_TABLE
                || effect.getType() == Effect.Type.BEFORE_FORFEIT_CARD) {
            Collection<PhysicalCard> lostCards = ((AbstractPreventableCardEffect) effect).getAffectedCardsMinusPreventedPlusAttached(game);
            for (PhysicalCard lostCard : lostCards) {
                if (Filters.and(filters).accepts(game.getGameState(), game.getModifiersQuerying(), lostCard))
                    return true;
            }
        }
        return false; */
    }

    public static boolean isBeingForfeitedFromTable(SwccgGame game, Effect effect, Filterable... filters) {
        return false;
        // TODO: Fix this

/*        if (effect.getType() == Effect.Type.BEFORE_FORFEIT_CARD) {
            Collection<PhysicalCard> lostCards = ((AbstractPreventableCardEffect) effect).getAffectedCardsMinusPreventedPlusAttached(game);
            for (PhysicalCard lostCard : lostCards) {
                if (Filters.and(filters).accepts(game.getGameState(), game.getModifiersQuerying(), lostCard))
                    return true;
            }
        }
        return false; */
    }

    public static boolean forceLost(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.FORCE_LOST) {
            LostForceResult lostForceResult = (LostForceResult) effectResult;
            return playerId.equals(lostForceResult.getPerformingPlayerId());
        }
        return false;
    }

    /**
     * Determines if the specified player just activated a Force.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean forceActivated(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.FORCE_ACTIVATED) {
            ActivatedForceResult activatedForceResult = (ActivatedForceResult) effectResult;
            return playerId.equals(activatedForceResult.getPerformingPlayerId());
        }
        return false;
    }

    /**
     * Determines if a Program Trap accepted by the filter is 'exploding'.
     * @param game the game
     * @param effectResult the effect result
     * @param programTrapFilter the Program Trap filter
     * @return true or false
     */
    public static boolean programTrapExploding(SwccgGame game, EffectResult effectResult, Filterable programTrapFilter) {
        if (effectResult.getType() == EffectResult.Type.EXPLODING_PROGRAM_TRAP) {
            ExplodingProgramTrapResult explodingProgramTrapResult = (ExplodingProgramTrapResult) effectResult;
            PhysicalCard programTrap = explodingProgramTrapResult.getProgramTrap();

            return Filters.and(Filters.in_play, programTrapFilter).accepts(game.getGameState(), game.getModifiersQuerying(), programTrap);
        }
        return false;
    }

    /**
     * Determines if the specified player just lost Force from battle damage.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean justLostForceFromBattleDamage(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.FORCE_LOST) {
            LostForceResult lostForceResult = (LostForceResult) effectResult;
            if (playerId.equals(lostForceResult.getPerformingPlayerId())
                    && lostForceResult.isBattleDamage()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the specified player just lost Force from a card accepted by the filter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean justLostForceFromCard(SwccgGame game, EffectResult effectResult, String playerId, Filterable cardFilter) {
        if (effectResult.getType() == EffectResult.Type.FORCE_LOST) {
            LostForceResult lostForceResult = (LostForceResult) effectResult;
            if (playerId.equals(lostForceResult.getPerformingPlayerId())) {
                PhysicalCard sourceCard = lostForceResult.getSourceCard();
                if (sourceCard != null
                        && Filters.and(cardFilter).accepts(game, sourceCard)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the specified player just lost more than a specified amount of Force from a Force drain.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param amount the amount
     * @return true or false
     */
    public static boolean justLostMoreThanXForceFromForceDrain(SwccgGame game, EffectResult effectResult, String playerId, int amount) {
        if (effectResult.getType() == EffectResult.Type.FORCE_LOST) {
            LostForceResult lostForceResult = (LostForceResult) effectResult;
            if (playerId.equals(lostForceResult.getPerformingPlayerId())
                    && lostForceResult.isFromForceDrain()) {
                return amount < lostForceResult.getAmountOfForceLost();
            }
        }
        return false;
    }

    /**
     * Determines if a spy accepted by the filter was just made an "undercover spy".
     * @param game the game
     * @param effectResult the effect result
     * @param spyFilter the spy filter
     * @return true or false
     */
    public static boolean putUndercover(SwccgGame game, EffectResult effectResult, Filterable spyFilter) {
        if (effectResult.getType() == EffectResult.Type.PUT_UNDERCOVER) {
            PhysicalCard card = ((PutUndercoverResult) effectResult).getUndercoverSpy();
            return Filters.and(spyFilter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        return false;
    }

    /**
     * Determines if a spy accepted by the filter just had its "cover broken".
     * @param game the game
     * @param effectResult the effect result
     * @param spyFilter the spy filter
     * @return true or false
     */
    public static boolean coverBroken(SwccgGame game, EffectResult effectResult, Filterable spyFilter) {
        if (effectResult.getType() == EffectResult.Type.COVER_BROKEN) {
            PhysicalCard card = ((CoverBrokenResult) effectResult).getUndercoverSpy();
            return Filters.and(spyFilter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        if (effectResult.getType() == EffectResult.Type.CAPTURED) {
            CaptureCharacterResult result = (CaptureCharacterResult) effectResult;
            PhysicalCard captive = result.getCapturedCard();
            return result.cardWasUndercover()
                    && Filters.and(spyFilter).accepts(game.getGameState(), game.getModifiersQuerying(), captive);
        }
        return false;
    }

    /**
     * Determines if a spy accepted by the filter just had its "cover broken" by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param spyFilter the spy filter
     * @return true or false
     */
    public static boolean coverBrokenBy(SwccgGame game, EffectResult effectResult, String playerId, Filterable spyFilter) {
        if (effectResult.getType() == EffectResult.Type.COVER_BROKEN) {
            PhysicalCard card = ((CoverBrokenResult) effectResult).getUndercoverSpy();
            return playerId.equals(effectResult.getPerformingPlayerId())
                    && Filters.and(spyFilter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        if (effectResult.getType() == EffectResult.Type.CAPTURED) {
            CaptureCharacterResult result = (CaptureCharacterResult) effectResult;
            PhysicalCard captive = result.getCapturedCard();
            return playerId.equals(effectResult.getPerformingPlayerId())
                    && result.cardWasUndercover()
                    && Filters.and(spyFilter).accepts(game.getGameState(), game.getModifiersQuerying(), captive);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter was just captured.
     * @param game the game
     * @param effectResult the effect result
     * @param captiveFilter the filter
     * @return true or false
     */
    public static boolean captured(SwccgGame game, EffectResult effectResult, Filterable captiveFilter) {
        if (effectResult.getType() == EffectResult.Type.CAPTURED) {
            CaptureCharacterResult result = (CaptureCharacterResult) effectResult;
            return Filters.and(captiveFilter).accepts(game.getGameState(), game.getModifiersQuerying(), result.getCapturedCard());
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter was just captured by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param captiveFilter the filter
     * @return true or false
     */
    public static boolean captured(SwccgGame game, EffectResult effectResult, String playerId, Filterable captiveFilter) {
        if (effectResult.getType() == EffectResult.Type.CAPTURED) {
            CaptureCharacterResult result = (CaptureCharacterResult) effectResult;
            return playerId.equals(result.getPerformingPlayerId())
                    && Filters.and(captiveFilter).accepts(game.getGameState(), game.getModifiersQuerying(), result.getCapturedCard());
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter was just 'frozen' and captured.
     * @param game the game
     * @param effectResult the effect result
     * @param captiveFilter the filter
     * @return true or false
     */
    public static boolean frozenAndCaptured(SwccgGame game, EffectResult effectResult, Filterable captiveFilter) {
        if (effectResult.getType() == EffectResult.Type.CAPTURED) {
            CaptureCharacterResult result = (CaptureCharacterResult) effectResult;
            PhysicalCard captive = result.getCapturedCard();
            return captive.isFrozen() && Filters.and(captiveFilter).accepts(game.getGameState(), game.getModifiersQuerying(), captive);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the captive filter was just captured by a card accepted by the captured by filter.
     * @param game the game
     * @param effectResult the effect result
     * @param captiveFilter the captive filter
     * @param capturedByFilter the captured by filter
     * @return true or false
     */
    public static boolean capturedBy(SwccgGame game, EffectResult effectResult, Filterable captiveFilter, Filterable capturedByFilter) {
        if (effectResult.getType() == EffectResult.Type.CAPTURED) {
            CaptureCharacterResult result = (CaptureCharacterResult) effectResult;
            PhysicalCard captive = result.getCapturedCard();

            if (Filters.and(captiveFilter).accepts(game.getGameState(), game.getModifiersQuerying(), captive)) {
                PhysicalCard sourceCard = result.getSourceCard();
                PhysicalCard cardFiringWeapon = result.getCardFiringWeapon();
                PhysicalCard escort = result.getSeizedBy();

                if ((sourceCard != null && Filters.and(capturedByFilter).accepts(game.getGameState(), game.getModifiersQuerying(), sourceCard))
                        || (cardFiringWeapon != null && Filters.and(capturedByFilter).accepts(game.getGameState(), game.getModifiersQuerying(), cardFiringWeapon))
                        || (escort != null && Filters.and(capturedByFilter).accepts(game.getGameState(), game.getModifiersQuerying(), escort))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by the captive filter was just seized by a card accepted by the seized by filter.
     * @param game the game
     * @param effectResult the effect result
     * @param captiveFilter the captive filter
     * @param seizedByFilter the seized by filter
     * @return true or false
     */
    public static boolean seizedBy(SwccgGame game, EffectResult effectResult, Filterable captiveFilter, Filterable seizedByFilter) {
        if (effectResult.getType() == EffectResult.Type.CAPTURED) {
            CaptureCharacterResult result = (CaptureCharacterResult) effectResult;
            PhysicalCard captive = result.getCapturedCard();

            if (Filters.and(captiveFilter).accepts(game, captive)) {
                PhysicalCard escort = result.getSeizedBy();
                if (escort != null && Filters.and(seizedByFilter).accepts(game, escort)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter was just 'frozen'.
     * @param game the game
     * @param effectResult the effect result
     * @param captiveFilter the filter
     * @return true or false
     */
    public static boolean frozen(SwccgGame game, EffectResult effectResult, Filterable captiveFilter) {
        if (effectResult.getType() == EffectResult.Type.FROZEN) {
            FrozenResult result = (FrozenResult) effectResult;
            PhysicalCard captive = result.getCaptive();
            return Filters.and(captiveFilter).accepts(game.getGameState(), game.getModifiersQuerying(), captive);
        }
        return false;
    }


    public static boolean released(SwccgGame game, EffectResult effectResult, Filterable captiveFilter) {
        if (effectResult.getType() == EffectResult.Type.RELEASED) {
            ReleaseCaptiveResult result = (ReleaseCaptiveResult) effectResult;
            PhysicalCard captive = result.getCaptiveReleased();
            return Filters.and(captiveFilter).accepts(game.getGameState(), game.getModifiersQuerying(), captive);
        }
        if (effectResult.getType() == EffectResult.Type.GONE_MISSING) {
            GoneMissingResult result = (GoneMissingResult) effectResult;
            if (result.wasCaptive()) {
                return Filters.and(captiveFilter).accepts(game.getGameState(), game.getModifiersQuerying(), result.getMissingCharacter());
            }
        }
        return false;
    }

    public static boolean captiveDelivered(SwccgGame game, EffectResult effectResult, Filterable captiveFilter) {
        if (effectResult.getType() == EffectResult.Type.DELIVERED_CAPTIVE_TO_PRISON) {
            PhysicalCard captive = ((DeliveredCaptiveToPrisonResult) effectResult).getCaptive();
            return Filters.and(captiveFilter).accepts(game.getGameState(), game.getModifiersQuerying(), captive);
        }
        return false;
    }

    public static boolean captiveDeliveredToPrison(SwccgGame game, EffectResult effectResult, Filterable captiveFilter, Filterable prisonFilter) {
        if (effectResult.getType() == EffectResult.Type.DELIVERED_CAPTIVE_TO_PRISON) {
            DeliveredCaptiveToPrisonResult result = (DeliveredCaptiveToPrisonResult) effectResult;
            PhysicalCard captive = result.getCaptive();
            PhysicalCard prison = result.getPrison();
            return Filters.and(captiveFilter).accepts(game.getGameState(), game.getModifiersQuerying(), captive)
                    && Filters.and(prisonFilter).accepts(game.getGameState(), game.getModifiersQuerying(), prison);
        }
        return false;
    }

    public static boolean captiveDeliveredToPrisonBy(SwccgGame game, EffectResult effectResult, Filterable escortFilter, Filterable prisonFilter) {
        if (effectResult.getType() == EffectResult.Type.DELIVERED_CAPTIVE_TO_PRISON) {
            DeliveredCaptiveToPrisonResult result = (DeliveredCaptiveToPrisonResult) effectResult;
            PhysicalCard escort = result.getEscort();
            PhysicalCard prison = result.getPrison();
            return Filters.and(escortFilter).accepts(game.getGameState(), game.getModifiersQuerying(), escort)
                    && Filters.and(prisonFilter).accepts(game.getGameState(), game.getModifiersQuerying(), prison);
        }
        return false;
    }

    public static boolean tookImprisonedCaptiveIntoCustody(SwccgGame game, EffectResult effectResult, Filterable captiveFilter) {
        if (effectResult.getType() == EffectResult.Type.TOOK_IMPRISONED_CAPTIVE_INTO_CUSTODY) {
            PhysicalCard captive = ((TookImprisonedCaptiveIntoCustodyResult) effectResult).getCaptive();
            return Filters.and(captiveFilter).accepts(game.getGameState(), game.getModifiersQuerying(), captive);
        }
        return false;
    }

    public static boolean leftFrozenCaptiveUnattended(SwccgGame game, EffectResult effectResult, Filterable captiveFilter) {
        if (effectResult.getType() == EffectResult.Type.LEFT_FROZEN_CAPTIVE_UNATTENDED) {
            PhysicalCard captive = ((LeaveFrozenCaptiveUnattendedResult) effectResult).getCaptive();
            return Filters.and(captiveFilter).accepts(game.getGameState(), game.getModifiersQuerying(), captive);
        }
        return false;
    }

    public static boolean tookUnattendedFrozenCaptiveIntoCustody(SwccgGame game, EffectResult effectResult, Filterable captiveFilter) {
        if (effectResult.getType() == EffectResult.Type.TOOK_UNATTENDED_FROZEN_CAPTIVE_INTO_CUSTODY) {
            PhysicalCard captive = ((TookUnattendedFrozenCaptiveIntoCustodyResult) effectResult).getCaptive();
            return Filters.and(captiveFilter).accepts(game.getGameState(), game.getModifiersQuerying(), captive);
        }
        return false;
    }


    /**
     * Determines if the add/modify duel destinies step during a duel is taking place.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isDuelAddOrModifyDuelDestiniesStep(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.DUEL_ADD_MODIFY_DUEL_DESTINIES_STEP);
    }

    /**
     * Determines if a duel was just canceled.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean duelCanceled(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.DUEL_CANCELED);
    }

    /**
     * Determines if a duel just ended.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean duelEnded(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.DUEL_ENDING) {
            return true;
        }
        return false;
    }

    /**
     * Determines if the Sabacc totals are being calculated. This is when actions that modify Sabacc totals are able to trigger.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean isCalculatingSabaccTotals(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.SABACC_TOTAL_CALCULATED);
    }

    /**
     * Determines if a card accepted by the filter just won a game of Cloud City Sabacc.
     * @param game the game
     * @param effectResult the effect result
     * @param winnerFilter the filter for card that won
     * @return true or false
     */
    public static boolean wonCloudCitySabacc(SwccgGame game, EffectResult effectResult, Filterable winnerFilter) {
        if (effectResult.getType() == EffectResult.Type.SABACC_WINNER_DETERMINED) {
            if (Filters.Cloud_City_Sabacc.accepts(game, game.getGameState().getSabaccState().getSabaccInterrupt())) {
                SabaccWinnerDeterminedResult sabaccWinnerDeterminedResult = (SabaccWinnerDeterminedResult) effectResult;
                PhysicalCard winner = sabaccWinnerDeterminedResult.getWinningCharacter();
                return winner != null
                            && Filters.and(winnerFilter).accepts(game, winner);
            }
        }
        return false;
    }

    /**
     * Determines if the relocate step for a card accepted by the filter being 'blown away'.
     * @param game the game
     * @param effectResult the effect result
     * @param blownAwayFilter the filter for the 'blown away' card
     * @return true or false
     */
    public static boolean isBlownAwayRelocateStep(SwccgGame game, EffectResult effectResult, Filterable blownAwayFilter) {
        if (effectResult.getType() == EffectResult.Type.BLOWN_AWAY_RELOCATE_STEP) {
            BlownAwayRelocateStepResult blownAwayRelocateStepResult = (BlownAwayRelocateStepResult) effectResult;
            return Filters.and(blownAwayFilter).accepts(game.getGameState(), game.getModifiersQuerying(), blownAwayRelocateStepResult.getBlownAwayCard());
        }
        return false;
    }

    /**
     * Determines if the calculate Force Loss step for a card accepted by the filter being 'blown away' is being performed.
     * @param game the game
     * @param effectResult the effect result
     * @param blownAwayFilter the filter for the 'blown away' card
     * @return true or false
     */
    public static boolean isBlownAwayCalculateForceLossStep(SwccgGame game, EffectResult effectResult, Filterable blownAwayFilter) {
        if (effectResult.getType() == EffectResult.Type.BLOWN_AWAY_CALCULATE_FORCE_LOSS_STEP) {
            BlownAwayCalculateForceLossStepResult blownAwayCalculateForceLossStepResult = (BlownAwayCalculateForceLossStepResult) effectResult;
            return Filters.and(blownAwayFilter).accepts(game.getGameState(), game.getModifiersQuerying(), blownAwayCalculateForceLossStepResult.getBlownAwayCard());
        }
        return false;
    }

    /**
     * Determines if the calculate Force Loss step for a card accepted by the filter being 'blown away' is being performed.
     * @param game the game
     * @param effectResult the effect result
     * @param blownAwayFilter the filter for the 'blown away' card
     * @return true or false
     */
    public static boolean isBlownAwayBySuperlaserCalculateForceLossStep(SwccgGame game, EffectResult effectResult, Filterable blownAwayFilter) {
        if (effectResult.getType() == EffectResult.Type.BLOWN_AWAY_CALCULATE_FORCE_LOSS_STEP) {
            BlownAwayCalculateForceLossStepResult blownAwayCalculateForceLossStepResult = (BlownAwayCalculateForceLossStepResult) effectResult;
            return blownAwayCalculateForceLossStepResult.isBySuperlaser()
                    && Filters.and(blownAwayFilter).accepts(game.getGameState(), game.getModifiersQuerying(), blownAwayCalculateForceLossStepResult.getBlownAwayCard());
        }
        return false;
    }


    /**
     * Determines if the last step for a card accepted by the filter being 'blown away'.
     * @param game the game
     * @param effectResult the effect result
     * @param blownAwayFilter the filter for the 'blown away' card
     * @return true or false
     */
    public static boolean isBlownAwayLastStep(SwccgGame game, EffectResult effectResult, Filterable blownAwayFilter) {
        if (effectResult.getType() == EffectResult.Type.BLOWN_AWAY_LAST_STEP) {
            BlownAwayLastStepResult blownAwayResult = (BlownAwayLastStepResult) effectResult;
            return Filters.and(blownAwayFilter).accepts(game.getGameState(), game.getModifiersQuerying(), blownAwayResult.getBlownAwayCard());
        }
        return false;
    }

    /**
     * Determines if a character accepted by the filter just went 'missing'.
     * @param game the game
     * @param effectResult the effect result
     * @param filters the filter
     * @return true or false
     */
    public static boolean goneMissing(SwccgGame game, EffectResult effectResult, Filterable filters) {
        if (effectResult.getType() == EffectResult.Type.GONE_MISSING) {
            GoneMissingResult goneMissingResult = (GoneMissingResult) effectResult;
            return Filters.and(filters).accepts(game.getGameState(), game.getModifiersQuerying(), goneMissingResult.getMissingCharacter());
        }
        return false;
    }

    /**
     * Determines if a character accepted by the filter was just found.
     * @param game the game
     * @param effectResult the effect result
     * @param filters the filter
     * @return true or false
     */
    public static boolean missingCharacterFound(SwccgGame game, EffectResult effectResult, Filterable filters) {
        if (effectResult.getType() == EffectResult.Type.FOUND) {
            FoundResult foundResult = (FoundResult) effectResult;
            return Filters.and(filters).accepts(game.getGameState(), game.getModifiersQuerying(), foundResult.getCharacterFound());
        }
        if (effectResult.getType() == EffectResult.Type.CAPTURED) {
            CaptureCharacterResult result = (CaptureCharacterResult) effectResult;
            PhysicalCard captive = result.getCapturedCard();
            return result.cardWasMissing()
                    && Filters.and(filters).accepts(game.getGameState(), game.getModifiersQuerying(), captive);
        }
        return false;
    }

    /**
     * Determines if Force retrieval was just initiated for the specified player to retrieve Force.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isForceRetrievalJustInitiated(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.FORCE_RETRIEVAL_INITIATED) {
            ForceRetrievalState forceRetrievalState = game.getGameState().getTopForceRetrievalState();
            return (forceRetrievalState != null && forceRetrievalState.canContinue() && playerId.equals(effectResult.getPerformingPlayerId()));
        }
        return false;
    }

    /**
     * Determines if the specified player is about to retrieve Force during Force retrieval.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean isAboutToRetrieveForce(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.FORCE_RETRIEVAL_ABOUT_TO_RETRIEVE) {
            ForceRetrievalState forceRetrievalState = game.getGameState().getTopForceRetrievalState();
            return (forceRetrievalState != null && forceRetrievalState.canContinue() && playerId.equals(effectResult.getPerformingPlayerId()));
        }
        return false;
    }

    /**
     * Determines if the specified player just retrieved Force.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean justRetrievedForce(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.RETRIEVED_FORCE) {
            return playerId.equals(effectResult.getPerformingPlayerId());
        }
        return false;
    }

    /**
     * Determines if the specified player just retrieved Force using a card accepted by the card filter.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @param cardFilter the card filter
     * @return true or false
     */
    public static boolean justRetrievedForceUsingCard(SwccgGame game, EffectResult effectResult, String playerId, Filterable cardFilter) {
        if (effectResult.getType() == EffectResult.Type.RETRIEVED_FORCE) {
            if (playerId.equals(effectResult.getPerformingPlayerId())) {
                PhysicalCard sourceCard = ((RetrieveForceResult) effectResult).getSourceCard();
                return sourceCard != null
                        && Filters.and(cardFilter).accepts(game, sourceCard);
            }
        }
        return false;
    }

    /**
     * Determines if the specified player just answered 'no' to a Hypo question.
     * @param game the game
     * @param effectResult the effect result
     * @param playerId the player
     * @return true or false
     */
    public static boolean justAnsweredNoToHypoQuestion(SwccgGame game, EffectResult effectResult, String playerId) {
        if (effectResult.getType() == EffectResult.Type.HYPO_QUESTION_ANSWERED) {
            HypoQuestionAnsweredResult result = (HypoQuestionAnsweredResult) effectResult;
            return playerId.equals(effectResult.getPerformingPlayerId())
                    && HypoQuestionAnsweredResult.Answer.NO == result.getAnswer();
        }
        return false;
    }

    /**
     * Determines if calculating an Epic Event total for an Epic Event accepted by the filter.
     * @param game the game
     * @param effectResult the effect result
     * @param epicEventFilter the filter
     * @return true or false
     */
    public static boolean calculatingEpicEventTotal(SwccgGame game, EffectResult effectResult, Filterable epicEventFilter) {
        if (effectResult.getType() == EffectResult.Type.CALCULATING_EPIC_EVENT_TOTAL) {
            CalculatingEpicEventTotalResult result = (CalculatingEpicEventTotalResult) effectResult;
            return Filters.and(epicEventFilter).accepts(game, result.getEpicEvent());
        }
        return false;
    }

    /**
     * Determines if a Sense or Alter destiny draw was successful.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean senseOrAlterDestinyDrawSuccessful(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.SENSE_ALTER_DESTINY_SUCCESSFUL) {
            return true;
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter was just relocated to Weather Vane.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean justRelocatedToWeatherVane(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.RELOCATED_TO_WEATHER_VANE) {
            PhysicalCard card = ((RelocateToWeatherVaneResult) effectResult).getCard();
            return Filters.and(filter).accepts(game, card);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter is about to be 'hit'.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAboutToBeHit(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_HIT) {
            AboutToBeHitResult aboutToBeHitResult = (AboutToBeHitResult) effectResult;
            PhysicalCard card = aboutToBeHitResult.getCardToBeHit();

            return !aboutToBeHitResult.getPreventableCardEffect().isEffectOnCardPrevented(card)
                    && Filters.and(Filters.onTable, filter).accepts(game.getGameState(), game.getModifiersQuerying(), card);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter is about to be 'hit' by a card accepted by the hit by filter.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAboutToBeHitBy(SwccgGame game, EffectResult effectResult, Filterable filter, Filterable hitByFilter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_HIT) {
            AboutToBeHitResult aboutToBeHitResult = (AboutToBeHitResult) effectResult;
            PhysicalCard card = aboutToBeHitResult.getCardToBeHit();

            if (!aboutToBeHitResult.getPreventableCardEffect().isEffectOnCardPrevented(card)
                    && Filters.and(Filters.onTable, filter).accepts(game, card)) {
                PhysicalCard hitByCard = aboutToBeHitResult.getHitByCard();
                SwccgBuiltInCardBlueprint hitByPermanentWeapon = aboutToBeHitResult.getHitByPermanentWeapon();
                PhysicalCard weaponFiredBy = aboutToBeHitResult.getCardFiringWeapon();

                return (hitByCard != null && Filters.and(hitByFilter).accepts(game, hitByCard))
                        || (hitByPermanentWeapon != null && Filters.and(hitByFilter).accepts(game, hitByPermanentWeapon))
                        || (weaponFiredBy != null && Filters.and(hitByFilter).accepts(game, weaponFiredBy));
            }
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter is about to be stolen.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAboutToBeStolen(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_STOLEN) {
            AboutToBeStolenResult aboutToStealCardResult = (AboutToBeStolenResult) effectResult;
            PhysicalCard card = aboutToStealCardResult.getCardToBeStolen();

            return !aboutToStealCardResult.getPreventableCardEffect().isEffectOnCardPrevented(card)
                    && Filters.and(filter).accepts(game, card);
        }
        return false;
    }

    /**
     * Determines if a card accepted by the filter is about to be stolen.
     * @param game the game
     * @param effectResult the effect result
     * @param filter the filter
     * @return true or false
     */
    public static boolean isAboutToCrossOver(SwccgGame game, EffectResult effectResult, Filterable filter) {
        if (effectResult.getType() == EffectResult.Type.ABOUT_TO_CROSS_OVER) {
            AboutToCrossOverResult aboutToCrossOverResult = (AboutToCrossOverResult) effectResult;
            PhysicalCard card = aboutToCrossOverResult.getCharacterToCrossOver();

            return !aboutToCrossOverResult.getPreventableCardEffect().isEffectOnCardPrevented(card)
                    && Filters.and(filter).accepts(game, card);
        }
        return false;
    }

    /**
     * Determines if an Objective card accepted by the filter was just flipped.
     * @param game the game
     * @param effectResult the effect result
     * @param filters the filter
     * @return true or false
     */
    public static boolean cardFlipped(SwccgGame game, EffectResult effectResult, Filterable filters) {
        if (effectResult.getType() == EffectResult.Type.DOUBLE_SIDED_CARD_FLIPPED) {
            PhysicalCard objective = ((DoubleSidedCardFlippedResult) effectResult).getCardFlipped();
            return Filters.and(filters).accepts(game, objective);
        }
        return false;
    }

    /**
     * Determines if Special Delivery was just completed by an escort accepted by the escort filter.
     * @param game the game
     * @param effectResult the effect result
     * @param escortFilter the escort filter
     * @return true or false
     */
    public static boolean specialDeliveryCompletedBy(SwccgGame game, EffectResult effectResult, Filterable escortFilter) {
        if (effectResult.getType() == EffectResult.Type.SPECIAL_DELIVERY_COMPLETED) {
            PhysicalCard escort = ((SpecialDeliveryCompletedResult) effectResult).getEscort();
            return Filters.and(escortFilter).accepts(game, escort);
        }
        return false;
    }

    /**
     * Determines if an Utinni Effect accepted by the filter was just completed.
     * @param game the game
     * @param effectResult the effect result
     * @param filters the filter
     * @return true or false
     */
    public static boolean utinniEffectCompleted(SwccgGame game, EffectResult effectResult, Filterable filters) {
        if (effectResult.getType() == EffectResult.Type.UTINNI_EFFECT_COMPLETED) {
            PhysicalCard utinniEffect = ((UtinniEffectCompletedResult) effectResult).getUtinniEffect();
            return Filters.and(filters).accepts(game, utinniEffect);
        }
        return false;
    }

    /**
     * Determines if a Jedi Test accepted by the Jedi Test filter was just completed by the specified player.
     * @param game the game
     * @param effectResult the effect result
     * @param jediTestFilter the Jedi Test filter
     * @return true or false
     */
    public static boolean jediTestCompleted(SwccgGame game, EffectResult effectResult, Filterable jediTestFilter) {
        if (effectResult.getType() == EffectResult.Type.JEDI_TEST_COMPLETED) {
            PhysicalCard jediTest = ((JediTestCompletedResult) effectResult).getJediTest();
            return Filters.and(jediTestFilter).accepts(game, jediTest);
        }
        return false;
    }

    /**
     * Determines if a Jedi Test accepted by the Jedi Test filter was just completed by a card accepted by the completed by filter.
     * @param game the game
     * @param effectResult the effect result
     * @param jediTestFilter the Jedi Test filter
     * @param completedByFilter the completed by filter
     * @return true or false
     */
    public static boolean jediTestCompletedBy(SwccgGame game, EffectResult effectResult, Filterable jediTestFilter, Filterable completedByFilter) {
        if (effectResult.getType() == EffectResult.Type.JEDI_TEST_COMPLETED) {
            JediTestCompletedResult completedResult = (JediTestCompletedResult) effectResult;
            return Filters.and(jediTestFilter).accepts(game, completedResult.getJediTest())
                    && Filters.and(completedByFilter).accepts(game, completedResult.getCompletedBy());
        }
        return false;
    }


    public static boolean forEachAsteroidCaveOrSpaceSlugBellyChanged(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.CHANGED_ASTEROID_CAVE_OR_SPACE_SLUG_BELLY);
    }

    public static boolean forEachSpaceSlugMouthChanged(SwccgGame game, EffectResult effectResult) {
        return (effectResult.getType() == EffectResult.Type.CLOSE_SPACE_SLUG_MOUTH
                    || effectResult.getType() == EffectResult.Type.OPEN_SPACE_SLUG_MOUTH);
    }

    /**
     * Determines if a system was just 'probed'.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean systemProbed(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.PROBED_SYSTEM) {
            return true;
        }
        return false;
    }

    /**
     * Deterines if a 'hidden base' was just 'probed'.
     * @param game the game
     * @param effectResult the effect result
     * @return true or false
     */
    public static boolean hiddenBaseProbed(SwccgGame game, EffectResult effectResult) {
        if (effectResult.getType() == EffectResult.Type.PROBED_HIDDEN_BASE) {
            return true;
        }
        return false;
    }
}
