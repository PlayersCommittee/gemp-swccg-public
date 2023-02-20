package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.effects.ConvertCharacterEffect;
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
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Lost
 * Title: Double Agent
 */
public class Card2_048 extends AbstractLostInterrupt {
    public Card2_048() {
        super(Side.LIGHT, 6, Title.Double_Agent, Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.R2);
        setLore("Typical of impersonators, Shada double-crossed everyone. Her true affiliations still remain a mystery.");
        setGameText("If both players have a spy at same site, draw destiny. Add 2 if opponent's spy is Undercover. Opponent's spy is lost if total destiny > 2. OR Opponent's Tonnika Sisters present at a site cross to your side.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final Filter yourSpy = Filters.and(Filters.your(self), Filters.spy, Filters.at(Filters.site), Filters.canBeTargetedBy(self), Filters.with(self, SpotOverride.INCLUDE_UNDERCOVER, Filters.and(Filters.opponents(playerId), Filters.spy, Filters.canBeTargetedBy(self))));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, SpotOverride.INCLUDE_UNDERCOVER, yourSpy)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose your spy", SpotOverride.INCLUDE_UNDERCOVER, yourSpy) {
                        @Override
                        protected void cardSelected(PhysicalCard yourSpy) {
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
            actions.add(action);
        }

        Filter filter2 = Filters.and(Filters.opponents(self), Filters.Tonnika_Sisters, Filters.presentAt(Filters.site));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter2)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Convert Tonnika Sisters");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Tonnika Sisters", filter2) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Convert " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ConvertCharacterEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}