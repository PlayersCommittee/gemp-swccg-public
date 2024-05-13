package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.AttackState;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.LightsaberCombatState;
import com.gempukku.swccgo.game.state.SabaccState;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collection;

/**
 * This class defines utility methods that are used when gathering information for display on the user interface.
 */
public class GuiUtils {

    public static float calculateForceGeneration(SwccgGame game, String playerId) {
        return game.getModifiersQuerying().getTotalForceGeneration(game.getGameState(), playerId);
    }

    public static float getSabaccTotal(SwccgGame game, String playerId) {
        // Check if not during sabacc game
        if (!game.getGameState().isDuringSabacc() || !game.getGameState().getSabaccState().isInitialCardsDrawn())
            return -1;

        // Check if sabacc final total is set
        SabaccState sabaccState = game.getGameState().getSabaccState();
        if (sabaccState != null) {
            Float finalTotal = sabaccState.getFinalSabaccTotal(playerId);
            if (finalTotal != null) {
                return finalTotal;
            }
        }

        for (PhysicalCard sabaccCard : game.getGameState().getSabaccHand(playerId)) {
            if (sabaccCard.getSabaccValue() == -1)
                return -2;
        }

        return game.getModifiersQuerying().getSabaccTotal(game.getGameState(), playerId);
    }

    public static float getBattleTotalPower(SwccgGame game, String playerId) {
        final BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null || battleState.isReachedDamageSegment())
            return -1;

        String preBattleDestinyPlayerIdToUse = battleState.isPreBattleDestinyTotalPowerSwitched() ? game.getOpponent(playerId) : playerId;

        float totalPower = game.getModifiersQuerying().getTotalPowerAtLocation(game.getGameState(), battleState.getBattleLocation(), preBattleDestinyPlayerIdToUse, true, false);

        // Add total destiny to power only
        totalPower += battleState.getTotalDestinyToPowerOnly(preBattleDestinyPlayerIdToUse);

        // Add total battle destiny to power
        totalPower += battleState.getTotalBattleDestiny(game, playerId);

        // Apply any modifiers after destinies are drawn
        totalPower += game.getModifiersQuerying().getTotalPowerDuringBattle(game.getGameState(), playerId, battleState.getBattleLocation());

        return Math.max(0, totalPower);
    }

    /**
     * Gets the number of battle destiny draws remaining for the specified player.
     * @param game the game
     * @param playerId the player
     * @return the number of battle destiny draws remaining
     */
    public static int getNumBattleDestinyLeftToDraw(SwccgGame game, String playerId) {
        final BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null || battleState.isBaseAttritionCalculated())
            return 0;

        return game.getModifiersQuerying().getNumBattleDestinyDraws(game.getGameState(), playerId, false, true) - battleState.getNumBattleDestinyDrawn(playerId);
    }

    /**
     * Gets the number of destiny draws to total power remaining for the specified player.
     * @param game the game
     * @param playerId the player
     * @return the number of destiny draws to total power remaining
     */
    public static int getNumDestinyToTotalPowerLeftToDraw(SwccgGame game, String playerId) {
        final BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null || battleState.isBaseAttritionCalculated())
            return 0;

        return game.getModifiersQuerying().getNumDestinyDrawsToTotalPowerOnly(game.getGameState(), playerId, false, true) - battleState.getNumDestinyToTotalPowerDrawn(playerId);
    }

    /**
     * Gets the number of destiny draws to attrition remaining for the specified player.
     * @param game the game
     * @param playerId the player
     * @return the number of destiny draws to attrition remaining
     */
    public static int getNumDestinyToAttritionLeftToDraw(SwccgGame game, String playerId) {
        final BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null || battleState.isBaseAttritionCalculated())
            return 0;

        return game.getModifiersQuerying().getNumDestinyDrawsToAttritionOnly(game.getGameState(), playerId, false, true) - battleState.getNumDestinyToAttritionDrawn(playerId);
    }

    public static float getBattleDamageRemaining(SwccgGame game, String playerId) {
        final BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null || !battleState.isReachedDamageSegment())
            return 0;

        return battleState.getBattleDamageRemaining(game, playerId);
    }

    public static float getBattleAttritionRemaining(SwccgGame game, String playerId) {
        final BattleState battleState = game.getGameState().getBattleState();
        if (battleState == null || !battleState.isBaseAttritionCalculated())
            return 0;

        // If there are no more cards that can be forfeited, then ignore any remaining attrition
        if (!Filters.canSpot(battleState.getAllCardsParticipating(), game,
                Filters.and(Filters.owner(playerId), Filters.mayBeForfeited)))
            return 0;

        return battleState.getAttritionRemaining(game, playerId);
    }


    public static boolean isImmuneToRemainingAttrition(SwccgGame game, String playerId) {
        // Determine if all cards present that can be forfeited are immune to the total attrition.
        if (getBattleAttritionRemaining(game, playerId) > 0) {

            GameState gameState = game.getGameState();
            BattleState battleState = gameState.getBattleState();
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

            // Get total attrition
            float totalAttrition = battleState.getAttritionTotal(game, playerId);

            // Immunity to attrition (less than)
            boolean allHaveSufficentImmunityToAttritionLessThan = true;
            // Immunity to attrition (exactly)
            boolean allHaveSufficentImmunityToAttritionOfExactly = true;

            // Get cards from battle that can be forfeited to satisfy battle damage and attrition
            Collection<PhysicalCard> cardsThatMayBeForfeited = Filters.filter(battleState.getAllCardsParticipating(), game,
                    Filters.and(Filters.owner(playerId), Filters.mayBeForfeited));

            for (PhysicalCard forfeitableCard : cardsThatMayBeForfeited) {
                // Only check cards present at the battle
                if (Filters.wherePresent(forfeitableCard).accepts(gameState, modifiersQuerying, battleState.getBattleLocation())) {

                    float exactImmunity = modifiersQuerying.getImmunityToAttritionOfExactly(gameState, forfeitableCard);
                    if (exactImmunity > 0) {
                        if (exactImmunity != totalAttrition) {
                            allHaveSufficentImmunityToAttritionOfExactly = false;
                            break;
                        }
                    }
                    else {
                        float immunityToLessThan = modifiersQuerying.getImmunityToAttritionLessThan(gameState, forfeitableCard);
                        if (immunityToLessThan <= totalAttrition) {
                            allHaveSufficentImmunityToAttritionLessThan = false;
                            break;
                        }
                    }
                }
            }

            return allHaveSufficentImmunityToAttritionOfExactly && allHaveSufficentImmunityToAttritionLessThan;
        }

        return false;
    }

    /**
     * Gets the duel (or lightsaber combat) total for the specified player for display on the user interface.
     * @param game the game
     * @param playerId the player
     * @return the duel total, or -1 if no duel total is available
     */
    public static float getDuelOrLightsaberCombatTotal(SwccgGame game, String playerId) {
        if (game.getGameState().isDuringDuel()) {
            return game.getModifiersQuerying().getDuelTotal(game.getGameState(), playerId);
        }
        else if (game.getGameState().isDuringLightsaberCombat()) {
            return game.getModifiersQuerying().getLightsaberCombatTotal(game.getGameState(), playerId);
        }
        return -1;
    }

    /**
     * Gets the number of duel (or lightsaber combat) destiny draws remaining for the specified player.
     * @param game the game
     * @param playerId the player
     * @return the number of duel (or lightsaber combat) destiny draws remaining
     */
    public static int getNumDuelOrLightsaberCombatDestinyLeftToDraw(SwccgGame game, String playerId) {
        if (game.getGameState().isDuringDuel()) {
            final DuelState duelState = game.getGameState().getDuelState();
            if (duelState == null || duelState.isReachedResults())
                return 0;

            return game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), playerId) - duelState.getNumDuelDestinyDrawn(playerId);
        }
        else if (game.getGameState().isDuringLightsaberCombat()) {
            final LightsaberCombatState lightsaberCombatState = game.getGameState().getLightsaberCombatState();
            if (lightsaberCombatState == null || lightsaberCombatState.isReachedResults())
                return 0;

            return game.getModifiersQuerying().getNumLightsaberCombatDestinyDraws(game.getGameState(), playerId) - lightsaberCombatState.getNumLightsaberCombatDestinyDrawn(playerId);
        }
        return 0;
    }

    /**
     * Gets the attacker total in an attack for display on the user interface.
     * @param game the game
     * @return the attacker total, or -1 if no attacker total is available
     */
    public static float getAttackAttackerTotal(SwccgGame game) {
        AttackState attackState = game.getGameState().getAttackState();
        if (attackState == null || attackState.isParasiteAttackingNonCreature()) {
            return -1;
        }
        return game.getModifiersQuerying().getAttackTotal(game.getGameState(), false);
    }

    /**
     * Gets the defender total in an attack for display on the user interface.
     * @param game the game
     * @return the defender total, or -1 if no defender total is available
     */
    public static float getAttackDefenderTotal(SwccgGame game) {
        AttackState attackState = game.getGameState().getAttackState();
        if (attackState == null || attackState.isParasiteAttackingNonCreature()) {
            return -1;
        }
        return game.getModifiersQuerying().getAttackTotal(game.getGameState(), true);
    }

    /**
     * Gets the number of attack (or ferocity) destiny draws for the attacker in an attack remaining for display on the user interface.
     * @param game the game
     * @return the number of attack or ferocity destiny draws remaining
     */
    public static int getNumAttackAttackerDestinyLeftToDraw(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        final AttackState attackState = gameState.getAttackState();
        if (attackState == null || attackState.isParasiteAttackingNonCreature() || attackState.isReachedDamageSegment()) {
            return 0;
        }
        if (attackState.isCreaturesAttackingEachOther() || attackState.isCreatureAttackingNonCreature()) {
            if (attackState.getCardsAttacking().isEmpty()) {
                return 0;
            }
            PhysicalCard creature = attackState.getCardsAttacking().iterator().next();
            if (creature == null || attackState.getFerocityDestinyTotal(creature) != null) {
                return 0;
            }
            return modifiersQuerying.getNumFerocityDestiny(gameState, creature);
        }
        else {
            if (attackState.getAttackDestinyTotal(attackState.getAttackerOwner()) != null) {
                return 0;
            }
            return modifiersQuerying.getNumAttackDestinyDraws(gameState, attackState.getAttackerOwner(), false, true);
        }
    }

    /**
     * Gets the number of attack (or ferocity) destiny draws for the defender in an attack remaining for display on the user interface.
     * @param game the game
     * @return the number of attack or ferocity destiny draws remaining
     */
    public static int getNumAttackDefenderDestinyLeftToDraw(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        final AttackState attackState = gameState.getAttackState();
        if (attackState == null || attackState.isParasiteAttackingNonCreature() || attackState.isReachedDamageSegment()) {
            return 0;
        }
        if (attackState.isCreaturesAttackingEachOther() || attackState.isNonCreatureAttackingCreature()) {
            if (attackState.getCardsDefending().isEmpty()) {
                return 0;
            }
            PhysicalCard creature = attackState.getCardsDefending().iterator().next();
            if (creature == null || attackState.getFerocityDestinyTotal(creature) != null) {
                return 0;
            }
            return modifiersQuerying.getNumFerocityDestiny(gameState, creature);
        }
        else {
            if (attackState.getAttackDestinyTotal(attackState.getDefenderOwner()) != null) {
                return 0;
            }
            return modifiersQuerying.getNumAttackDestinyDraws(gameState, attackState.getDefenderOwner(), false, true);
        }
    }

    /**
     * Gets the highest race total for the specified player.
     * @param game the game
     * @param playerId the player
     * @return the race total, or -1 if not during a Podrace
     */
    public static float getHighestRaceTotal(SwccgGame game, String playerId) {
        GameState gameState = game.getGameState();
        if (!gameState.isDuringPodrace()) {
            return -1;
        }
        return game.getModifiersQuerying().getHighestRaceTotal(gameState, playerId);
    }

    /**
     * Gets the politics total for the specified player.
     * @param game the game
     * @param playerId the player
     * @return the politics total, or -1 if galactic senate not on table
     */
    public static float getPoliticsTotal(SwccgGame game, String playerId) {
        GameState gameState = game.getGameState();
        if(!game.getModifiersQuerying().isSenateInSession()){
            return -1;
        }
        return game.getModifiersQuerying().getTotalPoliticsAtGalacticSenate(gameState, playerId);
    }


    /**
     * Format a float value as a String for display.
     * @param floatValue the float value
     */
    public static String formatAsString(Float floatValue) {
        return formatAsString(floatValue, false);
    }

    /**
     * Format a float value as a String for display.
     * @param floatValue the float value
     * @param shownOnTable true if shown on user interface table (less space available), otherwise false
     */
    public static String formatAsString(Float floatValue, boolean shownOnTable) {
        DecimalFormat df = new DecimalFormat(shownOnTable ? "#.#" : "#.#####");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(floatValue);
    }
}
