package com.gempukku.swccgo.cards.set302.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
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
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Character
 * Subtype: Imperial
 * Title: Valencia Sintarin
 */
public class Card302_011 extends AbstractImperial {
    public Card302_011() {
        super(Side.DARK, 3, 3, 2, 3, 4, "Valencia Sintarin", Uniqueness.UNIQUE, ExpansionSet.DJB_CORE, Rarity.V);
        setLore("Formerly of Imperial Intelligence Valencia was transfered to the Brotherhood's elite forces as her Force powers began to manifest themselves. She has potential, if she survives.");
        setGameText("Adds 1 to power and maneuver of anything she pilots. Opponent may not 'react' to or from same location.");
        addIcons(Icon.WARRIOR, Icon.PILOT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), 1));
        modifiers.add(new MayNotReactToLocationModifier(self, Filters.sameLocation(self), opponent));
        modifiers.add(new MayNotReactFromLocationModifier(self, Filters.sameLocation(self), opponent));
        return modifiers;
    }
}
