package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Jabba
 */
public class Card7_182 extends AbstractAlien {
    public Card7_182() {
        super(Side.DARK, 1, 3, 2, 3, 5, "Jabba", Uniqueness.UNIQUE);
        setLore("Gangster. Leader. Infamous Hutt crime lord. Operates his vast empire from an ancient monastery on Tatooine. Uses mercenary pilots to smuggle spice and other contraband.");
        setGameText("To use his landspeed requires +1 Force. Your aliens are deploy -1 to same location. While at Audience Chamber, adds 2 to forfeit of all your non-unique aliens and makes Scum and Villainy immune to Alter. Immune to attrition < 4.");
        addPersona(Persona.JABBA);
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.GANGSTER, Keyword.LEADER);
        setSpecies(Species.HUTT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atAudienceChamber = new AtCondition(self, Filters.Audience_Chamber);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MoveCostUsingLandspeedModifier(self, 1));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.not(self), Filters.alien),
                -1, Filters.sameLocation(self)));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.non_unique, Filters.alien), atAudienceChamber, 2));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Scum_And_Villainy, atAudienceChamber, Title.Alter));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
