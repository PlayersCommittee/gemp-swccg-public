package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Kitonak
 */
public class Card6_023 extends AbstractAlien {
    public Card6_023() {
        super(Side.LIGHT, 3, 2, 1, 1, 2, "Kitonak", Uniqueness.RESTRICTED_3);
        setLore("Natives of Kirdo III, a desert world, Kitonak possess tough, leathery skin. Many become musicians. Very patient. Thousands have been enslaved by the Empire.");
        setGameText("Power +1 at a Tatooine site, or power +2 at any desert. Immune to Gravel Storm, Sandwhirl and desert landspeed requirements. Immune to attrition < the number of your musicians present.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.MUSICIAN);
        setSpecies(Species.KITONAK);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.and(Filters.Tatooine_site, Filters.not(Filters.desert))), 1));
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.desert), 2));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Gravel_Storm));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Sandwhirl));
        modifiers.add(new ImmuneToLandspeedRequirementsModifier(self, Filters.desert));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new PresentEvaluator(self, Filters.and(Filters.your(self), Filters.musician))));
        return modifiers;
    }
}
