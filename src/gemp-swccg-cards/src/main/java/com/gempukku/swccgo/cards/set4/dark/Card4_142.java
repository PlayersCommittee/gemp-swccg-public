package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtOpponentsHandEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfPlayersNextTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.ChooseArbitraryCardsEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Lost
 * Title: Frustration
 */
public class Card4_142 extends AbstractLostInterrupt {
    public Card4_142() {
        super(Side.DARK, 3, Title.Frustration, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("'Rrraaaarrr!'");
        setGameText("During your control phase, peek at opponent's hand and target one non-Interrupt card you find there that has a deploy cost < total number of Light Side Force icons on table. Opponent must deploy a card of that title by the end of your next turn, or lose a card of that title from hand (if possible).");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                && GameConditions.hasHand(game, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Peek at opponent's hand");
            // Allow response(s)
            action.allowResponses("Peek at opponent's hand and target a non-Interrupt card",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PeekAtOpponentsHandEffect(action, playerId) {
                                        @Override
                                        protected void cardsPeekedAt(List<PhysicalCard> peekedAtCards) {
                                            final int lightForceIcons = getLightSideForceIconsOnTable(game);
                                            final Filter validTargetFilter = Filters.and(
                                                    Filters.not(Filters.Interrupt),
                                                    deployCostLessThan(lightForceIcons));
                                            Collection<PhysicalCard> validTargets = Filters.filter(peekedAtCards, game, validTargetFilter);
                                            if (validTargets.isEmpty()) {
                                                return;
                                            }

                                            action.appendEffect(
                                                    new ChooseArbitraryCardsEffect(action, playerId, "Target a non-Interrupt card with deploy cost < " + lightForceIcons + " Light Side Force icons",
                                                            validTargets, Filters.any, 1, 1, false) {
                                                        @Override
                                                        protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                                            final PhysicalCard targetedCard = selectedCards.iterator().next();
                                                            if (targetedCard == null) {
                                                                return;
                                                            }
                                                            final String targetedTitle = targetedCard.getTitle();
                                                            final int permCardId = self.getPermanentCardId();
                                                            final int gameTextSourceCardId = self.getCardId();
                                                            final int nextTurnNumber = game.getGameState().getPlayersLatestTurnNumber(playerId) + 1;
                                                            // Track the obligation on the proxy itself. Frustration is a Lost Interrupt, so
                                                            // ForRemainderOfGameData on the card is not reliable after it hits the Lost Pile.
                                                            final boolean[] stillPending = new boolean[] { true };
                                                            game.getGameState().sendMessage(playerId + " targets " + GameUtils.getCardLink(targetedCard)
                                                                    + ". " + opponent + " must deploy a card of that title by the end of " + playerId + "'s next turn, or lose a card of that title from hand (if possible)");

                                                            // Delayed obligation lasts until the end of the playing player's next turn.
                                                            // "Your next turn" is the next full turn of the Frustration player (turn number captured here),
                                                            // not the remainder of the current turn. Pattern matches Jedi Test #5 (Card4_078).
                                                            action.appendEffect(
                                                                    new AddUntilEndOfPlayersNextTurnActionProxyEffect(action, new AbstractActionProxy() {
                                                                        @Override
                                                                        public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                                                            List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                                                            if (!stillPending[0]) {
                                                                                return actions;
                                                                            }

                                                                            // Opponent deployed a card of the targeted title, which satisfies Frustration
                                                                            if (TriggerConditions.justDeployed(game, effectResult, opponent, Filters.title(targetedTitle))) {
                                                                                stillPending[0] = false;
                                                                                return actions;
                                                                            }

                                                                            // At the end of that next turn, lose a card of the title from hand if possible
                                                                            if (TriggerConditions.isEndOfYourTurn(game, effectResult, playerId)
                                                                                    && GameConditions.isTurnNumber(game, nextTurnNumber)) {
                                                                                stillPending[0] = false;
                                                                                final PhysicalCard source = game.findCardByPermanentId(permCardId);
                                                                                Collection<PhysicalCard> inHand = Filters.filter(game.getGameState().getHand(opponent), game, Filters.title(targetedTitle));
                                                                                if (source != null && !inHand.isEmpty()) {
                                                                                    RequiredGameTextTriggerAction action1 = new RequiredGameTextTriggerAction(source, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
                                                                                    action1.setText("Make " + opponent + " lose a " + targetedTitle + " from hand");
                                                                                    action1.appendEffect(
                                                                                            new LoseCardFromHandEffect(action1, inHand.iterator().next()));
                                                                                    actions.add(action1);
                                                                                }
                                                                            }
                                                                            return actions;
                                                                        }
                                                                    }, playerId)
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

    /**
     * Gets the total number of Light Side Force icons on table (locations and cards at those locations).
     * This is Light Side Force icons, not Cloud City icons.
     */
    private int getLightSideForceIconsOnTable(SwccgGame game) {
        int count = 0;
        for (PhysicalCard location : Filters.filterTopLocationsOnTable(game, Filters.any)) {
            count += GameConditions.getNumForceIconsHere(game, location, false, true);
        }
        return count;
    }

    /**
     * Filter that accepts cards whose deploy cost is strictly less than the specified value.
     */
    private Filter deployCostLessThan(final float cost) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!modifiersQuerying.hasDeployCostAttribute(physicalCard)) {
                    return false;
                }
                return modifiersQuerying.getDeployCost(gameState, physicalCard) < cost;
            }
        };
    }
}
