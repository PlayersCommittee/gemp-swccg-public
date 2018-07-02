package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;

/**
 * An effect that causes the player performing the action to exchange the just drawn destiny card with the stacked card.
 */
public class ExchangeDestinyCardWithStackedCardEffect extends AbstractSubActionEffect {
    private PhysicalCard _stackedCard;

    /**
     * Creates an effect that causes the player performing the action to exchange the just drawn destiny card with the stacked card.
     * @param action the action performing this effect
     * @param stackedCard the stacked card
     */
    public ExchangeDestinyCardWithStackedCardEffect(Action action, PhysicalCard stackedCard) {
        super(action);
        _stackedCard = stackedCard;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        final GameState gameState = game.getGameState();
                        final DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
                        final PhysicalCard stackedOn = _stackedCard.getStackedOn();
                        if (drawDestinyState != null && stackedOn != null) {

                            final DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
                            final String playerDrawingDestiny = drawDestinyEffect.getPlayerDrawingDestiny();
                            final PhysicalCard destinyCard = drawDestinyEffect.getDrawnDestinyCard();
                            if (destinyCard != null && destinyCard.getOwner().equals(_action.getPerformingPlayer())
                                    && destinyCard.getZone().isUnresolvedDestinyDraw()) {

                                subAction.appendEffect(
                                        new PassthruEffect(subAction) {
                                            @Override
                                            protected void doPlayEffect(SwccgGame game) {
                                                gameState.removeCardFromZone(_stackedCard);
                                                gameState.removeCardFromZone(destinyCard);
                                                gameState.stackCard(destinyCard, stackedOn, false, false, false);
                                                gameState.addCardToTopOfZone(_stackedCard, Zone.UNRESOLVED_DESTINY_DRAW, playerDrawingDestiny);
                                                drawDestinyState.getDrawDestinyEffect().setDrawnDestinyCard(_stackedCard);

                                                // Ask the player to choose the destiny value (if multiple exist)
                                                if (_stackedCard.getBlueprint().getDestiny() != null && !_stackedCard.getBlueprint().getDestiny().equals(_stackedCard.getBlueprint().getAlternateDestiny())) {
                                                    subAction.appendEffect(
                                                            new RefreshPrintedDestinyValuesEffect(subAction, Collections.singletonList(_stackedCard)) {
                                                                @Override
                                                                protected void refreshedPrintedDestinyValues() {
                                                                }
                                                            });
                                                }
                                                subAction.appendEffect(
                                                        new PassthruEffect(subAction) {
                                                            @Override
                                                            protected void doPlayEffect(SwccgGame game) {
                                                                DestinyType destinyType = drawDestinyState.getDrawDestinyEffect().getDestinyType();
                                                                float drawnDestinyValue;

                                                                if (destinyType == DestinyType.ASTEROID_DESTINY) {
                                                                    drawnDestinyValue = game.getModifiersQuerying().getAsteroidDestiny(game.getGameState(), _stackedCard, playerDrawingDestiny);
                                                                }
                                                                else if (destinyType == DestinyType.BATTLE_DESTINY) {
                                                                    drawnDestinyValue = game.getModifiersQuerying().getBattleDestiny(game.getGameState(), _stackedCard, playerDrawingDestiny);
                                                                }
                                                                else if (destinyType == DestinyType.CARBON_FREEZING_DESTINY) {
                                                                    drawnDestinyValue = game.getModifiersQuerying().getCarbonFreezingDestiny(game.getGameState(), _stackedCard);
                                                                }
                                                                else if (destinyType == DestinyType.DESTINY_TO_ATTRITION) {
                                                                    drawnDestinyValue = game.getModifiersQuerying().getDestinyToAttrition(game.getGameState(), _stackedCard, playerDrawingDestiny);
                                                                }
                                                                else if (destinyType == DestinyType.DESTINY_TO_TOTAL_POWER) {
                                                                    drawnDestinyValue = game.getModifiersQuerying().getDestinyToPower(game.getGameState(), _stackedCard, playerDrawingDestiny);
                                                                }
                                                                else if (destinyType == DestinyType.DUEL_DESTINY) {
                                                                    drawnDestinyValue = game.getModifiersQuerying().getDuelDestiny(game.getGameState(), _stackedCard, playerDrawingDestiny);
                                                                }
                                                                else if (destinyType == DestinyType.EPIC_EVENT_DESTINY) {
                                                                    drawnDestinyValue = game.getModifiersQuerying().getEpicEventDestiny(game.getGameState(), _stackedCard, playerDrawingDestiny);
                                                                }
                                                                else if (destinyType == DestinyType.EPIC_EVENT_AND_WEAPON_DESTINY) {
                                                                    drawnDestinyValue = game.getModifiersQuerying().getEpicEventAndWeaponDestiny(game.getGameState(), _stackedCard, playerDrawingDestiny);
                                                                }
                                                                else if (destinyType == DestinyType.LIGHTSABER_COMBAT_DESTINY) {
                                                                    drawnDestinyValue = game.getModifiersQuerying().getLightsaberCombatDestiny(game.getGameState(), _stackedCard, playerDrawingDestiny);
                                                                }
                                                                else if (destinyType == DestinyType.SEARCH_PARTY_DESTINY) {
                                                                    drawnDestinyValue = game.getModifiersQuerying().getSearchPartyDestiny(game.getGameState(), _stackedCard, playerDrawingDestiny);
                                                                }
                                                                else if (destinyType == DestinyType.TRAINING_DESTINY) {
                                                                    drawnDestinyValue = game.getModifiersQuerying().getTrainingDestiny(game.getGameState(), drawDestinyEffect.getAction().getActionSource(), _stackedCard, playerDrawingDestiny);
                                                                }
                                                                else if (destinyType == DestinyType.WEAPON_DESTINY) {
                                                                    drawnDestinyValue = game.getModifiersQuerying().getWeaponDestiny(game.getGameState(), _stackedCard, playerDrawingDestiny);
                                                                }
                                                                else {
                                                                    drawnDestinyValue = game.getModifiersQuerying().getDestinyForDestinyDraw(game.getGameState(), _stackedCard, drawDestinyEffect.getAction().getActionSource());
                                                                }
                                                                drawDestinyEffect.setDrawnDestinyCardValue(drawnDestinyValue);

                                                                gameState.sendMessage(_action.getPerformingPlayer() + " exchanged just drawn destiny card, " + GameUtils.getCardLink(destinyCard) + ", with " + GameUtils.getCardLink(_stackedCard) + " as a " + GuiUtils.formatAsString(drawnDestinyValue) + " for " + destinyType.getHumanReadable());
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            }
                        }
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
