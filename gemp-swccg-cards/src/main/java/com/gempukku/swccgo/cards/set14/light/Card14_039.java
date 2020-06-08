package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceStackedCardOutOfPlayEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Theed Palace
 * Type: Effect
 * Title: Wesa Ready To Do Our-sa Part
 */
public class Card14_039 extends AbstractNormalEffect {
    public Card14_039() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Wesa Ready To Do Our-sa Part", Uniqueness.UNIQUE);
        setLore("Every Gungan that died on the battle plains gave Amidala and her troops that much more time to capture the Viceroy.");
        setGameText("Deploy on table. If your Gungan was just lost from a site, may place him here. Once per battle, may place one of your Gungans here out of play to add 4 to your total power during that battle.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.your(self), Filters.Gungan), Filters.site)) {
            PhysicalCard cardLost = ((LostFromTableResult) effectResult).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Stack " + GameUtils.getFullName(cardLost));
            action.setActionMsg("Stack " + GameUtils.getCardLink(cardLost));
            // Perform result(s)
            action.appendEffect(
                    new StackOneCardFromLostPileEffect(action, cardLost, self, false, true, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        Filter filter = Filters.and(Filters.your(self), Filters.Gungan);

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasStackedCards(game, self, filter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place Gungan out of play");
            action.setActionMsg("Add 4 to total power");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new PlaceStackedCardOutOfPlayEffect(action, playerId, self, filter));
            // Perform result(s)
            action.appendEffect(
                    new ModifyTotalPowerUntilEndOfBattleEffect(action, 4, playerId, "Adds 4 to total power"));
            return Collections.singletonList(action);
        }
        return null;
    }
}