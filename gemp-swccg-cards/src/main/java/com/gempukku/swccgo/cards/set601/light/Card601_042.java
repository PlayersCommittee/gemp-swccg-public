package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AloneCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 4
 * Type: Character
 * Subtype: Alien
 * Title: Dash Rendar (V)
 */
public class Card601_042 extends AbstractAlien {
    public Card601_042() {
        super(Side.LIGHT, 3, 3, 3, 3, 5, "Dash Rendar", Uniqueness.UNIQUE);
        setLore("Emperor banished Rendar family from Coruscant. Became gambler and smuggler. Brought down AT-AT at the Battle of Hoth. Works for Rebel Alliance from time to time. Corellian.");
        setGameText("Adds 3 to power of anything he pilots. While piloting alone (or at same location as opponent's AT-AT or Imperial), draws one battle destiny if unable to otherwise and opponent draws no more than one battle destiny here. Dash is a matching pilot for any unique (•) Rogue speeder.");
        addPersona(Persona.DASH);
        addIcons(Icon.LEGACY_BLOCK_4, Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GAMBLER, Keyword.SMUGGLER, Keyword.ROGUE_SQUADRON);
        setSpecies(Species.CORELLIAN);
        setMatchingVehicleFilter(Filters.and(Filters.unique, Filters.speeder, Filters.Rogue_Squadron_vehicle));
        setVirtualSuffix(true);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition condition = new AndCondition(new PilotingCondition(self),
                new OrCondition(new AloneCondition(self),new WithCondition(self, Filters.or(Filters.Imperial, Filters.AT_AT))));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, condition, 1));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, condition, 1, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
