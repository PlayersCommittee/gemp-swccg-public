package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Starship
 * Subtype: Starfighter
 * Title: Bravo 5
 */
public class Card14_057 extends AbstractStarfighter {
    public Card14_057() {
        super(Side.LIGHT, 2, 1, 2, null, 5, 3, 3, Title.Bravo_5, Uniqueness.UNIQUE);
        setLore("Ellberger's starfighter at the battle of Naboo has excellent evasion capabilities, and can remain a threat to its target for as long as possible.");
        setGameText("Deploys -1 to Naboo. May add 1 pilot. While Ellberger piloting, immune to attrition < 3. While at Naboo, opponent's battleships present are power -1.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.REPUBLIC, Icon.NAV_COMPUTER);
        addKeywords(Keyword.BRAVO_SQUADRON);
        addModelType(ModelType.N_1_STARFIGHTER);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Ellberger);
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
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Ellberger), 3));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.opponents(self), Filters.battleship, Filters.present(self)),
                new AtCondition(self, Filters.Naboo_system), -1));
        return modifiers;
    }
}
