package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Character
 * Subtype: Imperial
 * Title: Snowtrooper Officer
 */
public class Card3_092 extends AbstractImperial {
    public Card3_092() {
        super(Side.DARK, 2, 3, 2, 2, 3, "Snowtrooper Officer", Uniqueness.RESTRICTED_3);
        setLore("Elite snowtrooper offers, like all Imperial Army officers, are well versed in Rebel tactics. They serve as infantry liaisons to AT-ATs.");
        setGameText("Deploy only on Hoth, but may move elsewhere. Snowtroopers are deploy -1 to same site. Your troopers may move to same Hoth site as a 'react.'");
        addIcons(Icon.HOTH, Icon.WARRIOR);
        addKeywords(Keyword.SNOWTROOPER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Hoth;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.snowtrooper, -1, Filters.sameSite(self)));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move trooper as a 'react'", new AtCondition(self, Filters.Hoth_site),
                self.getOwner(), Filters.and(Filters.your(self), Filters.trooper), Filters.sameSite(self)));
        return modifiers;
    }
}
