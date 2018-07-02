package com.gempukku.swccgo.cards.set204.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Character
 * Subtype: Imperial
 * Title: Stormtrooper Patrol
 */
public class Card204_044 extends AbstractImperial {
    public Card204_044() {
        super(Side.DARK, 3, 3, 3, 2, 4, "Stormtrooper Patrol", Uniqueness.RESTRICTED_3);
        setArmor(4);
        setLore("Imperial stormtroopers adopt strict security measures. Excellent communications and sheer numbers can hinder Rebel movement across entire territories.");
        setGameText("May deploy as a 'react' to a site. Each other stormtrooper present is power +1. Rebels are deploy +1 to same site.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_4);
        addKeywords(Keyword.STORMTROOPER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.site));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.other(self), Filters.stormtrooper, Filters.present(self)), 1));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Rebel, 1, Filters.sameSite(self)));
        return modifiers;
    }
}
