package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Rebel
 * Title: Incom Engineer
 */
public class Card7_022 extends AbstractRebel {
    public Card7_022() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "Incom Engineer");
        setLore("After narrowly escaping the nationalization of Incom by the Empire, many former employees joined the Rebellion. They spent long hours maintaining X-wings and T-47s.");
        setGameText("At same and related locations, adds 2 to your total battle destiny where your X-wing, T-47, T-16 and Z-95 is present and makes those vehicles and starships immune to attrition < 3 (< 5 if he is present with your maintenance droid).");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalBattleDestinyModifier(self, new DuringBattleAtCondition(Filters.and(Filters.sameOrRelatedLocation(self),
                Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.or(Filters.X_wing, Filters.T_47, Filters.T_16, Filters.Z_95))))),
                2, self.getOwner(), true));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.X_wing,
                Filters.T_47, Filters.T_16, Filters.Z_95), Filters.presentAt(Filters.sameOrRelatedLocation(self))),
                new ConditionEvaluator(3, 5, new PresentWithCondition(self, Filters.and(Filters.your(self), Filters.maintenance_droid)))));
        return modifiers;
    }
}
