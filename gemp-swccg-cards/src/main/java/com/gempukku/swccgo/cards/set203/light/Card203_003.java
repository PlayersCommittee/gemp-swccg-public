package com.gempukku.swccgo.cards.set203.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 3
 * Type: Character
 * Subtype: Rebel
 * Title: Captain Raymus Antilles
 */
public class Card203_003 extends AbstractRebel {
    public Card203_003() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Captain Raymus Antilles", Uniqueness.UNIQUE);
        setLore("Alderaanian leader.");
        setGameText("[Pilot] 2, 3: any Rebel capital starship. Deploys -1 aboard Tantive IV. While aboard Tantive IV, it is immune to Lateral Damage and attrition < 5.");
        addIcons(Icon.PILOT, Icon.VIRTUAL_SET_3);
        addKeywords(Keyword.LEADER, Keyword.CAPTAIN);
        setSpecies(Species.ALDERAANIAN);
        setMatchingStarshipFilter(Filters.Tantive_IV);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter tantiveIV = Filters.Tantive_IV;
        Condition aboardTantiveIV = new AboardCondition(self, tantiveIV);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.Rebel_capital_starship)));
        modifiers.add(new ImmuneToTitleModifier(self, tantiveIV, aboardTantiveIV, Title.Lateral_Damage));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, tantiveIV, aboardTantiveIV, 5));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostAboardModifier(self, -1, Filters.Tantive_IV));
        return modifiers;
    }
}
