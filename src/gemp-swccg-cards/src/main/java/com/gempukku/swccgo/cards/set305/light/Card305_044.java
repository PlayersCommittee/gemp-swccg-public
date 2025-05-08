package com.gempukku.swccgo.cards.set305.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A Better Tomorrow
 * Type: Interrupt
 * Subtype: Lost
 * Title: Hidden Corridors
 */
public class Card305_044 extends AbstractLostInterrupt {
    public Card305_044() {
        super(Side.LIGHT, 5, Title.Hidden_Corridors, Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.U);
        setLore("Designed after the Clone Wars, hidden corridors weave throughout the Quermian Senate providing safe passage to the Senators.");
        setGameText("During your move phase, target any number of your characters at Quermia Senate Hallway. Draw destiny. If destiny +2 > number of characters targeted, relocate those characters to one other interior Quermia site. Otherwise targets are lost.");
        addIcons(Icon.COU);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.canSpotLocation(game, Filters.Quermia_Senate_Hallway)
                && GameConditions.hasReserveDeck(game, playerId)) {
            final Collection<PhysicalCard> charactersToRelocate = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.character, Filters.at(Filters.Quermia_Senate_Hallway)));
            Collection<PhysicalCard> otherSites = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.interior_Quermia_site, Filters.not(Filters.Quermia_Senate_Hallway)));
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
            if (!validSites.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Relocate characters");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate to", Filters.in(validSites)) {
                            @Override
                            protected void cardSelected(final PhysicalCard toSite) {
                                Collection<PhysicalCard> validCharactersToRelocate = new LinkedList<PhysicalCard>();
                                // Figure out which characters can be relocated to the other site
                                for (PhysicalCard characterToRelocate : charactersToRelocate) {
                                    if (Filters.canBeRelocatedToLocation(toSite, true, 0).accepts(game, characterToRelocate)) {
                                        validCharactersToRelocate.add(characterToRelocate);
                                    }
                                }
                                action.appendTargeting(
                                        new TargetCardsOnTableEffect(action, playerId, "Choose characters to relocate", 1, Integer.MAX_VALUE, Filters.in(validCharactersToRelocate)) {
                                            @Override
                                            protected void cardsTargeted(final int targetGroupId, final Collection<PhysicalCard> cardsToRelocate) {
                                                action.addAnimationGroup(cardsToRelocate);
                                                action.addAnimationGroup(toSite);
                                                // Allow response(s)
                                                action.allowResponses("Relocate " + GameUtils.getAppendedNames(cardsToRelocate) + " to " + GameUtils.getCardLink(toSite),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                // This needs to be done in case the target(s) were changed during the responses.
                                                                final Collection<PhysicalCard> finalCharacters = action.getPrimaryTargetCards(targetGroupId);

                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new DrawDestinyEffect(action, playerId) {
                                                                            @Override
                                                                            protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                                final GameState gameState = game.getGameState();
                                                                                if (totalDestiny == null) {
                                                                                    gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                                    action.appendCost(
                                                                                            new LoseCardsFromTableEffect(action, finalCharacters));
                                                                                    return;
                                                                                }

                                                                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                                int numberOfCharacters = finalCharacters.size();
                                                                                gameState.sendMessage("Number of characters: " + numberOfCharacters);

                                                                                if ((totalDestiny + 2) > numberOfCharacters) {
                                                                                    gameState.sendMessage("Result: Success");
                                                                                    action.appendEffect(
                                                                                            new RelocateBetweenLocationsEffect(action, finalCharacters, toSite));
                                                                                }
                                                                                else {
                                                                                    gameState.sendMessage("Result: Failed");
                                                                                    action.appendEffect(
                                                                                            new LoseCardsFromTableEffect(action, finalCharacters));
                                                                                }
                                                                            }
                                                                        }
                                                                );
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}