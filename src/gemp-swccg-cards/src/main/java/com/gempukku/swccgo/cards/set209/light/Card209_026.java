package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractSite;
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
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromCostModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitToCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Location
 * Subtype: Site
 * Title: Scarif: Landing Pad Nine (Docking Bay)
 */
public class Card209_026 extends AbstractSite {
    public Card209_026() {
        super(Side.LIGHT, Title.Scarif_Landing_Pad_Nine, Title.Scarif, Uniqueness.UNIQUE, ExpansionSet.SET_9, Rarity.V);
        setLocationDarkSideGameText("Your docking bay transit from here requires 2 Force. Your combat vehicles deploy +1 here.");
        setLocationLightSideGameText("Your docking bay transit to or from here requires +1 Force (+4 Force if Shield Gate on table).");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.EXTERIOR_SITE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_9);
        addKeywords(Keyword.DOCKING_BAY);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, 2, playerOnDarkSideOfLocation));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.combat_vehicle), 1, Filters.Scarif_Docking_Bay));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition shieldGateOnTableCondition = new OnTableCondition(self, Filters.Shield_Gate);
        modifiers.add(new DockingBayTransitFromCostModifier(self, new ConditionEvaluator(1, 4, shieldGateOnTableCondition), playerOnLightSideOfLocation));
        modifiers.add(new DockingBayTransitToCostModifier(self, new ConditionEvaluator(1, 4, shieldGateOnTableCondition), playerOnLightSideOfLocation));
        return modifiers;
    }
}