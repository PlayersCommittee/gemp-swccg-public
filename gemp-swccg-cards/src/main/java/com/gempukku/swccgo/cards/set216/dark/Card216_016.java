package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.ArrayList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Location
 * Subtype: Site
 * Title: Scarif: Command Center
 */
public class Card216_016 extends AbstractSite {
    public Card216_016() {
        super(Side.DARK, "Scarif: Command Center", Title.Scarif);
        setLocationDarkSideGameText("If an Imperial leader here, add one destiny to your [Set 9] Epic Event total targeting a Scarif site.");
        setLocationLightSideGameText("If Shield Gate on table, opponent's Force drains here are +1.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.VIRTUAL_SET_16, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Icon.VIRTUAL_SET_9, Filters.Epic_Event), new HereCondition(self, Filters.Imperial_leader), ModifyGameTextType.COMMENCE_PRIMARY_IGNITION__ADDS_A_DESTINY_TO_TOTAL));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new ForceDrainModifier(self, new OnTableCondition(self, Filters.Shield_Gate), 1, game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }
}