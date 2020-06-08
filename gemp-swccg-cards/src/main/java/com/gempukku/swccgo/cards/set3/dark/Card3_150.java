package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Location
 * Subtype: Site
 * Title: Hoth: Wampa Cave (7th Marker)
 */
public class Card3_150 extends AbstractSite {
    public Card3_150() {
        super(Side.DARK, Title.Wampa_Cave, Title.Hoth);
        setLocationDarkSideGameText("Your Wampas deploy free here.");
        setLocationLightSideGameText("If you control, and Main Power Generators on table, Force drain -1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.HOTH, Icon.INTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.MARKER_7);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.wampa), self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new AndCondition(new ControlsCondition(playerOnLightSideOfLocation, self),
                new OnTableCondition(self, Filters.Main_Power_Generators)), -1, playerOnLightSideOfLocation));
        return modifiers;
    }
}