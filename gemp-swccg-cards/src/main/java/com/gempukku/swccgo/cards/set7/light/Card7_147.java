package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasMatchingPilotAboardCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToSystemModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyForWeaponFiredByModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Starship
 * Subtype: Starfighter
 * Title: Red 8
 */
public class Card7_147 extends AbstractStarfighter {
    public Card7_147() {
        super(Side.LIGHT, 2, 2, 3, null, 4, 5, 5, Title.Red_8, Uniqueness.UNIQUE);
        setLore("X-wing assigned to Bren Quersey by Wedge Antilles. Scored a hit on Black 2.");
        setGameText("Deploys free to Raithal. May add 1 pilot. Adds 1 to total weapon destiny when firing X-wing Laser Cannons. Immune to attrition < 4 when matching pilot aboard.");
        addIcons(Icon.SPECIAL_EDITION, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.RED_SQUADRON);
        addModelType(ModelType.X_WING);
        setPilotCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToSystemModifier(self, Title.Raithal));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalWeaponDestinyForWeaponFiredByModifier(self, 1, Filters.X_wing_Laser_Cannon));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasMatchingPilotAboardCondition(self), 4));
        return modifiers;
    }
}
