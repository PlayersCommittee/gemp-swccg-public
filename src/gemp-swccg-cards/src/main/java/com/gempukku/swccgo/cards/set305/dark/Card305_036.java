package com.gempukku.swccgo.cards.set305.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.PilotedCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A Better Tomorrow
 * Type: Vehicle
 * Subtype: Combat
 * Title: NR-N99 Persuader-Class Tank Droid
 */
public class Card305_036 extends AbstractCombatVehicle {
    public Card305_036() {
        super(Side.DARK, 2, 4, 5, 4, null, 2, 5, "NR-N99 Persuader-Class Tank Droid k", Uniqueness.RESTRICTED_3, ExpansionSet.ABT, Rarity.U);
        setLore("Manufactured by the Techno Union. They were primarily utilized by the Corporate Alliance. Provided similar firepower to the AAT favored by the Trade Federation but with improved speed.");
        setGameText("May carry 4 battle droids. While at same site as a battle droid, adds two battle destiny. May deploy AAT Laser Cannon aboard. Immune to attrition < 4.");
        addModelType(ModelType.NRN99);
        addIcons(Icon.ABT, Icon.PILOT, Icon.PRESENCE);
        setVehicleCapacity(4, Filters.battle_droid);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot() {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter aatLaserCannon = Filters.and(Filters.your(self), Filters.AAT_Laser_Cannon);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new PilotedCondition(self),
                new AtSameSiteAsCondition(self, Filters.battle_droid)), 2));
        modifiers.add(new MayDeployToTargetModifier(self, aatLaserCannon, self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
