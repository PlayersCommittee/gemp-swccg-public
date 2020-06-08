package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotExistAtTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Vehicle
 * Subtype: Transport
 * Title: Lift Tube
 */
public class Card1_148 extends AbstractTransportVehicle {
    public Card1_148() {
        super(Side.LIGHT, 6, 1, 0, null, 1, 3, 1, Title.Lift_Tube);
        setLore("System of enclosed high-speed vertical and horizontal transport cylinders. Used in space stations, large starships, Death Stars, etc.");
        setGameText("May carry 4 passengers. Deploy and move only at interior mobile sites. May move without presence aboard. Also, may move as a 'react' only to a battle or Force drain (if within range).");
        addKeywords(Keyword.ENCLOSED);
        setPassengerCapacity(4);
        setInteriorSiteVehicle(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotExistAtTargetModifier(self, Filters.not(Filters.interior_mobile_site)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactModifier(self));
        return modifiers;
    }
}
