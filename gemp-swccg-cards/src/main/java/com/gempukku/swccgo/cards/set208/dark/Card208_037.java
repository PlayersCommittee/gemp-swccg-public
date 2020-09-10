package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractFirstOrder;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 8
 * Type: Character
 * Subtype: First Order
 * Title: Captain Peavey
 */
public class Card208_037 extends AbstractFirstOrder {
    public Card208_037() {
        super(Side.DARK, 2, 3, 2, 2, 5, "Captain Peavey", Uniqueness.UNIQUE);
        setLore("Leader.");
        setGameText("[Pilot] 3. Deploys -1 to Finalizer. While piloting Finalizer, it is immune to attrition < 8 (< 10 while with a Resistance character or [Resistance] starship). May be targeted by Imperial Command as an admiral (even if a unit of Force).");
        addIcons(Icon.EPISODE_VII, Icon.PILOT, Icon.VIRTUAL_SET_8);
        addKeywords(Keyword.LEADER, Keyword.CAPTAIN);
        setMatchingStarshipFilter(Filters.Finalizer);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostAboardModifier(self, -1, Filters.Finalizer));
        modifiers.add(new MayBeTargetedByModifier(self, Title.Imperial_Command));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.Finalizer, Filters.hasPiloting(self)),
                new ConditionEvaluator(8, 10, new WithCondition(self, Filters.or(Filters.Resistance_character, Filters.and(Filters.starship, Icon.RESISTANCE))))));
        return modifiers;
    }
}
