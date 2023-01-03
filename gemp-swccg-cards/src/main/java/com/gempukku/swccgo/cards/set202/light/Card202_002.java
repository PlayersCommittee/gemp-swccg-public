package com.gempukku.swccgo.cards.set202.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 2
 * Type: Character
 * Subtype: Alien
 * Title: Palace Raider (V)
 */
public class Card202_002 extends AbstractAlien {
    public Card202_002() {
        super(Side.LIGHT, 2, 2, 1, 2, 3, Title.Palace_Raider, Uniqueness.UNRESTRICTED, ExpansionSet.SET_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Smugglers from many worlds are hunted by the Empire for providing arms and supplies to the Alliance. The Outer Rim is their refuge.");
        setGameText("[Pilot] 2. Palace Raiders (including this character) deploy -3 if You're A Slave? on table. While driving a vehicle, it is power +3, defense value +1, and moves for free.");
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_2);
        addKeywords(Keyword.SMUGGLER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, new OnTableCondition(self, Filters.Youre_A_Slave), -3));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition condition = new OnTableCondition(self, Filters.Youre_A_Slave);
        Filter vehicleDriving = Filters.and(Filters.vehicle, Filters.hasDriving(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, Filters.and(Filters.Palace_Raider, Filters.not(self)), condition, -3));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, vehicleDriving, 3));
        modifiers.add(new DefenseValueModifier(self, vehicleDriving, 1));
        modifiers.add(new MovesForFreeModifier(self, vehicleDriving));
        return modifiers;
    }
}
