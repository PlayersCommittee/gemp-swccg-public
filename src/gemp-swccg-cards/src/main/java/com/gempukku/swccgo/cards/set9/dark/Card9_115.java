package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractImperial;

import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostAboardModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveFromLocationToLocationUsingLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Imperial
 * Title: Major Rhymer
 */
public class Card9_115 extends AbstractImperial {
    public Card9_115() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Major Rhymer", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.U);
        setLore("Leader of Scimitar Squadron, the elite bomber wing assigned to defend the Endor shield generator from any ground assault.");
        setGameText("Deploys -2 aboard Scimitar 1. Adds 2 to power of anything he pilots. When piloting a bomber making a Bombing Run, prevents opponent's characters at same site from using landspeed.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT);
        addKeywords(Keyword.LEADER, Keyword.SCIMITAR_SQUADRON);
        setMatchingStarshipFilter(Filters.Scimitar_1);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostAboardModifier(self, -2, Filters.Scimitar_1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayNotMoveFromLocationToLocationUsingLandspeedModifier(self, Filters.and(Filters.opponents(self), Filters.character,
                Filters.atSameSite(self)), new PilotingCondition(self, Filters.and(Filters.bomber, Filters.makingBombingRun)), Filters.any, Filters.any));
        return modifiers;
    }
}
