package com.gempukku.swccgo.cards.set501.light;


import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.StackDestinyCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Effect
 * Subtype: Utinni
 * Title: Kessel Run (V)
 */
public class Card501_071 extends AbstractNormalEffect {
    public Card501_071() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, Title.Kessel_Run, Uniqueness.UNIQUE);
        setLore("Planet Kessel has infamous glitterstim spice mines attracting smugglers and pirates. A 'Kessel run' is a long, dangerous hyper-route they must travel quickly.");
        setGameText("Deploy on Kessel and draw destiny, stacking face up" +
                "here, until total > 12 (can't deploy otherwise). Once per" +
                "move phase, if your smuggler here, may place a" +
                "stacked card on Force Pile. If total = 0, Kessel Run" +
                "‘completed’ (place in Used Pile and retrieve 3 Force).");
        setTestingText("Kessel Run (V)");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Kessel_system;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        if (GameConditions.isOnceDuringEitherPlayersPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.MOVE)
                && GameConditions.isHere(game, self, Filters.and(Filters.your(playerId), Filters.smuggler))) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Place stacked card in Force Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ChooseStackedCardEffect(action, playerId, self) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            action.appendEffect(
                                    new PutStackedCardInForcePileEffect(action, playerId, selectedCard, false)
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw 'coaxium' destiny");
            action.setActionMsg("Draw 'coaxium' destiny");

            // Perform result(s)
            drawCoaxiumDestiny(action, self, game, self.getOwner(), gameTextSourceCardId);

            actions.add(action);
        }

        if (self.getWhileInPlayData() != null
                && GameConditions.hasStackedCardsTotalDestinyIsEqualTo(game, self, 0)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Complete Kessel Run");
            action.setActionMsg("Complete Kessel Run");
            action.appendEffect(
                    new SetWhileInPlayDataEffect(action, self, null)
            );
            action.appendEffect(
                    new RecordUtinniEffectCompletedEffect(action, self)
            );
            action.appendEffect(
                    new PlaceCardInUsedPileFromTableEffect(action, self)
            );
            action.appendEffect(
                    new RetrieveForceEffect(action, self.getOwner(), 3)
            );
            actions.add(action);
        }
        return actions;
    }


    private void drawCoaxiumDestiny(final Action action, final PhysicalCard self, SwccgGame game, final String playerId, final int gameTextSourceCardId) {
        // Perform result(s)
        if (GameConditions.hasReserveDeck(game, playerId)) {
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId, 1) {
                        @Override
                        protected List<ActionProxy> getDrawDestinyActionProxies(SwccgGame game, final DrawDestinyState drawDestinyState) {
                            ActionProxy actionProxy = new AbstractActionProxy() {
                                @Override
                                public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                    List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                    // Check condition(s)
                                    if (TriggerConditions.isDestinyJustDrawn(game, effectResult, drawDestinyState)
                                            && GameConditions.canStackDestinyCard(game)) {

                                        PhysicalCard card = drawDestinyState.getDrawDestinyEffect().getDrawnDestinyCard();
                                        RequiredGameTextTriggerAction action1 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                        action1.skipInitialMessageAndAnimation();
                                        action1.setPerformingPlayer(playerId);
                                        action1.setText("Stack drawn destiny");
                                        action1.setActionMsg(null);
                                        float value;
                                        if (self.getWhileInPlayData() != null) {
                                            value = self.getWhileInPlayData().getFloatValue() + card.getDestinyValueToUse();
                                        } else {
                                            value = card.getDestinyValueToUse();
                                        }
                                        action1.appendEffect(
                                                new SetWhileInPlayDataEffect(action, self, new WhileInPlayData(value))
                                        );
                                        // Perform result(s)
                                        action1.appendEffect(
                                                new StackDestinyCardEffect(action1, self)

                                        );
                                        actions.add(action1);
                                    }

                                    return actions;
                                }
                            };
                            return Collections.singletonList(actionProxy);
                        }

                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            if (self.getWhileInPlayData().getFloatValue() <= 12) {
                                drawCoaxiumDestiny(action, self, game, playerId, gameTextSourceCardId);
                            }
                        }
                    });

        } else {
            game.getGameState().sendMessage("Coaxium destiny never exceeded 12, deployment failed.");
            action.appendEffect(
                    new ReturnCardToHandFromTableEffect(action, self, Zone.RESERVE_DECK)
            );
        }
    }
}
