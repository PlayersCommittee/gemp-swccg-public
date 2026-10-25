package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
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

        // 1) Draw the ferocity destiny (if any), then any subtract-destiny-from-ferocity draws
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        int numDestiny = modifiersQuerying.getNumFerocityDestiny(gameState, _creature);
                        if (numDestiny == 0) {
                            appendSubtractDestinyFromFerocityDraws(game, subAction, null);
                            return;
                        }
                        gameState.sendMessage(performingPlayerId + " targets to draw " + numDestiny + " destiny for " + GameUtils.getCardLink(_creature) + "'s ferocity");
                        gameState.activatedCard(performingPlayerId, _creature);
                        subAction.appendEffect(
                                new DrawDestinyEffect(subAction, performingPlayerId, numDestiny, DestinyType.DESTINY) {
                                    @Override
                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                        if (totalDestiny == null) {
                                            appendSubtractDestinyFromFerocityDraws(game, subAction, null);
                                            return;
                                        }

                                        gameState.sendMessage("Total destiny for ferocity: " + GuiUtils.formatAsString(totalDestiny));
                                        appendSubtractDestinyFromFerocityDraws(game, subAction, totalDestiny);
                                    }
                                }
                        );
                    }
                });

        return subAction;
    }

    /**
     * After ferocity destinies (if any), draw destinies required by SUBTRACT_DESTINY_FROM_FEROCITY modifiers
     * (card owner of each modifier source draws) and subtract those amounts from the ferocity destiny total.
     */
    private void appendSubtractDestinyFromFerocityDraws(final SwccgGame game, final SubAction subAction, final Float ferocityDestinyTotal) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        final List<Modifier> subtractModifiers = new ArrayList<Modifier>(
                modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.SUBTRACT_DESTINY_FROM_FEROCITY, _creature));

        if (subtractModifiers.isEmpty()) {
            totalFerocityDestinyCalculated(ferocityDestinyTotal);
            return;
        }

        final float[] runningTotal = new float[]{ferocityDestinyTotal != null ? ferocityDestinyTotal : 0f};
        appendNextSubtractDestiny(game, subAction, subtractModifiers, 0, runningTotal);
    }

    private void appendNextSubtractDestiny(final SwccgGame game, final SubAction subAction, final List<Modifier> subtractModifiers,
                                           final int index, final float[] runningTotal) {
        if (index >= subtractModifiers.size()) {
            totalFerocityDestinyCalculated(runningTotal[0]);
            return;
        }

        final GameState gameState = game.getGameState();
        final Modifier modifier = subtractModifiers.get(index);
        final PhysicalCard source = modifier.getSource(gameState);
        final String drawingPlayer = source != null ? source.getOwner() : _creature.getOwner();

        gameState.sendMessage(drawingPlayer + " draws destiny to subtract from " + GameUtils.getCardLink(_creature) + "'s ferocity"
                + (source != null ? " due to " + GameUtils.getCardLink(source) : ""));
        subAction.appendEffect(
                new DrawDestinyEffect(subAction, drawingPlayer, 1, DestinyType.DESTINY) {
                    @Override
                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                        float subtractAmount = totalDestiny != null ? totalDestiny : 0f;
                        runningTotal[0] -= subtractAmount;
                        gameState.sendMessage("Destiny subtracted from ferocity: " + GuiUtils.formatAsString(subtractAmount)
                                + " (ferocity destiny total now " + GuiUtils.formatAsString(runningTotal[0]) + ")");
                        appendNextSubtractDestiny(game, subAction, subtractModifiers, index + 1, runningTotal);
                    }
                }
        );
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
