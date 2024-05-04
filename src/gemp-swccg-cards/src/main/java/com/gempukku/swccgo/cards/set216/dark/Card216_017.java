package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromCostModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Location
 * Subtype: Site
 * Title: Scarif: Landing Pad Five (Docking Bay)
 */
public class Card216_017 extends AbstractSite {
    public Card216_017() {
        super(Side.DARK, Title.Scarif_Landing_Pad_Five, Title.Scarif, Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setLocationDarkSideGameText("Your docking bay transit from here requires 1 Force (free if an Imperial leader here).");
        setLocationLightSideGameText("Your docking bay transit from here requires 3 Force (7 if Shield Gate on table).");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.PLANET, Icon.EXTERIOR_SITE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_16);
        addKeywords(Keyword.DOCKING_BAY);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition imperialLeaderHere = new HereCondition(self, Filters.Imperial_leader);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, 1, playerOnDarkSideOfLocation));
        modifiers.add(new DockingBayTransitFromForFreeModifier(self, imperialLeaderHere, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Condition shieldGateOnTableCondition = new OnTableCondition(self, Filters.Shield_Gate);
        modifiers.add(new DockingBayTransitFromCostModifier(self, new ConditionEvaluator(3, 7, shieldGateOnTableCondition), playerOnLightSideOfLocation));
        return modifiers;
    }
}