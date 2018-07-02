package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An effect that performs drawing asteroid destiny against a specified starship and carrying out the results.
 */
public class DrawAsteroidDestinyEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private PhysicalCard _starship;

    /**
     * Creates an effect that performs drawing asteroid destiny against a specified starship and carrying out the results.
     * @param action the action performing this effect
     * @param performingPlayerId the player to draw asteroid destiny
     * @param starship the starship
     */
    public DrawAsteroidDestinyEffect(Action action, String performingPlayerId, PhysicalCard starship) {
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
        final PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsPresentAt(gameState, _starship);

        final SubAction subAction = new SubAction(_action, _performingPlayerId);

        // 1) Record that drawing asteroid destiny is initiated (and cards involved)
        subAction.appendEffect(
                new RecordDrawAsteroidDestinyEffect(subAction, _starship, location));

        // 2) Draw the asteroid destiny and carry out the results
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {

                        subAction.insertEffect(
                                new DrawDestinyEffect(subAction, _performingPlayerId, 1, DestinyType.ASTEROID_DESTINY) {
                                    @Override
                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                        return Collections.singletonList(_starship);
                                    }
                                    @Override
                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                        if (totalDestiny == null) {
                                            gameState.sendMessage("Result: Failed due to failed destiny draw");
                                            return;
                                        }

                                        // Check if starship is still present at the location (as it may have already
                                        // been lost due to asteroid sector drawn for asteroid destiny)
                                        if (!Filters.present(location).accepts(game, _starship)) {
                                            return;
                                        }

                                        gameState.sendMessage("Total asteroid destiny: " + GuiUtils.formatAsString(totalDestiny));
                                        float armor = game.getModifiersQuerying().getArmor(gameState, _starship);
                                        float maneuver = game.getModifiersQuerying().getManeuver(gameState, _starship);
                                        if (armor > 0 || maneuver == 0) {
                                            gameState.sendMessage("Armor: " + GuiUtils.formatAsString(armor));
                                        }
                                        if (maneuver > 0 || armor == 0) {
                                            gameState.sendMessage("Maneuver: " + GuiUtils.formatAsString(maneuver));
                                        }

                                        if (totalDestiny > armor && totalDestiny > maneuver) {
                                            gameState.sendMessage("Result: Succeeded");
                                            subAction.insertEffect(
                                                    new LoseCardFromTableEffect(subAction, _starship));
                                        }
                                        else {
                                            gameState.sendMessage("Result: Failed");
                                        }
                                    }
                                }
                        );
                    }
                });

        // 3) End of asteroid destiny draw
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        gameState.endAsteroidDestinyDraw();
                    }
                });

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
