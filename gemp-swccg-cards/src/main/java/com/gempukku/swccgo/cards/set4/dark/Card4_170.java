package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Starship
 * Subtype: Starfighter
 * Title: Mist Hunter
 */
public class Card4_170 extends AbstractStarfighter {
    public Card4_170() {
        super(Side.DARK, 4, 4, 2, null, 3, 5, 3, "Mist Hunter", Uniqueness.UNIQUE);
        setLore("Commissioned by a group of Gand venture capitalists headed by Zuckuss. Manufactured by Byblos Drive Yards. Uses repulsor lift technology developed for combat cloud cars.");
        setGameText("May add 2 pilots (one must be a smuggler or bounty hunter) and 3 passengers. Immune to attrition < 3 if Zuckuss is piloting. Has ship-docking capability.");
        addIcons(Icon.DAGOBAH, Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.SHIP_DOCKING_CAPABILITY);
        addModelType(ModelType.BYBLOS_G1A_TRANSPORT);
        addPersona(Persona.MIST_HUNTER);
        setPilotCapacity(2);
        setPassengerCapacity(3);
        setMatchingPilotFilter(Filters.Zuckuss);
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        // Must be at least one smuggler or bounty hunter as pilot
        List<PhysicalCard> physicalCardList = game.getGameState().getPilotCardsAboard(game.getModifiersQuerying(), self, false);
        if (Filters.filter(physicalCardList, game, Filters.or(Filters.smuggler, Filters.bounty_hunter)).isEmpty())
            return Filters.or(Filters.smuggler, Filters.bounty_hunter);
        else
            return Filters.any;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Zuckuss), 3));
        return modifiers;
    }
}
