package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.evaluators.InBattleEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Character
 * Subtype: Republic
 * Title: Daultay Dofine (V)
 */
public class Card219_003 extends AbstractRepublic {
    public Card219_003() {
        super(Side.DARK, 2, 3, 3, 3, 6, "Daultay Dofine", Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setVirtualSuffix(true);
        setLore("Neimoidian Trade Federation captain who gained his current position through political backstabbing and family connections. Not favored by Darth Sidious.");
        setGameText("[Pilot] 2. Your total battle destiny at sites is +1 for each of your participating " +
                    "[Presence] droids that has a [Permanent Weapon] weapon but did not fire it in that battle. " +
                    "Weapon destinies at same system are -1.");
        addPersona(Persona.DOFINE);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PILOT, Icon.VIRTUAL_SET_19);
        setSpecies(Species.NEIMOIDIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.site, new InBattleEvaluator(self,
                Filters.and(Icon.PRESENCE, Filters.droid, Icon.PERMANENT_WEAPON, Filters.didNotFireAPermanentWeaponThisBattle(true))), self.getOwner(), true));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.at(Filters.sameSystem(self)), -1));
        return modifiers;
    }
}
