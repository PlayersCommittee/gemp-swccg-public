package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Bib Fortuna
 */
public class Card6_098 extends AbstractAlien {
    public Card6_098() {
        super(Side.DARK, 1, 3, 3, 1, 4, Title.Bib, Uniqueness.UNIQUE);
        setLore("Twi'lek leader and majordomo of Jabba's palace. Succeeded Jabba's last majordomo, Naroon Cuthus. Plotting to kill Jabba.");
        setGameText("Deploys only on Tatooine. Opponent's characters of ability < 4 may not move from same site as Bib to a Jabba's Palace site. When at a Jabba's Palace site, each of you Gamorreans and Niktos may deploy as a 'react' to any Jabba's Palace site.");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.TWILEK);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveFromLocationToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.character, Filters.abilityLessThan(4)),
                Filters.sameSite(self), Filters.Jabbas_Palace_site));
        modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "Deploy a Gamorrean or Nikto as a 'react'",
                new AtCondition(self, Filters.Jabbas_Palace_site), playerId, Filters.or(Filters.Gamorrean, Filters.Nikto),
                Filters.Jabbas_Palace_site));
        return modifiers;
    }
}
