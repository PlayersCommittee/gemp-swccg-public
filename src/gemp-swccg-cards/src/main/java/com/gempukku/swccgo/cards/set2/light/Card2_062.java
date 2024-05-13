package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpecialRule;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Location
 * Subtype: Site
 * Title: Death Star: Trench
 */
public class Card2_062 extends AbstractSite {
    public Card2_062() {
        super(Side.LIGHT, Title.Death_Star_Trench, Title.Death_Star, Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.R2);
        setLocationDarkSideGameText("'Trench Rules' in effect, your weapons fire free and Turbolaser Battery may deploy here.");
        setLocationLightSideGameText("'Trench Rules' in effect and each of your starfighters is maneuver -2 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.A_NEW_HOPE, Icon.EXTERIOR_SITE, Icon.MOBILE);
        addSpecialRulesInEffectHere(SpecialRule.TRENCH_RULES);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new FiresForFreeModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.weapon, Filters.here(self))));
        modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.Turbolaser_Battery), self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ManeuverModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starfighter, Filters.here(self)), -2));
        return modifiers;
    }
}