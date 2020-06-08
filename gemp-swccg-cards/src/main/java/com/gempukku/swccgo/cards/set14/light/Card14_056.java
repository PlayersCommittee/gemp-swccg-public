package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Starship
 * Subtype: Starfighter
 * Title: Bravo 4
 */
public class Card14_056 extends AbstractStarfighter {
    public Card14_056() {
        super(Side.LIGHT, 2, 1, 2, null, 4, 3, 3, Title.Bravo_4, Uniqueness.UNIQUE);
        setLore("Flown by Rya Kirsch at the Battle of Naboo. Bravo 4 was primarily used as a recon vessel before it began its assault on the Trade Federation's Droid Control Ship.");
        setGameText("Deploys -1 to Naboo. May add 1 pilot. While Rya piloting, immune to attrition < 4. While at Naboo system, adds 1 to each of your Force drains there.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.REPUBLIC, Icon.NAV_COMPUTER);
        addKeywords(Keyword.BRAVO_SQUADRON);
        addModelType(ModelType.N_1_STARFIGHTER);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Rya);
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
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Rya), 4));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new AtCondition(self, Filters.Naboo_system), 1, self.getOwner()));
        return modifiers;
    }
}
