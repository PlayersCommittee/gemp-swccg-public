package com.gempukku.swccgo.cards.set110.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Enhanced Jabba's Palace)
 * Type: Objective
 * Title: You Can Either Profit By This... / Or Be Destroyed
 */
public class Card110_004 extends AbstractObjective {
    public Card110_004() {
        super(Side.LIGHT, 0, Title.You_Can_Either_Profit_By_This);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Tatooine: Jabba's Palace and Audience Chamber (with Han frozen there, he may not be moved when frozen). Opponent may deploy up to two aliens at Audience Chamber. While this side up, opponent may not Force Drain at Audience Chamber and you may not Force drain at Tatooine locations. You may not play Frozen Assets. Luke, C-3PO and R2-D2 are deploy -2 at Jabba's Palace sites (Master Luke deploys for free instead). Flip this card if Han is on Tatooine and not a captive. Place out of play if Tatooine is 'blown away.'");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        final ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Jabbas_Palace, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Tatooine: Jabba's Palace to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Audience_Chamber, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Audience Chamber to deploy";
                    }
                });
        action.appendRequiredEffect(
                new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!GameConditions.hasGameTextModification(game, self, ModifyGameTextType.YOU_CAN_EITHER_PROFIT_BY_THIS__DO_NOT_DEPLOY_HAN_AT_START_OF_GAME)) {
                            action.appendRequiredEffect(
                                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.Han, Filters.Audience_Chamber, true, null, DeployAsCaptiveOption.deployAsUnattendedFrozenCaptive(), false) {
                                        @Override
                                        public String getChoiceText() {
                                            return "Choose Han to deploy to Audience Chamber as unattended 'frozen' captive";
                                        }
                                    });
                        }
                        action.appendOptionalEffect(
                                new DeployCardsToTargetFromReserveDeckEffect(action, opponent, Filters.alien, 0, 2, Filters.Audience_Chamber, true, false) {
                                    @Override
                                    public String getChoiceText(int numCardsToChoose) {
                                        return "Choose alien(s) to deploy to Audience Chamber";
                                    }
                                });
                    }
                }
        );
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard frozenHan = Filters.findFirstFromAllOnTable(game, Filters.Han);

        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotMoveModifier(self, Filters.and(frozenHan, Filters.frozenCaptive)), null));
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.Audience_Chamber, opponent));
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.Tatooine_location, playerId));
        modifiers.add(new MayNotPlayModifier(self, Filters.Frozen_Assets, playerId));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.or(Filters.and(Filters.Luke, Filters.not(Filters.Master_Luke)),
                Filters.R2D2, Filters.C3PO), -2, Filters.Jabbas_Palace_site));
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Master_Luke, Filters.Jabbas_Palace_site));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isBlownAwayLastStep(game, effectResult, Filters.Tatooine_system)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Han, Filters.on(Title.Tatooine), Filters.not(Filters.captive)))) {

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