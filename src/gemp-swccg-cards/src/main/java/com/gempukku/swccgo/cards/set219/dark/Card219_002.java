package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
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
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveGameTextCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Starship
 * Subtype: Capital
 * Title: Chimaera (V)
 */
public class Card219_002 extends AbstractCapitalStarship {
    public Card219_002() {
        super(Side.DARK, 1, 8, 9, 6, null, 3, 9, Title.Chimaera, Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setVirtualSuffix(true);
        setLore("Early model Imperial-class Star Destroyer. Acting command ship in the absence of Executor.");
        setGameText("May add 6 pilots, 6 passengers, and 4 TIEs. Permanent pilot provides ability of 2. " +
                    "While Thrawn piloting, immune to attrition < 5 and game text of We're In Attack Position Now may not be canceled.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_19);
        addModelType(ModelType.IMPERIAL_CLASS_STAR_DESTROYER);
        setPilotCapacity(6);
        setPassengerCapacity(6);
        setTIECapacity(4);
        setMatchingPilotFilter(Filters.Thrawn);
        addPersona(Persona.CHIMAERA);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Thrawn), 5));
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self, Filters.title(Title.Were_In_Attack_Position_Now),  new HasPilotingCondition(self, Filters.Thrawn)));
        return modifiers;
    }
}
