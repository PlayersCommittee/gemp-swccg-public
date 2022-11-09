package com.gempukku.swccgo.cards.set214.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AttemptToBlowAwayShieldGateTotalModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Starship
 * Subtype: Capital
 * Title: Intimidator & Persecutor
 */
public class Card214_005 extends AbstractCapitalStarship {
    public Card214_005() {
        super(Side.DARK, 1, 11, 12, 10, null, 2, 12, "Intimidator & Persecutor", Uniqueness.UNIQUE, ExpansionSet.SET_14, Rarity.V);
        addComboCardTitles("Intimidator", "Persecutor");
        setLore("");
        setGameText("Deploys -4 to Scarif or opponent's system. May add 4 pilots, 4 TIEs, and 2 vehicles. Permanent pilots provide total ability of 4. While at Scarif, subtracts 2 from attempts to 'blow away' Shield Gate.");
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_14);
        addIcon(Icon.PILOT, 2);
        addModelTypes(ModelType.IMPERIAL_CLASS_STAR_DESTROYER, ModelType.IMPERIAL_CLASS_STAR_DESTROYER);
        setPilotCapacity(4);
        setVehicleCapacity(2);
        setTIECapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<>();
        permanentsAboard.add(new AbstractPermanentPilot(2) {
        });
        permanentsAboard.add(new AbstractPermanentPilot(2) {
        });
        return permanentsAboard;
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
        modifiers.add(new AttemptToBlowAwayShieldGateTotalModifier(self, new AtCondition(self, Title.Scarif), -2));
        return modifiers;
    }
}
