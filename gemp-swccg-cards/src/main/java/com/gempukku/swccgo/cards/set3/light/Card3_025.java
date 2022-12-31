package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Wes Janson
 */
public class Card3_025 extends AbstractRebel {
    public Card3_025() {
        super(Side.LIGHT, 2, 2, 2, 2, 5, "Wes Janson", Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.R2);
        setLore("Served for many years as a gunner with friend Jek Porkins in the Tierfon Yellow Aces. Expert marksmanship helped the Alliance earn some of its earliest victories.");
        setGameText("Adds 1 to weapon destiny draws of anything he is aboard as a passenger (adds 3 if aboard Rogue 3 or with Wedge or Jek).");
        addIcons(Icon.HOTH, Icon.WARRIOR);
        addKeywords(Keyword.ROGUE_SQUADRON, Keyword.GUNNER);
        setMatchingVehicleFilter(Filters.Rogue_3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, Filters.hasPassenger(self), new ConditionEvaluator(1, 3,
                new OrCondition(new AboardCondition(self, Filters.Rogue_3), new WithCondition(self, Filters.or(Filters.Wedge, Filters.Jek))))));
        return modifiers;
    }
}
