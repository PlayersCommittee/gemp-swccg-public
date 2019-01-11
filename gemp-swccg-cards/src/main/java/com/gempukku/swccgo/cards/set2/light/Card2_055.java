package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;
import com.gempukku.swccgo.logic.timing.results.RelocatedBetweenLocationsResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Used
 * Title: Quite A Mercenary
 */
public class Card2_055 extends AbstractUsedInterrupt {
    public Card2_055() {
        super(Side.LIGHT, 5, Title.Quite_A_Mercenary);
        setLore("Smugglers and other rogues frequent spaceports along trade routes. 'Your friend is quite a mercenary. I wonder if he really cares about anything, or anybody.'");
        setGameText("If Elis Helrot just completed a transport, use 2 Force. Elis and all character transported are lost. (Immune to Sense.) OR Prevent a just deployed smuggler from moving this turn.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.transportCompletedBy(game, effectResult, Filters.Elis_Helrot)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {
            final PhysicalCard sourceCard = ((RelocatedBetweenLocationsResult) effectResult).getActionSource();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            action.setText("Make " + GameUtils.getFullName(sourceCard) + " and characters lost");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Allow response(s)
            action.allowResponses("Make " + GameUtils.getFullName(sourceCard) + " and all characters transported lost",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PutCardFromVoidInLostPileEffect(action, playerId, sourceCard));
                            Collection<PhysicalCard> charactersRelocated = Filters.filter(((RelocatedBetweenLocationsResult) effectResult).getMovedCards(), game, Filters.character);
                            if (!charactersRelocated.isEmpty()) {
                                action.appendEffect(
                                        new LoseCardsFromTableEffect(action, charactersRelocated));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, Filters.and(Filters.smuggler, Filters.canBeTargetedBy(self)))) {
            PhysicalCard cardDeployed = ((PlayCardResult) effectResult).getPlayedCard();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Prevent " + GameUtils.getFullName(cardDeployed) + " from moving");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose smuggler", cardDeployed) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard smuggler) {
                            action.addAnimationGroup(smuggler);
                            // Allow response(s)
                            action.allowResponses("Prevent " + GameUtils.getCardLink(smuggler) + " from moving",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalSmuggler = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new MayNotMoveUntilEndOfTurnEffect(action, finalSmuggler));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}