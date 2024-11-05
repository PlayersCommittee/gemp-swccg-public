package com.gempukku.swccgo.cards.set302.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToSystemModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyForWeaponFiredByModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Starship
 * Subtype: Starfighter
 * Title: T-85 X-Wing Fighter
 */
public class Card302_014 extends AbstractStarfighter {
    public Card302_014() {
        super(Side.LIGHT, 3, 3, 4, null, 4, 5, 4, "T-85 X-Wing Fighter", Uniqueness.RESTRICTED_3, ExpansionSet.DJB_CORE, Rarity.V);
        setLore("Incom-FreiTek Corporation's T-85 X-wing starfighter was an advanced version of the old T-65B X-wing starfighter. This model is the primary starfighter for the New Republic.");
        setGameText("May add 1 pilot and 1 astromech. Adds 1 to total weapon destiny when firing X-wing Laser Cannons. Proton Torpedoes deploy and fire free aboard.");
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.X_WING);
        setPilotCapacity(1);
		setAstromechCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.Proton_Torpedoes, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalWeaponDestinyForWeaponFiredByModifier(self, 1, Filters.X_wing_Laser_Cannon));
		modifiers.add(new FiresForFreeModifier(self, Filters.and(Filters.Proton_Torpedoes, Filters.attachedTo(self))));
        return modifiers;
    }
}
