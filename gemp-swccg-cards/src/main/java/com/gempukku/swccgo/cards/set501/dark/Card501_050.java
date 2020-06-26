package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 13
 * Type: Effect
 * Title: There Are Many Hunting You Now
 */
public class Card501_050 extends AbstractNormalEffect {
    public Card501_050() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "There Are Many Hunting You Now", Uniqueness.UNIQUE);
        setGameText("Deploy on table. Once during your turn, may stack top card of Lost Pile face down (as a ‘Hatred’ card) on an opponent’s character\n" +
                "with your Inquisitor. That character’s gametext is canceled for remainder of turn unless opponent loses 1 force. [Immune To Alter.]");
        addImmuneToCardTitle(Title.Alter);
        addIcon(Icon.VIRTUAL_SET_13);
        setTestingText("There Are Many Hunting You Now");
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        Filter opponentsCharacterWithYourInquisitor = Filters.and(Filters.opponents(playerId), Filters.character, Filters.with(self, Filters.and(Filters.your(playerId), Filters.inquisitor)));
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasLostPile(game, playerId)
                && GameConditions.canSpot(game, self, opponentsCharacterWithYourInquisitor)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Stack a 'Hatred Card'");
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target opponent's character with your Inquisitor", opponentsCharacterWithYourInquisitor) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            // Perform result(s) - Stack top card of lost pile and set as hatred card
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            final PhysicalCard topCardOfLostPile = game.getGameState().getTopOfLostPile(playerId);
                                            action.appendEffect(
                                                    new StackOneCardFromLostPileEffect(action, topCardOfLostPile, targetedCard, true, false, true)
                                            );
                                            action.appendEffect(
                                                    new PassthruEffect(action) {
                                                        @Override
                                                        protected void doPlayEffect(SwccgGame game) {
                                                            topCardOfLostPile.setHatredCard(true);
                                                        }
                                                    }
                                            );
                                            action.appendEffect(
                                                    new PlayoutDecisionEffect(action, game.getOpponent(playerId),
                                                            new YesNoDecision("Lose 1 force to prevent " + GameUtils.getCardLink(targetedCard) + "'s gametext from being cancelled") {
                                                                @Override
                                                                protected void yes() {
                                                                    action.appendEffect(
                                                                            new LoseForceEffect(action, game.getOpponent(playerId), 1)
                                                                    );
                                                                }

                                                                @Override
                                                                protected void no() {
                                                                    action.appendEffect(
                                                                            new CancelGameTextUntilEndOfTurnEffect(action, targetedCard)
                                                                    );
                                                                }
                                                            }
                                                    )
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
