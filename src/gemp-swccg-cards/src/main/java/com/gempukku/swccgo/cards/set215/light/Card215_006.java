package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AbilityRequiredForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Location
 * Subtype: Site
 * Title: Death Star: Central Core (Light)
 */
public class Card215_006 extends AbstractSite {
    public Card215_006() {
        super(Side.LIGHT, Title.Death_Star_Central_Core, Title.Death_Star, Uniqueness.UNIQUE, ExpansionSet.SET_15, Rarity.V);
        setLocationDarkSideGameText("While Obi-Wan here, total ability of 6 or more required for you to draw battle destiny here.");
        setLocationLightSideGameText("While opponent occupies, you may not play Blast The Door, Kid! during battles on Death Star.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_15);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        
        String opponent = game.getOpponent(playerOnLightSideOfLocation);
        Condition opponentOccupies = new OccupiesCondition(opponent, self);
        Condition duringBattleOnDeathStar = new DuringBattleAtCondition(Filters.Death_Star_site);
        modifiers.add(new MayNotPlayModifier(self, Filters.Blast_The_Door_Kid, new AndCondition(opponentOccupies, duringBattleOnDeathStar), playerOnLightSideOfLocation));

        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();

        Condition obiWanHere = new HereCondition(self, Filters.ObiWan);
        modifiers.add(new AbilityRequiredForBattleDestinyModifier(self, self, obiWanHere, 6, playerOnDarkSideOfLocation));
        
        return modifiers;
    }
}
