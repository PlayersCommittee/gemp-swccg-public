package com.gempukku.swccgo.cards.set205.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsDestinyToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 5
 * Type: Starship
 * Subtype: Starfighter
 * Title: Han, Chewie, And The Falcon (V)
 */
public class Card205_007 extends AbstractStarfighter {
    public Card205_007() {
        super(Side.LIGHT, 2, 6, 8, null, 6, 7, 8, "Han, Chewie, And The Falcon", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setComboCard(true);
        setLore("Although temperamental, this trusty hunk of junk always seems to perform for its proud owner and his Wookiee co-pilot when needed the most.");
        setGameText("May add 2 passengers. Permanent pilots are •Han and •Chewie, who provide ability of 5. If in battle, add one destiny to total power. Immune to Come With Me, Life Debt and attrition < 5.");
        addPersonas(Persona.FALCON, Persona.HAN,Persona.CHEWIE);
        addIcons(Icon.REFLECTIONS_III, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_5);
        addIcon(Icon.PILOT, 2);
        setPassengerCapacity(2);
        addModelType(ModelType.HEAVILY_MODIFIED_LIGHT_FREIGHTER);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(Persona.HAN, 3) {
        });
        permanentsAboard.add(new AbstractPermanentPilot(Persona.CHEWIE, 2) {
        });
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Title.Come_With_Me));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Life_Debt));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        modifiers.add(new AddsDestinyToPowerModifier(self, 1));
        return modifiers;
    }
}
