package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ReduceAttritionEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Nebit
 */
public class Card7_191 extends AbstractAlien {
    public Card7_191() {
        super(Side.DARK, 3, 2, 2, 1, 3, "Nebit", Uniqueness.UNIQUE);
        setLore("Jawa leader responsible for organizing raids on rival Jawa factions. Detested warrior. Hates being 'frocked.'");
        setGameText("Deploys only on Tatooine. When in a battle with at least two of your other Jawas, adds one battle destiny. When your total battle destiny at Nebit's site is greater than opponent's total battle destiny, Nebit reduces attrition against you by 3.");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.JAWA);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new InBattleWithCondition(self, 2, Filters.and(Filters.your(self), Filters.Jawa)), 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.isDuringBattleAt(game, Filters.sameSite(self))
                && GameConditions.hasGreaterBattleDestinyTotal(game, playerId, false)
                && GameConditions.isAttritionRemaining(game, playerId)
                && GameConditions.canModifyAttritionAgainst(game, playerId)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reduce attrition");
            // Perform result(s)
            action.appendEffect(
                    new ReduceAttritionEffect(action, playerId, 3));
            return Collections.singletonList(action);
        }
        return null;
    }
}
