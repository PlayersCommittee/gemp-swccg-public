package com.gempukku.swccgo.cards.set224.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 24
 * Type: Character
 * Subtype: Imperial
 * Title: Baron Soontir Fel (V)
 */
public class Card224_002 extends AbstractImperial {
    public Card224_002() {
        super(Side.DARK, 1, 4, 2, 3, 5, Title.Fel, Uniqueness.UNIQUE, ExpansionSet.SET_24, Rarity.V);
        setLore("Corellian Baron. Leader of famed 181st Imperial Fighter Wing. Taught at the Imperial Academy on Prefsbelt IV. Instructed Biggs Darklighter.");
        setGameText("Deploys -1 to Endor. Adds 3 to the power of anything he pilots. When piloting a starship, adds one battle destiny. Anything he pilots is immune to attrition <5.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_24);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.CORELLIAN);
        setVirtualSuffix(true);
    }

    
    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.Endor_location));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AddsBattleDestinyModifier(self, new PilotingCondition(self, Filters.starship), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.hasPiloting(self), 5));
        return modifiers;
    }
}
