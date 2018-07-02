package com.gempukku.swccgo.cards.set112.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.DrivingCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Jabba's Palace Sealed Deck)
 * Type: Character
 * Subtype: Alien
 * Title: Palace Raider
 */
public class Card112_005 extends AbstractAlien {
    public Card112_005() {
        super(Side.LIGHT, 2, 2, 1, 2, 3, Title.Palace_Raider);
        setLore("Smugglers from many worlds are hunted by the Empire for providing arms and supplies to the Alliance. The Outer Rim is their refuge.");
        setGameText("Adds 2 to power of anything he pilots (or 3 to power of anything he drives). When driving a transport vehicle, it moves for free and he adds one battle destiny.");
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SMUGGLER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsPowerToDrivenBySelfModifier(self, 3));
        modifiers.add(new MovesForFreeModifier(self, Filters.and(Filters.transport_vehicle, Filters.hasDriving(self))));
        modifiers.add(new AddsBattleDestinyModifier(self, new DrivingCondition(self, Filters.transport_vehicle), 1));
        return modifiers;
    }
}
