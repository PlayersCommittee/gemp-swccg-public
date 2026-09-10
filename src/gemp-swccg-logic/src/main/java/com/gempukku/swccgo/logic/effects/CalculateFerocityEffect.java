package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * Shared "get ferocity" path: draws ferocity destinies (if any), applies Disarming-style
 * subtract-destiny-from-ferocity draws, then returns the creature's calculated ferocity
 * including modifiers. Used by Attack (via {@link DrawFerocityDestinyEffect}) and by cards
 * such as Yaggle Gakkle that need the final ferocity value without inventing card-local draws.
 */
public abstract class CalculateFerocityEffect extends AbstractSubActionEffect {
    private final PhysicalCard _creature;

    /**
     * Creates an effect that calculates ferocity for a creature (destinies + modifiers).
     * @param action the action performing this effect
     * @param creature the creature
     */
    public CalculateFerocityEffect(Action action, PhysicalCard creature) {
        super(action);
        _creature = creature;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action);

        subAction.appendEffect(
                new DrawFerocityDestinyEffect(subAction, _creature) {
                    @Override
                    protected void totalFerocityDestinyCalculated(Float totalFerocityDestiny) {
                        GameState gameState = game.getGameState();
                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                        if (totalFerocityDestiny == null
                                && modifiersQuerying.getNumFerocityDestiny(gameState, _creature) > 0) {
                            ferocityCalculationFailed();
                            return;
                        }

                        float ferocity = modifiersQuerying.getFerocity(gameState, _creature, totalFerocityDestiny);
                        gameState.sendMessage(GameUtils.getCardLink(_creature) + "'s ferocity: " + GuiUtils.formatAsString(ferocity));
                        ferocityCalculated(ferocity, totalFerocityDestiny);
                    }
                }
        );

        return subAction;
    }

    /**
     * Called when ferocity was successfully calculated (destinies drawn if required, modifiers applied).
     * @param ferocity the final ferocity value (never negative; clamped by modifiers querying)
     * @param ferocityDestinyTotal the net ferocity destiny total after any subtract-destiny draws, or null if none were drawn
     */
    protected abstract void ferocityCalculated(float ferocity, Float ferocityDestinyTotal);

    /**
     * Called when ferocity calculation failed because a required ferocity destiny draw failed.
     * Default: no-op. Override to report failure to the player.
     */
    protected void ferocityCalculationFailed() {
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
