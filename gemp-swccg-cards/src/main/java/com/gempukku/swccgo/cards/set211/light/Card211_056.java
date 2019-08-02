package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Virtual Set 11
 * Type: Character
 * Subtype: Rebel
 * Title: •••Rebel Trooper Reinforcements
 */
public class Card211_056 extends AbstractRebel {
    public Card211_056() {
        super(Side.LIGHT, 3, 4, 3, 2, 5,"Rebel Trooper Reinforcements", Uniqueness.RESTRICTED_3);
        setLore("Rebels rely on hidden 'cells' of undercover operatives. Striking from hidden bases, troops or starfighters can arrive in a battle zone at any time.");
        setGameText("May deploy as a 'react' to a site. Deploys -2 to a site where opponent has more characters than you. Ambush is a Used Interrupt.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_11);
        addKeywords(Keyword.TROOPER);
        setArmor(4);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        Filter siteWithMoreOpponentsCharacters = Filters.and(Filters.site, Filters.wherePlayerHasFewerCharacters(self, self.getOwner()));
        modifiers.add(new DeployCostToLocationModifier(self, -2, siteWithMoreOpponentsCharacters));
        modifiers.add(new MayDeployAsReactModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new UsedInterruptModifier(self, Filters.title(Title.Ambush)));
        return modifiers;
    }
}