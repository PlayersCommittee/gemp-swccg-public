package com.gempukku.swccgo.cards.set112.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Jabba's Palace Sealed Deck)
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Hutt Trade Route (Desert)
 */
public class Card112_009 extends AbstractSite {
    public Card112_009() {
        super(Side.LIGHT, Title.Hutt_Trade_Route, Title.Tatooine);
        setLocationDarkSideGameText("Unless you control Hutt Trade Route, your game text on Tatooine: Jabba's Palace is canceled.");
        setLocationLightSideGameText("Your characters here aboard vehicles are each power +1.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.PREMIUM, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.DESERT);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.Jabbas_Palace,
                new UnlessCondition(new ControlsCondition(playerOnDarkSideOfLocation, Filters.Hutt_Trade_Route)), playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.character,
                Filters.here(self), Filters.aboardAnyVehicle), 1));
        return modifiers;
    }
}