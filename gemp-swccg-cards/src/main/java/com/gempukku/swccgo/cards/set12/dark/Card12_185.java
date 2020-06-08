package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Starship
 * Subtype: Capital
 * Title: Trade Federation Droid Control Ship
 */
public class Card12_185 extends AbstractCapitalStarship {
    public Card12_185() {
        super(Side.DARK, 2, 6, 5, 7, null, 3, 6, "Trade Federation Droid Control Ship", Uniqueness.DIAMOND_1);
        setLore("These heavily modified battleships are used to control and direct the Trade Federation's automated army. Easily identified by its array of sensors and antennae.");
        setGameText("May add 4 pilots, 4 passengers, 2 vehicles, and 4 starfighters. Has ship-docking capability. Permanent pilot provides ability of 2. While a droid starfighter here, adds one battle destiny.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.TRADE_FEDERATION, Icon.PILOT, Icon.NAV_COMPUTER);
        addKeywords(Keyword.DROID_CONTROL_SHIP);
        addModelType(ModelType.TRADE_FEDERATION_BATTLESHIP);
        setPilotCapacity(4);
        setPassengerCapacity(4);
        setVehicleCapacity(2);
        setStarfighterCapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new HereCondition(self, Filters.droid_starfighter), 1));
        return modifiers;
    }
}
