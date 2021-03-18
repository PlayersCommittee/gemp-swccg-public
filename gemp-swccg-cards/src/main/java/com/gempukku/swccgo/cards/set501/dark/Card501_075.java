package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Location
 * Subtype: Site
 * Title: Scarif: Beach
 */
public class Card501_075 extends AbstractSite {
    public Card501_075() {
        super(Side.DARK, "Scarif: Beach", Title.Scarif);
        setLocationLightSideGameText("You may not deploy Effects on this site. Your combat vehicles and spies are forfeit +1 here.");
        setLocationDarkSideGameText("Krennic and death troopers are power +1 here.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_14);
        setTestingText("Scarif: Beach");
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToTargetModifier(self, Filters.and(Filters.owner(playerOnLightSideOfLocation), Filters.Effect), self));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.or(Filters.spy, Filters.combat_vehicle)), 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.or(Keyword.DEATH_TROOPER, Filters.Krennic), 1));
        return modifiers;
    }
}