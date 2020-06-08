package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
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
 * Subtype: Rebel
 * Title: Echo Base Trooper Officer
 */
public class Card3_007 extends AbstractRebel {
    public Card3_007() {
        super(Side.LIGHT, 2, 3, 2, 2, 3, "Echo Base Trooper Officer", Uniqueness.RESTRICTED_3);
        setLore("Like many Rebel specialists, Trey Callum defected from the Imperial officer corps. Trooper officers are masters at stretching the meager resources of the Rebellion.");
        setGameText("Deploy only on Hoth but may move elsewhere. Echo Base Troopers deploy -1 to same site. Your troopers may move to same Hoth site as a 'react.'");
        addIcons(Icon.HOTH, Icon.WARRIOR);
        addKeywords(Keyword.ECHO_BASE_TROOPER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Hoth;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Echo_Base_trooper, -1, Filters.sameSite(self)));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move trooper as a 'react'", new AtCondition(self, Filters.Hoth_site),
                self.getOwner(), Filters.and(Filters.your(self), Filters.trooper), Filters.sameSite(self)));
        return modifiers;
    }
}
