package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Starship
 * Subtype: Starfighter
 * Title: Bravo 2
 */
public class Card14_054 extends AbstractStarfighter {
    public Card14_054() {
        super(Side.LIGHT, 2, 1, 2, null, 4, 3, 3, Title.Bravo_2, Uniqueness.UNIQUE);
        setLore("Starfighter assigned to Officer Dolphe at the battle of Naboo. Damaged when the Trade Federation first invaded his planet, but Dolphe was able to make the necessary repairs.");
        setGameText("Deploys -1 to Naboo. May add 1 pilot. While Dolphe piloting, immune to attrition < 4. While at Naboo system, your Bravo Squadron pilots at same system are forfeit +2.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.REPUBLIC, Icon.NAV_COMPUTER);
        addKeywords(Keyword.BRAVO_SQUADRON);
        addModelType(ModelType.N_1_STARFIGHTER);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Dolphe);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.Deploys_at_Naboo));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Dolphe), 4));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.Bravo_Squadron_pilot, Filters.atSameSystem(self)),
                new AtCondition(self, Filters.Naboo_system), 2));
        return modifiers;
    }
}
