package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.actions.battle.BattleDamageSegmentAction;

/**
 * The effect result that is emitted when a player is about to lose Force to battle damage or forfeit cards from battle.
 */
public class AboutToLoseOrForfeitDuringDamageSegmentResult extends EffectResult implements AboutToLoseForceResult {
    private String _playerToLoseOrForfeit;
    private BattleDamageSegmentAction.ChooseCardToLoseOrForfeitEffect _effect;

    /**
     * Creates an effect result that is emitted when a player is about to lose Force to battle damage or forfeit cards from battle.
     * @param action the action
     * @param playerToLoseOrForfeit the player to lose Force or forfeit cards from battle
     * @param effect the effect that can be used to get information about the current choice of losing Force to battle damage or forfeit cards from battle
    */
    public AboutToLoseOrForfeitDuringDamageSegmentResult(Action action, String playerToLoseOrForfeit, BattleDamageSegmentAction.ChooseCardToLoseOrForfeitEffect effect) {
        super(Type.ABOUT_TO_LOSE_OR_FORFEIT_DURING_DAMAGE_SEGMENT, action.getPerformingPlayer());
        _playerToLoseOrForfeit = playerToLoseOrForfeit;
        _effect = effect;
    }

    /**
     * Gets the source card of the Force loss.
     * @return the source card
     */
    public PhysicalCard getSourceCard() {
        return null;
    }

    /**
     * Gets the player to lose Force to battle damage or forfeit cards from battle.
     * @return the player
     */
    public String getPlayerToLoseForce() {
        return _playerToLoseOrForfeit;
    }

    /**
     * Determines if the Force loss is from a Force drain.
     * @return true or false
     */
    public boolean isForceDrain() {
        return false;
    }

    /**
     * Determines if the Force loss is from an 'insert' card.
     * @return true or false
     */
    public boolean isFromInsertCard() {
        return false;
    }

    /**
     * Determines if the Force loss is from battle damage.
     * @return true or false
     */
    public boolean isBattleDamage() {
        return true;
    }

    /**
     * Gets the amount of Force to lose. This value is calculated at the time this method is called.
     * @param game the game
     * @return the amount of Force to lose
     */
    public float getForceLossAmount(SwccgGame game) {
        return game.getGameState().getBattleState().getBattleDamageRemaining(game, _playerToLoseOrForfeit);
    }

    /**
     * Determines if the Force loss may not be reduced.
     * @return true or false
     */
    public boolean isCannotBeReduced() {
        return false;
    }

    /**
     * Determines if the current Force loss or forfeit card choice has already been fulfilled.
     * @return true if it is already fulfilled, otherwise false
     */
    public boolean isAlreadyFulfilled() {
        return _effect.isFulfilledByOtherAction();
    }

    /**
     * Sets the current loss of Force or forfeit card from battle action as already fulfilled by another action.
     * Example: Mantellian Savrip or The Professor can be used to forfeit cards (or reduce battle damage).
     */
    public void setFulfilled() {
        _effect.setFulfilledByOtherAction();
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Battle damage segment";
    }
}
