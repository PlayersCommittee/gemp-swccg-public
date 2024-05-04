package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedBySpecificWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Mos Eisley (V)
 */
public class Card221_037 extends AbstractSite {
    public Card221_037() {
        super(Side.DARK, Title.Mos_Eisley, Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("Imperial stormtroopers here are forfeit +1 (and power +1 if a sandtrooper).");
        setLocationLightSideGameText("Imperial stormtroopers here may not be targeted by Clash Of Sabers or blasters.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.Imperial, Filters.stormtrooper, Filters.here(self)), 1));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Imperial, Filters.stormtrooper, Filters.here(self), Filters.sandtrooper), 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.and(Filters.Imperial, Filters.stormtrooper, Filters.here(self)), Filters.Clash_Of_Sabers));
        modifiers.add(new MayNotBeTargetedBySpecificWeaponsModifier(self, Filters.and(Filters.Imperial, Filters.stormtrooper, Filters.here(self)), Filters.blaster));
        return modifiers;
    }
}