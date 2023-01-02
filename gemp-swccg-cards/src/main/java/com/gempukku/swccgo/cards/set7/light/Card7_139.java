package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
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
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Objective
 * Title: Rescue The Princess / Sometimes I Amaze Even Myself
 */
public class Card7_139 extends AbstractObjective {
    public Card7_139() {
        super(Side.LIGHT, 0, Title.Rescue_The_Princess, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Detention Block Corridor (with Leia of ability < 4 imprisoned there), Docking Bay 327, Yavin 4: Docking Bay and Yavin 4: War Room. {While} this side up, your spies, 8D8, Revolution, Death Star Plans and Detention Block Control Room may not deploy to Death Star. Cards that release captives are immune to Sense and Alter. May not play Nabrun Leids. Flip this card if you move Leia to Yavin 4: War Room. Place out of play if Leia is lost from table (you may not deploy Death Star Plans for remainder of game).");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Detention_Block_Corridor, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Detention Block Corridor to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardToTargetFromReserveDeckEffect(action, Filters.and(Filters.Leia, Filters.abilityLessThan(4)), Filters.Detention_Block_Corridor, true, null, DeployAsCaptiveOption.deployAsImprisonedCaptive(), false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Leia of ability < 4 to deploy into Detention Block Corridor as 'imprisoned' captive";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Docking_Bay_327, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Docking Bay 327 to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Yavin_4_Docking_Bay, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Yavin 4: Docking Bay to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Yavin_4_War_Room, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Yavin 4: War Room to deploy";
                    }
                });
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.and(Filters.your(self),
                Filters.or(Filters.spy, Filters._8D8, Filters.Revolution, Filters.Death_Star_Plans)), Filters.Death_Star_location));
        modifiers.add(new MayNotDeployModifier(self, Filters.Detention_Block_Control_Room, playerId));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.canReleaseCaptives, Title.Sense));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.canReleaseCaptives, Title.Alter));
        modifiers.add(new MayNotPlayModifier(self, Filters.Nabrun_Leids, playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, Filters.Leia)
                && !GameConditions.hasGameTextModification(game, self, ModifyGameTextType.RESCUE_THE_PRINCESS__CANNOT_BE_PLACED_OUT_OF_PLAY)
                && GameConditions.canBePlacedOutOfPlay(game, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfGameModifierEffect(action,
                            new MayNotDeployModifier(self, Filters.Death_Star_Plans, playerId), null));
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.movedToLocationBy(game, effectResult, playerId, Filters.Leia, Filters.Yavin_4_War_Room)
                && GameConditions.canBeFlipped(game, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
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