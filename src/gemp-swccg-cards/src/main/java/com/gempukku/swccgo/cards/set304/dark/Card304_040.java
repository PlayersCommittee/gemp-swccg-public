package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Starship
 * Subtype: Capital
 * Title: ISN Gargoyle
 */
public class Card304_040 extends AbstractCapitalStarship {
    public Card304_040() {
        super(Side.DARK, 2, 5, 4, 3, null, 4, 5, "ISN Gargoyle", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Cloaked in bureaucratic paperwork the Gargoyle is whispered to be assigned to the Imperial Security Bureau (ISB).");
		setGameText("May add 3 pilots, 5 passengers, and 4 TIEs. Permanent pilot provides ability of 3. Adds one battle destiny if Scholae Palatinae Emperor on table. Immune to attrition < 5.");
        addPersona(Persona.INVISIBLE_HAND);
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.CSP);
        addIcon(Icon.PILOT, 1);
        addModelType(ModelType.IMPERIAL_CLASS_ESCORT_CRUISER);
        setPilotCapacity(3);
        setPassengerCapacity(5);
        setTIECapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(3) {
        });
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        modifiers.add(new AddsBattleDestinyModifier(self, new OnTableCondition(self, Filters.CSP_EMPEROR), 1));
        return modifiers;
    }
}

