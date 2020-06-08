package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.PowerEvaluator;
import com.gempukku.swccgo.cards.evaluators.SubtractEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Objective
 * Title: Dantooine Base Operations / More Dangerous Than You Realize
 */
public class Card7_135_BACK extends AbstractObjective {
    public Card7_135_BACK() {
        super(Side.LIGHT, 7, Title.More_Dangerous_Than_You_Realize);
        setGameText("While this side up, opponent's Force drains are -1. At Dantooine locations, each Imperial is deploy +2. Your squadrons may deploy to Dantooine (deploy cost = squadron's power -3), are immune to attrition < 4 and may draw one battle destiny if not able to otherwise. Your Force drains are +1 at systems where you have a squadron present. Flip this card if opponent controls at least two Dantooine locations. Place out of play if Dantooine is 'blown away.'");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter yourSquadrons = Filters.and(Filters.your(self), Filters.squadron);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.any, -1, opponent));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Imperial, 2, Filters.Dantooine_location));
        modifiers.add(new MayDeployToSystemModifier(self, yourSquadrons, Title.Dantooine));
        modifiers.add(new UseCalculationForDeployCostModifier(self, yourSquadrons, new SubtractEvaluator(new PowerEvaluator(), 3)) {
            @Override
            public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
                return "Deploy cost = power - 3";
            }
        });
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, yourSquadrons, 4));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, yourSquadrons, 1));
        modifiers.add(new ForceDrainModifier(self, Filters.wherePresent(self, yourSquadrons), 1, playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isBlownAwayLastStep(game, effectResult, Filters.Dantooine_system)) {

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
                && GameConditions.controls(game, opponent, 2, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Dantooine_location)) {

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