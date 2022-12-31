package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Tala 1
 */
public class Card9_084 extends AbstractStarfighter {
    public Card9_084() {
        super(Side.LIGHT, 3, 2, 2, null, 4, 3, 3, Title.Tala_1, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("Heavily modified Headhunter. Operationally assigned to support Madine's commando team. Hyperdrive added to allow it to operate independently of capital starship support.");
        setGameText("May add 1 pilot. May be carried like a vehicle. Power and maneuver +2 at opponent's system (except battlegrounds) or at any sector. Immune to attrition < 4 when Colonel Cracken piloting.");
        addIcons(Icon.DEATH_STAR_II, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.MODIFIED_Z_95_HEADHUNTER);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Cracken);
    }

    @Override
    public boolean isVehicleSlotOfStarshipCompatible() {
        return true;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        Condition atSystemOrSector = new AtCondition(self, Filters.or(Filters.and(Filters.opponents(self), Filters.system,
                Filters.except(Filters.battleground)), Filters.sector));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, atSystemOrSector, 2));
        modifiers.add(new ManeuverModifier(self, atSystemOrSector, 2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Cracken), 4));
        return modifiers;
    }
}
