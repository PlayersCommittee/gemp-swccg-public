package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Starship
 * Subtype: Capital
 * Title: Trade Federation Battleship
 */
public class Card12_184 extends AbstractCapitalStarship {
    public Card12_184() {
        super(Side.DARK, 2, 7, 6, 6, null, 3, 7, "Trade Federation Battleship", Uniqueness.UNRESTRICTED, ExpansionSet.CORUSCANT, Rarity.U);
        setLore("Massive cargo vessels that were converted by the Trade Federation into armed battleships. Each is equipped with a large contingent of droid starfighters and battle droids.");
        setGameText("May add 4 pilots, 4 passengers, 2 vehicles and 4 starfighters. Has ship-docking capability. Permanent pilot provides ability of 1. While with any starfighter, draws one battle destiny if unable to otherwise.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.TRADE_FEDERATION, Icon.PILOT, Icon.NAV_COMPUTER);
        addModelType(ModelType.TRADE_FEDERATION_BATTLESHIP);
        setPilotCapacity(4);
        setPassengerCapacity(4);
        setVehicleCapacity(2);
        setStarfighterCapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new WithCondition(self, Filters.starfighter), 1));
        return modifiers;
    }
}
