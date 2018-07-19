package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMasterFirstOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfPlayersNextTurnEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.BattleInitiatedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Character
 * Subtype: First Order
 * Title: Supreme Leader Snoke
 */
public class Card209_039 extends AbstractDarkJediMasterFirstOrder {
    public Card209_039() {
        super(Side.DARK, 1, 5, 3, 7, 8, "Supreme Leader Snoke", Uniqueness.UNIQUE);
        setLore("Leader.");
        setGameText("If you just initiated a battle where all your ability is provided by First Order characters and/or [First Order] starships, opponent loses 1 Force. If just lost, Kylo is power +3 until end of your next turn. Immune to attrition < 8 (< 4 if with Kylo).");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_9);
        addPersona(Persona.SNOKE);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, playerId)
                && GameConditions.isAllAbilityAtLocationProvidedBy(game, self, playerId, Filters.here(self), Filters.or(Filters.First_Order_character, Filters.and(Icon.FIRST_ORDER, Filters.starship)))) {
            BattleInitiatedResult initiateBattleResult = (BattleInitiatedResult) effectResult;
            String battleInitiator = initiateBattleResult.getPerformingPlayerId();
            String defender = game.getOpponent(battleInitiator);

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 Force");
            action.setActionMsg("Make opponent lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, defender, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make Kylo power +3");
            // Perform result(s)
            action.appendEffect(
                    new ModifyPowerUntilEndOfPlayersNextTurnEffect(action, self.getOwner(), Filters.Kylo, 3, "Makes Kylo power +3"));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(8, 4, new WithCondition(self, Filters.Kylo))));
        return modifiers;
    }


}
