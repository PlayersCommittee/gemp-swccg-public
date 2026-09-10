package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.RecirculateEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ForfeitedCardToUsedPileFromTableResult;
import com.gempukku.swccgo.logic.timing.results.PutCardInCardPileFromOffTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Subtype: Immediate
 * Title: Descent Into The Dark
 */
public class Card4_020 extends AbstractImmediateEffect {
    public Card4_020() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Descent_Into_The_Dark, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("Jedi training is a journey into the depths of an apprentice's subconscious, where one must learn to use the Force wisely. \"A Jedi's strength flows from the Force.\"");
        setGameText("During your turn, if either player just placed a card in a Used Pile, deploy on table. All Used Piles are immediately re-circulated. When any player places one or more cards in a Used Pile, Immediate Effect canceled.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isDuringYourTurn(game, playerId)
                && justPlacedCardInAUsedPile(game, effectResult)) {

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, null, null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s) - on deploy, recirculate all Used Piles
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
            action.setText("Re-circulate Used Piles");
            action.setActionMsg("Re-circulate all Used Piles");
            // Perform result(s)
            action.appendEffect(
                    new RecirculateEffect(action, playerId));
            action.appendEffect(
                    new RecirculateEffect(action, opponent));
            actions.add(action);
        }

        // Check condition(s) - cancel when any player places one or more cards in a Used Pile
        if (justPlacedCardInAUsedPile(game, effectResult)
                && GameConditions.canBeCanceled(game, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2);
            action.setSingletonTrigger(true);
            action.setText("Cancel");
            action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            actions.add(action);
        }

        return actions;
    }

    /**
     * Card-local Used Pile placement check (does not broaden shared TriggerConditions).
     * Covers from-table, from-off-table (including played Used Interrupts), and forfeit-to-Used.
     */
    private boolean justPlacedCardInAUsedPile(SwccgGame game, EffectResult effectResult) {
        if (TriggerConditions.justPlacedInUsedPileFromTable(game, effectResult, Filters.any)) {
            return true;
        }
        if (effectResult.getType() == EffectResult.Type.FORFEITED_TO_USED_PILE_FROM_TABLE) {
            PhysicalCard card = ((ForfeitedCardToUsedPileFromTableResult) effectResult).getCard();
            return card != null && GameUtils.getZoneFromZoneTop(card.getZone()) == Zone.USED_PILE;
        }
        if (effectResult.getType() == EffectResult.Type.PUT_IN_CARD_PILE_FROM_OFF_TABLE) {
            PutCardInCardPileFromOffTableResult result = (PutCardInCardPileFromOffTableResult) effectResult;
            if (result.getCardPile() == Zone.USED_PILE) {
                PhysicalCard card = result.getCard();
                return card != null && GameUtils.getZoneFromZoneTop(card.getZone()) == Zone.USED_PILE;
            }
        }
        return false;
    }
}
