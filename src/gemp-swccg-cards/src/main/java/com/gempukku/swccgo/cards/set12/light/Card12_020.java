package com.gempukku.swccgo.cards.set12.light;

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
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Alien
 * Title: Phylo Gandish
 */
public class Card12_020 extends AbstractAlien {
    public Card12_020() {
        super(Side.LIGHT, 2, 4, 1, 4, 4, "Phylo Gandish", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.R);
        setLore("Good-natured former pilot whose family owns a galactic transportation company. Years of travel has her to learn many loopholes in spaceport protocol.");
        setGameText("Deploys for free at a docking bay. Adds 2 to the power of anything she pilots. While at opponent's docking bay, adds 2 to your Force drains there, and your other characters there are defense value +1 and immune to attrition < 4.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PILOT);
        addKeywords(Keyword.FEMALE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.docking_bay));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atOpponentsDockingBay = new AtCondition(self, Filters.and(Filters.opponents(self), Filters.docking_bay));
        Filter otherCharacters = Filters.and(Filters.your(self), Filters.other(self), Filters.character, Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), atOpponentsDockingBay, 2, self.getOwner()));
        modifiers.add(new DefenseValueModifier(self, otherCharacters, atOpponentsDockingBay, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, otherCharacters, atOpponentsDockingBay, 4));
        return modifiers;
    }
}
