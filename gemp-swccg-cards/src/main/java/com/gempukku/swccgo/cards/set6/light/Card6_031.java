package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Palejo Reshad
 */
public class Card6_031 extends AbstractAlien {
    public Card6_031() {
        super(Side.LIGHT, 2, 2, 1, 2, 2, "Palejo Reshad", Uniqueness.UNIQUE);
        setLore("Corellian spice trader. Makes a large profit by selling spice in Jabba's court. Secretly uses part of the profit to help fund the Rebellion.");
        setGameText("Adds 2 to power of anything he pilots. While at Audience Chamber, all your Corellians are power and forfeit +1 (+2 if non-unique) and your Force generation at the Corellia system is +2.");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT);
        setSpecies(Species.CORELLIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition wysvModifyGameText = new GameTextModificationCondition(self, ModifyGameTextType.LEGACY__PALEJO_RESHAD__TREAT_AUDIENCE_CHAMBER_AS_CORELLIA);
        Condition atAudienceChamber = new OrCondition(new AndCondition(new NotCondition(wysvModifyGameText), new AtCondition(self, Filters.Audience_Chamber))
                , new AndCondition(wysvModifyGameText, new AtCondition(self, Filters.Corellia_location)));
        Filter yourCorellians = Filters.and(Filters.your(self), Filters.Corellian);
        Evaluator evaluator = new CardMatchesEvaluator(1, 2, Filters.non_unique);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, yourCorellians, atAudienceChamber, evaluator));
        modifiers.add(new ForfeitModifier(self, yourCorellians, atAudienceChamber, evaluator));
        modifiers.add(new ForceGenerationModifier(self, Filters.Corellia_system, atAudienceChamber, 2, self.getOwner()));
        return modifiers;
    }
}
