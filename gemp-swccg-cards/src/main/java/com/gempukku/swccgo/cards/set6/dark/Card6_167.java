package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractUniqueVehicleSite;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EnterExitCostForCharactersModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Location
 * Subtype: Site
 * Title: Jabba's Sail Barge: Passenger Deck
 */
public class Card6_167 extends AbstractUniqueVehicleSite {
    public Card6_167() {
        super(Side.DARK, Title.Passenger_Deck, Persona.JABBAS_SAIL_BARGE);
        setLocationDarkSideGameText("Deploy on Jabba's Sail Barge. If you occupy, Sail Barge is immune to attrition.");
        setLocationLightSideGameText("Your characters may enter/exit here for 4 Force each.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.JABBAS_PALACE, Icon.INTERIOR_SITE);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionModifier(self, Filters.Jabbas_Sail_Barge, new OccupiesCondition(playerOnDarkSideOfLocation, self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EnterExitCostForCharactersModifier(self, 4, playerOnLightSideOfLocation));
        return modifiers;
    }
}