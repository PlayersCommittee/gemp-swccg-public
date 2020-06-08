package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Prophetess
 */
public class Card1_191 extends AbstractAlien {
    public Card1_191() {
        super(Side.DARK, 2, 3, 1, 4, 2, "Prophetess", Uniqueness.UNIQUE);
        setLore("Renowned female psychic. Predictor of doom. Agent for Governor Aryon of Tatooine. Tailed Jabba and his thugs to Docking Bay 94 when they confronted Han Solo.");
        setGameText("If you initiate a battle where present, you must predict the winner before the battle starts. If correct, randomly retrieve one lost card. If incorrect, lose 1 Force. Immune to attrition < 3.");
        addKeywords(Keyword.FEMALE);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        final String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, playerId, Filters.wherePresent(self))) {
            final boolean mayNotContributeToForceRetrieval = !Filters.mayContributeToForceRetrieval.accepts(game, self);

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Predict battle winner");
            // Perform result(s)
            action.appendEffect(
                    new PredictWinnerEffect(action, playerId) {
                        @Override
                        protected void winnerPredicted(final String predictedWinner) {
                            final int permCardId = self.getPermanentCardId();
                            action.appendEffect(
                                    new AddUntilEndOfBattleActionProxyEffect(action,
                                            new AbstractActionProxy() {
                                                @Override
                                                public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                                    final PhysicalCard self = game.findCardByPermanentId(permCardId);

                                                    // Check condition(s)
                                                    if (TriggerConditions.battleResultDetermined(game, effectResult)) {
                                                        if (TriggerConditions.wonBattle(game, effectResult, predictedWinner)) {

                                                            final RequiredGameTextTriggerAction action1 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                                            action1.setText("Retrieve 1 Force randomly");
                                                            // Perform result(s)
                                                            action1.appendEffect(
                                                                    new SendMessageEffect(action1, playerId + " correctly predicted the winner of the battle"));
                                                            if (mayNotContributeToForceRetrieval) {
                                                                action1.appendEffect(
                                                                        new SendMessageEffect(action1, "Force retrieval not allowed due to including cards not allowed to contribute to Force retrieval"));
                                                            } else {
                                                                action1.appendEffect(
                                                                        new RetrieveForceEffect(action1, playerId, 1, true));
                                                            }
                                                            return Collections.singletonList((TriggerAction) action1);
                                                        }
                                                        else {

                                                            final RequiredGameTextTriggerAction action2 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                                            action2.setText("Lose 1 Force");
                                                            // Perform result(s)
                                                            action2.appendEffect(
                                                                    new SendMessageEffect(action2, playerId + " incorrectly predicted the winner of the battle"));
                                                            action2.appendEffect(
                                                                    new LoseForceEffect(action2, playerId, 1));
                                                            return Collections.singletonList((TriggerAction) action2);
                                                        }
                                                    }
                                                    return null;
                                                }
                                            }
                                    ));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
}
