package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Location
 * Subtype: Site
 * Title: Scarif: Data Vault
 */
public class Card209_025 extends AbstractSite {
    public Card209_025() {
        super(Side.LIGHT, Title.DataVault, Title.Scarif);
        setLocationLightSideGameText("While opponent occupies, Stardust (and any character it is on) may not move from here.");
        setLocationDarkSideGameText("All immunity to attrition (and Ephant Mon's game text) here is canceled.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_9);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter stardustAndCarrier = Filters.or(Filters.Stardust, Filters.hasAttached(Filters.Stardust));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition whileOpponentOccupies = new AndCondition(new OccupiesCondition(game.getDarkPlayer(), self));
        modifiers.add(new MayNotMoveFromLocationModifier(self, stardustAndCarrier, whileOpponentOccupies, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelImmunityToAttritionModifier(self, Filters.here(self)));
        modifiers.add(new CancelsGameTextModifier(self, Filters.Ephant_Mon));
        return modifiers;
    }
}