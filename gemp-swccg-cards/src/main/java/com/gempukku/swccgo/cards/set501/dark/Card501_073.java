package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Starship
 * Subtype: Capital
 * Title: Intimidator & Persecutor
 */
public class Card501_073 extends AbstractCapitalStarship {
    public Card501_073() {
        super(Side.DARK, 1, 11, 12, 10, null, 2, 12, "Intimidator & Persecutor", Uniqueness.UNIQUE);
        addComboCardTitles("Intimidator", "Persecutor");
        setLore("");
        setGameText("Deploys -4 to Scarif or opponent's system. May add 4 pilots, 4 TIEs, and 2 vehicles. Permanent pilots provide total ability of 4. While at Scarif, subtracts 2 from attempts to 'blow away' Shield Gate.");
        addIcons(Icon.PILOT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_14);
        addModelTypes(ModelType.IMPERIAL_CLASS_STAR_DESTROYER, ModelType.IMPERIAL_CLASS_STAR_DESTROYER);
        setPilotCapacity(4);
        setVehicleCapacity(2);
        setTIECapacity(4);
        setTestingText("Intimidator & Persecutor");
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(4) {
        });
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, -4, Filters.or(Filters.and(Filters.opponents(self), Filters.system), Filters.Scarif_system)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.Shield_Gate, new AtCondition(self, Title.Scarif), ModifyGameTextType.SUBTRACT_TWO_FROM_BLOW_AWAY_SHIELD_GATE));
        return modifiers;
    }
}
