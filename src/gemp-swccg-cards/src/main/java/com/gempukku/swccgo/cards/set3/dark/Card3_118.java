package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
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
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CollapseSiteEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.MovedResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Lost
 * Title: Collapsing Corridor
 */
public class Card3_118 extends AbstractLostInterrupt {
    public Card3_118() {
        super(Side.DARK, 2, "Collapsing Corridor", Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.R2);
        setLore("Most of Echo Base was tunneled out of a huge glacier by Rebel engineers. Its icy passageways could not withstand Imperial shelling.");
        setGameText("If you just moved an AT-AT to innermost marker, target one underground site on Hoth. Draw destiny. Target 'collapsed' if destiny > marker number.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        Filter undergroundSiteFilter = Filters.and(Filters.underground_site, Filters.Hoth_site);

        // Check condition(s)
        if (TriggerConditions.movedToLocationBy(game, effectResult, playerId, Filters.AT_AT, Filters.innermostMarker)
                && GameConditions.canTarget(game, self, undergroundSiteFilter)) {
            final Integer markerNumber = game.getModifiersQuerying().getMarkerNumber(game.getGameState(), ((MovedResult) effectResult).getMovedTo());
            if (markerNumber != null) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("'Collapse' underground site");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose underground site", undergroundSiteFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, PhysicalCard undergroundSite) {
                                action.addAnimationGroup(undergroundSite);
                                // Allow response(s)
                                action.allowResponses("'Collapse' " + GameUtils.getCardLink(undergroundSite),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                final PhysicalCard finalSite = action.getPrimaryTargetCard(targetGroupId1);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new DrawDestinyEffect(action, playerId) {
                                                            @Override
                                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                GameState gameState = game.getGameState();
                                                                if (totalDestiny == null) {
                                                                    gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                    return;
                                                                }

                                                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                gameState.sendMessage("Marker number: " + markerNumber);
                                                                if (totalDestiny > markerNumber) {
                                                                    gameState.sendMessage("Result: Succeeded");
                                                                    action.appendEffect(
                                                                            new CollapseSiteEffect(action, finalSite));
                                                                } else {
                                                                    gameState.sendMessage("Result: Failed");
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
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}