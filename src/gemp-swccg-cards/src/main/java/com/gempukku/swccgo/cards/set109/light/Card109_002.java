package com.gempukku.swccgo.cards.set109.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Enhanced Cloud City)
 * Type: Starship
 * Subtype: Starfighter
 * Title: Lando In Millennium Falcon
 */
public class Card109_002 extends AbstractStarfighter {
    public Card109_002() {
        super(Side.LIGHT, 2, 5, 3, null, 3, 5, 6, "Lando In Millennium Falcon", Uniqueness.UNIQUE, ExpansionSet.ENHANCED_CLOUD_CITY, Rarity.PM);
        setLore("Heavily modified Corellian YT-1300 freighter. 'She's the fastest hunk of junk in the galaxy.'");
        setGameText("May add 1 pilot and 2 passengers. Permanent pilot is •Lando, who provides ability of 3 and adds 3 to power. May not be piloted by Han unless he won a hand of sabacc this game. Immune to attrition < 5.");
        addPersonas(Persona.FALCON);
        addIcons(Icon.PREMIUM, Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.MODIFIED_LIGHT_FREIGHTER);
        setPilotCapacity(1);
        setPassengerCapacity(2);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentPilot(Persona.LANDO, 3) {
                    @Override
                    public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                        List<Modifier> modifiers = new LinkedList<Modifier>();
                        modifiers.add(new PowerModifier(self, 3));
                        return modifiers;
                    }});
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        if (!game.getModifiersQuerying().hasWonSabaccGame(game.getGameState(), Filters.Han))
            return Filters.not(Filters.Han);
        else
            return Filters.any;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
