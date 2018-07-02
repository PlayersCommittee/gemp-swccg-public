package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.DefendingBattleCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Character
 * Subtype: Alien
 * Title: Trooper Utris M'toc
 */
public class Card5_010 extends AbstractAlien {
    public Card5_010() {
        super(Side.LIGHT, 2, 3, 2, 2, 4, "Trooper Utris M'toc", Uniqueness.UNIQUE);
        setLore("Imzig Cloud City trooper and Wing Guard leader. Attempts to protect Cloud City citizens from less reputable members of local constabulary. Never accepts a bribe.");
        setGameText("Deploys only on Cloud City, but may move elsewhere. Deploys free to same Cloud City site as Lando. Power +2 when defending a battle. Your other Cloud City troopers are forfeit +2 where present.");
        addIcons(Icon.CLOUD_CITY, Icon.WARRIOR);
        addKeywords(Keyword.CLOUD_CITY_TROOPER, Keyword.LEADER);
        setSpecies(Species.IMZIG);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Cloud_City;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.and(Filters.Cloud_City_site, Filters.sameSiteAs(self, Filters.Lando))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new DefendingBattleCondition(self), 2));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.Cloud_City_trooper,
                Filters.at(Filters.wherePresent(self))), 2));
        return modifiers;
    }
}
