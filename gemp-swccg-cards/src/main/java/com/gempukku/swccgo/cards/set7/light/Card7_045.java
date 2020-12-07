package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;
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
 * Title: Thedit
 */
public class Card7_045 extends AbstractAlien {
    public Card7_045() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "Thedit", Uniqueness.UNIQUE);
        setLore("Organizes perimeter patrols for Kalit's territory. Inspiring leader. Keeps a watchful eye for krayt dragons, Tusken Raiders, and Wittin's bandits.");
        setGameText("Deploys only on Tatooine. When in a battle with at least two of your other Jawas, adds one battle destiny. When your total battle destiny at Thedit's site is greater than opponent's total battle destiny, Thedit reduces attrition against you by 3.");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.JAWA);
        addPersona(Persona.THEDIT);
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

        if(GameConditions.canSpot(game, self, Filters.Thedit)) {
            final PhysicalCard thedit = Filters.findFirstActive(game, self, Filters.Thedit);
            // Check condition(s)
            if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                    && GameConditions.isDuringBattleAt(game, Filters.sameSite(thedit))
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
        }
        return null;
    }
}
