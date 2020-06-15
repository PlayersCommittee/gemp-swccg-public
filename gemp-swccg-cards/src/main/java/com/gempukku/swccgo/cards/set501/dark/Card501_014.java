package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalAbilityForBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Location
 * Subtype: Site
 * Title: Executor: Meditation Chamber (v)
 */
public class Card501_014 extends AbstractUniqueStarshipSite {
    public Card501_014() {
        super(Side.DARK, Title.Meditation_Chamber, Persona.EXECUTOR);
        setLocationDarkSideGameText("While your Imperial here, Executor is deploy - 7 (except to Fondor).");
        setLocationLightSideGameText("Total ability of 6 or more required for either player to draw battle destiny here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.DAGOBAH, Icon.INTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_13);
        setVirtualSuffix(true);
        setTestingText("Executor: Meditation Chamber (v)");
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Executor, new HereCondition(self, Filters.Imperial), -7, Filters.not(Filters.Fondor)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new TotalAbilityForBattleDestinyModifier(self, self, 6, playerOnLightSideOfLocation));
        modifiers.add(new TotalAbilityForBattleDestinyModifier(self, self, 6, game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }
}