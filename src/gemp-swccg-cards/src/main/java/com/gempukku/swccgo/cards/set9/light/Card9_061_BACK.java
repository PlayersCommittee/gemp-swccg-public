package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CrossOverCharacterEffect;
import com.gempukku.swccgo.logic.effects.DepleteLifeForceEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.TransferEscortedCaptiveToNewEscortEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTransferredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Objective
 * Title: There Is Good In Him / I Can Save Him
 */
public class Card9_061_BACK extends AbstractObjective {
    public Card9_061_BACK() {
        super(Side.LIGHT, 7, Title.I_Can_Save_Him, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setGameText("While this side up, at end of each of opponent's turns, opponent loses 2 Force unless Vader is escorting Luke. At any time, an Imperial escorting Luke may transfer Luke to Vader, if present with Vader. Vader may not transfer Luke. Once during each of your turns, if Vader present with Luke (even as a non-frozen captive), may shuffle Reserve Deck and draw destiny. If destiny > 14, Vader crosses to Light Side, totally depleting opponent's Life Force. Flip if Luke neither present with Vader nor a captive.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isEndOfOpponentsTurn(game, effectResult, self)
                && !GameConditions.canSpot(game, self, Filters.and(Filters.Vader, Filters.escorting(Filters.Luke)))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make opponent lose 2 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 2));
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE,
                Filters.and(Filters.Luke, Filters.or(Filters.captive, Filters.presentWith(self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Vader))))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;

        // Check condition(s)
        PhysicalCard captiveLuke = Filters.findFirstActive(game, self, SpotOverride.INCLUDE_CAPTIVE,
                Filters.and(Filters.Luke, Filters.captiveNotProhibitedFromBeingTransferred,
                        Filters.escortedBy(self, Filters.and(Filters.Imperial, Filters.except(Filters.Vader)))));
        if (captiveLuke != null) {
            PhysicalCard vader = Filters.findFirstActive(game, self, Filters.and(Filters.Vader, Filters.presentWith(captiveLuke), Filters.canEscortCaptive(captiveLuke)));
            if (vader != null) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Transfer Luke to Vader");
                action.setActionMsg("Transfer " + GameUtils.getCardLink(captiveLuke) + " to " + GameUtils.getCardLink(vader));
                action.addAnimationGroup(captiveLuke);
                action.addAnimationGroup(vader);
                // Perform result(s)
                action.appendEffect(
                        new TransferEscortedCaptiveToNewEscortEffect(action, captiveLuke, vader));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_4;
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDrawDestiny(game, playerId)) {
            final PhysicalCard vader = Filters.findFirstActive(game, self,
                    Filters.and(Filters.Vader, Filters.presentWith(self, SpotOverride.INCLUDE_CAPTIVE, Filters.Luke)));
            if (vader != null) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Shuffle Reserve Deck and draw destiny");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new ShuffleReserveDeckEffect(action));
                action.appendEffect(
                        new DrawDestinyEffect(action, playerId) {
                            @Override
                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                GameState gameState = game.getGameState();
                                ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                if (totalDestiny == null) {
                                    gameState.sendMessage("Result: Failed due to failed destiny draw");
                                    return;
                                }
                                float crossoverAttemptTotal = modifiersQuerying.getCrossoverAttemptTotal(gameState, vader, totalDestiny);
                                gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(crossoverAttemptTotal));
                                if (crossoverAttemptTotal > 14) {
                                    gameState.sendMessage("Result: Succeeded");
                                    action.appendEffect(
                                            new CrossOverCharacterEffect(action, vader));
                                    action.appendEffect(
                                            new DepleteLifeForceEffect(action, opponent));
                                }
                                else {
                                    gameState.sendMessage("Result: Failed");
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
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTransferredModifier(self, Filters.and(Filters.Luke, Filters.escortedBy(self, Filters.Vader))));
        return modifiers;
    }
}