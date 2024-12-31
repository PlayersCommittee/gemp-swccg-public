package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.CaptureWithSeizureEffect;
import com.gempukku.swccgo.logic.effects.DisembarkEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTransferredModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBePlacedOutOfPlayModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Objective
 * Title: Hostile Takeover/Usurped
 */
public class Card304_122 extends AbstractObjective {
    public Card304_122() {
        super(Side.DARK, 0, Title.Hostile_Takeover, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Monolith Throne Room, Competitive Advantage and Complications. For remainder of game, Scanning Crew may not be played. Opponent's cards that place a character out of play may not target Locita. You may deploy Thran (deploy -2) from Reserve Deck; reshuffle. Opponent may deploy Locita from Reserve Deck (deploy -2; reshuffle) or Lost Pile. If Locita is present with Kamjin and Kamjin is not escorting a captive, Locita is captured and seized by Kamjin. Kamjin may not transfer Locita. Flip this card if Locita captured.");
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Monolith_Throne_Room, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Monolith Throne Room to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Competitive_Advantage, true, false) {
                    @Override
                    public String getChoiceText() { return "Choose Competitive Advantage to deploy"; }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Complications, true, false) {
                    @Override
                    public String getChoiceText() { return "Choose Complications to deploy"; }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        boolean targetsKaiInsteadOfLocita = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_KAI_INSTEAD_OF_LOCITA);
        boolean targetsHikaruInsteadOfLocita = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_HIKARU_INSTEAD_OF_LOCITA);
        final Condition targetsKaiInsteadOfLocitaCondition = new GameTextModificationCondition(self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_KAI_INSTEAD_OF_LOCITA);
        final Condition targetsHikaruInsteadOfLocitaCondition = new GameTextModificationCondition(self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_HIKARU_INSTEAD_OF_LOCITA);
        final Condition targetsLocitaCondition = new AndCondition(new NotCondition(targetsKaiInsteadOfLocitaCondition), new NotCondition(targetsHikaruInsteadOfLocitaCondition));

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);

        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotPlayModifier(self, Filters.Scanning_Crew), null));
        final int permCardId = self.getPermanentCardId();
        action.appendEffect(
                new AddUntilEndOfGameActionProxyEffect(action,
                        new AbstractActionProxy() {
                            @Override
                            public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                PhysicalCard self = game.findCardByPermanentId(permCardId);
                                boolean targetsKaiInsteadOfLocita = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_KAI_INSTEAD_OF_LOCITA);
                                boolean targetsHikaruInsteadOfLocita = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_HIKARU_INSTEAD_OF_LOCITA);

                                GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

                                // Check condition(s)
                                if (targetsKaiInsteadOfLocita) {
                                    if (TriggerConditions.isTableChanged(game, effectResult)) {
                                        final PhysicalCard kai = Filters.findFirstActive(game, self, Filters.and(Filters.Kai, Filters.not(Filters.captive)));
                                        if (kai != null) {
                                            PhysicalCard kamjin = Filters.findFirstActive(game, self, Filters.and(Filters.Kamjin, Filters.presentWith(kai), Filters.not(Filters.or(Filters.isLeavingTable, Filters.escorting(Filters.any)))));
                                            if (kamjin != null) {

                                                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                                                action.setSingletonTrigger(true);
                                                action.setText("Capture Kai");
                                                action.setActionMsg("Have " + GameUtils.getCardLink(kamjin) + " capture and seize " + GameUtils.getCardLink(kai));
                                                // Perform result(s)
                                                if (Filters.and(Filters.aboard(Filters.open_vehicle), Filters.not(Filters.canEscortCaptive(kai))).accepts(game, kamjin)) {
                                                    // Disembark first if no capacity available
                                                    action.appendEffect(
                                                            new DisembarkEffect(action, kamjin, game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), kamjin), false, false));
                                                }
                                                action.appendEffect(
                                                        new CaptureWithSeizureEffect(action, kai, kamjin));
                                                actions.add(action);
                                            }
                                        }
                                    }
                                    return actions;
                                }
                                else if (targetsHikaruInsteadOfLocita) {
                                    if (TriggerConditions.isTableChanged(game, effectResult)) {
                                        final PhysicalCard hikaru = Filters.findFirstActive(game, self, Filters.and(Filters.Hikaru, Filters.not(Filters.captive)));
                                        if (hikaru != null) {
                                            PhysicalCard kamjin = Filters.findFirstActive(game, self, Filters.and(Filters.Kamjin, Filters.presentWith(hikaru), Filters.not(Filters.or(Filters.isLeavingTable, Filters.escorting(Filters.any)))));
                                            if (kamjin != null) {

                                                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                                                action.setSingletonTrigger(true);
                                                action.setText("Capture Hikaru");
                                                action.setActionMsg("Have " + GameUtils.getCardLink(kamjin) + " capture and seize " + GameUtils.getCardLink(hikaru));
                                                // Perform result(s)
                                                if (Filters.and(Filters.aboard(Filters.open_vehicle), Filters.not(Filters.canEscortCaptive(hikaru))).accepts(game, kamjin)) {
                                                    // Disembark first if no capacity available
                                                    action.appendEffect(
                                                            new DisembarkEffect(action, kamjin, game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), kamjin), false, false));
                                                }
                                                action.appendEffect(
                                                        new CaptureWithSeizureEffect(action, hikaru, kamjin));
                                                actions.add(action);
                                            }
                                        }
                                    }
                                    return actions;
                                }
                                else {
                                    if (TriggerConditions.isTableChanged(game, effectResult)
                                            && !GameConditions.hasGameTextModification(game, self, ModifyGameTextType.HOSTILE_TAKEOVER__MAY_NOT_CAPTURE_LOCITA)) {
                                        final PhysicalCard locita = Filters.findFirstActive(game, self, Filters.and(Filters.Locita, Filters.not(Filters.captive)));
                                        if (locita != null) {
                                            PhysicalCard kamjin = Filters.findFirstActive(game, self, Filters.and(Filters.Kamjin, Filters.presentWith(locita), Filters.not(Filters.or(Filters.isLeavingTable, Filters.escorting(Filters.any)))));
                                            if (kamjin != null) {

                                                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                                                action.setSingletonTrigger(true);
                                                action.setText("Capture Locita");
                                                action.setActionMsg("Have " + GameUtils.getCardLink(kamjin) + " capture and seize " + GameUtils.getCardLink(locita));
                                                // Perform result(s)
                                                if (Filters.and(Filters.aboard(Filters.open_vehicle), Filters.not(Filters.canEscortCaptive(locita))).accepts(game, kamjin)) {
                                                    // Disembark first if no capacity available
                                                    action.appendEffect(
                                                            new DisembarkEffect(action, kamjin, game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), kamjin), false, false));
                                                }
                                                action.appendEffect(
                                                        new CaptureWithSeizureEffect(action, locita, kamjin));
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
                        new MayNotBeTransferredModifier(self, Filters.and(Filters.Locita, Filters.escortedBy(self, Filters.Kamjin)), targetsLocitaCondition), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotBeTransferredModifier(self, Filters.and(Filters.Kai, Filters.escortedBy(self, Filters.Kamjin)), targetsKaiInsteadOfLocitaCondition), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotBeTransferredModifier(self, Filters.and(Filters.Hikaru, Filters.escortedBy(self, Filters.Kamjin)), targetsHikaruInsteadOfLocitaCondition), null));
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.HOSTILE_TAKEOVER__DOWNLOAD_THRAN;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.THRAN)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Thran from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Thran, -2, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, final SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        boolean targetsKaiInsteadOfLocita = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_KAI_INSTEAD_OF_LOCITA);
        boolean targetsHikaruInsteadOfLocita = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_HIKARU_INSTEAD_OF_LOCITA);

        GameTextActionId gameTextActionId = GameTextActionId.HOSTILE_TAKEOVER__DOWNLOAD_LOCITA;

        if (targetsKaiInsteadOfLocita) {
            // Check condition(s)
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.KAI)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Kai from Reserve Deck");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromReserveDeckEffect(action, Filters.Kai, -2, true));
                actions.add(action);
            }

            gameTextActionId = GameTextActionId.HOSTILE_TAKEOVER__DOWNLOAD_LOCITA_FROM_LOST_PILE;

            // Check condition(s)
            if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, Persona.KAI)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Kai from Lost Pile");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromLostPileEffect(action, Filters.Kai, false));
                actions.add(action);
            }

            return actions;
        } else if (targetsHikaruInsteadOfLocita) {
            // Check condition(s)
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.HIKARU)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Hikaru from Reserve Deck");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromReserveDeckEffect(action, Filters.Hikaru, -2, true));
                actions.add(action);
            }

            gameTextActionId = GameTextActionId.HOSTILE_TAKEOVER__DOWNLOAD_LOCITA_FROM_LOST_PILE;

            // Check condition(s)
            if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, Persona.HIKARU)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Hikaru from Lost Pile");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromLostPileEffect(action, Filters.Hikaru, false));
                actions.add(action);
            }

            return actions;
        } else {
            // Check condition(s)
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.LOCITA)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Locita from Reserve Deck");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Locita, Filters.grantedMayBeTargetedBy(self)), -2, true) {
                            @Override
                            protected void cardDeployed(PhysicalCard card) {
                                if (Filters.title("Hikaru Lap'lamiz").accepts(game, card)) {
                                    game.getGameState().sendMessage(playerId + " Make Competitive Advantage, Your Destiny, and opponent's [Death Star II] objective target Hikaru instead of Locita for remainder of game using " + GameUtils.getCardLink(card));
                                    game.getModifiersEnvironment().addUntilEndOfGameModifier(new ModifyGameTextModifier(card, Filters.or(Filters.and(Filters.opponents(card), Icon.DEATH_STAR_II, Filters.Objective), Filters.Competitive_Advantage, Filters.Your_Destiny), ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_HIKARU_INSTEAD_OF_LOCITA));
                                }
                            }
                        });
                actions.add(action);
            }

            gameTextActionId = GameTextActionId.HOSTILE_TAKEOVER__DOWNLOAD_LOCITA_FROM_LOST_PILE;

            // Check condition(s)
            if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, Persona.LOCITA)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Locita from Lost Pile");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromLostPileEffect(action, Filters.or(Filters.Locita, Filters.grantedMayBeTargetedBy(self)), false) {
                            @Override
                            protected void cardDeployed(PhysicalCard card) {
                                if (Filters.title("Hikaru Lap'lamiz").accepts(game, card)) {
                                    game.getGameState().sendMessage(playerId + " Make Competitive Advantage, Your Destiny, and opponent's [Death Star II] objective target Hikaru instead of Locita for remainder of game using " + GameUtils.getCardLink(card));
                                    game.getModifiersEnvironment().addUntilEndOfGameModifier(new ModifyGameTextModifier(card, Filters.or(Filters.and(Filters.opponents(card), Icon.DEATH_STAR_II, Filters.Objective), Filters.Competitive_Advantage, Filters.Your_Destiny), ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_HIKARU_INSTEAD_OF_LOCITA));
                                }
                            }
                        });
                actions.add(action);
            }

            return actions;
        }
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        boolean targetsKaiInsteadOfLocita = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_KAI_INSTEAD_OF_LOCITA);
        boolean targetsHikaruInsteadOfLocita = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_HIKARU_INSTEAD_OF_LOCITA);

        if (targetsKaiInsteadOfLocita) {
            // Check condition(s)
            if (TriggerConditions.isTableChanged(game, effectResult)
                    && GameConditions.canBeFlipped(game, self)
                    && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Kai, Filters.captive))) {

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
        } else if (targetsHikaruInsteadOfLocita) {
            // Check condition(s)
            if (TriggerConditions.isTableChanged(game, effectResult)
                    && GameConditions.canBeFlipped(game, self)
                    && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Hikaru, Filters.captive))) {

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
                    && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Locita, Filters.captive))) {

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