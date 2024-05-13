package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Alien
 * Title: Grotto Werribee
 */
public class Card12_107 extends AbstractAlien {
    public Card12_107() {
        super(Side.DARK, 1, 4, 1, 4, 3, "Grotto Werribee", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.R);
        setLore("Once a junior traffic controller for an interstellar shipping corporation, Grotto brags that there is nothing about docking bay procedure that he doesn't know. Information broker.");
        setGameText("Deploys -1 to a docking bay. Adds 2 to the power of anything he pilots. While at opponent's docking bay, adds 2 to your Force drains there, and your vehicles and other characters there are immune to attrition < 4.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PILOT);
        addKeywords(Keyword.INFORMATION_BROKER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.docking_bay));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atOpponentsDockingBay = new AtCondition(self, Filters.and(Filters.opponents(self), Filters.docking_bay));
        Filter vehiclesAndOtherCharacters = Filters.and(Filters.your(self), Filters.or(Filters.vehicle,
                Filters.and(Filters.other(self), Filters.character)), Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), atOpponentsDockingBay, 2, self.getOwner()));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, vehiclesAndOtherCharacters, atOpponentsDockingBay, 4));
        return modifiers;
    }
}
