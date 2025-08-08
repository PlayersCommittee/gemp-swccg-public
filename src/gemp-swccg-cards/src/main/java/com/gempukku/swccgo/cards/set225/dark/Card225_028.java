package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Location
 * Subtype: Site
 * Title: Supremacy: Bridge
 */
public class Card225_028 extends AbstractUniqueStarshipSite {
    public Card225_028() {
        super(Side.DARK, Title.Supremacy_Bridge, Persona.SUPREMACY, ExpansionSet.SET_25, Rarity.V);
        setLocationDarkSideGameText("While you control, Supremacy is power +3.");
        setLocationLightSideGameText("Unless your piloted [Resistance] starship at a system, force drain -1.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.EPISODE_VII, Icon.VIRTUAL_SET_25);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, Filters.Supremacy, new ControlsCondition(playerOnDarkSideOfLocation, self), 3));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, self, new UnlessCondition(new AtCondition(self, Filters.and(Filters.piloted, Filters.starship, Filters.resistance), 
                        Filters.system)), -1, playerOnLightSideOfLocation));
        return modifiers;
    }
}