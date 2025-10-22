package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelDuelEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.effects.PutStackedCardInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Interrupt
 * Subtype: Lost
 * Title: Dark Rage
 */
public class Card13_064 extends AbstractLostInterrupt {
    public Card13_064() {
        super(Side.DARK, 5, "Dark Rage", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("While Qui-Gon meditated to regain strength lost during the ferocious duel, Darth Maul paced impatiently outside the energy barrier like a caged krayt dragon.");
        setGameText("If opponent just initiated a duel, use 3 Force to cancel it. OR Take Deep Hatred into hand from Reserve Deck; reshuffle. OR If Maul has no combat cards, target a Jedi to place one of their combat cards (random selection) in opponent's Used Pile. (Immune to Sense.)");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1; //cancel duel
        String opponent = game.getOpponent(playerId);
        final float action1Cost = 3;

        // Check condition(s)
        if (TriggerConditions.duelInitiatedBy(game, effectResult, opponent)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, action1Cost)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setImmuneTo(Title.Sense);
            action.setText("Cancel duel");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, action1Cost));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelDuelEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId gameTextActionId;

        // Check condition(s)
        gameTextActionId = GameTextActionId.DARK_RAGE__UPLOAD_DEEP_HATRED;
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setImmuneTo(Title.Sense);
            action.setText("Take Deep Hatred into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take Deep Hatred into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Deep_Hatred, true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3; //remove combat card from jedi
        Filter maulFilter = Filters.and(Filters.Maul, Filters.not(Filters.hasStacked(1, Filters.combatCard)));
        Filter jediFilter = Filters.and(Filters.Jedi, Filters.hasStacked(1, Filters.combatCard));

        if(GameConditions.canSpot(game,self,maulFilter) && GameConditions.canTarget(game,self,jediFilter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setImmuneTo(Title.Sense);
            action.setText("Send a Jedi's combat card to Used Pile");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose a Jedi", jediFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard jediTarget) {
                            action.addAnimationGroup(jediTarget);
                            // Allow response(s)
                            action.allowResponses("Move one of " + GameUtils.getCardLink(jediTarget) + "'s combat cards to Used Pile",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            if(finalTarget != null) {
                                                Collection<PhysicalCard> stackedCombatCards = Filters.filter(game.getGameState().getStackedCards(finalTarget), game, Filters.combatCard);
                                                if (!stackedCombatCards.isEmpty()) {
                                                    PhysicalCard randomCombatCard = GameUtils.getRandomCards(stackedCombatCards, 1).getFirst();
                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new PutStackedCardInUsedPileEffect(action, playerId, randomCombatCard, true));

                                                }
                                            }
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
