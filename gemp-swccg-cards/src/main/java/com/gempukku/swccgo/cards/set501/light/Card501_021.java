package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.InPlayDataAsFloatEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelDestinyDrawsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Jedi Master
 * Title: Qui-Gon Jinn, Serene Jedi
 */
public class Card501_021 extends AbstractJediMaster {
    public Card501_021() {
        super(Side.LIGHT, 1, 6, 6, 7, 7, "Qui-Gon Jinn, Serene Jedi", Uniqueness.UNIQUE);
        setLore("");
        setGameText("Once per game, may deploy Meditation on Qui-Gon from Lost Pile. During battle, while alone, opponent may not cancel your destiny draws and your total battle destiny is +1 for each weapon destiny that was drawn while alone. Immune to attrition.");
        addPersona(Persona.QUIGON);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        setTestingText("Qui-Gon Jinn, Serene Jedi");
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.QUIGON_JINN_SERENCE_JEDI__DOWNLOAD_MEDITATION_FROM_LOST_PILE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Meditation from Lost Pile");
            action.setActionMsg("Deploy a Meditation on " + GameUtils.getCardLink(self) + " from Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromLostPileEffect(action, Filters.title("Meditation"), Filters.sameCardId(self), false));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.isAlone(game, self)) {
            if (GameConditions.cardHasWhileInPlayDataSet(self)) {
                self.setWhileInPlayData(new WhileInPlayData(self.getWhileInPlayData().getFloatValue() + 1));
            } else {
                self.setWhileInPlayData(new WhileInPlayData(1F));
            }
        }

        if (TriggerConditions.battleEndedAt(game, effectResult, Filters.here(self))) {
            self.setWhileInPlayData(null);
        }
        return actions;
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionModifier(self));
        modifiers.add(new TotalBattleDestinyModifier(self, new AndCondition(new InBattleCondition(self), new AloneCondition(self)), new InPlayDataAsFloatEvaluator(self), self.getOwner()));
        modifiers.add(new MayNotCancelDestinyDrawsModifier(self, new AndCondition(new InBattleCondition(self), new AloneCondition(self)), game.getOpponent(self.getOwner())));
        return modifiers;
    }
}