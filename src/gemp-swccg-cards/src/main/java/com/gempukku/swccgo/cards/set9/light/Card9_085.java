package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Tala 2
 */
public class Card9_085 extends AbstractStarfighter {
    public Card9_085() {
        super(Side.LIGHT, 2, 1, 2, null, 4, 3, 3, Title.Tala_2, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("Equipped with sophisticated sensor suite to monitor Imperial activity during Rebel commando operations. Hyperdrive allows long-range reconnaissance.");
        setGameText("May add 1 pilot. May be carried like a vehicle. When your spy aboard, opponent's spies and scouts are deploy +2 to related sites. Immune to attrition < 4 when Lieutenant Blount piloting.");
        addIcons(Icon.DEATH_STAR_II, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.MODIFIED_Z_95_HEADHUNTER);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Lieutenant_Blount);
    }

    @Override
    public boolean isVehicleSlotOfStarshipCompatible() {
        return true;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.or(Filters.spy, Filters.scout)),
                new HasAboardCondition(self, Filters.and(Filters.your(self), Filters.spy)), 2, Filters.relatedSite(self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Lieutenant_Blount), 4));
        return modifiers;
    }
}
