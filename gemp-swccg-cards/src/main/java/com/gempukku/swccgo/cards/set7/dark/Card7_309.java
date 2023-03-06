package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AbilityMoreThanRequiredForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Starship
 * Subtype: Starfighter
 * Title: Vader's Personal Shuttle
 */
public class Card7_309 extends AbstractStarfighter {
    public Card7_309() {
        super(Side.DARK, 3, 2, 2, null, 2, 3, 4, "Vader's Personal Shuttle", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Customized transport of Lord Vader. Employs advanced sensor jamming gear. Modified with enhanced tactical displays constructed to the Dark Lord's specifications.");
        setGameText("May add 1 pilot and 4 passengers. Permanent pilot aboard provides ability of 2. Moves for free. While Vader aboard, opponent must have ability > 5 to draw battle destiny at same system. May not Tallon Roll.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelTypes(ModelType.LAMBDA_CLASS_SHUTTLE);
        addPersona(Persona.VADERS_PERSONAL_SHUTTLE);
        setPilotCapacity(1);
        setPassengerCapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesForFreeModifier(self));
        modifiers.add(new AbilityMoreThanRequiredForBattleDestinyModifier(self, Filters.sameSystem(self),
                new HasAboardCondition(self, Filters.Vader), 5, game.getOpponent(self.getOwner())));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.Tallon_Roll));
        return modifiers;
    }
}
