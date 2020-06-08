package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Title: Abyss
 */
public class Card5_111 extends AbstractNormalEffect {
    public Card5_111() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Abyss");
        setLore("Careless abuse of the Force by a young Jedi trainee can have dire consequences. When hanging from the weather vane of Cloud City, Luke's balance was his only hope.");
        setGameText("Deploy on table. If a unique (•) card is drawn for destiny and a duplicate is on table, destiny card is lost (destiny = 0). If duplicated card is a character, it loses immunity to attrition for rest of turn and player must lose 2 Force or lose that character. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)) {
            DestinyDrawnResult destinyDrawnResult = (DestinyDrawnResult) effectResult;
            final PhysicalCard cardDrawn = destinyDrawnResult.getCard();
            if (cardDrawn != null
                    && Filters.unique.accepts(game, cardDrawn)) {
                final PhysicalCard duplicatedCard = Filters.findFirstActive(game, self, Filters.sameTitleAs(cardDrawn));
                if (duplicatedCard != null) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Make destiny card lost");
                    action.setActionMsg("Make destiny card lost and reset destiny to 0");
                    // Perform result(s)
                    action.appendEffect(
                            new PassthruEffect(action) {
                                @Override
                                protected void doPlayEffect(final SwccgGame game) {
                                    // Check if duplicated card is still in play
                                    if (Filters.in_play.accepts(game.getGameState(), game.getModifiersQuerying(), duplicatedCard)) {
                                        action.appendEffect(
                                                new ResetDestinyEffect(action, 0));
                                        action.appendEffect(
                                                new LoseDestinyCardEffect(action));
                                        if (Filters.character.accepts(game, duplicatedCard)) {
                                            action.appendEffect(
                                                    new CancelImmunityToAttritionUntilEndOfTurnEffect(action, duplicatedCard,
                                                            "Cancels " + GameUtils.getCardLink(duplicatedCard) + "'s immunity to attrition"));
                                            final String cardOwner = duplicatedCard.getOwner();
                                            action.appendEffect(
                                                    new PlayoutDecisionEffect(action, cardOwner,
                                                            new MultipleChoiceAwaitingDecision("Choose an action", new String[]{"Lose 2 Force", "Lose " + GameUtils.getCardLink(duplicatedCard)}) {
                                                                @Override
                                                                protected void validDecisionMade(int index, String result) {
                                                                    if (index == 0) {
                                                                        game.getGameState().sendMessage(cardOwner + " chooses to lose 2 Force");
                                                                        action.appendEffect(
                                                                                new LoseForceEffect(action, cardOwner, 2));
                                                                    }
                                                                    else {
                                                                        game.getGameState().sendMessage(cardOwner + " chooses to lose " + GameUtils.getCardLink(duplicatedCard));
                                                                        action.appendEffect(
                                                                                new LoseCardFromTableEffect(action, duplicatedCard));
                                                                    }
                                                                }
                                                            }));
                                        }
                                    }
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}