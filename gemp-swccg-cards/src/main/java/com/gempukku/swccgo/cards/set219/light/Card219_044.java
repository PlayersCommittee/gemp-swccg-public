package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.EndOfTurnLimitCounterNotReachedCondition;
import com.gempukku.swccgo.logic.conditions.GreaterThanCondition;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.InitiateBattlesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Set: Set 19
 * Type: Effect
 * Title: Steady, Steady & Bargaining Table
 */
public class Card219_044 extends AbstractNormalEffect {
    public Card219_044() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Steady, Steady & Bargaining Table", Uniqueness.UNIQUE);
        addComboCardTitles(Title.Steady_Steady, "Bargaining Table");
        setGameText("Deploy on table. At your battlegrounds, you initiate battles for free. " +
                "At sites where you have two aliens of the same species, all immunity to attrition is canceled. " +
                "Once per turn, if you have more battlegrounds on table than opponent, subtracts 1 from deploy cost of your alien being deployed. " +
                "Opponent may use 3 Force to cancel Gungan Energy Shield. [Immune to Alter.]");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_19);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<>();
        String playerId = self.getOwner();

        OnTableEvaluator yourBattlegroundsOnTable = new OnTableEvaluator(self, Filters.and(Filters.your(playerId), Filters.battleground));
        OnTableEvaluator opponentsBattlegroundsOnTable = new OnTableEvaluator(self, Filters.and(Filters.opponents(playerId), Filters.battleground));

        GreaterThanCondition moreBattlegroundsCondition = new GreaterThanCondition(yourBattlegroundsOnTable, opponentsBattlegroundsOnTable);

        modifiers.add(new DeployCostModifier(self, Filters.and(Filters.your(self), Filters.alien),
                new AndCondition(moreBattlegroundsCondition, new EndOfTurnLimitCounterNotReachedCondition(self, 1)),
                -1));

        Filter cardsWithYourAliensOfTheSameSpecies = Filters.with(self, Filters.and(Filters.your(playerId), Filters.at(Filters.site), Filters.alienWithAnotherAlienOfSameSpecies));
        modifiers.add(new CancelImmunityToAttritionModifier(self, cardsWithYourAliensOfTheSameSpecies));
        modifiers.add(new InitiateBattlesForFreeModifier(self, Filters.and(Filters.your(playerId), Filters.battleground), playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployed(game, effectResult, self.getOwner(), Filters.and(Filters.your(self), Filters.alien))) {
            //need to account for deploying simultaneously with another card
            PhysicalCard card1 = ((PlayCardResult) effectResult).getPlayedCard();
            PhysicalCard card2 = ((PlayCardResult) effectResult).getOtherPlayedCard();
            if (card1 == null || card2 == null || !(Filters.and(card1).accepts(game, self) || Filters.and(card2).accepts(game, self))) {
                game.getModifiersQuerying().getUntilEndOfTurnLimitCounter(self, self.getOwner(), gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT).incrementToLimit(1, 1);
            }
        }
        return super.getGameTextRequiredAfterTriggers(game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.canUseForce(game, playerId, 3)
                && GameConditions.canTargetToCancel(game, self, Filters.Gungan_Energy_Shield)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Cancel Gungan Energy Shield");
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Gungan_Energy_Shield, Title.Gungan_Energy_Shield, 3);
            return Collections.singletonList(action);
        }
        return null;
    }
}
