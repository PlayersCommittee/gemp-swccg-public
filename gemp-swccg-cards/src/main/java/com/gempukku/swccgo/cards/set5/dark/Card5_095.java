package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Character
 * Subtype: Alien
 * Title: Cloud City Trooper
 */
public class Card5_095 extends AbstractAlien {
    public Card5_095() {
        super(Side.DARK, 3, 2, 2, 1, 3, "Cloud City Trooper", Uniqueness.UNRESTRICTED, ExpansionSet.CLOUD_CITY, Rarity.C);
        setLore("Officially titled Cloud City Wing Guards. Some are holdovers from previous administrations which were more totalitarian. A moderate pay scale breeds corruption.");
        setGameText("Deploys only on Cloud City, but may move elsewhere. Power -1 when at a site other than a Cloud City site.");
        addIcons(Icon.CLOUD_CITY, Icon.WARRIOR);
        addKeywords(Keyword.CLOUD_CITY_TROOPER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Cloud_City;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.and(Filters.site, Filters.not(Filters.Cloud_City_site))), -1));
        return modifiers;
    }
}
