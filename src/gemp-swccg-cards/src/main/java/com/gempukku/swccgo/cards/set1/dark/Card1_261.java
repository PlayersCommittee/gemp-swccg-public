package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Nevar Yalnal
 */
public class Card1_261 extends AbstractLostInterrupt {
    public Card1_261() {
        super(Side.DARK, 6, Title.Nevar_Yalnal, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Immense Ranat scavenger from Aralia. Slyly spies for anyone willing to pay his price. Outcast. Works as a laborer for Hrchek, the Saurin droid trader.");
        setGameText("If both players have a spy at same site, draw destiny. Add 2 if opponent's spy is Undercover. Opponent's spy is lost if total destiny > 2.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {

        final Filter yourSpy = Filters.and(Filters.your(self), Filters.spy, Filters.at(Filters.site), Filters.canBeTargetedBy(self), Filters.with(self, SpotOverride.INCLUDE_UNDERCOVER, Filters.and(Filters.opponents(playerId), Filters.spy, Filters.canBeTargetedBy(self))));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, SpotOverride.INCLUDE_UNDERCOVER, yourSpy)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose your spy", SpotOverride.INCLUDE_UNDERCOVER, yourSpy) {
                        @Override
                        protected void cardSelected(final PhysicalCard yourSpy) {
                            action.addAnimationGroup(yourSpy);
                            Filter opponentSpy = Filters.and(Filters.opponents(playerId), Filters.spy, Filters.atSameSite(yourSpy), Filters.canBeTargetedBy(self));
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose opponent's spy", SpotOverride.INCLUDE_UNDERCOVER, TargetingReason.TO_BE_LOST, opponentSpy) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId, final PhysicalCard opponentsSpy) {
                                            action.addAnimationGroup(opponentsSpy);
                                            // Allow response(s)
                                            action.allowResponses("Make " + GameUtils.getCardLink(opponentsSpy) + " lost",
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final PhysicalCard finalOpponentsSpy = action.getPrimaryTargetCard(targetGroupId);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId) {
                                                                        @Override
                                                                        protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                                                                            if (Filters.undercover_spy.accepts(game, finalOpponentsSpy)) {
                                                                                Modifier modifier = new TotalDestinyModifier(self, drawDestinyState.getId(), 2);
                                                                                return Collections.singletonList(modifier);
                                                                            }
                                                                            return null;
                                                                        }

                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                            GameState gameState = game.getGameState();
                                                                            if (totalDestiny == null) {
                                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                                return;
                                                                            }

                                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                            if (totalDestiny > 2) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new LoseCardFromTableEffect(action, finalOpponentsSpy));
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

                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}