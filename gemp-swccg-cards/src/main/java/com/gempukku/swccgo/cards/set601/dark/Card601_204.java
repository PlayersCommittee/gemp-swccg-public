package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 6
 * Type: Starship
 * Subtype: Capital
 * Title: Blockade Support Ship
 */
public class Card601_204 extends AbstractCapitalStarship {
    public Card601_204() {
        super(Side.DARK, 2, 5, 5, 5, null, 3, 7, Title.Blockade_Support_Ship, Uniqueness.UNIQUE);
        setLore("These heavily modified battleships are used to control and direct the Trade Federation's automated army. Easily identified by its array of sensors and antennae.");
        setGameText("Unless Fondor on table, may deploy -3 as a 'react'. May add 3 pilots and 4 passengers. Permanent pilot provides ability of 2.");
        addModelType(ModelType.TRADE_FEDERATION_BATTLESHIP);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.TRADE_FEDERATION, Icon.PILOT, Icon.NAV_COMPUTER, Icon.LEGACY_BLOCK_6);
        setPilotCapacity(3);
        setPassengerCapacity(4);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactModifier(self, new UnlessCondition(new OnTableCondition(self, Filters.Fondor)), -3));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }
}
