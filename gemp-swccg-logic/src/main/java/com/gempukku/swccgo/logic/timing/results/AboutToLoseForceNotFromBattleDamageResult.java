package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * The effect result that is emitted when a player is about to lose Force that is not from battle damage.
 */
public class AboutToLoseForceNotFromBattleDamageResult extends EffectResult implements AboutToLoseForceResult {
    private PhysicalCard _source;
    private String _playerToLoseForce;
    private LoseForceEffect _effect;

    /**
     * Creates an effect result that is emitted when a player is about to lose Force that is not from battle damage.
     * @param action the action
     * @param playerToLoseForce the player to lose Force
     * @param effect the effect that can be used to get information about the Force loss
    */
    public AboutToLoseForceNotFromBattleDamageResult(Action action, String playerToLoseForce, LoseForceEffect effect) {
        super(Type.ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE, action.getPerformingPlayer());
        _source = action.getActionSource();
        _playerToLoseForce = playerToLoseForce;
        _effect = effect;
    }

    /**
     * Gets the source card of the Force loss.
     * @return the source card
     */
    public PhysicalCard getSourceCard() {
        return _source;
    }

    /**
     * Gets the player to lose Force.
     * @return the player to lose Force
     */
    public String getPlayerToLoseForce() {
        return _playerToLoseForce;
    }

    /**
     * Determines if the Force loss is from a Force drain.
     * @return true or false
     */
    public boolean isForceDrain() {
        return _effect.isForceDrain();
    }

    /**
     * Determines if the Force loss is from an 'insert' card.
     * @return true or false
     */
    public boolean isFromInsertCard() {
        return _effect.isFromInsertCard();
    }

    /**
     * Determines if the Force loss is from battle damage.
     * @return true or false
     */
    public boolean isBattleDamage() {
        return false;
    }

    /**
     * Gets the amount of Force to lose. This value is calculated at the time this method is called.
     * @param game the game
     * @return the amount of Force to lose
     */
    public float getForceLossAmount(SwccgGame game) {
        return _effect.getForceLossRemaining(game);
    }

    /**
     * Determines if the Force loss may not be reduced.
     * @return true or false
     */
    public boolean isCannotBeReduced(SwccgGame game) {
        return _effect.isCannotBeReduced(game);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "About to lose " + GuiUtils.formatAsString(getForceLossAmount(game)) + " Force";
    }
}
