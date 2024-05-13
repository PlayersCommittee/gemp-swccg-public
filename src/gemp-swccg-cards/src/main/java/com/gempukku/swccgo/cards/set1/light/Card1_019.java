package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalForceGenerationModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Rebel
 * Title: Luke Skywalker
 */
public class Card1_019 extends AbstractRebel {
    public Card1_019() {
        super(Side.LIGHT, 1, 3, 3, 4, 7, "Luke Skywalker", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R1);
        setLore("Son of Anakin Skywalker. Student of Obi-Wan Kenobi. Honed piloting skills while bullseyeing womp rats in Beggar's Canyon aboard T-16 skyhopper.");
        setGameText("While Luke is not on Tatooine your total Force generation is +1. Adds 3 to power of anything he pilots. When piloting Red 5, also adds 2 to maneuver. Immune to attrition < 3.");
        addPersona(Persona.LUKE);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.RED_SQUADRON);
        setMatchingStarshipFilter(Filters.Red_5);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalForceGenerationModifier(self, new NotCondition(new OnCondition(self, Title.Tatooine)), 1, self.getOwner()));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ManeuverModifier(self, Filters.and(Filters.Red_5, Filters.hasPiloting(self)), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
}
