package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelDuelEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Lost
 * Title: Jedi Escape
 */
public class Card11_036 extends AbstractLostInterrupt {
    public Card11_036() {
        super(Side.LIGHT, 5, "Jedi Escape", Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.C);
        setLore("Qui-Gon escaped with his friends from Tatooine, barely getting away from the clutches of Darth Maul.");
        setGameText("If opponent just initiated a duel against one of your non-captive Jedi at a site, use 1 Force to cancel the duel and you may relocate any number of your characters there to an adjacent site.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.duelInitiatedAgainst(game, effectResult, opponent, Filters.and(Filters.your(self), Filters.non_captive, Filters.Jedi, Filters.at(Filters.site)))
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final PhysicalCard duelLocation = game.getGameState().getDuelState().getLocation();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel duel");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelDuelEffect(action));
                            action.appendEffect(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(final SwccgGame game) {
                                            final GameState gameState = game.getGameState();
                                            final Collection<PhysicalCard> charactersToRelocate = Filters.filterActive(game, self,
                                                    Filters.and(Filters.your(self), Filters.character, Filters.at(duelLocation), Filters.canBeRelocatedToLocation(Filters.adjacentSite(duelLocation), true, 0)));
                                            Collection<PhysicalCard> otherSites = Filters.filterTopLocationsOnTable(game, Filters.adjacentSite(duelLocation));
                                            final Collection<PhysicalCard> validSites = new LinkedList<PhysicalCard>();
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
                                                action.appendEffect(
                                                        new PlayoutDecisionEffect(action, playerId,
                                                                new YesNoDecision("Do you want to relocate characters to an adjacent site?") {
                                                                    @Override
                                                                    protected void yes() {
                                                                        final SubAction subAction = new SubAction(action);
                                                                        subAction.appendTargeting(
                                                                                new ChooseCardOnTableEffect(subAction, playerId, "Choose site to relocate to", Filters.in(validSites)) {
                                                                                    @Override
                                                                                    protected void cardSelected(final PhysicalCard toSite) {
                                                                                        Collection<PhysicalCard> validCharactersToRelocate = new LinkedList<PhysicalCard>();
                                                                                        // Figure out which characters can be relocated to the other site
                                                                                        for (PhysicalCard characterToRelocate : charactersToRelocate) {
                                                                                            if (Filters.canBeRelocatedToLocation(toSite, true, 0).accepts(game, characterToRelocate)) {
                                                                                                validCharactersToRelocate.add(characterToRelocate);
                                                                                            }
                                                                                        }
                                                                                        subAction.appendTargeting(
                                                                                                new TargetCardsOnTableEffect(subAction, playerId, "Choose characters to relocate", 1, Integer.MAX_VALUE, Filters.in(validCharactersToRelocate)) {
                                                                                                    @Override
                                                                                                    protected void cardsTargeted(final int targetGroupId, final Collection<PhysicalCard> cardsToRelocate) {
                                                                                                        gameState.sendMessage(playerId + " chooses to relocate " + GameUtils.getAppendedNames(cardsToRelocate) + " to " + GameUtils.getCardLink(toSite));
                                                                                                        subAction.addAnimationGroup(cardsToRelocate);
                                                                                                        subAction.addAnimationGroup(toSite);
                                                                                                        subAction.allowResponses(
                                                                                                                new UnrespondableEffect(subAction) {
                                                                                                                    @Override
                                                                                                                    protected void performActionResults(Action targetingAction) {
                                                                                                                        // Perform result(s)
                                                                                                                        subAction.appendEffect(
                                                                                                                                new RelocateBetweenLocationsEffect(subAction, cardsToRelocate, toSite));
                                                                                                                    }
                                                                                                                }
                                                                                                        );
                                                                                                    }
                                                                                                }
                                                                                        );
                                                                                    }
                                                                                }
                                                                        );
                                                                        action.appendEffect(
                                                                                new StackActionEffect(action, subAction));
                                                                    }
                                                                    @Override
                                                                    protected void no() {
                                                                        gameState.sendMessage(playerId + " chooses to not relocate characters to an adjacent site");
                                                                    }
                                                                }
                                                        )
                                                );
                                            }
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