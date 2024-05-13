package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Imperial
 * Title: Admiral Motti
 */
public class Card1_164 extends AbstractImperial {
    public Card1_164() {
        super(Side.DARK, 1, 3, 3, 3, 5, "Admiral Motti", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R2);
        setLore("Senior Navy Commander of Death Star. Believes in technology. Ridiculed the Force. Ambitious leader. Promoted due to support of New Order, not military skills. Hates Vader.");
        setGameText("Deploys -2 if at least two Imperial starships on table. Adds 2 to power of anything he pilots. Subtracts 1 from forfeit of Rebel pilots at same system.");
        addPersona(Persona.MOTTI);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.ADMIRAL, Keyword.COMMANDER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, new OnTableCondition(self, 2, Filters.Imperial_starship), -2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.Rebel_pilot, Filters.atSameSystem(self)), -1));
        return modifiers;
    }
}
