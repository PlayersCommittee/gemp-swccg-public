package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Interrupt
 * Subtype: Lost
 * Title: Ascension Guns
 */
public class Card14_040 extends AbstractLostInterrupt {
    public Card14_040() {
        super(Side.LIGHT, 5, Title.Ascension_Guns, Uniqueness.UNIQUE);
        setLore("Modified S-5 security blasters can be used for a wide array of purposes");
        setGameText("During your move phase, target any number of your characters at Theed Palace Hallway. Draw destiny. If destiny +2 > number of characters targeted, relocate those characters to one other interior Naboo site. Otherwise targets are lost.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.canSpotLocation(game, Filters.Theed_Palace_Hallway)
                && GameConditions.hasReserveDeck(game, playerId)) {
            final Collection<PhysicalCard> charactersToRelocate = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.character, Filters.at(Filters.Theed_Palace_Hallway)));
            Collection<PhysicalCard> otherSites = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.interior_Naboo_site, Filters.not(Filters.Theed_Palace_Hallway)));
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