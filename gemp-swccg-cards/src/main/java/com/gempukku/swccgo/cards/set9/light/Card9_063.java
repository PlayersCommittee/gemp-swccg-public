package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireAnyNumberOfWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Blue Squadron 5
 */
public class Card9_063 extends AbstractStarfighter {
    public Card9_063() {
        super(Side.LIGHT, 2, 2, 4, null, 2, 3, 4, Title.Blue_Squadron_5, Uniqueness.UNIQUE);
        setLore("Blue Squadron B-Wing. Ordered to lead attack on Executor. Drew enemy fighters away from strike force led by General Calrissian and Commander Antilles.");
        setGameText("May add 1 pilot. May fire two or more weapons during battle. Each of its weapon destiny draws is +2. Immune to attrition < 4 when Ten Numb piloting.");
        addIcons(Icon.DEATH_STAR_II, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.B_WING);
        addKeywords(Keyword.BLUE_SQUADRON);
        setPilotCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayFireAnyNumberOfWeaponsModifier(self, new DuringBattleCondition()));
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Ten_Numb), 4));
        return modifiers;
    }
}
