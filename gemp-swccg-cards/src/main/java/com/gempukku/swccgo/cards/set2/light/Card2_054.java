package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceRandomCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Used
 * Title: Out Of Commission
 */
public class Card2_054 extends AbstractUsedInterrupt {
    public Card2_054() {
        super(Side.LIGHT, 5, Title.Out_Of_Commission);
        setLore("'I hope that old man got the tractor beam out of commission or this is gonna be a real short trip.'");
        setGameText("During your control phase, use 2 Force to release a starship held by any Tractor Beam. (Not effective on Death Star Tractor Beam if Central Core in play.) OR Randomly select one card from opponent's Lost Pile or Blaster Rack and place out of play.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // TODO: Add releasing of starship from Tractor Beam later.

        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.hasLostPile(game, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, GameTextActionId.OUT_OF_COMMISSION__PLACE_CARD_OUT_OF_PLAY);
            action.setText("Place card from Lost Pile out of play");
            // Allow response(s)
            action.allowResponses("Place a random card from opponent's Lost Pile out of play",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlaceRandomCardOutOfPlayFromLostPileEffect(action, opponent));
                        }
                    }
            );
            actions.add(action);
        }

        Filter blasterRackFilter = Filters.and(Filters.opponents(self), Filters.Blaster_Rack, Filters.hasStacked(Filters.any));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, blasterRackFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, GameTextActionId.OUT_OF_COMMISSION__PLACE_CARD_OUT_OF_PLAY);
            action.setText("Place card from Blaster Rack out of play");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Blaster Rack", blasterRackFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard blasterRack) {
                            action.addAnimationGroup(blasterRack);
                            // Allow response(s)
                            action.allowResponses("Place a random card from " + GameUtils.getCardLink(blasterRack) + " out of play",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalBlasterRack = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            Collection<PhysicalCard> randomCards = GameUtils.getRandomCards(game.getGameState().getStackedCards(finalBlasterRack), 1);
                                            if (!randomCards.isEmpty()) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new PlaceCardOutOfPlayFromOffTableEffect(action, randomCards.iterator().next()));
                                            }
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}