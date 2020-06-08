package com.gempukku.swccgo.cards.set106.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Official Tournament Sealed Deck)
 * Type: Starship
 * Subtype: Starfighter
 * Title: Z-95 Headhunter
 */
public class Card106_009 extends AbstractStarfighter {
    public Card106_009() {
        super(Side.LIGHT, 3, 2, 2, null, 4, null, 2, "Z-95 Headhunter");
        setLore("Developed by Incom/Subpro. Atmospheric fighter adapted for space travel. AF-4 version refitted with Incom 2a fission engines and two Taim & Bak KX5 laser cannons.");
        setGameText("Permanent pilot aboard provides ability of 1. Power and Maneuver +2 at non-unique cloud sectors. May be carried aboard starships like a vehicle.");
        addIcons(Icon.PREMIUM, Icon.INDEPENDENT, Icon.PILOT);
        addKeywords(Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.Z_95_HEADHUNTER);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atNonuniqueCloudSector = new AtCondition(self, Filters.and(Filters.non_unique, Filters.cloud_sector));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, atNonuniqueCloudSector, 2));
        modifiers.add(new ManeuverModifier(self, atNonuniqueCloudSector, 2));
        return modifiers;
    }

    @Override
    public boolean isVehicleSlotOfStarshipCompatible() {
        return true;
    }
}
