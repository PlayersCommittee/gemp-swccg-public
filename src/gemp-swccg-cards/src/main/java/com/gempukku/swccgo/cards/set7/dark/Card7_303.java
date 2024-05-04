package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractSquadron;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Starship
 * Subtype: Squadron
 * Title: Death Star Assault Squadron
 */
public class Card7_303 extends AbstractSquadron {
    public Card7_303() {
        super(Side.DARK, 1, 12, 3, null, 3, null, 10, "Death Star Assault Squadron", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Notoriety gained during the assaults on Ralltiir and Mon Calamari makes this the most feared squadron in the Empire. Defended the Death Star during the Battle of Yavin.");
        setGameText("Permanent pilots aboard are •Darth Vader, •DS-61-2 and •DS-61-3, who provide total ability of 10 and add 9 to total power of •Vader's Custom TIE, •Black 2 and •Black 3.");
        addIcons(Icon.SPECIAL_EDITION);
        addIcon(Icon.PILOT, 3);
        addKeywords(Keyword.NO_HYPERDRIVE);
        addModelTypes(ModelType.TIE_ADVANCED_X1, ModelType.TIE_LN, ModelType.TIE_LN);
        addPersonas(Persona.VADERS_CUSTOM_TIE, Persona.BLACK_2, Persona.BLACK_3);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(Persona.VADER, 6) {
            @Override
            public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                List<Modifier> modifiers = new LinkedList<Modifier>();
                modifiers.add(new PowerModifier(self, 9));
                return modifiers;
            }
        });
        permanentsAboard.add(new AbstractPermanentPilot(Persona.DS_61_2, 2) {});
        permanentsAboard.add(new AbstractPermanentPilot(Persona.DS_61_3, 2) {});
        return permanentsAboard;
    }
}
