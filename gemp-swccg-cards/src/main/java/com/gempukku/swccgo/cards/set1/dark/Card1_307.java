package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractCreatureVehicle;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextAbilityModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Vehicle
 * Subtype: Creature
 * Title: Bantha
 */
public class Card1_307 extends AbstractCreatureVehicle {
    public Card1_307() {
        super(Side.DARK, 3, 1, 1, null, 2, 1, 3, "Bantha");
        setLore("Transport, pack animal. Many breeds of different sizes and colors. Three meters tall. Can go weeks without food or water. Found throughout the galaxy.");
        setGameText("May carry 2 passengers. Bantha ability = 1/2. May move as a 'react' only to a battle or Force drain (if within range). Each Tusken Raider at same exterior site is power +1.");
        addKeywords(Keyword.BANTHA);
        setPassengerCapacity(2);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextAbilityModifier(self, 0.5));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactModifier(self));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Tusken_Raider, Filters.atSameSite(self)),
                new AtCondition(self, Filters.exterior_site), 1));
        return modifiers;
    }
}
