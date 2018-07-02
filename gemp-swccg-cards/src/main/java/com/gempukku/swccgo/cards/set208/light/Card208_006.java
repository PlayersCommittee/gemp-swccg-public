package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Character
 * Subtype: Rebel
 * Title: General Carlist Rieekan (V)
 */
public class Card208_006 extends AbstractRebel {
    public Card208_006() {
        super(Side.LIGHT, 1, 3, 2, 2, 5, "General Carlist Rieekan", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("One of the original founders of the Rebel Alliance. Former civilian strategist with the House of Organa. Somber leader of Echo Base.");
        setGameText("Deploys -1 to a war room. While at a war room, your capital starships at Rebel Base systems may not be targeted by weapons and your Rebels and T-47s at same and related Rebel Base locations move for free to same or related locations.");
        addIcons(Icon.HOTH, Icon.WARRIOR, Icon.VIRTUAL_SET_8);
        addKeywords(Keyword.GENERAL, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.war_room));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition whileAtWarRoom = new AtCondition(self, Filters.war_room);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.and(Filters.your(self), Filters.capital_starship,
                Filters.at(Filters.Rebel_Base_system)), whileAtWarRoom));
        modifiers.add(new MovesFreeToLocationModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.Rebel, Filters.T_47),
                Filters.at(Filters.and(Filters.sameOrRelatedLocation(self), Filters.Rebel_Base_location))), whileAtWarRoom, Filters.sameOrRelatedLocation(self)));
        return modifiers;
    }
}
