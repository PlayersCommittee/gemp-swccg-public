package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.SatisfyAllAttritionEffect;
import com.gempukku.swccgo.cards.evaluators.ForceIconsAtLocationEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.common.Persona;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Imperial
 * Title: Ellac Conrat
 */
public class Card304_005 extends AbstractImperial {
    public Card304_005() {
        super(Side.DARK, 1, 4, 2, 4, 4, "Ellac Conrat", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Born on Raxus Sencundus he and his sister grew up under an abusive father. Over the past couple years he's grown deeper in the Dark Side guided by his master, Sykes Jade.");
        setGameText("If in a battle with Kamjin may be sacrificed to satisify all attrition. ");
        addIcons(Icon.CSP, Icon.WARRIOR);
        addPersona(Persona.ELLAC);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.canForfeitToSatisfyAttrition(game, playerId, self)
                && GameConditions.isAtLocation(game, self, Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.Kamjin)))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Forfeit to satisfy all attrition");
            // Pay cost(s)
            action.appendCost(
                    new ForfeitCardFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new SatisfyAllAttritionEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
