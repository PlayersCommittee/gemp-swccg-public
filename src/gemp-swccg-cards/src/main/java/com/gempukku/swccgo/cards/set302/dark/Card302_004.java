package com.gempukku.swccgo.cards.set302.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Character
 * Subtype: Imperial
 * Title: Brotherhood Trooper
 */
public class Card302_004 extends AbstractImperial {
    public Card302_004() {
        super(Side.DARK, 2, 2, 2, 2, 2, "Brotherhood Trooper", Uniqueness.UNRESTRICTED, ExpansionSet.DJB_CORE, Rarity.V);
        setLore("Loyal to the throne, the Brotherhood's elite troopers serve the Council without question. While their primary duty is defending the throne they're available to any Clan...for a price.");
        setGameText("Deploys free to same site as one of your Imperials with ability > 2.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.STORMTROOPER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.Imperial, Filters.abilityMoreThan(2)))));
        return modifiers;
    }
}
