package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Starship
 * Subtype: Starfighter
 * Title: Bravo 3
 */
public class Card14_055 extends AbstractStarfighter {
    public Card14_055() {
        super(Side.LIGHT, 2, 1, 2, null, 4, 3, 3, Title.Bravo_3, Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.U);
        setLore("Arven Wendik's starfighter at the Battle of Naboo. Oldest starfighter in the Naboo corps.");
        setGameText("Deploys -1 to Naboo. May add one pilot. While Arven piloting, immune to attrition < 4. While at Naboo system, opponent's battle destiny draws are -1 here.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.REPUBLIC, Icon.NAV_COMPUTER);
        addKeywords(Keyword.BRAVO_SQUADRON);
        addModelType(ModelType.N_1_STARFIGHTER);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.Arven);
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
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.Arven), 4));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.here(self), new AtCondition(self, Filters.Naboo_system),
                -1, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
