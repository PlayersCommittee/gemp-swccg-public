package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.*;
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
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Tawss Khaa
 */
public class Card7_044 extends AbstractAlien {
    public Card7_044() {
        super(Side.LIGHT, 2, 3, 3, 4, 3, "Tawss Khaa", Uniqueness.UNIQUE);
        setLore("Female Nimbanel fortune teller. Roams the wastes of Tatooine. Trained in the ways of a Sakiyan hunter. Now tracks them for the Alliance. Risk-taker.");
        setGameText("If you just initiated battle where present, predict the winner. If correct, randomly retrieve 1 Force. If incorrect, lose 1 Force. Power +3 when present with opponent's alien of ability > 3. Immune to attrition < 3.");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.NIMBANEL);
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
                                                public List<TriggerAction> getRequiredAfterTriggers(SwccgGame swccgGame, EffectResult effectResult) {
                                                    final PhysicalCard self = game.findCardByPermanentId(permCardId);

                                                    // Check condition(s)
                                                    if (TriggerConditions.battleResultDetermined(game, effectResult)) {
                                                        if (TriggerConditions.wonBattle(swccgGame, effectResult, predictedWinner)) {

                                                            final TriggerAction action1 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                                            action.setText("Retrieve 1 Force randomly");
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
                                                            return Collections.singletonList(action1);
                                                        }
                                                        else {

                                                            final TriggerAction action2 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                                            action.setText("Lose 1 Force");
                                                            // Perform result(s)
                                                            action2.appendEffect(
                                                                    new SendMessageEffect(action2, playerId + " incorrectly predicted the winner of the battle"));
                                                            action2.appendEffect(
                                                                    new LoseForceEffect(action2, playerId, 1));
                                                            return Collections.singletonList(action2);
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
        modifiers.add(new PowerModifier(self, new PresentWithCondition(self, Filters.and(Filters.opponents(self), Filters.alien,
                Filters.abilityMoreThan(3))), 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
}
