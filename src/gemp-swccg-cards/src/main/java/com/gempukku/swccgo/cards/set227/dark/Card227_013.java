package com.gempukku.swccgo.cards.set227.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OccupiesWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Location
 * Subtype: Site
 * Title: Separatist Command Center
 */
public class Card227_013 extends AbstractSite {
    public Card227_013() {
        super(Side.DARK, Title.Separatist_Command_Center, Uniqueness.DIAMOND_1, ExpansionSet.SET_27, Rarity.V);
        setLocationDarkSideGameText("While you occupy with a non-unique [Presence] card, your non-unique [Presence] cards are power +1 at same and related sites.");
        setLocationLightSideGameText("Deploys only to a [Clone Army] or [Separatist] system. Immune to Ounee Ta.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_I, Icon.SCOMP_LINK, Icon.SEPARATIST, Icon.VIRTUAL_SET_27);
    }
    @Override
    public boolean mayNotBePartOfSystem(SwccgGame game, String system) {
        return Filters.filterTopLocationsOnTable(game, Filters.and(Filters.system, Filters.or(Icon.SEPARATIST, Icon.CLONE_ARMY), Filters.partOfSystem(system))).isEmpty();
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();

        Filter yourNonuniquePresenceCards = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.non_unique, Icon.PRESENCE);
        Filter yourNonuniquePresenceCardsAtSameAndRelatedSites = Filters.and(yourNonuniquePresenceCards, Filters.atSameOrRelatedSite(self));

        Condition condition = new OccupiesWithCondition(playerOnDarkSideOfLocation, self, yourNonuniquePresenceCards);

        modifiers.add(new PowerModifier(self, yourNonuniquePresenceCardsAtSameAndRelatedSites, condition, 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToTitleModifier(self, Title.Ounee_Ta));
        return modifiers;
    }
}
