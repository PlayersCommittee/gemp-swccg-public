package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.GameTextAction;
import com.gempukku.swccgo.logic.timing.EffectResult;

// This interface provides methods that allow
// modifiers to automatically be removed after
// a specified period.
//
public interface ModifiersEnvironment {

    ModifierHook addAlwaysOnModifier(Modifier modifier);

    void addCardSpecificAlwaysOnModifiers(SwccgGame game, PhysicalCard card);

    /**
     * Adds a modifier that expires when the current turn is finished.
     * @param modifier the modifier
     */
    void addUntilEndOfTurnModifier(Modifier modifier);

    /**
     * Adds a modifier that expires when the next turn is finished.
     * @param modifier the modifier
     */
    void addUntilEndOfNextTurnModifier(Modifier modifier);

    /**
     * Adds a modifier that expires when the specified players next turn is finished.
     * @param modifier the modifier
     */
    void addUntilEndOfPlayersNextTurnModifier(Modifier modifier, String playerId);

    /**
     * Adds a modifier that expires when the current Force drain is finished.
     * @param modifier the modifier
     */
    void addUntilEndOfForceDrainModifier(Modifier modifier);

    /**
     * Adds a modifier that expires when the current Force loss (not including battle damage) is finished.
     * @param modifier the modifier
     */
    void addUntilEndOfForceLossModifier(Modifier modifier);

    /**
     * Adds a modifier that expires when the current Force retrieval is finished.
     * @param modifier the modifier
     */
    void addUntilEndOfForceRetrievalModifier(Modifier modifier);

    /**
     * Adds a modifier that expires when the current draw destiny is finished.
     * @param modifier the modifier
     */
    void addUntilEndOfDrawDestinyModifier(Modifier modifier);

    /**
     * Adds a modifier that expires when the current drawn destiny within a draw destiny action is finished.
     * @param modifier the modifier
     */
    void addUntilEndOfEachDrawnDestinyModifier(Modifier modifier);

    /**
     * Adds a modifier that expires when the current blow away is finished.
     * @param modifier the modifier
     */
    void addUntilEndOfBlowAwayModifier(Modifier modifier);

    /**
     * Adds a modifier that expires when the card is finished being played.
     * @param modifier the modifier
     * @param card the card
     */
    void addUntilEndOfCardPlayedModifier(Modifier modifier, PhysicalCard card);

    /**
     * Adds a modifier that expires when the specified effect result is complete.
     * @param modifier the modifier
     * @param effectResult the effect result
     */
    void addUntilEndOfEffectResultModifier(Modifier modifier, EffectResult effectResult);

    /**
     * Adds a modifier that expires when the specified game text action is complete.
     * @param modifier the modifier
     * @param gameTextAction the game text action
     */
    void addUntilEndOfGameTextActionModifier(Modifier modifier, GameTextAction gameTextAction);

    /**
     * Adds a modifier that expires when the current weapon firing is finished.
     * @param modifier the modifier
     */
    void addUntilEndOfWeaponFiringModifier(Modifier modifier);

    /**
     * Adds a modifier that expires when the current tractor beam is finished.
     * @param modifier the modifier
     */
    void addUntilEndOfTractorBeamModifier(Modifier modifier);

    /**
     * Adds a modifier that expires when the damage segment of the current battle is reached.
     * @param modifier the modifier
     */
    void addUntilDamageSegmentOfBattleModifier(Modifier modifier);

    /**
     * Adds a modifier that expires when the current battle is finished.
     * @param modifier the modifier
     */
    void addUntilEndOfBattleModifier(Modifier modifier);

    /**
     * Adds a modifier that expires when the current attack is finished.
     * @param modifier the modifier
     */
    void addUntilEndOfAttackModifier(Modifier modifier);

    /**
     * Adds a modifier that expires when the current duel is finished.
     * @param modifier the modifier
     */
    void addUntilEndOfDuelModifier(Modifier modifier);

    /**
     * Adds a modifier that expires when the current lightsaber combat is finished.
     * @param modifier the modifier
     */
    void addUntilEndOfLightsaberCombatModifier(Modifier modifier);

    /**
     * Adds a modifier that expires when the current Epic Event action is finished.
     * @param modifier the modifier
     */
    void addUntilEndOfEpicEventModifier(Modifier modifier);

    /**
     * Adds a modifier that expires when the current Sabacc game is finished.
     * @param modifier the modifier
     */
    void addUntilEndOfSabaccModifier(Modifier modifier);

    /**
     * Adds a modifier that does not expire during the game.
     * @param modifier the modifier
     */
    void addUntilEndOfGameModifier(Modifier modifier);

    /**
     * Adds a modifier that expires when the specified players next turn begins.
     * @param modifier the modifier
     */
    void addUntilStartOfPlayersNextTurnModifier(Modifier modifier, String playerId);

    /**
     * Adds a modifier that expires when the next turn begins.
     * @param modifier the modifier
     */
    void addUntilStartOfNextTurnModifier(Modifier modifier);


    /**
     * Removes modifiers whose expire condition is met.
     */
    void removeExpiredModifiers();

    /**
     * Removes modifiers that expire when the current turn is finished.
     */
    void removeEndOfTurnModifiers();

    /**
     * Removes modifiers that expire when the current turn begins.
     */
    void removeStartOfTurnModifiers();

    /**
     * Removes counters that expire when the current turn is finished.
     */
    void removeEndOfTurnCounters();

    /**
     * Removes modifiers that expire when the captives captivity ends.
     */
    void removeEndOfCaptivity(PhysicalCard captive);

    /**
     * Removes modifiers that expire when the Podrace ends.
     */
    void removeEndOfPodrace();

    /**
     * Removes modifiers that expire when the current Force drain is finished.
     */
    void removeEndOfForceDrain();

    /**
     * Removes modifiers and counters that expire when the current Force loss is finished.
     */
    void removeEndOfForceLoss();

    /**
     * Removes modifiers that expire when the current Force retrieval is finished.
     */
    void removeEndOfForceRetrieval();

    /**
     * Removes modifiers that expire when the current draw destiny is finished.
     */
    void removeEndOfDrawDestiny();

    /**
     * Removes modifiers that expire when the current drawn destiny within a draw destiny action is finished.
     */
    void removeEndOfEachDrawnDestiny();

    /**
     * Removes modifiers that expire when the current blow away is finished.
     */
    void removeEndOfBlowAway();

    /**
     * Removes modifiers that expire when the current attack is finished.
     */
    void removeEndOfAttack();

    /**
     * Removes modifiers that expire when the damage segment of battle is reached.
     */
    void removeReachedDamageSegmentOfBattle();

    /**
     * Removes modifiers that expire when the current battle is finished.
     */
    void removeEndOfBattle();

    /**
     * Removes modifiers that expire when the current Sabacc game is finished.
     */
    void removeEndOfSabacc();

    /**
     * Removes modifiers that expire when the current duel is finished.
     */
    void removeEndOfDuel();

    /**
     * Removes modifiers that expire when the current lightsaber combat is finished.
     */
    void removeEndOfLightsaberCombat();

    /**
     * Removes modifiers that expire when the current epic event action is finished.
     */
    void removeEndOfEpicEvent();

    /**
     * Removes modifiers that expire when the specified effect result is complete.
     * @param effectResult the effect result
     */
    void removeEndOfEffectResult(EffectResult effectResult);

    /**
     * Removes modifiers that expire when playing the specified card is complete.
     * @param card the card
     */
    void removeEndOfCardPlayed(PhysicalCard card);

    /**
     * Removes modifiers that expire when the current game text action is finished.
     */
    void removeEndOfGameTextAction();

    /**
     * Removes modifiers that expire when the current weapon firing is finished.
     */
    void removeEndOfWeaponFiring();

    /**
     * Removes modifiers that expire when the current tractor beam is finished.
     */
    void removeEndOfTractorBeam();
}
