package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ExcludedFromBattleResult;

import java.util.*;

/**
 * Set: Set 0
 * Type: Defensive Shield
 * Title: Weapons Display (V)
 */
public class Card200_030 extends AbstractDefensiveShield {
    public Card200_030() {
        super(Side.LIGHT, "Weapons Display");
        setVirtualSuffix(true);
        setLore("The X-wing's display panel allows for different firing patterns for different weapons. This gives the pilot the ability to switch weapon types with minimum time and energy loss.");
        setGameText("Plays on table. Whenever opponent excludes any character(s) from battle, they lose 2 Force. At end of opponent's turn, if you control two battlegrounds (a site and a system) and opponent deployed a card with ability and did not initiate a battle, may retrieve 1 Force.");
        addIcons(Icon.REFLECTIONS_III, Icon.VIRTUAL_DEFENSIVE_SHIELD);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        List<GameTextActionId> idList = new ArrayList<>(Arrays.asList(
                GameTextActionId.OTHER_CARD_ACTION_1, GameTextActionId.OTHER_CARD_ACTION_2, GameTextActionId.OTHER_CARD_ACTION_3, GameTextActionId.OTHER_CARD_ACTION_4
                , GameTextActionId.OTHER_CARD_ACTION_5, GameTextActionId.OTHER_CARD_ACTION_6, GameTextActionId.OTHER_CARD_ACTION_7));
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        if (TriggerConditions.justExcludedFromBattle(game, effectResult, opponent, Filters.character)) {
            int numExclusions = numberOfExclusionsFromBattle(game, effectResult, opponent, Filters.character);
            for (int i = 0; i < Math.min(numExclusions, idList.size()); i++) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, idList.get(i));
                action.setText("Make " + opponent + " lose 2 Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, 2));
                actions.add(action);
            }
        }
        return actions;
    }

    public static int numberOfExclusionsFromBattle(SwccgGame game, EffectResult effectResult, String playerId, Filterable cardExcludedFilter) {
        if (effectResult.getType() == EffectResult.Type.EXCLUDED_FROM_BATTLE) {
            ExcludedFromBattleResult excludedResult = (ExcludedFromBattleResult) effectResult;
            Collection<PhysicalCard> excludedCards = Filters.filter(excludedResult.getCardsExcluded(), game, cardExcludedFilter);
            Set<Integer> uniqueExcludingCards = new HashSet<>();
            for (PhysicalCard excludedCard : excludedCards) {
                PhysicalCard excludedByCard = excludedResult.getExcludedByCard(excludedCard);
                if (excludedByCard != null && playerId.equals(excludedByCard.getOwner()) && !uniqueExcludingCards.contains(excludedByCard.getCardId())) {
                    uniqueExcludingCards.add(excludedByCard.getCardId());
                }
            }
            return uniqueExcludingCards.size();
        }
        return 0;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isEndOfOpponentsTurn(game, effectResult, self)
                && GameConditions.hasDeployedAtLeastXCardsWithAbilityThisTurn(game, opponent, 1, Filters.any)
                && !GameConditions.hasInitiatedBattleThisTurn(game, opponent)
                && GameConditions.controls(game, playerId, Filters.battleground_site)
                && GameConditions.controls(game, playerId, Filters.battleground_system)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}