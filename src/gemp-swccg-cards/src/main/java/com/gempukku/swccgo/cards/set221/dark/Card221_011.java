package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.LostInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeConvertedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Location
 * Subtype: Site
 * Title: Cloud City: Chasm Walkway (V)
 */
public class Card221_011 extends AbstractSite {
    public Card221_011() {
        super(Side.DARK, Title.Chasm_Walkway, Title.Bespin, Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("If Vader's Malediction on table and Vader alone here, opponent's Interrupts are Lost Interrupts.");
        setLocationLightSideGameText("If you occupy, opponent's Chasm Walkway game text here is canceled. May not be converted.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.CLOUD_CITY, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_21);
        addKeywords(Keyword.CLOUD_CITY_LOCATION);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new LostInterruptModifier(self, Filters.and(Filters.opponents(playerOnDarkSideOfLocation), Filters.Interrupt), new AndCondition(new OnTableCondition(self, Filters.title(Title.Vaders_Malediction)), new HereCondition(self, Filters.and(Filters.Vader, Filters.alone)))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.and(Filters.Chasm_Walkway, Filters.here(self)), new OccupiesCondition(playerOnLightSideOfLocation, self), game.getOpponent(playerOnLightSideOfLocation)));
        modifiers.add(new MayNotBeConvertedModifier(self));
        return modifiers;
    }
}