package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.PutCardInVoidEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.FailCostEffect;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Used
 * Title: Odin Nesloor
 */
public class Card12_065 extends AbstractUsedInterrupt {
    public Card12_065() {
        super(Side.LIGHT, 4, "Odin Nesloor", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.U);
        setLore("Odin's family mysteriously disappeared during a hyperspace jump. He scours the galaxy for information on them, and his best lead has brought him to Tatooine.");
        setGameText("During your move phase, target any or all of your characters at one exterior site to 'transport' (relocate) to another exterior site. Draw destiny. Use that much Force to 'transport,' or place Interrupt in Lost Pile.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)) {
            Collection<PhysicalCard> fromSites = Filters.filterTopLocationsOnTable(game,
                    Filters.and(Filters.exterior_site, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.character, Filters.atLocation(Filters.any), Filters.canBeRelocatedToLocation(Filters.exterior_site, true, 0)))));
            if (!fromSites.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("'Transport' characters");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose site to 'transport' from", Filters.in(fromSites)) {
                            @Override
                            protected void cardSelected(final PhysicalCard fromSite) {
                                final Collection<PhysicalCard> charactersToRelocate = Filters.filterActive(game, self,
                                        Filters.and(Filters.your(self), Filters.character, Filters.atLocation(fromSite), Filters.canBeRelocatedToLocation(Filters.exterior_site, true, 0)));
                                Collection<PhysicalCard> otherSites = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.exterior_site, Filters.not(fromSite)));
                                Collection<PhysicalCard> validSites = new LinkedList<PhysicalCard>();
                                // Figure out which sites any of the cards can be relocated to
                                for (PhysicalCard otherSite : otherSites) {
                                    for (PhysicalCard characterToRelocate : charactersToRelocate) {
                                        if (Filters.canBeRelocatedToLocation(otherSite, true, 0).accepts(game, characterToRelocate)) {
                                            validSites.add(otherSite);
                                            break;
                                        }
                                    }
                                }
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose site to 'transport' to", Filters.in(validSites)) {
                                            @Override
                                            protected void cardTargeted(final int siteTargetingGroupId, final PhysicalCard toSite) {
                                                Collection<PhysicalCard> validCharactersToRelocate = new LinkedList<PhysicalCard>();
                                                // Figure out which characters can be relocated to the other site
                                                for (PhysicalCard characterToRelocate : charactersToRelocate) {
                                                    if (Filters.canBeRelocatedToLocation(toSite, true, 0).accepts(game, characterToRelocate)) {
                                                        validCharactersToRelocate.add(characterToRelocate);
                                                    }
                                                }
                                                action.appendTargeting(
                                                        new TargetCardsOnTableEffect(action, playerId, "Choose characters to 'transport'", 1, Integer.MAX_VALUE, Filters.in(validCharactersToRelocate)) {
                                                            @Override
                                                            protected void cardsTargeted(final int targetGroupId, final Collection<PhysicalCard> cardsToRelocate) {
                                                                action.addAnimationGroup(cardsToRelocate);
                                                                action.addAnimationGroup(toSite);
                                                                // Pay cost(s)
                                                                // Draw destiny to determine cost to 'transport'
                                                                action.appendCost(
                                                                        new DrawDestinyEffect(action, playerId) {
                                                                            @Override
                                                                            protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                                final GameState gameState = game.getGameState();
                                                                                ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                                                if (totalDestiny == null) {
                                                                                    gameState.sendMessage("Result: 'Transport' cost not paid due to failed destiny draw");
                                                                                    action.appendCost(
                                                                                            new PutCardInVoidEffect(action, self));
                                                                                    action.appendCost(
                                                                                            new PutCardFromVoidInLostPileEffect(action, playerId, self));
                                                                                    action.appendCost(
                                                                                            new FailCostEffect(action));
                                                                                    return;
                                                                                }

                                                                                final float moveCost = modifiersQuerying.getRelocateBetweenLocationsCost(gameState, cardsToRelocate, fromSite, toSite, totalDestiny);
                                                                                gameState.sendMessage("'Transport' cost: " + GuiUtils.formatAsString(moveCost));

                                                                                if (!GameConditions.canUseForce(game, playerId, moveCost)) {

                                                                                    gameState.sendMessage("Result: Player unable to pay 'transport' cost");
                                                                                    action.appendCost(
                                                                                            new PutCardInVoidEffect(action, self));
                                                                                    action.appendCost(
                                                                                            new PutCardFromVoidInLostPileEffect(action, playerId, self));
                                                                                    action.appendCost(
                                                                                            new FailCostEffect(action));
                                                                                    return;
                                                                                }

                                                                                gameState.sendMessage("Result: Player may choose to 'transport'");
                                                                                // Ask player to use Force for transport or Interrupt is lost
                                                                                action.appendCost(
                                                                                        new PlayoutDecisionEffect(action, playerId,
                                                                                                new YesNoDecision((moveCost > 0 ) ? "Do you want to use " + GuiUtils.formatAsString(moveCost) + " Force to 'transport'?" : "Do you want to 'transport'?") {
                                                                                                    @Override
                                                                                                    protected void yes() {
                                                                                                        gameState.sendMessage(playerId + " chooses to 'transport' using " + GameUtils.getCardLink(self));
                                                                                                        if (moveCost > 0) {
                                                                                                            action.appendCost(
                                                                                                                    new UseForceEffect(action, playerId, moveCost));
                                                                                                        }
                                                                                                        // Allow response(s)
                                                                                                        action.allowResponses("'Transport' " + GameUtils.getAppendedNames(cardsToRelocate) + " to " + GameUtils.getCardLink(toSite),
                                                                                                                new RespondablePlayCardEffect(action) {
                                                                                                                    @Override
                                                                                                                    protected void performActionResults(Action targetingAction) {
                                                                                                                        // Get the targeted card(s) from the action using the targetGroupId.
                                                                                                                        // This needs to be done in case the target(s) were changed during the responses.
                                                                                                                        Collection<PhysicalCard> finalCharacters = action.getPrimaryTargetCards(targetGroupId);
                                                                                                                        PhysicalCard finalToSite = action.getPrimaryTargetCard(siteTargetingGroupId);

                                                                                                                        // Perform result(s)
                                                                                                                        action.appendEffect(
                                                                                                                                new RelocateBetweenLocationsEffect(action, finalCharacters, finalToSite));
                                                                                                                    }
                                                                                                                }
                                                                                                        );
                                                                                                    }
                                                                                                    @Override
                                                                                                    protected void no() {
                                                                                                        gameState.sendMessage(playerId + " chooses to not 'transport' using " + GameUtils.getCardLink(self));
                                                                                                        action.appendCost(
                                                                                                                new PutCardInVoidEffect(action, self));
                                                                                                        action.appendCost(
                                                                                                                new PutCardFromVoidInLostPileEffect(action, playerId, self));
                                                                                                        action.appendCost(
                                                                                                                new FailCostEffect(action));
                                                                                                    }
                                                                                                }
                                                                                        )
                                                                                );
                                                                            }
                                                                        });
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}