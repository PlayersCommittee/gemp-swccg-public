package com.gempukku.swccgo.cards.set202.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
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
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardsToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Set: Set 2
 * Type: Effect
 * Title: Jabba's Trophies
 */
public class Card202_012 extends AbstractNormalEffect {
    public Card202_012() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Jabbas_Trophies, Uniqueness.UNIQUE, ExpansionSet.SET_2, Rarity.V);
        setLore("Oola had to choose between giving in to Jabba's constant advances or resisting him and inciting his wrath.");
        setGameText("Deploy on table. If Jabba just Force drained (or won a battle), stack top card of opponent's Lost Pile here; may draw top card of your Reserve Deck. If 4 or more cards here, may return this Effect and a character with Jabba at Audience Chamber to owner's hand. [Immune to Alter]");
        addIcons(Icon.VIRTUAL_SET_2);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter sameLocationAsJabba = Filters.sameLocationAs(self, Filters.Jabba);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if ((TriggerConditions.forceDrainCompleted(game, effectResult, playerId, sameLocationAsJabba)
                || TriggerConditions.wonBattleAt(game, effectResult, playerId, sameLocationAsJabba))) {
            PhysicalCard topCardOfLostPile = game.getGameState().getTopOfLostPile(opponent);
            if (topCardOfLostPile != null) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setPerformingPlayer(playerId);
                action.setText("Stack top card of opponent's Lost Pile");
                // Perform result(s)
                action.appendEffect(
                        new StackOneCardFromLostPileEffect(action, topCardOfLostPile, self, false, false, false));
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(final SwccgGame game) {
                                if (GameConditions.hasReserveDeck(game, playerId)) {
                                    action.appendEffect(
                                            new PlayoutDecisionEffect(action, playerId,
                                                    new YesNoDecision("Do you want to draw top card of your Reserve Deck?") {
                                                        @Override
                                                        protected void yes() {
                                                            action.appendEffect(
                                                                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
                                                        }
                                                        protected void no() {
                                                            game.getGameState().sendMessage(playerId + " chooses to not draw top card of Reserve Deck");
                                                        }
                                                    }
                                            )
                                    );
                                }
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.hasStackedCards(game, self, 4)) {
            Filter targetFilter = Filters.and(Filters.character, Filters.with(self, Filters.Jabba), Filters.at(Filters.Audience_Chamber));
            if (GameConditions.canTarget(game, self, targetFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Return this Effect and a character to hand");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", targetFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard target) {
                                action.addAnimationGroup(target);
                                // Allow response(s)
                                action.allowResponses("Return " + GameUtils.getCardLink(self) + " and " + GameUtils.getCardLink(target) + " to owner's hand",
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ReturnCardsToHandFromTableEffect(action, Arrays.asList(self, target)));
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