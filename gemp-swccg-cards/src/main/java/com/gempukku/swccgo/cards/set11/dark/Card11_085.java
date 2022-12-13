package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Podracer Collision
 */
public class Card11_085 extends AbstractUsedOrLostInterrupt {
    public Card11_085() {
        super(Side.DARK, 5, Title.Podracer_Collision, Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.U);
        setLore("Podracing demands constant attention to many variables as well as all opponents. Failure to do so can often result in reduced performance.");
        setGameText("USED: If you just verified opponent's Reserve Deck, search that Reserve Deck and place one Interrupt found there out of play. LOST: Lose 1 Force to remove a race destiny (random selection) from any Podracer and place it in owner's hand.");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.PODRACER_COLLISION__SEARCH_OPPONENT_RESERVE_DECK;

        // Check condition(s)
        if (TriggerConditions.justVerifiedOpponentsReserveDeck(game, effectResult, playerId)
                && GameConditions.canSearchOpponentsReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Place an Interrupt from Reserve Deck out of play");
            // Allow response(s)
            action.allowResponses("Search opponent's Reserve Deck and place an Interrupt found there out of play",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlaceCardOutOfPlayFromReserveDeckEffect(action, playerId, opponent, Filters.Interrupt, false));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final Filter podracerFilter = Filters.and(Filters.Podracer, Filters.hasStacked(Filters.raceDestiny));

        // Check condition(s)
        if (GameConditions.isDuringPodrace(game)
                && GameConditions.canTarget(game, self, podracerFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Return a race destiny to hand");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Podracer", podracerFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedPodracer) {
                            action.addAnimationGroup(targetedPodracer);
                            // Pay cost(s)
                            action.appendCost(
                                    new LoseForceEffect(action, playerId, 1, true));
                            // Allow response(s)
                            action.allowResponses("Return a random race destiny from " + GameUtils.getCardLink(targetedPodracer) + " to hand",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            PhysicalCard finalPodracer = action.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            if (finalPodracer != null) {
                                                List<PhysicalCard> randomRaceDestiny = GameUtils.getRandomCards(Filters.filter(finalPodracer.getCardsStacked(), game, Filters.raceDestiny), 1);
                                                if (!randomRaceDestiny.isEmpty()) {
                                                    action.appendEffect(
                                                            new ReturnCardToHandFromOffTableEffect(action, randomRaceDestiny.get(0)));
                                                }
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