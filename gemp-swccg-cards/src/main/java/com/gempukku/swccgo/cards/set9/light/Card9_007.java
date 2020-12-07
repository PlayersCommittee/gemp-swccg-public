package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.FireWeaponCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Rebel
 * Title: Captain Verrack
 */
public class Card9_007 extends AbstractRebel {
    public Card9_007() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, "Captain Verrack", Uniqueness.UNIQUE);
        setLore("Mon Calamari computer technician. Expert in power systems. His skills are desperately needed by Rebel fleet. Gunner.");
        setGameText("Allows your starship weapons at same location to fire and deploy for 1 less Force. While aboard your capital starship, adds 1 to each of its weapon destiny draws (2 when targeting a capital starship).");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.GUNNER, Keyword.CAPTAIN);
        setSpecies(Species.MON_CALAMARI);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourCapitalStarshipAboard = Filters.and(Filters.your(self), Filters.capital_starship, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new FireWeaponCostModifier(self, Filters.and(Filters.your(self), Filters.starship_weapon, Filters.atSameLocation(self)), -1));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.starship_weapon), -1, Filters.sameLocation(self)));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, yourCapitalStarshipAboard, 1, Filters.not(Filters.capital_starship)));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, yourCapitalStarshipAboard, 2, Filters.capital_starship));
        return modifiers;
    }
}
