package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.FiredWeaponsInBattleCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendModifierEffectsModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Effect
 * Title: Crossfire
 */
public class Card8_121 extends AbstractNormalEffect {
    public Card8_121() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Crossfire, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Scout walker pilots are trained to set up a deadly heavy fire zone. This tactic can be disrupted by enemy weapons fire.");
        setGameText("Deploy on table. When you fire two weapons (except lightsabers) in a battle, your total power is +5. Also, S-foils and Maneuvering Flaps are suspended where you have either a weapon present or a starship (or vehicle) with maneuver > 3 present. (Immune to Alter.)");
        addIcons(Icon.ENDOR);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String player = self.getOwner();
        Filter filter = Filters.locationAndCardsAtLocation(Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.or(Filters.weapon,
                Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.maneuverMoreThan(3))))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.battleLocation, new FiredWeaponsInBattleCondition(player, 2, Filters.except(Filters.lightsaber)), 5, player));
        modifiers.add(new SuspendModifierEffectsModifier(self, filter, Filters.or(Filters.S_foils, Filters.Maneuvering_Flaps)));
        return modifiers;
    }
}