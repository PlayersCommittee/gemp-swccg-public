package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactToBattleForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Vehicle
 * Subtype: Transport
 * Title: Mobquet A-1 Deluxe Floater
 */
public class Card2_157 extends AbstractTransportVehicle {
    public Card2_157() {
        super(Side.DARK, 4, 2, 2, null, 5, 3, 5, "Mobquet A-1 Deluxe Floater");
        setLore("Enclosed landspeeder often used by nefarious characters due to its luxury and evasive capabilities. Features include automated steering and fine Corellian leather.");
        setGameText("May add 1 driver and 1 passenger. Moves free if Jabba or any bounty hunter aboard. May move for free as a 'react' to a battle where your thief, smuggler or bounty hunter is participating.");
        addIcons(Icon.A_NEW_HOPE, Icon.SCOMP_LINK);
        addKeywords(Keyword.ENCLOSED, Keyword.LANDSPEEDER);
        setDriverCapacity(1);
        setPassengerCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesForFreeModifier(self, new HasAboardCondition(self, Filters.or(Filters.Jabba, Filters.bounty_hunter))));
        modifiers.add(new MayMoveAsReactToBattleForFreeModifier(self, new DuringBattleWithParticipantCondition(Filters.and(Filters.your(self),
                Filters.or(Filters.thief, Filters.smuggler, Filters.bounty_hunter)))));
        return modifiers;
    }
}
