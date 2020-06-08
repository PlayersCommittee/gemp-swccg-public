package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.DuringDuelWithParticipantCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EachDuelDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Effect
 * Title: They Will Be No Match For You
 */
public class Card12_141 extends AbstractNormalEffect {
    public Card12_141() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "They Will Be No Match For You", Uniqueness.UNIQUE);
        setLore("'At last we will reveal ourselves to the Jedi.'");
        setGameText("Deploy on table. When Maul initiates a duel, Hunt Down And Destroy The Jedi (or Their Fire Has Gone Out Of The Universe) is not placed out of play. While armed with a lightsaber, Maul's duel destiny draws are each +1. (Immune to Alter.)");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.or(Filters.Hunt_Down_And_Destroy_The_Jedi,
                Filters.Their_Fire_Has_Gone_Out_Of_The_Universe), ModifyGameTextType.HUNT_DOWN__DO_NOT_PLACE_OUT_OF_PLAY_IF_MAUL_DUELS));
        modifiers.add(new EachDuelDestinyModifier(self, new DuringDuelWithParticipantCondition(Filters.and(Filters.your(self), Filters.Maul,
                Filters.armedWith(Filters.lightsaber))), 1, playerId));
        return modifiers;
    }
}