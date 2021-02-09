package com.gempukku.swccgo.cards.set110.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.InBattleInitiatedByOwnerCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Enhanced Jabba's Palace)
 * Type: Starship
 * Subtype: Starfighter
 * Title: IG-88 In IG-2000
 */
public class Card110_008 extends AbstractStarfighter {
    public Card110_008() {
        super(Side.DARK, 1, 5, 3, null, 3, 3, 4, "IG-88 In IG-2000", Uniqueness.UNIQUE);
        setLore("Starship adapted to the assassin droid's specifications. Flight controls linked directly to processing unit. Real-time relays minimize response time.");
        setGameText("May add 2 passengers. Permanent pilot is •IG-88, who adds 2 to power and 3 to maneuver. May initiate battle. When in a battle you initiate, adds one battle destiny. Ion Cannon may deploy aboard.");
        addPersonas(Persona.IG2000);
        addIcons(Icon.PREMIUM, Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.TRILON_AGGRESSOR);
        setPassengerCapacity(2);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentPilot(Persona.IG88, 0) {
                    @Override
                    public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                        List<Modifier> modifiers = new LinkedList<Modifier>();
                        modifiers.add(new PowerModifier(self, 2));
                        modifiers.add(new ManeuverModifier(self, 3));
                        return modifiers;
                    }
                });
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayInitiateBattleModifier(self));
        modifiers.add(new AddsBattleDestinyModifier(self, new InBattleInitiatedByOwnerCondition(self), 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.your(self), Filters.Ion_Cannon), self));
        return modifiers;
    }
}
