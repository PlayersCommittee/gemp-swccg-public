package com.gempukku.swccgo.cards.set227.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 27
 * Type: Character
 * Subtype: Rebel
 * Title: Luthen Rael
 */
public class Card227_021 extends AbstractRebel {
    public Card227_021() {
        super(Side.LIGHT, 1, 4, 2, 4, 6, "Luthen Rael", Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        setLore("Spy. Leader. Massassi Group.");
        setGameText("Once per turn, if at a site you control and your spy is in battle, may add or subtract 1 from a just drawn destiny. If alone at a non-battleground, may lose 1 Force to place Luthen out of play; may activate up to 2 Force.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_27);
        addKeywords(Keyword.SPY, Keyword.LEADER);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if ((GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId))
            && GameConditions.isAtLocation(game, self, Filters.and(Filters.site, Filters.controls(playerId)))
            && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(playerId), Filters.spy))
            && TriggerConditions.isDestinyJustDrawn(game, effectResult)) {

            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Add or subtract 1 from destiny draw");
            // Update usage limit(s)
            action.appendUsage(
                new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                new PlayoutDecisionEffect(action, playerId,
                    new MultipleChoiceAwaitingDecision("Choose an option", new String[]{"Add 1", "Subtract 1"}) {
                        @Override
                        protected void validDecisionMade(int index, String result) {
                            if (index == 0) {
                                action.appendEffect(
                                    new ModifyDestinyEffect(action, 1));
                            } else {
                                action.appendEffect(
                                    new ModifyDestinyEffect(action, -1));
                            }
                        }
                    }
                )
            );

            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {

        if (GameConditions.isAlone(game, self)
            && GameConditions.isAtLocation(game, self, Filters.non_battleground_location)
            && Filters.canBeTargetedBy(self, TargetingReason.TO_BE_PLACED_OUT_OF_PLAY).accepts(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Place Luthen out of play");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target Luthen to place out of play", Filters.title("Luthen Rael")) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.appendCost(
                        new LoseForceEffect(action, playerId, 1));
                    action.allowResponses(new RespondableEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            final GameState gameState = game.getGameState();
                            PhysicalCard luthen = action.getPrimaryTargetCard(targetGroupId);
                            action.appendEffect(
                                new PlaceCardOutOfPlayFromTableEffect(action, luthen));
                            int maxForceForPlayerToActivate = Math.min(2, gameState.getReserveDeckSize(playerId));
                            if (maxForceForPlayerToActivate > 0) {
                                action.appendEffect(
                                    new PlayoutDecisionEffect(action, playerId,
                                        new IntegerAwaitingDecision("Choose amount of Force to activate", 0, maxForceForPlayerToActivate, maxForceForPlayerToActivate) {
                                            @Override
                                            public void decisionMade(final int result) throws DecisionResultInvalidException {
                                                // Perform result(s)
                                                action.appendEffect(
                                                    new ActivateForceEffect(action, playerId, result));
                                            }
                                        }
                                    )
                                );
                            }
                        }
                    });

                }
            });

            return Collections.singletonList(action);
        }

        return null;
    }
}
