package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTransferredModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBePlacedOutOfPlayModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Objective
 * Title: Bring Him Before Me
 */
public class Card9_151 extends AbstractObjective {
    public Card9_151() {
        super(Side.DARK, 0, Title.Bring_Him_Before_Me);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Throne Room, Insignificant Rebellion and Your Destiny. For remainder of game, Scanning Crew may not be played. Opponent's cards that place a character out of play may not target Luke. You may deploy Emperor (deploy -2) from Reserve Deck; reshuffle. Opponent may deploy Luke from Reserve Deck (deploy -2; reshuffle) or Lost Pile. If Luke is present with Vader and Vader is not escorting a captive, Luke is captured and seized by Vader. Vader may not transfer Luke. Flip this card if Luke captured.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Throne_Room, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Throne Room to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Insignificant_Rebellion, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Insignificant Rebellion to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Your_Destiny, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Your Destiny to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        boolean targetsLeiaInsteadOfLuke = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE);
        final Condition targetsLeiaInsteadOfLukeCondition = new GameTextModificationCondition(self, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE);
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);

        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotPlayModifier(self, Filters.Scanning_Crew), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotTargetToBePlacedOutOfPlayModifier(self, Filters.Luke, Filters.not(Filters.title(Title.We_Need_Luke_Skywalker))), null));
        final int permCardId = self.getPermanentCardId();
        action.appendEffect(
                new AddUntilEndOfGameActionProxyEffect(action,
                        new AbstractActionProxy() {
                            @Override
                            public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                PhysicalCard self = game.findCardByPermanentId(permCardId);
                                boolean targetsLeiaInsteadOfLuke = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE);

                                GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

                                // Check condition(s)
                                if (targetsLeiaInsteadOfLuke) {
                                    if (TriggerConditions.isTableChanged(game, effectResult)) {
                                        final PhysicalCard leia = Filters.findFirstActive(game, self, Filters.and(Filters.Leia, Filters.not(Filters.captive)));
                                        if (leia != null) {
                                            PhysicalCard vader = Filters.findFirstActive(game, self, Filters.and(Filters.Vader, Filters.presentWith(leia), Filters.not(Filters.or(Filters.isLeavingTable, Filters.escorting(Filters.any)))));
                                            if (vader != null) {

                                                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                                                action.setSingletonTrigger(true);
                                                action.setText("Capture Leia");
                                                action.setActionMsg("Have " + GameUtils.getCardLink(vader) + " capture and seize " + GameUtils.getCardLink(leia));
                                                // Perform result(s)
                                                if (Filters.and(Filters.aboard(Filters.open_vehicle), Filters.not(Filters.canEscortCaptive(leia))).accepts(game, vader)) {
                                                    // Disembark first if no capacity available
                                                    action.appendEffect(
                                                            new DisembarkEffect(action, vader, game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), vader), false, false));
                                                }
                                                action.appendEffect(
                                                        new CaptureWithSeizureEffect(action, leia, vader));
                                                actions.add(action);
                                            }
                                        }
                                    }
                                    return actions;
                                }
                                else {
                                    if (TriggerConditions.isTableChanged(game, effectResult)
                                            && !GameConditions.hasGameTextModification(game, self, ModifyGameTextType.BRING_HIM_BEFORE_ME__MAY_NOT_CAPTURE_LUKE)) {
                                        final PhysicalCard luke = Filters.findFirstActive(game, self, Filters.and(Filters.Luke, Filters.not(Filters.captive)));
                                        if (luke != null) {
                                            PhysicalCard vader = Filters.findFirstActive(game, self, Filters.and(Filters.Vader, Filters.presentWith(luke), Filters.not(Filters.or(Filters.isLeavingTable, Filters.escorting(Filters.any)))));
                                            if (vader != null) {

                                                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                                                action.setSingletonTrigger(true);
                                                action.setText("Capture Luke");
                                                action.setActionMsg("Have " + GameUtils.getCardLink(vader) + " capture and seize " + GameUtils.getCardLink(luke));
                                                // Perform result(s)
                                                if (Filters.and(Filters.aboard(Filters.open_vehicle), Filters.not(Filters.canEscortCaptive(luke))).accepts(game, vader)) {
                                                    // Disembark first if no capacity available
                                                    action.appendEffect(
                                                            new DisembarkEffect(action, vader, game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), vader), false, false));
                                                }
                                                action.appendEffect(
                                                        new CaptureWithSeizureEffect(action, luke, vader));
                                                actions.add(action);
                                            }
                                        }
                                    }
                                    return actions;
                                }
                            }
                        }
                )
        );
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotBeTransferredModifier(self, Filters.and(Filters.Luke, Filters.escortedBy(self, Filters.Vader)), new NotCondition(targetsLeiaInsteadOfLukeCondition)), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotBeTransferredModifier(self, Filters.and(Filters.Leia, Filters.escortedBy(self, Filters.Vader)), targetsLeiaInsteadOfLukeCondition), null));
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BRING_HIM_BEFORE_ME__DOWNLOAD_EMPEROR;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.EMPEROR)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Emperor from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Emperor, -2, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        boolean targetsLeiaInsteadOfLuke = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE);

        GameTextActionId gameTextActionId = GameTextActionId.BRING_HIM_BEFORE_ME__DOWNLOAD_LUKE;

        if (targetsLeiaInsteadOfLuke) {
            // Check condition(s)
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.LEIA)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Leia from Reserve Deck");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromReserveDeckEffect(action, Filters.Leia, -2, true));
                actions.add(action);
            }

            gameTextActionId = GameTextActionId.BRING_HIM_BEFORE_ME__DOWNLOAD_LUKE_FROM_LOST_PILE;

            // Check condition(s)
            if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, Persona.LEIA)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Leia from Lost Pile");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromLostPileEffect(action, Filters.Leia, false));
                actions.add(action);
            }

            return actions;
        } else {
            // Check condition(s)
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.LUKE)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Luke from Reserve Deck");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromReserveDeckEffect(action, Filters.Luke, -2, true));
                actions.add(action);
            }

            gameTextActionId = GameTextActionId.BRING_HIM_BEFORE_ME__DOWNLOAD_LUKE_FROM_LOST_PILE;

            // Check condition(s)
            if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, Persona.LUKE)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Luke from Lost Pile");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromLostPileEffect(action, Filters.Luke, false));
                actions.add(action);
            }

            return actions;
        }
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        boolean targetsLeiaInsteadOfLuke = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE);

        if (targetsLeiaInsteadOfLuke) {
            // Check condition(s)
            if (TriggerConditions.isTableChanged(game, effectResult)
                    && GameConditions.canBeFlipped(game, self)
                    && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Leia, Filters.captive))) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, self));
                return Collections.singletonList(action);
            }
            return null;
        } else {
            // Check condition(s)
            if (TriggerConditions.isTableChanged(game, effectResult)
                    && GameConditions.canBeFlipped(game, self)
                    && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Luke, Filters.captive))) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, self));
                return Collections.singletonList(action);
            }
            return null;
        }
    }
}