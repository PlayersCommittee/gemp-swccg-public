package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromBottomOfForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Character
 * Subtype: Resistance
 * Title: Rey
 */
public class Card204_009 extends AbstractResistance {
    public Card204_009() {
        super(Side.LIGHT, 1, 4, 4, 4, 7, Title.Rey, Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setLore("Female scavenger.");
        setGameText("[Pilot] 3. Once per game, when deployed, may take any card into hand from Used Pile; reshuffle. Your total battle destiny here is +1. Once per turn, may draw bottom card of your Force Pile. Immune to attrition < 3 (< 6 if on Jakku).");
        addIcons(Icon.EPISODE_VII, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_4);
        addKeywords(Keyword.FEMALE, Keyword.SCAVENGER);
        addPersona(Persona.REY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.here(self), 1, playerId));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(3, 6, new OnCondition(self, Title.Jakku))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.REY__CARD_FROM_USED_PILE;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Used Pile");
            action.setActionMsg("Take any card into hand from Used Pile");

            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromUsedPileEffect(action, playerId, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasForcePile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw bottom card of Force Pile");
            action.setActionMsg("Draw bottom card of Force Pile into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromBottomOfForcePileEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
