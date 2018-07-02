package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Desert Landing Site
 */
public class Card11_092 extends AbstractSite {
    public Card11_092() {
        super(Side.DARK, "Tatooine: Desert Landing Site", Title.Tatooine);
        setLocationDarkSideGameText("Maul deploys free here. If Maul here and If The Trace Was Correct on table, Force drain +2 here.");
        setLocationLightSideGameText("If Amidala at a battleground site (or captive), opponent may not Force drain here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.DESERT);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Maul, self));
        modifiers.add(new ForceDrainModifier(self, new AndCondition(new HereCondition(self, Filters.Maul),
                new OnTableCondition(self, Filters.If_The_Trace_Was_Correct)), 2, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition amidalaAtBattlegroundSiteOrCaptive = new OnTableCondition(self, SpotOverride.INCLUDE_CAPTIVE,
                Filters.and(Filters.Amidala, Filters.or(Filters.captive, Filters.at(Filters.battleground_site))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, amidalaAtBattlegroundSiteOrCaptive, game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }
}