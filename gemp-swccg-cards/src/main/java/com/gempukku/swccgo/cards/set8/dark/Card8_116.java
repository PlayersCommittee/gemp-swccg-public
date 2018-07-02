package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Effect
 * Title: An Entire Legion Of My Best Troops
 */
public class Card8_116 extends AbstractNormalEffect {
    public Card8_116() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "An Entire Legion Of My Best Troops", Uniqueness.UNIQUE);
        setLore("Stormtrooper standard gear includes plastoid composite armor, utility belt, positive-grip boots and energy sinks to dissipate blaster fire.");
        setGameText("Deploy on your side of table. Stormtroopers (except biker scouts) have armor = 4. Also, your blaster rifles, Stormtrooper Utility Belts and Blaster Scopes are deploy -1 and are destiny +2 when drawn for weapon or battle destiny. (Immune to Alter.)");
        addIcons(Icon.ENDOR);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.or(Filters.blaster_rifle, Filters.Stormtrooper_Utility_Belt, Filters.Blaster_Scope));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetArmorModifier(self, Filters.and(Filters.stormtrooper, Filters.except(Filters.biker_scout)), 4));
        modifiers.add(new DeployCostModifier(self, filter, -1));
        modifiers.add(new DestinyWhenDrawnForWeaponDestinyModifier(self, filter, 2));
        modifiers.add(new DestinyWhenDrawnForBattleDestinyModifier(self, filter, 2));
        return modifiers;
    }
}