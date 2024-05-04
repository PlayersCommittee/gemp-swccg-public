package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractCreature;
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
import com.gempukku.swccgo.logic.modifiers.CancelForceIconModifier;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextFerocityModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Creature
 * Title: Nudj
 */
public class Card4_005 extends AbstractCreature {
    public Card4_005() {
        super(Side.LIGHT, 2, 1, null, 1, 0, "Nudj", Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.C);
        setLore("Tree-nesting reptile. Favors moist caves and marshes. Extremely docile, despite fearsome appearance. Responsive to Force energy.");
        setGameText("* Ferocity = destiny - 4. Habitat: planet sites (except Hoth and Tatooine) and Dark Waters. Cumulatively absorbs (temporarily cancels) one [Dark Side Force] icon present. Parasite: None.");
        addModelType(ModelType.SWAMP);
        addIcons(Icon.DAGOBAH, Icon.SELECTIVE_CREATURE);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.and(Filters.planet_site, Filters.not(Filters.or(Filters.Hoth_site, Filters.Tatooine_site))), Filters.sameSiteAs(self, Filters.Dark_Waters));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextFerocityModifier(self, -4, 1));
        modifiers.add(new CancelForceIconModifier(self, Filters.wherePresent(self), 1, Icon.DARK_FORCE, true));
        return modifiers;
    }
}
