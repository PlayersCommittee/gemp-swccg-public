package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.*;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 10
 * Type: Starship
 * Subtype: Starfighter
 * Title: Black 3 (V)
 */
public class Card210_028 extends AbstractStarfighter {
    public Card210_028() {
        super(Side.DARK, 3, 1, 1, null, 3, null, 3, "Black 3", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("TIE/ln fighter of pilot DS-61-3. Stylized image of Corellian slice-hound painted on inner hatch.");
        setGameText("May add 1 pilot. While with a Black Squadron TIE, Force drain +1 here. While DS-61-3 piloting, immune to attrition < 4 and during battle here, players draw destiny from the bottom of their Reserve Deck.");
        addPersona(Persona.BLACK_3);
        addModelType(ModelType.TIE_LN);
        addIcons(Icon.VIRTUAL_SET_10);
        addKeywords(Keyword.NO_HYPERDRIVE, Keyword.BLACK_SQUADRON);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.DS_61_3);
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {

        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.DS_61_3), 4));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new WithCondition(self, Filters.Black_Squadron_tie), 1, playerId));
        modifiers.add(new DrawDestinyFromBottomOfDeckModifier(self, new AndCondition(new HasPilotingCondition(self, Filters.DS_61_3), new DuringBattleWithParticipantCondition(self))));
        return modifiers;
    }
}
