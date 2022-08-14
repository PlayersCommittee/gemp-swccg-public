package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.SubstituteDestinyEffect;
import com.gempukku.swccgo.logic.effects.choose.StackDestinyCardEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Effect
 * Title: Sando Aqua Monster
 */
public class Card13_041 extends AbstractNormalEffect {
    public Card13_041() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Sando_Aqua_Monster, Uniqueness.UNIQUE);
        setLore("The sando aqua monster is quite a mystery to Naboo oceanographers. A reclusive predator by nature, this undersea giant appears to have no natural enemies.");
        setGameText("Deploy on table. Cancels Colo Claw Fish. While no card here, you may draw destiny and place it face-up here. If you are about to draw a card for weapon or battle destiny, you may instead use card here, then place it in Lost Pile. (Immune to Alter.)");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Colo_Claw_Fish)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            if (GameConditions.canTargetToCancel(game, self, Filters.Colo_Claw_Fish)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Colo_Claw_Fish, Title.Colo_Claw_Fish);
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (!GameConditions.hasStackedCards(game, self)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw destiny and stack card");
            action.setActionMsg("Draw destiny and stack drawn destiny on " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected List<ActionProxy> getDrawDestinyActionProxies(SwccgGame game, final DrawDestinyState drawDestinyState) {
                            ActionProxy actionProxy = new AbstractActionProxy() {
                                @Override
                                public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                    List<TriggerAction> actions = new LinkedList<TriggerAction>();

                                    // Check condition(s)
                                    if (TriggerConditions.isDestinyDrawComplete(game, effectResult, drawDestinyState)
                                            && GameConditions.canStackDestinyCard(game)) {

                                        RequiredGameTextTriggerAction action1 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                        action1.skipInitialMessageAndAnimation();
                                        action1.setPerformingPlayer(playerId);
                                        action1.setText("Stack drawn destiny");
                                        action1.setActionMsg(null);
                                        // Perform result(s)
                                        action1.appendEffect(
                                                new StackDestinyCardEffect(action1, self));
                                        actions.add(action1);
                                    }
                                    return actions;
                                }
                            };
                            return Collections.singletonList(actionProxy);
                        }
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if ((TriggerConditions.isAboutToDrawBattleDestiny(game, effectResult, playerId)
                || TriggerConditions.isAboutToDrawWeaponDestiny(game, effectResult, playerId))
                && GameConditions.hasStackedCards(game, self)
                && GameConditions.canSubstituteDestiny(game)) {
            final PhysicalCard stackedCard = game.getGameState().getStackedCards(self).get(0);

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Substitute destiny");
            // Pay cost(s)
            action.appendCost(
                    new RefreshPrintedDestinyValuesEffect(action, Collections.singletonList(stackedCard)) {
                        @Override
                        protected void refreshedPrintedDestinyValues() {
                            final float destinyValue = game.getModifiersQuerying().getDestiny(game.getGameState(), stackedCard);
                            DestinyType destinyType = game.getGameState().getTopDrawDestinyState().getDrawDestinyEffect().getDestinyType();
                            action.setActionMsg("Substitute " + GameUtils.getCardLink(stackedCard) + "'s destiny value of " + GuiUtils.formatAsString(destinyValue) + " for " + destinyType.getHumanReadable());
                            // Perform result(s)
                            action.appendEffect(
                                    new SubstituteDestinyEffect(action, destinyValue));
                            action.appendEffect(
                                    new PutStackedCardInLostPileEffect(action, playerId, stackedCard, false));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}