package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.ResetForceRetrievalFromCardModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Effect
 * Title: You've Got A Lot Of Guts Coming Here
 */
public class Card12_052 extends AbstractNormalEffect {
    public Card12_052() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "You've Got A Lot Of Guts Coming Here", Uniqueness.UNIQUE);
        setLore("The Empire, Lando Calrissian, Jabba the Hutt. For Han Solo, it can be very hard to tell when your past is going to catch up with you.");
        setGameText("Deploy on table. Unique (•) Rebels of ability = 3 are power and forfeit +1 (or power and forfeit +2 if at a Cloud City or Jabba's Palace site). While Han at a battleground, opponent retrieves no Force from Scum And Villainy. (Immune to Alter.)");
        addIcons(Icon.CORUSCANT);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Filter rebelFilter = Filters.and(Filters.unique, Filters.Rebel, Filters.abilityEqualTo(3));
        Evaluator evaluator = new CardMatchesEvaluator(1, 2, Filters.at(Filters.or(Filters.Cloud_City_site, Filters.Jabbas_Palace_site)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, rebelFilter, evaluator));
        modifiers.add(new ForfeitModifier(self, rebelFilter, evaluator));
        modifiers.add(new ResetForceRetrievalFromCardModifier(self, Filters.Scum_And_Villainy,
                new AtCondition(self, Filters.Han, Filters.battleground), 0, opponent));
        return modifiers;
    }
}