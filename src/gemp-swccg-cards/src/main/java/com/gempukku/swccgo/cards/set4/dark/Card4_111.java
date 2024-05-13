package com.gempukku.swccgo.cards.set4.dark;

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
 * Title: Sleen
 */
public class Card4_111 extends AbstractCreature {
    public Card4_111() {
        super(Side.DARK, 3, 2, null, 2, 0, "Sleen", Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.C);
        setLore("Slow, omnivorous swamp forager. Eats insects. Seeks damp, dark environments strong with the Force.");
        setGameText("* Ferocity = destiny - 3. Habitat: planet sites (except Hoth and Tatooine) and Dark Waters. Cumulatively absorbs (temporarily cancels) one [Light Side Force] icon present. Parasite: None.");
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
        modifiers.add(new DefinedByGameTextFerocityModifier(self, -3, 1));
        modifiers.add(new CancelForceIconModifier(self, Filters.wherePresent(self), 1, Icon.LIGHT_FORCE, true));
        return modifiers;
    }
}
