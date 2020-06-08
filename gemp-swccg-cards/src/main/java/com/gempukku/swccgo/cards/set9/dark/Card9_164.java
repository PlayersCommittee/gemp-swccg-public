package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Saber 3
 */
public class Card9_164 extends AbstractStarfighter {
    public Card9_164() {
        super(Side.DARK, 3, 1, 3, null, 4, null, 3, Title.Saber_3, Uniqueness.UNIQUE);
        setLore("TIE interceptor assigned to fly on the wing of Baron Fel. Often ordered to remain at a distance from Fel to give the Baron maneuvering room during combat.");
        setGameText("May add 1 pilot. Cannons are deploy -2 aboard. Immune to attrition < 3 when DS-181-3 piloting.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.SABER_SQUADRON, Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.TIE_INTERCEPTOR);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.DS_181_3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.cannon, -2, self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.DS_181_3), 3));
        return modifiers;
    }
}
