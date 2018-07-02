package com.gempukku.swccgo.cards.set204.dark;

import com.gempukku.swccgo.cards.AbstractFirstOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.BattleResultDeterminedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Character
 * Subtype: First Order
 * Title: Kylo Ren
 */
public class Card204_043 extends AbstractFirstOrder {
    public Card204_043() {
        super(Side.DARK, 1, 5, 5, 5, 7, "Kylo Ren", Uniqueness.UNIQUE);
        setLore("Leader.");
        setGameText("Adds one destiny to total power when with a [First Order] shuttle. While present at a battleground, Honor Of The Jedi is suspended. Whenever a player loses a battle here, that player loses 2 Force. Immune to attrition < 4.");
        addPersona(Persona.KYLO);
        addIcons(Icon.EPISODE_VII, Icon.WARRIOR, Icon.VIRTUAL_SET_4);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsDestinyToPowerModifier(self, new WithCondition(self, Filters.and(Icon.FIRST_ORDER, Filters.shuttle)), 1));
        modifiers.add(new SuspendsCardModifier(self, Filters.Honor_Of_The_Jedi, new PresentAtCondition(self, Filters.battleground)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleResultDetermined(game, effectResult)
                && GameConditions.isDuringBattleAt(game, Filters.here(self))) {
            BattleResultDeterminedResult result = (BattleResultDeterminedResult) effectResult;
            String loser = result.getLoser();
            if (loser != null) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + loser + " lose 2 Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, loser, 2));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
