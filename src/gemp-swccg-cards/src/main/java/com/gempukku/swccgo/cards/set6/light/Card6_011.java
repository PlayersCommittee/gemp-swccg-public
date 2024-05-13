package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
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
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToLandspeedRequirementsModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Droopy McCool
 */
public class Card6_011 extends AbstractAlien {
    public Card6_011() {
        super(Side.LIGHT, 3, 2, 1, 1, 3, "Droopy McCool", Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLore("Kitonak musician. Lead jizz wailer. Searching for other Kitonak rumored to be living on Tatooine. Rarely uses his real name, Snit.");
        setGameText("Power +2 at any desert or Tatooine site. Immune to Gravel Storm, Sandwhirl and desert landspeed requirements. While at Audience Chamber, all your other Kitonaks are forfeit +2. Immune to attrition < number of your musicians on table.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.MUSICIAN);
        setSpecies(Species.KITONAK);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.or(Filters.desert, Filters.Tatooine_site)), 2));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Gravel_Storm));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Sandwhirl));
        modifiers.add(new ImmuneToLandspeedRequirementsModifier(self, Filters.desert));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.Kitonak),
                new AtCondition(self, Filters.Audience_Chamber), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new OnTableEvaluator(self, Filters.and(Filters.your(self), Filters.musician))));
        return modifiers;
    }
}
