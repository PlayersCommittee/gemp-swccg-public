package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Cantina Brawl
 */
public class Card1_073 extends AbstractLostInterrupt {
    public Card1_073() {
        super(Side.LIGHT, 5, Title.Cantina_Brawl);
        setLore("'...watch your step. This place can be a little rough.' The Mos Eisley Cantina harbors smugglers, thieves, cutthroats, criminals and bounty hunters!");
        setGameText("Use 2 Force to cause a fight to break out in the Cantina. Both players draw destiny. All characters in Cantina with an ability number matching either destiny draw are lost.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final Filter cantina = Filters.and(Filters.Cantina, Filters.sameLocationAs(self, SpotOverride.INCLUDE_ALL, Filters.character));

        // Check condition(s)
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)
                && GameConditions.canTarget(game, self, cantina)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setActionMsg("Cause fight to break out in the Cantina");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Cantina", cantina) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                        @Override
                        protected void cardTargeted(final int targetGroupId1, PhysicalCard targetedCantina) {
                            action.addAnimationGroup(targetedCantina);
                            // Set secondary target filter(s)
                            action.addSecondaryTargetFilter(Filters.Cantina);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 2));
                            // Allow response(s)
                            action.allowResponses("Cause fight to break out in " + GameUtils.getCardLink(targetedCantina),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            final PhysicalCard finalCantina = targetingAction.getPrimaryTargetCard(targetGroupId1);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, final List<Float> destinyDrawValues1, final Float totalDestiny1) {
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, opponent) {
                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues2, Float totalDestiny2) {
                                                                            GameState gameState = game.getGameState();
                                                                            if (totalDestiny1 == null && totalDestiny2 == null) {
                                                                                gameState.sendMessage("Result: Failed due to failed destiny draws");
                                                                                return;
                                                                            }

                                                                            Filter abilityFilter1 = (totalDestiny1 != null) ? Filters.abilityEqualTo(totalDestiny1) : Filters.none;
                                                                            Filter abilityFilter2 = (totalDestiny2 != null) ? Filters.abilityEqualTo(totalDestiny2) : Filters.none;
                                                                            Collection<PhysicalCard> charactersToMakeLost = Filters.filterAllOnTable(game, Filters.and(Filters.character, Filters.at(finalCantina), Filters.canBeTargetedBy(self), Filters.or(abilityFilter1, abilityFilter2)));
                                                                            if (!charactersToMakeLost.isEmpty()) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new LoseCardsFromTableEffect(action, charactersToMakeLost, true));
                                                                            } else {
                                                                                gameState.sendMessage("Result: No characters at site with matching ability number that can be lost");
                                                                            }
                                                                        }
                                                                    });
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
        return null;
    }
}