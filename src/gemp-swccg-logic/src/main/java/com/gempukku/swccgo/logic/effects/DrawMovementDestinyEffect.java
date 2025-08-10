package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An effect that performs drawing movement destiny against a specified starship and carrying out the results.
 */
public abstract class DrawMovementDestinyEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private PhysicalCard _starship;

    /**
     * Creates an effect that performs drawing movement destiny against a specified starship and carrying out the results.
     * @param action the action performing this effect
     * @param performingPlayerId the player to draw movement destiny
     * @param starship the starship
     */
    public DrawMovementDestinyEffect(Action action, String performingPlayerId, PhysicalCard starship) {
        super(action);
        _performingPlayerId = performingPlayerId;
        _starship = starship;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        final PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsPresentAt(gameState, _starship);
        final DrawMovementDestinyEffect that = this;

        final SubAction subAction = new SubAction(_action, _performingPlayerId);

        // 1) Record that drawing movement destiny is initiated
        subAction.appendEffect(
                new RecordDrawMovementDestinyEffect(subAction, _starship, location));

        // 2) Draw the movement destiny and carry out the results
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {

                        subAction.insertEffect(
                                new DrawDestinyEffect(subAction, _performingPlayerId, 1, DestinyType.MOVEMENT_DESTINY) {
                                    @Override
                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                        return Collections.singletonList(_starship);
                                    }
                                    @Override
                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                        that.destinyDraws(game, destinyCardDraws, destinyDrawValues, totalDestiny);
                                    }
                                }
                        );
                    }
                });

        // 3) End of movement destiny draw
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        gameState.endMovementDestinyDraw();
                    }
                });

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * This method called when the drawing destiny is complete to inform the action performing this draw destiny what the
     * drawn destiny and totals were.
     * @param game the game
     * @param destinyCardDraws the cards drawn (and chosen, if applicable) and not canceled for destiny. Substituted destinies
     *                         are represented as null in this list. The list will be empty if if all destiny draws failed
     *                         or were canceled.
     * @param destinyDrawValues the destiny values. These represent the value for the destiny card draw in the same position in
     *                          destinyCardDraws. The list will be empty if if all destiny draws failed or were canceled.
     * @param totalDestiny the total destiny value, or null if all destiny draws failed or were canceled
     */
    protected abstract void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny);
}
