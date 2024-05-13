package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtOpponentsHandEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.LoseCardsFromHandEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Scanning Crew
 */
public class Card1_266 extends AbstractUsedInterrupt {
    public Card1_266() {
        super(Side.DARK, 3, Title.Scanning_Crew, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Imperials use sensitive equipment to search captured ships for shielded compartments. Scanning crew BT-445 planned to search the Millennium Falcon.");
        setGameText("Use 1 Force to glance at the cards in the opponent's hand for 10 seconds. You may move any one Rebel you find there to the top of opponent's Used Pile.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.hasHand(game, opponent)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Peek at opponent's hand");
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
                                    new PeekAtOpponentsHandEffect(action, playerId) {
                                        @Override
                                        protected void cardsPeekedAt(List<PhysicalCard> peekedAtCards) {
                                            if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.SCANNING_CREW__CARDS_WITH_REBEL_IN_TITLE_LOST)) {
                                                Collection<PhysicalCard> cardsToLose = Filters.filter(peekedAtCards, game, Filters.and(Filters.titleContains("Rebel"), Filters.canBeTargetedBy(self)));
                                                if (!cardsToLose.isEmpty()) {
                                                    action.appendEffect(
                                                            new LoseCardsFromHandEffect(action, opponent, cardsToLose));
                                                }
                                            }                                                                                                  ;
                                            action.appendEffect(
                                                    new PassthruEffect(action) {
                                                        @Override
                                                        protected void doPlayEffect(final SwccgGame game) {
                                                            final Filter rebelFilter = Filters.and(Filters.Rebel, Filters.canBeTargetedBy(self));
                                                            if (GameConditions.hasInHand(game, opponent, rebelFilter)) {
                                                                action.appendEffect(
                                                                        new PlayoutDecisionEffect(action, playerId,
                                                                                new YesNoDecision("Do you want to place a Rebel from opponent's hand in Used Pile?") {
                                                                                    @Override
                                                                                    protected void yes() {
                                                                                        game.getGameState().sendMessage(playerId + " chooses to place a Rebel in Used Pile");
                                                                                        action.appendEffect(
                                                                                                new PutCardFromHandOnUsedPileEffect(action, playerId, opponent, rebelFilter, false) {
                                                                                                    @Override
                                                                                                    public String getChoiceText(int numCardsToChoose) {
                                                                                                        return "Choose Rebel to place in Used Pile";
                                                                                                    }
                                                                                                }
                                                                                        );
                                                                                    }
                                                                                    @Override
                                                                                    protected void no() {
                                                                                        game.getGameState().sendMessage(playerId + " chooses to not place a Rebel in Used Pile");
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
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}