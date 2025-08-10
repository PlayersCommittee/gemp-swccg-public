package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.List;

/**
 * An effect that performs drawing ferocity destiny for a specified creature.
 */
public abstract class DrawFerocityDestinyEffect extends AbstractSubActionEffect {
    private PhysicalCard _creature;

    /**
     * Creates an effect that performs drawing ferocity destiny for a specified creature.
     * @param action the action performing this effect
     * @param creature the creature
     */
    public DrawFerocityDestinyEffect(Action action, PhysicalCard creature) {
        super(action);
        _creature = creature;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        final String performingPlayerId = _creature.getOwner();

        final SubAction subAction = new SubAction(_action, performingPlayerId);

        // 1) Draw the ferocity destiny (if any)
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        int numDestiny = modifiersQuerying.getNumFerocityDestiny(gameState, _creature);
                        if (numDestiny == 0) {
                            totalFerocityDestinyCalculated(null);
                            return;
                        }
                        gameState.sendMessage(performingPlayerId + " targets to draw " + numDestiny + " destiny for " + GameUtils.getCardLink(_creature) + "'s ferocity");
                        gameState.activatedCard(performingPlayerId, _creature);
                        subAction.appendEffect(
                                new DrawDestinyEffect(subAction, performingPlayerId, numDestiny, DestinyType.DESTINY) {
                                    @Override
                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                        if (totalDestiny == null) {
                                            totalFerocityDestinyCalculated(null);
                                            return;
                                        }

                                        gameState.sendMessage("Total destiny for ferocity: " + GuiUtils.formatAsString(totalDestiny));
                                        totalFerocityDestinyCalculated(totalDestiny);
                                    }
                                }
                        );
                    }
                });

        return subAction;
    }

    /**
     * This method is called when the total ferocity destiny is calculated.
     * @param totalFerocityDestiny the total ferocity destiny, or null
     */
    protected abstract void totalFerocityDestinyCalculated(Float totalFerocityDestiny);

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
