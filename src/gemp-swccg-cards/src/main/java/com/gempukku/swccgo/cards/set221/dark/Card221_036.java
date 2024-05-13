package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.DeployOnlyUsingOwnForceToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ResetDeployCostToLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Jawa Camp (V)
 */
public class Card221_036 extends AbstractSite {
    public Card221_036() {
        super(Side.DARK, Title.Jawa_Camp, Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("If your trooper here and no aliens on Tatooine, Force drain +1 at Jawa Camp.");
        setLocationLightSideGameText("Jawas deploy here for 1 Force from their owner only. Obi-Wan moves to here for free.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, Filters.Jawa_Camp,
                new AndCondition(new HereCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.trooper)), new NotCondition(new OnTableCondition(self, Filters.and(Filters.alien, Filters.on(Title.Tatooine))))),
                1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployOnlyUsingOwnForceToLocationModifier(self, Filters.Jawa, self));
        modifiers.add(new ResetDeployCostToLocationModifier(self, Filters.Jawa, 1, self));
        modifiers.add(new MovesFreeToLocationModifier(self, Filters.ObiWan, Filters.here(self)));
        return modifiers;
    }
}