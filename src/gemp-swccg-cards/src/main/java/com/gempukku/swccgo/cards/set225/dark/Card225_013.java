package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.cards.AbstractFirstOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ArmorModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Character
 * Subtype: First Order
 * Title: Captain Moden Canady
 */
public class Card225_013 extends AbstractFirstOrder {
    public Card225_013() {
        super(Side.DARK, 2, 3, 2, 2, 5, "Captain Moden Canady", Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("Leader.");
        setGameText("[Pilot] 2. While piloting a [First Order] starship, it is armor and immunity to attrition +1 (if Fulminatrix, it is armor +2 and immune to attrition < 6 instead). During battle, cancels Alternatives To Fighting, Hit And Run, and opponent's 'reacts.'");
        addIcons(Icon.EPISODE_VII, Icon.PILOT, Icon.VIRTUAL_SET_25);
        addKeywords(Keyword.CAPTAIN, Keyword.LEADER);
        setMatchingStarshipFilter(Filters.Fulminatrix);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Evaluator evaluator = new CardMatchesEvaluator(1, 2, Filters.Fulminatrix);
        Condition pilotingFOStarshipCond = new PilotingCondition(self, Filters.First_Order_starship);
        Filter pilotingFOStarshipNotFulminatrix = Filters.and(Filters.hasPiloting(self), Filters.First_Order_starship, Filters.not(Filters.Fulminatrix));
        Filter pilotingFulminatrix = Filters.and(Filters.hasPiloting(self), Filters.Fulminatrix);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ArmorModifier(self, Filters.First_Order_starship, pilotingFOStarshipCond, evaluator));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, pilotingFOStarshipNotFulminatrix, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, pilotingFulminatrix, 6));
        modifiers.add(new MayNotReactToLocationModifier(self, Filters.here(self), opponent));
        modifiers.add(new MayNotReactFromLocationModifier(self, Filters.here(self), opponent));    
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Alternatives_To_Fighting, Filters.Hit_And_Run))
                && GameConditions.isDuringBattleAt(game, Filters.here(self))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

}
