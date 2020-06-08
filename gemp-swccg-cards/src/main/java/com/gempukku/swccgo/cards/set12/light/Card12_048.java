package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractPoliticalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Effect
 * Subtype: Political
 * Title: The Gravest Of Circumstances
 */
public class Card12_048 extends AbstractPoliticalEffect {
    public Card12_048() {
        super(Side.LIGHT, 3, "The Gravest Of Circumstances", Uniqueness.UNIQUE);
        setLore("'The Naboo system has been invaded by the droid armies of the Trade Federation.'");
        setGameText("Deploy on table. If no senator here, you may place a senator here from hand to add 3 to a battle destiny just drawn. If a taxation agenda here, during your turn may place any one card from hand on Used Pile; opponent must use 1 Force or lose 1 Force.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                && !GameConditions.hasStackedCards(game, self, Filters.senator)
                && GameConditions.hasInHand(game, playerId, Filters.or(Filters.senator, Filters.grantedToBePlacedOnOwnersPoliticalEffect))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add 3 to battle destiny");
            // Pay cost(s)
            action.appendCost(
                    new StackCardFromHandEffect(action, playerId, self, Filters.or(Filters.senator, Filters.grantedToBePlacedOnOwnersPoliticalEffect)));
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, 3));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasStackedCards(game, self, Filters.taxation_agenda)
                && GameConditions.hasHand(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place card from hand in Used Pile");
            action.setActionMsg("Place a card from hand in Used Pile and make opponent use 1 Force or lose 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PutCardFromHandOnUsedPileEffect(action, playerId));
            List<StandardEffect> choices = new LinkedList<StandardEffect>();
            choices.add(new UseForceEffect(action, opponent, 1));
            choices.add(new LoseForceEffect(action, opponent, 1));
            action.appendEffect(
                    new ChooseEffectEffect(action, opponent, choices));
            return Collections.singletonList(action);
        }
        return null;
    }
}