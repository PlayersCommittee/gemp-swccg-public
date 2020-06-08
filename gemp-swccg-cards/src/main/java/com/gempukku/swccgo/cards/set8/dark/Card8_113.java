package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyDrawForActionSourceModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Sergeant Irol
 */
public class Card8_113 extends AbstractImperial {
    public Card8_113() {
        super(Side.DARK, 2, 2, 2, 2, 3, Title.Irol, Uniqueness.UNIQUE);
        setLore("Cocky stormtrooper from an Outer Rim forest planet. Confident biker scout and hunter. Relies on superior balance and strength to operate his speeder bike.");
        setGameText("May deploy -1 as a 'react' to any forest, jungle or exterior Endor site. Adds 3 to power of any speeder bike he pilots. When vehicle he drives or pilots is targeted by High-speed Tactics or Get Alongside That One, adds 2 to your destiny draw.");
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.BIKER_SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.or(Filters.forest, Filters.jungle, Filters.exterior_Endor_site), -1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.speeder_bike));
        modifiers.add(new DestinyDrawForActionSourceModifier(self, Filters.and(Filters.or(Filters.Highspeed_Tactics, Filters.Get_Alongside_That_One),
                Filters.cardBeingPlayedTargeting(self, Filters.and(Filters.vehicle, Filters.or(Filters.hasDriving(self), Filters.hasPiloting(self))))), 2, playerId));
        return modifiers;
    }
}
