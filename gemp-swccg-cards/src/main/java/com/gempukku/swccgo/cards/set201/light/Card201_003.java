package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCloakModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 1
 * Type: Character
 * Subtype: Alien
 * Title: Melas (V)
 */
public class Card201_003 extends AbstractAlien {
    public Card201_003() {
        super(Side.LIGHT, 2, 3, 2, 4, 4, "Melas", Uniqueness.UNIQUE, ExpansionSet.SET_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Sarkan smuggler. Smokes an Essoomian gruu pipe to heighten awareness. Exiled from his home planet of Sarka for displaying curiosity in other aliens. Misses his homeworld.");
        setGameText("[Pilot] 2. Opponent's starships here may not 'cloak'. Smoke Screen is a Used Interrupt when targeting Melas.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.VIRTUAL_SET_1);
        setSpecies(Species.SARKAN);
        addKeywords(Keyword.SMUGGLER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayNotCloakModifier(self, Filters.and(Filters.opponents(self), Filters.starship, Filters.here(self))));
        modifiers.add(new UsedInterruptModifier(self, Filters.and(Filters.Smoke_Screen, Filters.cardBeingPlayedTargeting(self, self))));
        return modifiers;
    }
}
