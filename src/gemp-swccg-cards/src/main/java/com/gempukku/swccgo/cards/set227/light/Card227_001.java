package com.gempukku.swccgo.cards.set227.light;

import com.gempukku.swccgo.cards.AbstractStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * Set: Set 27
 * Type: Interrupt
 * Subtype: Lost Or Starting
 * Title: Podrace Prep
 */
public class Card227_001 extends AbstractStartingInterrupt {
    public Card227_001() {
        super(Side.LIGHT, 3, "Podrace Prep", Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        setVirtualSuffix(true);
        setLore("Advanced preparation in Podracing is usually the key to winning. A little extra work at the start can mean a lot in the long run.");
        setGameText("If Credits Will Do Fine on table, deploy Podrace Arena, a Podracer, Boonta Eve Podrace, and three Effects that deploy for free and are always [Immune to Alter]. Opponent may deploy a Podracer (or play a Defensive Shield from under their Starting Effect). Place Interrupt in Lost Pile.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.VIRTUAL_SET_27);
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.Credits_Will_Do_Fine)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
            action.setText("Deploy cards from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy Podrace Arena, a Podracer, Boonta Eve Podrace, and three Effects that deploys for free and are always immune to Alter. Opponent may deploy a Podracer (or play a Defensive Shield from under their Starting Effect)",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            if (GameConditions.hasInReserveDeck(game, playerId, Filters.Podrace_Arena)
                                    && GameConditions.hasInReserveDeck(game, playerId, Filters.Podracer)
                                    && GameConditions.hasInReserveDeck(game, playerId, Filters.Boonta_Eve_Podrace)
                                    && GameConditions.hasInReserveDeck(game, playerId, Filters.and(Filters.Effect, Filters.deploysForFree))) {
                                action.appendEffect(
                                        new DeployCardFromReserveDeckEffect(action, Filters.Podrace_Arena, true, false));
                                action.appendEffect(
                                        new DeployCardFromReserveDeckEffect(action, Filters.Podracer, true, false));
                                action.appendEffect(
                                        new DeployCardFromReserveDeckEffect(action, Filters.Boonta_Eve_Podrace, true, false));
                                action.appendEffect(
                                        new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.deploysForFree, Filters.always_immune_to_Alter), 3, 3, true, false));

                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, opponent,
                                                new MultipleChoiceAwaitingDecision("Choose an option", new String[]{"Play a Defensive Shield from under Starting Effect", "Deploy a Podracer (from Reserve Deck)", "Do nothing"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        if (index == 0) {

                                                            game.getGameState().sendMessage(opponent + " chooses to play a Defensive Shield from under their Starting Effect");
                                                            PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.opponents(playerId), Filters.Starting_Effect));
                                                            if (startingEffect != null) {
                                                                Filter filter = Filters.and(Filters.Defensive_Shield, Filters.playable(self));
                                                                if (GameConditions.hasStackedCards(game, startingEffect, filter)) {

                                                                    final SubAction subAction = new SubAction(action, opponent);
                                                                    subAction.appendTargeting(
                                                                            new ChooseStackedCardEffect(action, opponent, startingEffect, filter) {
                                                                                @Override
                                                                                protected void cardSelected(PhysicalCard selectedCard) {
                                                                                    // Perform result(s)
                                                                                    subAction.appendEffect(
                                                                                            new PlayStackedDefensiveShieldEffect(action, startingEffect, selectedCard));
                                                                                }
                                                                            }
                                                                    );
                                                                    action.appendEffect(new StackActionEffect(action, subAction));
                                                                }
                                                            }
                                                        } else if (index == 1) {
                                                            game.getGameState().sendMessage(opponent + " chooses to deploy a Podracer");
                                                            action.appendEffect(
                                                                    new DeployCardsToTargetFromReserveDeckEffect(action, opponent, Filters.Podracer, 0, 1, Filters.Podrace_Arena, true, false) {
                                                                        @Override
                                                                        public String getChoiceText(int numCardsToChoose) {
                                                                            return "Choose Podracer to deploy";
                                                                        }
                                                                    });
                                                        } else {
                                                            game.getGameState().sendMessage(opponent + " chooses not to deploy a Podracer or play a Defensive Shield");
                                                        }
                                                    }
                                                }
                                        )
                                );
                            }
                            action.appendEffect(
                                    new PutCardFromVoidInLostPileEffect(action, playerId, self));
                        }
                    }
            );
            return action;
        }
        return null;
    }
}
