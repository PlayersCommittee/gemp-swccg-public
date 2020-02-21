package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractFirstOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 12
 * Type: Character
 * Subtype: First Order
 * Title: Allegiant General Pryde
 */
public class Card501_044 extends AbstractFirstOrder {
    public Card501_044() {
        super(Side.DARK, 3, 3, 3, 3, 5, "Allegiant General Pryde", Uniqueness.UNIQUE);
        setLore("Leader");
        setGameText("[Pilot] 2. Hux is lost. Adds one battle destiny with opponents Resistance character. If Palpatine on table, your total attrition against opponent here is +1 for each First Order character here.");
        addIcons(Icon.EPISODE_VII, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_12);
        addKeywords(Keyword.LEADER, Keyword.GENERAL);
        setTestingText("Allegiant General Pryde");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.and(Filters.opponents(self.getOwner()), Filters.Resistance_character)), 1));
        modifiers.add(new AttritionModifier(self, Filters.here(self), new OnTableCondition(self, Filters.Palpatine), new HereEvaluator(self, Filters.First_Order_character), game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Hux is lost.
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
            && GameConditions.canSpot(game, self, Filters.Hux)) {
            PhysicalCard hux = Filters.findFirstActive(game, self, Filters.Hux);
            if (hux != null) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make Hux lost");
                action.setActionMsg("Make " + GameUtils.getCardLink(hux) + " lost");

                // Perform result(s)
                action.appendEffect(
                        new LoseCardFromTableEffect(action, hux));
                actions.add(action);
            }
        }
        return actions;
    }
}
