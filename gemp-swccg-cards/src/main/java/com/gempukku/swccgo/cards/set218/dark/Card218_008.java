package com.gempukku.swccgo.cards.set218.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 18
 * Type: Starship
 * Subtype: Starfighter
 * Title: Alpha 1
 */
public class Card218_008 extends AbstractStarfighter {
    public Card218_008() {
        super(Side.DARK, 2, 2, 3, null, 4, 3, 4, "Alpha 1", Uniqueness.UNIQUE);
        setGameText("May add 1 pilot. While Stele piloting: immune to attrition < 5, game text of Fighters Coming In may not be canceled, and the power of your TIE Defenders here may not be reduced.");
        addIcons(Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_18);
        addModelType(ModelType.TIE_DEFENDER);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.persona(Persona.MAAREK_STELE));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition stelePiloting = new HasPilotingCondition(self, Filters.persona(Persona.MAAREK_STELE));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, stelePiloting, 5));
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self, Filters.title(Title.Fighters_Coming_In), stelePiloting));
        modifiers.add(new MayNotHavePowerReducedModifier(self, Filters.and(Filters.your(self), Filters.TIE_Defender, Filters.here(self)), stelePiloting, playerId));
        modifiers.add(new MayNotHavePowerReducedModifier(self, Filters.and(Filters.your(self), Filters.TIE_Defender, Filters.here(self)), stelePiloting, opponent));
        return modifiers;
    }
}
