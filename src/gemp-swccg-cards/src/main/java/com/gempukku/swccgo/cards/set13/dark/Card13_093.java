package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.MayNotMoveUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.BattleInitiatedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Interrupt
 * Subtype: Lost
 * Title: Through The Corridor
 */
public class Card13_093 extends AbstractLostInterrupt {
    public Card13_093() {
        super(Side.DARK, 3, "Through The Corridor", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("Obi-Wan was anxious, Qui-Gon was patient, and Maul was angry.");
        setGameText("If opponent just initiated battle against Maul where opponent has two Jedi, cancel that battle and you may relocate one of those Jedi (your choice) to any adjacent site. That Jedi may not move for remainder of turn. (Immune to Sense.)");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final Filter jediFilter = Filters.and(Filters.opponents(self), Filters.Jedi, Filters.participatingInBattle);

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Maul)
                && GameConditions.canSpot(game, self, 2, jediFilter)) {
            PhysicalCard battleLocation = ((BattleInitiatedResult) effectResult).getLocation();
            if (GameConditions.canSpotLocation(game, Filters.adjacentSite(battleLocation))) {
                Collection<PhysicalCard> jedis = Filters.filter(game.getGameState().getBattleState().getAllCardsParticipating(), game, Filters.and(jediFilter, Filters.canBeTargetedBy(self)));
                if (!jedis.isEmpty()) {
                    // Figure out which destroyer droids can be relocated to an adjacent site
                    final List<PhysicalCard> validJedi = new LinkedList<PhysicalCard>();
                    for (PhysicalCard jedi : jedis) {
                        if (Filters.canBeRelocatedToLocation(Filters.adjacentSite(jedi), true, 0).accepts(game, jedi)) {
                            validJedi.add(jedi);
                        }
                    }
                    if (!validJedi.isEmpty()) {

                        final PlayInterruptAction action = new PlayInterruptAction(game, self);
                        action.setText("Cancel battle");
                        action.setImmuneTo(Title.Sense);
                        // Choose target(s)
                        action.appendTargeting(
                                new PlayoutDecisionEffect(action, playerId,
                                        new YesNoDecision("Do you want to relocate a Jedi to an adjacent site?") {
                                            @Override
                                            protected void yes() {
                                                action.appendTargeting(
                                                        new TargetCardOnTableEffect(action, playerId, "Choose Jedi", Filters.in(validJedi)) {
                                                            @Override
                                                            protected void cardTargeted(final int targetGroupId, final PhysicalCard jediToRelocate) {
                                                                Collection<PhysicalCard> otherSites = Filters.filterTopLocationsOnTable(game,
                                                                        Filters.and(Filters.adjacentSite(jediToRelocate), Filters.locationCanBeRelocatedTo(jediToRelocate, false, false, true, 0, false)));
                                                                action.appendTargeting(
                                                                        new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(jediToRelocate) + " to", Filters.in(otherSites)) {
                                                                            @Override
                                                                            protected void cardSelected(final PhysicalCard siteSelected) {
                                                                                action.addAnimationGroup(jediToRelocate);
                                                                                action.addAnimationGroup(siteSelected);
                                                                                // Allow response(s)
                                                                                action.allowResponses("Cancel battle and relocate " + GameUtils.getCardLink(jediToRelocate) + " to " + GameUtils.getCardLink(siteSelected),
                                                                                        new RespondablePlayCardEffect(action) {
                                                                                            @Override
                                                                                            protected void performActionResults(Action targetingAction) {
                                                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                                                // This needs to be done in case the target(s) were changed during the responses.
                                                                                                PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);

                                                                                                // Perform result(s)
                                                                                                action.appendEffect(
                                                                                                        new CancelBattleEffect(action));
                                                                                                action.appendEffect(
                                                                                                        new RelocateBetweenLocationsEffect(action, finalCharacter, siteSelected));
                                                                                                action.appendEffect(
                                                                                                        new MayNotMoveUntilEndOfTurnEffect(action, finalCharacter));
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

                                            @Override
                                            protected void no() {
                                                // Allow response(s)
                                                action.allowResponses(
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new CancelBattleEffect(action));
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                )
                        );
                        return Collections.singletonList(action);
                    }
                }
            }

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel battle");
            action.setImmuneTo(Title.Sense);
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelBattleEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}