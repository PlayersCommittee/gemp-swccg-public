package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Jawa Camp
 */
public class Card1_292 extends AbstractSite {
    public Card1_292() {
        super(Side.DARK, Title.Jawa_Camp, Title.Tatooine);
        setLocationDarkSideGameText("Your Jawas deploy here for 1 Force from you only.");
        setLocationLightSideGameText("All your Jawas are power and forfeit -1 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourJawas = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.Jawa);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployOnlyUsingOwnForceToLocationModifier(self, yourJawas, self));
        modifiers.add(new ResetDeployCostToLocationModifier(self, yourJawas, 1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourJawasHere = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Jawa, Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, yourJawasHere, -1));
        modifiers.add(new ForfeitModifier(self, yourJawasHere, -1));
        return modifiers;
    }
}