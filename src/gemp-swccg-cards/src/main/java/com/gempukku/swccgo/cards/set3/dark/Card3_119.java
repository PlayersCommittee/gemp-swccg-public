package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Used
 * Title: ComScan Detection
 */
public class Card3_119 extends AbstractUsedInterrupt {
    public Card3_119() {
        super(Side.DARK, 4, "ComScan Detection", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.C2);
        setLore("The Imperial Navy boasts the best communications network in the galaxy. Sophisticated control technology allows the Empire to dispatch armed forces without delay.");
        setGameText("If opponent just moved a character, vehicle, or starship as a 'react' to a location, you may immediately move one of your vehicles or starships, if within range, to that location (as a regular move).");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        Filter opponentCard = Filters.and(Filters.opponents(self), Filters.or(Filters.character, Filters.vehicle, Filters.starship));

        // Check condition(s)
        if (TriggerConditions.movedToLocation(game, effectResult, opponentCard, Filters.location)) {
            final MovedResult movedResult = (MovedResult) effectResult;
            final PhysicalCard cardMoved = movedResult.getMovedCards().iterator().next();

            if (movedResult.isReact() && GameConditions.canSpot(game, self, cardMoved)) {
                final PhysicalCard toLocation = movedResult.getMovedTo();

                final Filter yourVehicleOrStarship = Filters.and(Filters.your(self), Filters.or(Filters.vehicle, Filters.starship), Filters.movableAsRegularMove(playerId, false, 0, false, Filters.and(toLocation)), Filters.canBeTargetedBy(self));
                final Collection<PhysicalCard> yourActiveCards = Filters.filterActive(game, self, yourVehicleOrStarship);
                if (!yourActiveCards.isEmpty()) {
                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Move your vehicle or starship");
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose vehicle or starship", yourVehicleOrStarship) {
                                @Override
                                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                    action.addAnimationGroup(targetedCard);
                                    // Allow response(s)
                                    action.allowResponses("Move " + GameUtils.getCardLink(targetedCard) + " as a regular move to " + GameUtils.getFullName(cardMoved) + "'s location.",
                                            new RespondablePlayCardEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    // Get the targeted card(s) from the action using the targetGroupId.
                                                    // This needs to be done in case the target(s) were changed during the responses.
                                                    final PhysicalCard finalCard = action.getPrimaryTargetCard(targetGroupId);

                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new MoveCardAsRegularMoveEffect(action, playerId, finalCard, false, false, toLocation));
                                                }
                                            }
                                    );
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}